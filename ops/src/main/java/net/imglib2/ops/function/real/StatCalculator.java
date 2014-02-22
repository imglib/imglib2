/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

// Most definitions care of Digital Imaage Processing, Gonzalez & Woods, 2008

package net.imglib2.ops.function.real;

import java.util.Arrays;

import net.imglib2.ops.function.Function;
import net.imglib2.ops.pointset.PointSet;
import net.imglib2.ops.pointset.PointSetIterator;
import net.imglib2.ops.util.Tuple2;
import net.imglib2.type.numeric.RealType;

import org.scijava.util.DoubleArray;


// NOTE:
//   For a couple methods this class uses a PrimitiveDoubleArray to store copies
// of data. It could use an Img<DoubleType> instead in many cases and we should
// work towards the elimination of PrimitiveDoubleArray.
//   However there are cases where we cannot do so both efficiently and safely.
// Imagine you have a ConditionalPointSet where it constrains the values of the
// coords to be in a rectangle. You compute median and the set has 10 values.
// Now you translate() the ConditionalPointSet. If you calc the size() of the
// moved set the number of elements may have changed. A fixed Img<DoubleType>
// is inadequate for this. You may try to access beyond the end or incorrectly
// calc the median by including some uninitialized values. And for efficiency's
// sake we don't want to reallocate the Img every time reset() is called since
// it may be called once per point as a region slides over an image point by
// point. And also we want to avoid pointSet.size() calls because they can be
// expensive.
//   PrimitiveDoubleArray is useful here. It records value within an auto
// expanding array. You can query and sort values within a subset of allocated
// space. Thus reset() does not become inefficient. And moving
// ConditionalPointSets does not cause incorrect calculations and crashes.

// TODO: define a class that is an Img<DoubleType> that replaces
// PrimitiveDoubleArray. It should auto expand. It should have the concept of
// an allocated size and a used element size. Finally one should be able to
// efficiently sort the used values within it.

// BDZ 7-19-13
// all the above may have been addressed by using SciJava's DoubleArray.

/**
 * 
 * StatCollector calculates statistics from a {@link PointSet} region of a
 * {@link Function}. Most methods use minimal memory.
 * 
 * @author Barry DeZonia
 *
 */
public class StatCalculator<T extends RealType<T>> {

	// -- instance variables --
	
	private Function<long[],T> func;
	private PointSet region;
	private PointSetIterator iter;
	private final DoubleArray values; // see NOTE at top re: this use
	
	// -- constructor --

	/**
	 * Create a StatCalculator upon a region of a function.
	 * @param func
	 * The {@link Function} to gather samples from
	 * @param region
	 * The {@link PointSet} region over which to gather samples
	 */
	public StatCalculator(Function<long[],T> func, PointSet region) {
		this.func = func;
		this.region = region;
		this.iter = region.iterator();
		this.values = new DoubleArray();
	}

	// -- public api --

	/**
	 * Resets the StatCalculator to work with a new function and/or region. The
	 * calculator does the minimum amount of reinitialization.
	 * 
	 * @param newFunc
	 * The new {@link Function} to use for obtaining sample values
	 * @param newRegion
	 * The new {@link PointSet} region over which to gather samples
	 */
	public void reset(Function<long[],T> newFunc, PointSet newRegion) {
		func = newFunc;
		if (newRegion == region) {
			iter.reset();
		}
		else {
			region = newRegion;
			iter = region.iterator();
		}
		values.clear();
	}

	/**
	 * Computes an alpha trimmed mean upon the current region of the current
	 * function. Note that this method uses memory to make a copy of the input
	 * values. Larger input regions might require a lot of memory.
	 * 
	 * @param alpha A number between 0 and 0.5 specifying the proportion of
	 *          samples to ignore on each end.
	 * @return The measured value
	 */
	public double alphaTrimmedMean(double alpha) {
		if ((alpha < 0) || (alpha >= 0.5))
				throw new IllegalArgumentException("alpha value must be >= 0 and < 0.5");
		T tmp = func.createOutput();
		values.clear();
		iter.reset();
		while (iter.hasNext()) {
			long[] pos = iter.next();
			func.compute(pos, tmp);
			values.add(tmp.getRealDouble());
		}
		Arrays.sort(values.getArray(), 0, values.size());
		double tailSize = alpha * values.size();
		// can we avoid interpolation?
		if (tailSize == Math.floor(tailSize)) {
			// yes, trim count is exactly an integer
			return calcTrimmedMean(values, (int) tailSize);
		}
		// no, trim count is a float value
		// calc two trimmed means and interpolate to find the value between them
		double mean1 = calcTrimmedMean(values, (int) Math.floor(tailSize));
		double mean2 = calcTrimmedMean(values, (int) Math.ceil(tailSize));
		double fraction = tailSize - Math.floor(tailSize);
		double interpolation = ((1 - fraction) * mean1) + (fraction * mean2);
		return interpolation;
	}

