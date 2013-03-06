/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2013 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
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
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package net.imglib2.histogram;

import net.imglib2.Dimensions;

/**
 * A Histogram1d is a histogram that tracks up to four kinds of values: 1)
 * values in the center of the distribution 2) values to the left of the center
 * of the distribution (lower tail) 3) values to the right of the center of the
 * distribution (upper tail) 4) values outside the other areas
 * <p>
 * Note: the last three classifications may not be present depending upon the
 * makeup of the input data.
 * 
 * @author Barry DeZonia
 */
public class Histogram1d<T> implements Dimensions {

	// -- instance variables --

	private BinMapper1d<T> mapper;
	private Iterable<T> data;
	private DiscreteFrequencyDistribution distrib;
	private long[] pos;
	private long ignoredCount;

	// -- constructor --

	/**
	 * Construct a histogram from an iterable set of data and a bin mapping
	 * algorithm.
	 * 
	 * @param data The iterable set of values to calculate upon
	 * @param mapper The algorithm used to map values to bins
	 */
	public Histogram1d(Iterable<T> data, BinMapper1d<T> mapper) {
		this.data = data;
		this.mapper = mapper;
		this.distrib =
			new DiscreteFrequencyDistribution(new long[] { mapper.getBinCount() });
		this.pos = new long[1];
		populateBins();
	}

	// -- public api --

	/**
	 * Returns true if the histogram has tail bins at both ends which count
	 * extreme values.
	 */
	public boolean hasTails() {
		return mapper.hasTails();
	}

	/**
	 * Returns the frequency count of values in the lower tail bin (if any).
	 */
	public long lowerTailCount() {
		if (!hasTails()) return 0;
		pos[0] = 0;
		return distrib.frequency(pos);
	}

	/**
	 * Returns the frequency count of values in the upper tail bin (if any).
	 */
	public long upperTailCount() {
		if (!hasTails()) return 0;
		pos[0] = mapper.getBinCount() - 1;
		return distrib.frequency(pos);
	}

	/**
	 * Returns the frequency count of all values in the middle of the
	 * distribution.
	 */
	public long valueCount() {
		return distributionCount() - lowerTailCount() - upperTailCount();
	}

	/**
	 * Returns the frequency count of all values in the distribution: lower tail +
	 * middle + upper tail. Does not include ignored values.
	 */
	public long distributionCount() {
		return distrib.totalValues();
	}

	/**
	 * Returns the frequency count of values that were ignored because they could
	 * not be mapped to any bin.
	 */
	public long ignoredCount() {
		return ignoredCount;
	}

	/**
	 * Returns the total count of all values observed; both within and without the
	 * entire distribution. Thus it includes ignored values. One should decide
	 * carefully between using distributionCount() and totalCount().
	 */
	public long totalCount() {
		return distributionCount() + ignoredCount();
	}

	/**
	 * Returns the frequency count of values within a bin using a representative
	 * value. Not that multiple values can be mapped to one bin so this is NOT the
	 * frequency count of this exact value in the distribution.
	 * 
	 * @param value A representative value of interest
	 */
	public long frequency(T value) {
		long bin = mapper.map(value);
		return frequency(bin);
	}

	/**
	 * Returns the frequency count of the values within a bin.
	 */
	public long frequency(long binPos) {
		if (binPos < 0 || binPos >= mapper.getBinCount()) return 0;
		pos[0] = binPos;
		return distrib.frequency(pos);
	}

	/**
	 * Returns the relative frequency of values within a bin using a
	 * representative value. Note that multiple values can be mapped to one bin so
	 * this is NOT the relative frequency of this exact value in the distribution.
	 * <p>
	 * This calculation is of the number of values in the bin divided by either
	 * the number of values in the distribution or the number of values in the
	 * center of the distribution (tails ignored).
	 * <p>
	 * One can devise other ways to count relative frequencies that consider
	 * ignored values also. If needed one can use the various count methods and
	 * frequency methods to calculate any relative frequency desired.
	 * 
	 * @param value A representative value of interest
	 * @param includeTails Flag for determining whether to include tails in
	 *          calculation.
	 */
	public double relativeFrequency(T value, boolean includeTails) {
		long bin = mapper.map(value);
		return relativeFrequency(bin, includeTails);
	}

