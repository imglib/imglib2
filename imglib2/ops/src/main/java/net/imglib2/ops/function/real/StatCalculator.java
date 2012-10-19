/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
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
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

// Most definitions care of Digital Imaage Processing, Gonzalez & Woods, 2008

package net.imglib2.ops.function.real;

import net.imglib2.ops.function.Function;
import net.imglib2.ops.pointset.PointSet;
import net.imglib2.ops.pointset.PointSetIterator;
import net.imglib2.type.numeric.RealType;

/**
 * 
 * StatCollector calculates statistics from a {@link PointSet} region of a
 * {@link Function}. Most methods use minimal memory.
 * 
 * @author Barry DeZonia
 *
 */
public class StatCalculator<T extends RealType<T>> {

	private Function<long[],T> func;
	private PointSet region;
	private PointSetIterator iter;
	private final PrimitiveDoubleArray values;
	
	public StatCalculator(Function<long[],T> func, PointSet region) {
		this.func = func;
		this.region = region;
		this.iter = region.createIterator();
		this.values = new PrimitiveDoubleArray();
	}

	public void reset(Function<long[],T> newFunc, PointSet newRegion) {
		func = newFunc;
		if (newRegion == region) {
			iter.reset();
		}
		else {
			region = newRegion;
			iter = region.createIterator();
		}
		values.clear();
	}
	
	public double alphaTrimmedMean(int halfTrimSize){
		T tmp = func.createOutput();
		values.clear();
		iter.reset();
		while (iter.hasNext()) {
			long[] pos = iter.next();
			func.compute(pos, tmp);
			values.add(tmp.getRealDouble());
		}
		final int trimSize = halfTrimSize * 2;
		final int numElements = values.size();
		if (numElements <= trimSize)
			throw new IllegalArgumentException(
				"number of samples must be greater than number of trimmed values");
		values.sortValues();
		final int top = values.size() - halfTrimSize;
		double sum = 0;
		for (int i = halfTrimSize; i < top; i++) {
			sum += values.get(i);
		}
		return sum / (numElements - trimSize);
	}

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

	public double geometricMean() {
		return Math.pow(product(), 1.0/region.calcSize());
	}
	
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

	// reference: http://www.tc3.edu/instruct/sbrown/stat/shape.htm
	
	public double kurtosisBiased() {
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

	public double kurtosisUnbiased() {
		double n = region.calcSize();
		double biasedValue = kurtosisBiased();
		double unbiasedValue = biasedValue * (n+1) + 6;
		unbiasedValue *= (n-1) / ((n-2) * (n-3));
		return unbiasedValue;
	}
	
	// reference: http://www.tc3.edu/instruct/sbrown/stat/shape.htm
	
	public double kurtosisExcessBiased() {
		return kurtosisBiased() - 3;
	}
	
	// reference: http://www.tc3.edu/instruct/sbrown/stat/shape.htm

	public double kurtosisExcessUnbiased() {
		return kurtosisUnbiased() - 3;
	}
	
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
	
	public double median() {
		T tmp = func.createOutput();
		values.clear();
		iter.reset();
		while (iter.hasNext()) {
			long[] pos = iter.next();
			func.compute(pos, tmp);
			values.add(tmp.getRealDouble());
		}
		final int numElements = values.size();
		if (numElements <= 0)
			throw new IllegalArgumentException(
				"number of samples must be greater than 0");
		
		values.sortValues();

		// odd number of elements
		if ((numElements % 2) == 1)
			return values.get(numElements/2);
		
		// else an even number of elements
		double value1 = values.get((numElements/2) - 1); 
		double value2 = values.get((numElements/2));
		return (value1 + value2) / 2;
	}
	
	public double midpoint() {
		return (min() + max()) / 2;
	}
	
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
	
	public double skewBiased() {
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
	
	public double skewUnbiased() {
		double n = region.calcSize();
		double biasedValue = skewBiased();
		double unbiasedValue = biasedValue * Math.sqrt(n * (n-1)) / (n-2);
		return unbiasedValue;
	}
	
	public double stdDevBiased() {
		return Math.sqrt(varianceBiased());
	}
	
	public double stdDevUnbiased() {
		return Math.sqrt(varianceUnbiased());
	}
	
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
	
	public double varianceBiased() {
		double sum = sumOfSquaredDeviations();
		long numElements = region.calcSize();
		return sum / numElements;
	}

	public double varianceUnbiased() {
		double sum = sumOfSquaredDeviations();
		long numElements = region.calcSize();
		return sum / (numElements-1);
	}
	
	public double weightedAverage(double[] weights) {
		long numElements = region.calcSize();
		if (numElements != weights.length)
			throw new IllegalArgumentException(
				"number of weights does not equal number of samples");
		double sum = weightedSum(weights);
		return sum / numElements;
	}
	
	public double weightedSum(double[] weights) {
		long numElements = region.calcSize();
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
}