	/**
	 * Computes the arithmetic mean (or average) upon the current region of the
	 * current function.
	 * 
	 * @return The measured value
	 */
	public double arithmeticMean() {
		T tmp = func.createOutput();
		double sum = 0;
		long numElements = 0;
		iter.reset();
		while (iter.hasNext()) {
			long[] pos = iter.next();
			func.compute(pos, tmp);
			sum += tmp.getRealDouble();
			numElements++;
		}
		return sum / numElements;
	}

	/**
	 * Computes the center of mass point of the current region of the current
	 * function. Returns it as a pair of Doubles. The first element in the pair is
	 * the x component and the second element is the y component.
	 * 
	 * @return The measured point stored in a Tuple2
	 */
	public Tuple2<Double, Double> centerOfMassXY() {
		T tmp = func.createOutput();
		double sumV = 0;
		double sumX = 0;
		double sumY = 0;
		iter.reset();
		while (iter.hasNext()) {
			long[] pos = iter.next();
			func.compute(pos, tmp);
			double v = tmp.getRealDouble();
			sumV += v;
			sumX += v * pos[0];
			sumY += v * pos[1];
		}
		double cx = (sumX / sumV) + 0.5;
		double cy = (sumY / sumV) + 0.5;
		return new Tuple2<Double, Double>(cx, cy);
	}

	/**
	 * Computes the centroid point of the current region and returns it as a pair
	 * of Doubles. The first element in the pair is the x component and the second
	 * element is the y component.
	 * 
	 * @return The measured point stored in a Tuple2
	 */
	public Tuple2<Double, Double> centroidXY() {
		double sumX = 0;
		double sumY = 0;
		long numElements = 0;
		iter.reset();
		while (iter.hasNext()) {
			long[] pos = iter.next();
			sumX += pos[0];
			sumY += pos[1];
			numElements++;
		}
		double cx = (sumX / numElements) + 0.5;
		double cy = (sumY / numElements) + 0.5;
		return new Tuple2<Double, Double>(cx, cy);
	}

	/**
	 * Computes the contraharmonic mean upon the current region of the current
	 * function.
	 * 
	 * @return The measured value
	 */
	public double contraharmonicMean(double order) {
		T tmp = func.createOutput();
		double sum1 = 0;
		double sum2 = 0;
		iter.reset();
		while (iter.hasNext()) {
			long[] pos = iter.next();
			func.compute(pos, tmp);
			double value = tmp.getRealDouble();
			sum1 += Math.pow(value, order+1);
			sum2 += Math.pow(value, order);
		}
		return sum1 / sum2;
	}

	/**
	 * Computes the geometric mean upon the current region of the
	 * current function.
	 * 
	 * @return
	 * The measured value
	 */
	public double geometricMean() {
		return Math.pow(product(), 1.0/region.size());
	}
	
	/**
	 * Computes the harmonic mean upon the current region of the
	 * current function.
	 * 
	 * @return
	 * The measured value
	 */
	public double harmonicMean() {
		T tmp = func.createOutput();
		double sum = 0;
		long numElements = 0;
		iter.reset();
		while (iter.hasNext()) {
			long[] pos = iter.next();
			func.compute(pos, tmp);
			double value = tmp.getRealDouble();
			sum += 1 / value;
			numElements++;
		}
		return numElements / sum; // looks weird but it is correct
	}

	/**
	 * Computes the maximum value upon the current region of the
	 * current function.
	 * 
	 * @return
	 * The measured value
	 */
	public double max() {
		T tmp = func.createOutput();
		double max = Double.NEGATIVE_INFINITY;
		iter.reset();
		while (iter.hasNext()) {
			long[] pos = iter.next();
			func.compute(pos, tmp);
			double value = tmp.getRealDouble();
			max = Math.max(max, value);
		}
		return max;
	}
	
	/**
	 * Computes the median upon the current region of the current
	 * function. Note that this method uses memory to make a copy of the input
	 * values. Larger input regions might require a lot of memory.
	 * 
	 * @return
	 * The measured value
	 */
	public double median() {
		T tmp = func.createOutput();
		values.clear();
		iter.reset();
		while (iter.hasNext()) {
			long[] pos = iter.next();
			func.compute(pos, tmp);
			values.add(tmp.getRealDouble());
		}
		int count = values.size();

		if (count <= 0)
			throw new IllegalArgumentException(
				"number of samples must be greater than 0");
		
		Arrays.sort(values.getArray(), 0, count);

		// odd number of elements
		if ((count % 2) == 1) return values.getValue(count / 2);
		
		// else an even number of elements
		double value1 = values.getValue((count / 2) - 1);
		double value2 = values.getValue((count / 2));
		return (value1 + value2) / 2;
	}
	
