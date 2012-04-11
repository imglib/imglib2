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

/**
 * 
 * StatCollector calculates statistics from a list of doubles stored in a
 * {@link: PrimitiveDoubleArray}.
 * 
 * @author Barry DeZonia
 *
 */
public class StatCalculator {

	public double alphaTrimmedMean(PrimitiveDoubleArray values, int halfTrimSize){
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

	public double arithmeticMean(PrimitiveDoubleArray values) {
		final int numElements = values.size();
		if (numElements <= 0)
			throw new IllegalArgumentException(
				"number of samples must be greater than 0");
		double sum = 0;
		for (int i = 0; i < numElements; i++)
			sum += values.get(i);
		return sum / numElements;
	}

	public double contraharmonicMean(PrimitiveDoubleArray values, double order) {
		final int numElements = values.size();
		if (numElements <= 0)
			throw new IllegalArgumentException(
				"number of samples must be greater than 0");
		double sum1 = 0;
		double sum2 = 0;
		for (int i = 0; i < numElements; i++) {
			double value = values.get(i);
			sum1 += Math.pow(value, order+1);
			sum2 += Math.pow(value, order);
		}
		return sum1 / sum2;
	}

	public double geometricMean(PrimitiveDoubleArray values) {
		int numElements = values.size();
		if (numElements <= 0)
			throw new IllegalArgumentException(
					"number of samples must be greater than 0");
		double prod = 1;
		for (int i = 0; i < numElements; i++)
			prod *= values.get(i);
		return Math.pow(prod, 1.0/numElements);
	}
	
	public double harmonicMean(PrimitiveDoubleArray values) {
		int numElements = values.size();
		if (numElements <= 0)
			throw new IllegalArgumentException(
					"number of samples must be greater than 0");
		double sum = 0;
		for (int i = 0; i < numElements; i++)
			sum += 1 / values.get(i);
		return numElements / sum; // looks weird but it is correct
	}

	public double max(PrimitiveDoubleArray values) {
		final int numElements = values.size();
		if (numElements <= 0)
			throw new IllegalArgumentException(
				"number of samples must be greater than 0");
		double max = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < numElements; i++) {
			max = Math.max(max, values.get(i));
		}
		return max;
	}
	
	public double median(PrimitiveDoubleArray values) {
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
	
	public double midpoint(PrimitiveDoubleArray values) {
		double min = min(values);
		double max = max(values);
		return (min + max) / 2;
	}
	
	public double min(PrimitiveDoubleArray values) {
		final int numElements = values.size();
		if (numElements <= 0)
			throw new IllegalArgumentException(
				"number of samples must be greater than 0");
		double min = Double.POSITIVE_INFINITY;
		for (int i = 0; i < numElements; i++) {
			min = Math.min(min, values.get(i));
		}
		return min;
	}

	public double product(PrimitiveDoubleArray values) {
		final int numElements = values.size();
		if (numElements <= 0)
			throw new IllegalArgumentException(
				"number of samples must be greater than 0");
		double prod = 1;
		for (int i = 0; i < numElements; i++)
			prod *= values.get(i);
		return prod;
	}
	
	public double sampleVariance(PrimitiveDoubleArray values) {
		final int numElements = values.size();
		if (numElements <= 1) return 0;
		double sum = sumOfSquaredDeviations(values);
		return sum / (numElements-1);
	}

	public double sum(PrimitiveDoubleArray values) {
		final int numElements = values.size();
		if (numElements <= 0)
			throw new IllegalArgumentException(
				"number of samples must be greater than 0");
		double sum = 0;
		for (int i = 0; i < numElements; i++)
			sum += values.get(i);
		return sum;
	}
	
	public double sumOfSquaredDeviations(PrimitiveDoubleArray values) {
		final int numElements = values.size();
		if (numElements <= 0) return 0;
		final double mean = arithmeticMean(values);
		double sum = 0;
		for (int i = 0; i < numElements; i++) {
			double term = values.get(i) - mean;
			sum += (term * term);
		}
		return sum;
	}
	
	public double variance(PrimitiveDoubleArray values) {
		final int numElements = values.size();
		if (numElements <= 1) return 0;
		double sum = sumOfSquaredDeviations(values);
		return sum / numElements;
	}
}
