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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.IterableRealInterval;
import net.imglib2.Positionable;
import net.imglib2.RandomAccess;
import net.imglib2.RealPositionable;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.type.numeric.integer.LongType;

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
public class HistogramNd<T> implements Img<LongType> {

	// -- instance variables --

	private List<BinMapper1d<T>> mappers;
	private DiscreteFrequencyDistribution distrib;
	private long[] pos;
	private long ignoredCount;
	@SuppressWarnings("synthetic-access")
	private Incrementer incrementer = new Incrementer();
	@SuppressWarnings("synthetic-access")
	private Decrementer decrementer = new Decrementer();

	// -- constructors --

	/**
	 * Construct a histogram from a list of bin mapping algorithms. Use
	 * countData() to populate it.
	 * 
	 * @param mappers The algorithms used to map values to bins
	 */
	public HistogramNd(List<BinMapper1d<T>> mappers) {
		this.mappers = mappers;
		long[] dims = new long[mappers.size()];
		for (int i = 0; i < mappers.size(); i++) {
			dims[i] = mappers.get(i).getBinCount();
		}
		distrib = new DiscreteFrequencyDistribution(dims);
		pos = new long[mappers.size()];
		ignoredCount = 0;
	}

	/**
	 * Construct a histogram whose bin mappings match another histogram. After
	 * this construction the histogram bins are unpopulated.
	 * 
	 * @param other The histogram to copy.
	 */
	public HistogramNd(HistogramNd<T> other) {
		List<BinMapper1d<T>> mappersCopy = new ArrayList<BinMapper1d<T>>();
		for (BinMapper1d<T> m : mappers) {
			mappersCopy.add(m.copy());
		}
		mappers = mappersCopy;
		distrib = other.distrib.copy();
		pos = other.pos.clone();
		ignoredCount = 0;
	}