	/**
	 * Returns the relative frequency of values within a bin.
	 * <p>
	 * This calculation is of the number of values in the bin divided by either
	 * the number of values in the distribution or the number of values in the
	 * center of the distribution (tails ignored).
	 * <p>
	 * One can devise other ways to count relative frequencies that consider
	 * ignored values also. If needed one can use the various count methods and
	 * frequency methods to calculate any relative frequency desired.
	 * 
	 * @param binPos The position of the bin of interest
	 * @param includeTails Flag for determining whether to include tails in
	 *          calculation.
	 */
	public double relativeFrequency(long binPos, boolean includeTails) {
		double numer = frequency(binPos);
		long denom = includeTails ? distributionCount() : valueCount();
		return numer / denom;
	}

	/**
	 * Returns the number of bins contained in the histogram.
	 */
	public long getBinCount() {
		return mapper.getBinCount();
	}

	/**
	 * Returns a bin position by mapping from a representative value.
	 */
	public long map(T value) {
		return mapper.map(value);
	}

	/**
	 * Recalculates the underlying bin distribution. Use this if the iterable data
	 * source has changed after this histogram was built.
	 */
	public void recalc() {
		populateBins();
	}

	/**
	 * Gets the value associated with the center of a bin.
	 * 
	 * @param binPos The bin number of interest
	 * @param value The output to fill with the center value
	 */
	public void getCenterValue(long binPos, T value) {
		mapper.getCenterValue(binPos, value);
	}

	/**
	 * Gets the value associated with the left edge of a bin.
	 * 
	 * @param binPos The bin number of interest
	 * @param value The output to fill with the left edge value
	 */
	public void getLowerBound(long binPos, T value) {
		mapper.getLowerBound(binPos, value);
	}

	/**
	 * Gets the value associated with the right edge of the bin.
	 * 
	 * @param binPos The bin number of interest
	 * @param value The output to fill with the right edge value
	 */
	public void getUpperBound(long binPos, T value) {
		mapper.getUpperBound(binPos, value);
	}

	/**
	 * Returns true if the given bin interval is closed on the right
	 * 
	 * @param binPos The bin number of the interval of interest
	 */
	public boolean includesUpperBound(long binPos) {
		return mapper.includesUpperBound(binPos);
	}

	/**
	 * Returns true if the given bin interval is closed on the left
	 * 
	 * @param binPos The bin number of the interval of interest
	 */
	public boolean includesLowerBound(long binPos) {
		return mapper.includesLowerBound(binPos);
	}

	/**
	 * Returns true if a given value is mapped to the lower tail of the
	 * distribution.
	 * 
	 * @param value The value to determine the location of
	 */
	public boolean isInLowerTail(T value) {
		if (!hasTails()) return false;
		long bin = mapper.map(value);
		return bin == 0;
	}

	/**
	 * Returns true if a given value is mapped to the upper tail of the
	 * distribution.
	 * 
	 * @param value The value to determine the location of
	 */
	public boolean isInUpperTail(T value) {
		if (!hasTails()) return false;
		long bin = mapper.map(value);
		return bin == getBinCount() - 1;
	}

	/**
	 * Returns true if a given value is mapped to the middle of the distribution.
	 * 
	 * @param value The value to determine the location of
	 */
	public boolean isInMiddle(T value) {
		if (!hasTails()) return true;
		long bin = mapper.map(value);
		return (bin > 0) && (bin < getBinCount() - 1);
	}

	/**
	 * Returns true if a given value is outside the distribution.
	 * 
	 * @param value The value to determine the location of
	 */
	public boolean isOutside(T value) {
		long bin = mapper.map(value);
		return (bin == Long.MIN_VALUE) || (bin == Long.MAX_VALUE);
	}

	/**
	 * Return the number of dimensions of the frequency distribution of this
	 * histogram.
	 */
	@Override
	public int numDimensions() {
		return distrib.numDimensions();
	}

	/**
	 * Return the size of the given dimension of the frequency distribution of
	 * this histogram.
	 */
	@Override
	public long dimension(int d) {
		return distrib.dimension(d);
	}

	/**
	 * Fill the provided long[] with the sizes of all dimensions of the frequency
	 * distribution of this histogram.
	 */
	@Override
	public void dimensions(long[] dims) {
		distrib.dimensions(dims);
	}

	// -- helpers --

	private void populateBins() {
		distrib.resetCounters();
		ignoredCount = 0;
		for (T value : data) {
			long bin = mapper.map(value);
			if (bin == Long.MIN_VALUE || bin == Long.MAX_VALUE) {
				ignoredCount++;
			}
			else {
				pos[0] = bin;
				distrib.increment(pos);
			}
		}
	}
}
