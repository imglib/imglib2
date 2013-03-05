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

package net.imglib2.histogram.rev2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// TODO - calculate lazily but should be able to count upper/lower/middle in
// one pass rather than the multiple passes that are now in place.

/**
 * A HistogramNd is an n-dimensional histogram that tracks up to four kinds of
 * values: 1) values in the center of the distribution 2) values to the left of
 * the center of the distribution (lower tail) 3) values to the right of the
 * center of the distribution (upper tail) 4) values outside the other areas
 * <p>
 * Note: the last three classifications may not be present depending upon the
 * makeup of the input data.
 * 
 * @author Barry DeZonia
 */
public class HistogramNd<T> {

	// -- instance variables --

	private List<BinMapper1d<T>> mappers;
	private DiscreteFrequencyDistribution distrib;
	private Iterable<List<T>> iterable;
	private List<Iterable<T>> iterables;
	private long[] pos;
	private long ignoredCount;

	// -- constructors --

	/**
	 * Construct a histogram from an iterable set of data and a list of bin
	 * mapping algorithms. Must be given one iterable data source that returns
	 * muitlple data values at each point.
	 * 
	 * @param data The iterable set of values to calculate upon
	 * @param mappers The algorithms used to map values to bins
	 */
	public HistogramNd(Iterable<List<T>> data, List<BinMapper1d<T>> mappers) {
		this.iterable = data;
		this.iterables = null;
		this.mappers = mappers;
		init();
	}
	
	/**
	 * Construct a histogram from an iterable set of data and a list of bin
	 * mapping algorithms. Must be given multiple iterable data sources that each
	 * return a single data value at each point.
	 * 
	 * @param data The iterable set of values to calculate upon
	 * @param mappers The algorithms used to map values to bins
	 */
	public HistogramNd(List<Iterable<T>> data, List<BinMapper1d<T>> mappers) {
		this.iterable = null;
		this.iterables = data;
		this.mappers = mappers;
		init();
	}

	/** constructor helper */
	private void init() {
		long[] dims = new long[mappers.size()];
		for (int i = 0; i < mappers.size(); i++) {
			dims[i] = mappers.get(i).getBinCount();
		}
		distrib = new DiscreteFrequencyDistribution(dims);
		pos = new long[mappers.size()];
		populateBins();
	}
	
	// -- public api --

	/**
	 * Returns true if the histogram has tail bins which count extreme values for
	 * the given dimension.
	 */
	public boolean hasTails(int dim) {
		return mappers.get(dim).hasTails();
	}

	/**
	 * Returns true if the histogram has tail bins which count extreme values for
	 * one or more dimensions
	 */
	public boolean hasTails() {
		for (int i = 0; i < mappers.size(); i++) {
			if (hasTails(i)) return true;
		}
		return false;
	}

	/**
	 * Returns the frequency count of values in the lower tail bin (if any) for
	 * the given dimension.
	 */
	public long lowerTailCount(int dim) {
		if (!hasTails(dim)) return 0;
		long sum = 0;
		Points points = new Points();
		while (points.hasNext()) {
			long[] binPos = points.next();
			if (binPos[dim] == 0) sum += distrib.frequency(binPos);
		}
		return sum;
	}

	/**
	 * Returns the frequency count of values in all lower tail bins (if any).
	 */
	public long lowerTailCount() {
		if (!hasTails()) return 0;
		long sum = 0;
		Points points = new Points();
		while (points.hasNext()) {
			long[] binPos = points.next();
			for (int i = 0; i < mappers.size(); i++) {
				if (binPos[i] == 0) {
					sum += distrib.frequency(binPos);
					break;
				}
			}
		}
		return sum;
	}
	
	/**
	 * Returns the frequency count of values in the upper tail bin (if any) for
	 * the given dimension.
	 */
	public long upperTailCount(int dim) {
		if (!hasTails(dim)) return 0;
		long dimSize = mappers.get(dim).getBinCount();
		long sum = 0;
		Points points = new Points();
		while (points.hasNext()) {
			long[] binPos = points.next();
			if (binPos[dim] == dimSize - 1) sum += distrib.frequency(binPos);
		}
		return sum;
	}

	/**
	 * Returns the frequency count of values in all upper tail bins (if any).
	 */
	public long upperTailCount() {
		if (!hasTails()) return 0;
		long sum = 0;
		Points points = new Points();
		while (points.hasNext()) {
			long[] binPos = points.next();
			for (int i = 0; i < mappers.size(); i++) {
				if (binPos[i] == mappers.get(i).getBinCount() - 1) {
					sum += distrib.frequency(binPos);
					break;
				}
			}
		}
		return sum;
	}

	/**
	 * Returns the frequency count of all values in the middle of the distribution
	 * for a given dimension.
	 */
	public long valueCount(int dim) {
		boolean hasTails = hasTails(dim);
		long dimSize = mappers.get(dim).getBinCount();
		long sum = 0;
		Points points = new Points();
		while (points.hasNext()) {
			long[] binPos = points.next();
			boolean inTail = false;
			if (hasTails && (binPos[dim] == 0) || (binPos[dim] == dimSize - 1)) {
				inTail = true;
			}
			if (!inTail) sum += distrib.frequency(binPos);
		}
		return sum;
	}