	/**
	 * Computes the midpoint value upon the current region of the
	 * current function. Midpoint = (min + max) / 2;
	 * 
	 * @return
	 * The measured value
	 */
	public double midpoint() {
		return (min() + max()) / 2;
	}
	
	/**
	 * Computes the minimum value upon the current region of the
	 * current function.
	 * 
	 * @return
	 * The measured value
	 */
	public double min() {
		T tmp = func.createOutput();
		double min = Double.POSITIVE_INFINITY;
		iter.reset();
		while (iter.hasNext()) {
			long[] pos = iter.next();
			func.compute(pos, tmp);
			double value = tmp.getRealDouble();
			min = Math.min(min, value);
		}
		return min;
	}

	// reference: http://www.tc3.edu/instruct/sbrown/stat/shape.htm
	
	/**
	 * Computes the (biased) kurtosis value upon the current region of the
	 * current function.
	 * 
	 * @return
	 * The measured value
	 */
	public double populationKurtosis() {
		T tmp = func.createOutput();
		double xbar = arithmeticMean(); 
		double s2 = 0;
		double s4 = 0;
		long numElements = 0;
		iter.reset();
		while (iter.hasNext()) {
			long[] pos = iter.next();
			func.compute(pos, tmp);
			double value = tmp.getRealDouble();
			numElements++;
			double v = value - xbar;
			double v2 = v * v;
			double v4 = v2 * v2;
			s2 += v2;
			s4 += v4;
		}
		double n = numElements;
		double m2 = s2 / n;
		double m4 = s4 / n;
		return m4 / (m2 * m2);
	}
	
	// reference: http://www.tc3.edu/instruct/sbrown/stat/shape.htm
	
	/**
	 * Computes the (biased) kurtosis excess value upon the current region of the
	 * current function.
	 * 
	 * @return
	 * The measured value
	 */
	public double populationKurtosisExcess() {
		return populationKurtosis() - 3;
	}
	
	// reference: http://www.tc3.edu/instruct/sbrown/stat/shape.htm
	
	/**
	 * Computes the (biased) skew value upon the current region of the
	 * current function.
	 * 
	 * @return
	 * The measured value
	 */
	public double populationSkew() {
		T tmp = func.createOutput();
		double xbar = arithmeticMean(); 
		double s2 = 0;
		double s3 = 0;
		long numElements = 0;
		iter.reset();
		while (iter.hasNext()) {
			long[] pos = iter.next();
			func.compute(pos, tmp);
			double value = tmp.getRealDouble();
			numElements++;
			double v = value - xbar;
			double v2 = v * v;
			double v3 = v2 * v;
			s2 += v2;
			s3 += v3;
		}
		double n = numElements;
		double m2 = s2 / n;
		double m3 = s3 / n;
		return m3 / Math.pow(m2, 1.5);
	}
	
	/**
	 * Computes the (biased) standard deviation upon the current region of the
	 * current function.
	 * 
	 * @return
	 * The measured value
	 */
	public double populationStdDev() {
		return Math.sqrt(populationVariance());
	}
	
	/**
	 * Computes the (biased) variance upon the current region of the
	 * current function.
	 * 
	 * @return
	 * The measured value
	 */
	public double populationVariance() {
		double sum = sumOfSquaredDeviations();
		long numElements = region.size();
		return sum / numElements;
	}

	/**
	 * Computes the product of all the values of the current region of the
	 * current function.
	 * 
	 * @return
	 * The measured value
	 */
	public double product() {
		T tmp = func.createOutput();
		double prod = 1;
		iter.reset();
		while (iter.hasNext()) {
			long[] pos = iter.next();
			func.compute(pos, tmp);
			double value = tmp.getRealDouble();
			prod *= value;
		}
		return prod;
	}

	// reference: http://www.tc3.edu/instruct/sbrown/stat/shape.htm

	/**
	 * Computes the (unbiased) kurtosis value upon the current region of the
	 * current function.
	 * 
	 * @return
	 * The measured value
	 */
	public double sampleKurtosis() {
		double n = region.size();
		double biasedValue = populationKurtosis();
		double unbiasedValue = biasedValue * (n+1) + 6;
		unbiasedValue *= (n-1) / ((n-2) * (n-3));
		return unbiasedValue;
	}
	