	/**
	 * Construct a histogram from an iterable set of data and a list of bin
	 * mapping algorithms. Must be given one iterable data source that returns
	 * multiple data values at each point.
	 * 
	 * @param data The iterable set of values to calculate upon
	 * @param mappers The algorithms used to map values to bins
	 */
	public HistogramNd(Iterable<List<T>> data, List<BinMapper1d<T>> mappers) {
		this(mappers);
		init(data);
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
		this(mappers);
		init(data);
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
		Cursor<?> cursor = distrib.localizingCursor();
		long[] binPos = new long[distrib.numDimensions()];
		while (cursor.hasNext()) {
			cursor.next();
			cursor.localize(binPos);
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
		Cursor<?> cursor = distrib.localizingCursor();
		long[] binPos = new long[distrib.numDimensions()];
		while (cursor.hasNext()) {
			cursor.next();
			cursor.localize(binPos);
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
		Cursor<?> cursor = distrib.localizingCursor();
		long[] binPos = new long[distrib.numDimensions()];
		while (cursor.hasNext()) {
			cursor.next();
			cursor.localize(binPos);
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
		Cursor<?> cursor = distrib.localizingCursor();
		long[] binPos = new long[distrib.numDimensions()];
		while (cursor.hasNext()) {
			cursor.next();
			cursor.localize(binPos);
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
		Cursor<?> cursor = distrib.localizingCursor();
		long[] binPos = new long[distrib.numDimensions()];
		while (cursor.hasNext()) {
			cursor.next();
			cursor.localize(binPos);
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
		Cursor<?> cursor = distrib.localizingCursor();
		long[] binPos = new long[distrib.numDimensions()];
		while (cursor.hasNext()) {
			cursor.next();
			cursor.localize(binPos);
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
		Cursor<?> cursor = distrib.localizingCursor();
		long[] binPos = new long[distrib.numDimensions()];
		while (cursor.hasNext()) {
			cursor.next();
			cursor.localize(binPos);
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
	 * representative values. Note that multiple values can be mapped to one bin
	 * so this is NOT the frequency count of this exact set of values in the
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
	 * Returns true if the given bin interval is closed on the right for the given
	 * dimension.
	 * 
	 * @param dim The dimension of interest
	 * @param binPos The bin number of the interval of interest
	 */
	public boolean includesUpperBound(int dim, long binPos) {
		return mappers.get(dim).includesUpperBound(binPos);
	}

	/**
	 * Returns true if the given bin interval is closed on the left for the given
	 * dimension.
	 * 
	 * @param dim The dimension of interest
	 * @param binPos The bin number of the interval of interest
	 */
	public boolean includesLowerBound(int dim, long binPos) {
		return mappers.get(dim).includesLowerBound(binPos);
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

	/**
	 * Get the discrete frequency distribution associated with this histogram.
	 */
	public DiscreteFrequencyDistribution dfd() {
		return distrib;
	}

	/**
	 * Counts the data contained in the given data source using the underlying bin
	 * distribution.
	 * 
	 * @param data The total data to count
	 */
	public void countData(Iterable<List<T>> data) {
		init(data);
	}

	/**
	 * Counts the data contained in the given data source using the underlying bin
	 * distribution.
	 * 
	 * @param data The total data to count
	 */
	public void countData(List<Iterable<T>> data) {
		init(data);
	}

	/**
	 * Counts additional data contained in a given iterable collection. One can
	 * use this to update an existing histogram with a subset of values.
	 * 
	 * @param data The new data to count
	 */
	public void addData(Iterable<List<T>> data) {
		add(data);
	}

	/**
	 * Counts additional data contained in a given iterable collection. One can
	 * use this to update an existing histogram with a subset of values.
	 * 
	 * @param data The new data to count
	 */
	public void addData(List<Iterable<T>> data) {
		add(data);
	}

	/**
	 * Uncounts some original data contained in a given iterable collection. One
	 * can use this to update an existing histogram with a subset of values.
	 * 
	 * @param data The old data to uncount
	 */
	public void subtractData(Iterable<List<T>> data) {
		subtract(data);
	}

	/**
	 * Uncounts some original data contained in a given iterable collection. One
	 * can use this to update an existing histogram with a subset of values.
	 * 
	 * @param data The old data to uncount
	 */
	public void subtractData(List<Iterable<T>> data) {
		subtract(data);
	}

	/**
	 * Directly increment a bin by position.
	 * 
	 * @param binPos The 1-d index of the bin
	 */
	public void increment(long[] binPos) {
		distrib.increment(binPos);
	}

	/**
	 * Directly decrement a bin by position.
	 * 
	 * @param binPos The 1-d index of the bin
	 */
	public void decrement(long[] binPos) {
		distrib.decrement(binPos);
	}

	/**
	 * Directly increment a bin by value.
	 * 
	 * @param values The values to map to a bin position
	 */
	public void increment(List<T> values) {
		count(values, incrementer);
	}

	/**
	 * Directly decrement a bin by value,
	 * 
	 * @param values The values to map to a bin position
	 */
	public void decrement(List<T> values) {
		count(values, decrementer);
	}

	/**
	 * Resets all data counts to 0.
	 */
	public void resetCounters() {
		reset();
	}

	// -- delegated Img methods --

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

	@Override
	public RandomAccess<LongType> randomAccess() {
		return distrib.randomAccess();
	}

	@Override
	public RandomAccess<LongType> randomAccess(Interval interval) {
		return distrib.randomAccess(interval);
	}

	@Override
	public long min(int d) {
		return distrib.min(d);
	}

	@Override
	public void min(long[] min) {
		distrib.min(min);
	}

	@Override
	public void min(Positionable min) {
		distrib.min(min);
	}

	@Override
	public long max(int d) {
		return distrib.max(d);
	}

	@Override
	public void max(long[] max) {
		distrib.max(max);
	}

	@Override
	public void max(Positionable max) {
		distrib.max(max);
	}

	@Override
	public double realMin(int d) {
		return distrib.realMin(d);
	}

	@Override
	public void realMin(double[] min) {
		distrib.realMin(min);
	}

	@Override
	public void realMin(RealPositionable min) {
		distrib.realMin(min);
	}

	@Override
	public double realMax(int d) {
		return distrib.realMax(d);
	}

	@Override
	public void realMax(double[] max) {
		distrib.realMax(max);
	}

	@Override
	public void realMax(RealPositionable max) {
		distrib.realMax(max);
	}

	@Override
	public Cursor<LongType> cursor() {
		return distrib.cursor();
	}

	@Override
	public Cursor<LongType> localizingCursor() {
		return distrib.localizingCursor();
	}

	@Override
	public long size() {
		return distrib.size();
	}

	@Override
	public LongType firstElement() {
		return distrib.firstElement();
	}

	@Override
	public Object iterationOrder() {
		return distrib.iterationOrder();
	}

	@Override
	@Deprecated
	public boolean equalIterationOrder(IterableRealInterval<?> f) {
		return distrib.equalIterationOrder(f);
	}

	@Override
	public Iterator<LongType> iterator() {
		return distrib.iterator();
	}

	@Override
	public ImgFactory<LongType> factory() {
		return distrib.factory();
	}

	@Override
	public Img<LongType> copy() {
		return new HistogramNd<T>(this);
	}

	// -- helpers --

	private void reset() {
		distrib.resetCounters();
		ignoredCount = 0;
	}

	private void init(Iterable<List<T>> data) {
		reset();
		add(data);
	}

	private void init(List<Iterable<T>> data) {
		reset();
		add(data);
	}

	private void add(Iterable<List<T>> data) {
		modifyCounts(data, incrementer);
	}

	private void add(List<Iterable<T>> data) {
		modifyCounts(data, incrementer);
	}

	private void subtract(Iterable<List<T>> data) {
		modifyCounts(data, decrementer);
	}

	private void subtract(List<Iterable<T>> data) {
		modifyCounts(data, decrementer);
	}

	private void modifyCounts(Iterable<List<T>> data, Counter counter) {
		Iterator<List<T>> iter = data.iterator();
		while (iter.hasNext()) {
			count(iter.next(), counter);
		}
	}

	private void modifyCounts(List<Iterable<T>> data, Counter counter) {
		List<T> vals = new ArrayList<T>(mappers.size());
		List<Iterator<T>> iters = new ArrayList<Iterator<T>>();
		for (int i = 0; i < data.size(); i++) {
			iters.add(data.get(i).iterator());
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
				count(vals, counter);
			}
		}
		while (hasNext);
	}

	private void count(List<T> values, Counter counter) {
		map(values, pos);
		boolean ignored = false;
		for (int i = 0; i < pos.length; i++) {
			if (pos[i] == Long.MIN_VALUE || pos[i] == Long.MAX_VALUE) {
				ignored = true;
				break;
			}
		}
		counter.count(pos, ignored);
	}

	private interface Counter {

		void count(long[] pos, boolean ignored);
	}

	private class Decrementer implements Counter {

		@SuppressWarnings("synthetic-access")
		@Override
		public void count(long[] position, boolean ignored) {
			if (ignored) ignoredCount--;
			else distrib.decrement(position);
		}
	}

	private class Incrementer implements Counter {

		@SuppressWarnings("synthetic-access")
		@Override
		public void count(long[] position, boolean ignored) {
			if (ignored) ignoredCount++;
			else distrib.increment(position);
		}
	}

}