	/**
	 * Returns the frequency count of all values in the middle of the
	 * distribution.
	 */
	public long valueCount() {
		// NB : would like to do this:
		// return distributionCount() - lowerTailCount() - upperTailCount();
		//   But this double counts some tail bins.
		if (!hasTails()) return distributionCount();
		long sum = 0;
		Points points = new Points();
		while (points.hasNext()) {
			long[] binPos = points.next();
			boolean inTail = false;
			for (int i = 0; i < binPos.length; i++) {
				if ((binPos[i] == 0) || (binPos[i] == mappers.get(i).getBinCount() - 1))
				{
					inTail = true;
					break;
				}
			}
			if (!inTail) sum += distrib.frequency(binPos);
		}
		return sum;
	}

	/**
	 * Returns the frequency count of all values in the specified dimension of the
	 * distribution: lower tail + middle + upper tail. Does not include ignored
	 * values.
	 */
	public long distributionCount(int dim, long dimVal) {
		long sum = 0;
		Points points = new Points();
		while (points.hasNext()) {
			long[] binPos = points.next();
			if (binPos[dim] != dimVal) continue;
			sum += distrib.frequency(binPos);
		}
		return sum;
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
	 * Returns the frequency count of values within a bin using a set of
	 * representative values. Not that multiple values can be mapped to one bin so
	 * this is NOT the frequency count of this exact set of values in the
	 * distribution.
	 * 
	 * @param values A set of representative values of interest
	 */
	public long frequency(List<T> values) {
		map(values, pos);
		return frequency(pos);
	}

	/**
	 * Returns the frequency count of the values within a bin.
	 */
	public long frequency(long[] binPos) {
		return distrib.frequency(binPos);
	}

	/**
	 * Returns the relative frequency of values within a bin using a set of
	 * representative values. Note that multiple values can be mapped to one bin
	 * so this is NOT the relative frequency of this exact set of values in the
	 * distribution.
	 * <p>
	 * This calculation is of the number of values in the bin divided by either
	 * the number of values in the distribution or the number of values in the
	 * center of the distribution (tails ignored).
	 * <p>
	 * One can devise other ways to count relative frequencies that consider
	 * ignored values also. If needed one can use the various count methods and
	 * frequency methods to calculate any relative frequency desired.
	 * 
	 * @param values A representative set of values of interest
	 * @param includeTails Flag for determining whether to include tails in
	 *          calculation.
	 */
	public double relativeFrequency(List<T> values, boolean includeTails) {
		map(values, pos);
		return relativeFrequency(pos, includeTails);
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
	public double relativeFrequency(long[] binPos, boolean includeTails) {
		double numer = frequency(binPos);
		long denom = includeTails ? totalCount() : valueCount();
		return numer / denom;
	}

	/**
	 * Returns the number of bins contained in the histogram.
	 */
	public long getBinCount() {
		if (mappers.size() == 0) return 0;
		long count = 1;
		for (int i = 0; i < mappers.size(); i++) {
			count *= mappers.get(i).getBinCount();
		}
		return count;
	}

	/**
	 * Fills a bin position by mapping from a set of representative values.
	 */
	public void map(List<T> values, long[] binPos) {
		for (int i = 0; i < mappers.size(); i++) {
			binPos[i] = mappers.get(i).map(values.get(i));
		}
	}

	/**
	 * Recalculates the underlying bin distribution. Use this if the iterable data
	 * sources have changed after this histogram was built.
	 */
	public void recalc() {
		populateBins();
	}

	/**
	 * Gets the values associated with the center of a bin.
	 * 
	 * @param binPos The bin index of interest
	 * @param values The outputs to fill with the center values
	 */
	public void getCenterValues(long[] binPos, List<T> values) {
		for (int i = 0; i < mappers.size(); i++) {
			T value = values.get(i);
			mappers.get(i).getCenterValue(binPos[i], value);
		}
	}

	/**
	 * Gets the values associated with the left edge of a bin.
	 * 
	 * @param binPos The bin index of interest
	 * @param values The outputs to fill with the left edge values
	 */
	public void getLowerBounds(long[] binPos, List<T> values) {
		for (int i = 0; i < mappers.size(); i++) {
			T value = values.get(i);
			mappers.get(i).getLowerBound(binPos[i], value);
		}
	}

	/**
	 * Gets the values associated with the right edge of a bin.
	 * 
	 * @param binPos The bin index of interest
	 * @param values The outputs to fill with the right edge values
	 */
	public void getUpperBounds(long[] binPos, List<T> values) {
		for (int i = 0; i < mappers.size(); i++) {
			T value = values.get(i);
			mappers.get(i).getUpperBound(binPos[i], value);
		}
	}

	/**
	 * Returns true if the given bin interval is closed on the right
	 * 
	 * @param binPos The bin number of the interval of interest
	 */
	public boolean includesUpperBounds(long[] binPos) {
		for (int i = 0; i < mappers.size(); i++) {
			if (!mappers.get(i).includesUpperBound(binPos[i])) return false;
		}
		return true;
	}

	/**
	 * Returns true if the given bin interval is closed on the left
	 * 
	 * @param binPos The bin number of the interval of interest
	 */
	public boolean includesLowerBounds(long[] binPos) {
		for (int i = 0; i < mappers.size(); i++) {
			if (!mappers.get(i).includesLowerBound(binPos[i])) return false;
		}
		return true;
	}

	/**
	 * Returns true if a given set of values are mapped to the lower tail of the
	 * distribution.
	 * 
	 * @param values The set of values to determine the location of
	 */
	public boolean isInLowerTail(List<T> values) {
		for (int i = 0; i < mappers.size(); i++) {
			if (hasTails(i)) {
				long binPos = mappers.get(i).map(values.get(i));
				if (binPos == 0) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Returns true if a given set of values are mapped to the upper tail of the
	 * distribution.
	 * 
	 * @param values The set of values to determine the location of
	 */
	public boolean isInUpperTail(List<T> values) {
		for (int i = 0; i < mappers.size(); i++) {
			if (hasTails(i)) {
				long binPos = mappers.get(i).map(values.get(i));
				if (binPos == mappers.get(i).getBinCount() - 1) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Returns true if a given set of values are mapped to the middle of the
	 * distribution.
	 * 
	 * @param values The set of values to determine the location of
	 */
	public boolean isInMiddle(List<T> values) {
		for (int i = 0; i < mappers.size(); i++) {
			if (hasTails(i)) {
				long binPos = mappers.get(i).map(values.get(i));
				if ((binPos == 0) || (binPos == mappers.get(i).getBinCount() - 1)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Returns true if a given set of values are outside the distribution.
	 * 
	 * @param values The set of values to determine the location of
	 */
	public boolean isOutside(List<T> values) {
		for (int i = 0; i < mappers.size(); i++) {
			long binPos = mappers.get(i).map(values.get(i));
			if ((binPos == Long.MIN_VALUE) || (binPos == Long.MAX_VALUE)) {
				return true;
			}
		}
		return false;
	}

	// -- helpers --

	private void populateBins() {
		if (iterable != null) populateBinsFromSingleIterable();
		else populateBinsFromListOfIterables();
	}

	private void populateBinsFromSingleIterable() {
		distrib.resetCounters();
		ignoredCount = 0;
		Iterator<List<T>> iter = iterable.iterator();
		while (iter.hasNext()) {
			List<T> values = iter.next();
			map(values, pos);
			boolean ignored = false;
			for (int i = 0; i < pos.length; i++) {
				if (pos[i] == Long.MIN_VALUE || pos[i] == Long.MAX_VALUE) {
					ignored = true;
					break;
				}
			}
			if (ignored) ignoredCount++;
			else distrib.increment(pos);
		}
	}

	private void populateBinsFromListOfIterables() {
		distrib.resetCounters();
		ignoredCount = 0;
		List<T> vals = new ArrayList<T>(mappers.size());
		List<Iterator<T>> iters = new ArrayList<Iterator<T>>();
		for (int i = 0; i < iterables.size(); i++) {
			iters.add(iterables.get(i).iterator());
			vals.add(null);
		}
		boolean hasNext = true;
		do {
			for (int i = 0; i < iters.size(); i++) {
				if (!iters.get(i).hasNext()) hasNext = false;
			}
			if (hasNext) {
				for (int i = 0; i < iters.size(); i++) {
					vals.set(i, iters.get(i).next());
				}
				map(vals, pos);
				boolean ignored = false;
				for (int i = 0; i < pos.length; i++) {
					if (pos[i] == Long.MIN_VALUE || pos[i] == Long.MAX_VALUE) {
						ignored = true;
						break;
					}
				}
				if (ignored) ignoredCount++;
				else distrib.increment(pos);
			}
		}
		while (hasNext);
	}

	// TODO - this code should be replaced with PointSet code but that requires
	// stuff to be moved into imglib core from OPS.

	@SuppressWarnings("synthetic-access")
	private class Points {

		private long[] point;

		Points() {
		}

		boolean hasNext() {
			if (point == null) return true;
			for (int i = 0; i < point.length; i++) {
				if (point[i] < mappers.get(i).getBinCount() - 1) return true;
			}
			return false;
		}

		long[] next() {
			if (point == null) {
				point = new long[mappers.size()];
				return point;
			}
			for (int i = 0; i < point.length; i++) {
				point[i]++;
				if (point[i] <= mappers.get(i).getBinCount() - 1) return point;
				point[i] = 0;
			}
			throw new IllegalStateException("incrementing beyond end");
		}

	}

}