	// reference: http://www.tc3.edu/instruct/sbrown/stat/shape.htm

	/**
	 * Computes the (unbiased) kurtosis excess value upon the current region of
	 * the current function.
	 * 
	 * @return
	 * The measured value
	 */
	public double sampleKurtosisExcess() {
		return sampleKurtosis() - 3;
	}
	
	/**
	 * Computes the (unbiased) skew value upon the current region of the
	 * current function.
	 * 
	 * @return
	 * The measured value
	 */
	public double sampleSkew() {
		double n = region.size();
		double biasedValue = populationSkew();
		double unbiasedValue = biasedValue * Math.sqrt(n * (n-1)) / (n-2);
		return unbiasedValue;
	}
	
	/**
	 * Computes the (unbiased) standard deviation upon the current region of the
	 * current function.
	 * 
	 * @return
	 * The measured value
	 */
	public double sampleStdDev() {
		return Math.sqrt(sampleVariance());
	}
	
	/**
	 * Computes the (unbiased) variance upon the current region of the
	 * current function.
	 * 
	 * @return
	 * The measured value
	 */
	public double sampleVariance() {
		double sum = sumOfSquaredDeviations();
		long numElements = region.size();
		return sum / (numElements-1);
	}
	
	/**
	 * Computes the sum of all the values of the current region of the
	 * current function.
	 * 
	 * @return
	 * The measured value
	 */
	public double sum() {
		T tmp = func.createOutput();
		double sum = 0;
		iter.reset();
		while (iter.hasNext()) {
			long[] pos = iter.next();
			func.compute(pos, tmp);
			double value = tmp.getRealDouble();
			sum += value;
		}
		return sum;
	}
	
	/**
	 * Computes the sum of squared deviations of the values of the current region
	 * of the current function.
	 * 
	 * @return
	 * The measured value
	 */
	public double sumOfSquaredDeviations() {
		T tmp = func.createOutput();
		final double xbar = arithmeticMean();
		double sum = 0;
		iter.reset();
		while (iter.hasNext()) {
			long[] pos = iter.next();
			func.compute(pos, tmp);
			double value = tmp.getRealDouble();
			double term = value - xbar;
			sum += (term * term);
		}
		return sum;
	}
	
	/**
	 * Computes a trimmed mean upon the current region of the current function.
	 * Note that this method uses memory to make a copy of the input values.
	 * Larger input regions might require a lot of memory.
	 * 
	 * @param halfTrimSize The number of samples to ignore from each end of the
	 *          data
	 * @return The measured value
	 */
	public double trimmedMean(int halfTrimSize) {
		T tmp = func.createOutput();
		values.clear();
		iter.reset();
		while (iter.hasNext()) {
			long[] pos = iter.next();
			func.compute(pos, tmp);
			values.add(tmp.getRealDouble());
		}
		Arrays.sort(values.getArray(), 0, values.size());
		return calcTrimmedMean(values, halfTrimSize);
	}

	/**
	 * Computes a weighted average of the current function values over the current
	 * region. The weights are provided and there must be as many weights as there
	 * are points in the current region.
	 * 
	 * @return The measured value
	 */
	public double weightedAverage(double[] weights) {
		long numElements = region.size();
		if (numElements != weights.length)
			throw new IllegalArgumentException(
				"number of weights does not equal number of samples");
		double sum = weightedSum(weights);
		return sum / numElements;
	}
	
	/**
	 * Computes a weighted sum of the current function values over the current
	 * region. The weights are provided and there must be as many weights as there
	 * are points in the current region.
	 * 
	 * @return
	 * The measured value
	 */
	public double weightedSum(double[] weights) {
		long numElements = region.size();
		if (numElements != weights.length)
			throw new IllegalArgumentException(
				"number of weights does not equal number of samples");
		T tmp = func.createOutput();
		double sum = 0;
		int i = 0;
		iter.reset();
		while (iter.hasNext()) {
			long[] pos = iter.next();
			func.compute(pos, tmp);
			double value = tmp.getRealDouble();
			sum += weights[i++] * value;
		}
		return sum;
	}

	// -- helpers --

	// NB - assumes values already sorted

	private double calcTrimmedMean(DoubleArray vals, int halfTrim) {
		final int trimSize = halfTrim * 2;
		final int numElem = vals.size();
		if (numElem <= trimSize) throw new IllegalArgumentException(
			"number of samples must be greater than number of trimmed values");
		final int top = numElem - halfTrim;
		double sum = 0;
		for (int i = halfTrim; i < top; i++) {
			sum += vals.getValue(i);
		}
		return sum / (numElem - trimSize);
	}
}
