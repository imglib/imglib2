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
 * @author Barry DeZonia
 */
public class HistogramNd<T> {

	private List<BinMapper1d<T>> mappers;
	private DiscreteFrequencyDistribution distrib;
	private Iterable<List<T>> iterable;
	private List<Iterable<T>> iterables;
	private long[] pos;
	private long ignoredCount;

	public HistogramNd(Iterable<List<T>> data, List<BinMapper1d<T>> mappers) {
		this.iterable = data;
		this.iterables = null;
		this.mappers = mappers;
		init();
	}
	
	public HistogramNd(List<Iterable<T>> data, List<BinMapper1d<T>> mappers) {
		this.iterable = null;
		this.iterables = data;
		this.mappers = mappers;
		init();
	}

	private void init() {
		long[] dims = new long[mappers.size()];
		for (int i = 0; i < mappers.size(); i++) {
			dims[i] = mappers.get(i).getBinCount();
		}
		distrib = new DiscreteFrequencyDistribution(dims);
		pos = new long[mappers.size()];
		ignoredCount = 0;
		populateBins();
	}
	
	boolean hasTails() {
		for (int i = 0; i < mappers.size(); i++) {
			if (hasTails(i)) return true;
		}
		return false;
	}

	boolean hasTails(int dim) {
		return mappers.get(dim).hasTails();
	}

	long lowerTailCount(int dim) {
		if (!hasTails(dim)) return 0;
		long sum = 0;
		Points points = new Points();
		while (points.hasNext()) {
			long[] binPos = points.next();
			if (binPos[dim] == 0) sum += distrib.frequency(binPos);
		}
		return sum;
	}

	long lowerTailCount() {
		long sum = 0;
		for (int i = 0; i < mappers.size(); i++) {
			sum += lowerTailCount(i);
		}
		return sum;
	}
	
	long upperTailCount(int dim) {
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

	long upperTailCount() {
		long sum = 0;
		for (int i = 0; i < mappers.size(); i++) {
			sum += upperTailCount(i);
		}
		return sum;
	}

	long valueCount(int dim) {
		return totalCount(dim) - lowerTailCount(dim) - upperTailCount(dim);
	}

	long valueCount() {
		return totalCount() - lowerTailCount() - upperTailCount();
	}

	long totalCount(int dim) {
		boolean hasTails = hasTails(dim);
		long sum = 0;
		Points points = new Points();
		while (points.hasNext()) {
			long[] binPos = points.next();
			if (hasTails) {
				if (binPos[dim] == 0) continue;
				if (binPos[dim] == mappers.get(dim).getBinCount() - 1) continue;
			}
			sum += distrib.frequency(binPos);
		}
		return sum;
	}

	long distributionCount() {
		return distrib.totalValues();
	}

	long ignoredCount() {
		return ignoredCount;
	}

	long totalCount() {
		return distributionCount() + ignoredCount();
	}

	long frequency(List<T> values) {
		map(values, pos);
		return frequency(pos);
	}

	long frequency(long[] binPos) {
		return distrib.frequency(binPos);
	}

	double relativeFrequency(List<T> values, boolean includeTails) {
		map(values, pos);
		return relativeFrequency(pos, includeTails);
	}

	double relativeFrequency(long[] binPos, boolean includeTails) {
		double numer = frequency(binPos);
		long denom = includeTails ? totalCount() : valueCount();
		return numer / denom;
	}

	long getBinCount() {
		if (mappers.size() == 0) return 0;
		long count = 1;
		for (int i = 0; i < mappers.size(); i++) {
			count *= mappers.get(i).getBinCount();
		}
		return count;
	}

	void map(List<T> values, long[] binPos) {
		for (int i = 0; i < mappers.size(); i++) {
			binPos[i] = mappers.get(i).map(values.get(i));
		}
	}

	void recalc() {
		populateBins();
	}

	void getCenterValues(long[] binPos, List<T> values) {
		for (int i = 0; i < mappers.size(); i++) {
			T value = values.get(i);
			mappers.get(i).getCenterValue(binPos[i], value);
		}
	}

	void getLowerBounds(long[] binPos, List<T> values) {
		for (int i = 0; i < mappers.size(); i++) {
			T value = values.get(i);
			mappers.get(i).getLowerBound(binPos[i], value);
		}
	}

	void getUpperBounds(long[] binPos, List<T> values) {
		for (int i = 0; i < mappers.size(); i++) {
			T value = values.get(i);
			mappers.get(i).getUpperBound(binPos[i], value);
		}
	}

	boolean includesUpperBounds(long[] binPos) {
		for (int i = 0; i < mappers.size(); i++) {
			if (!mappers.get(i).includesUpperBound(binPos[i])) return false;
		}
		return true;
	}

	boolean includesLowerBounds(long[] binPos) {
		for (int i = 0; i < mappers.size(); i++) {
			if (!mappers.get(i).includesLowerBound(binPos[i])) return false;
		}
		return true;
	}

	boolean isInLowerTail(List<T> values) {
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

	boolean isInUpperTail(List<T> values) {
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

	boolean isInMiddle(List<T> values) {
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
			for (int i = 0; i < pos.length; i++) {
				if (pos[i] == Long.MIN_VALUE || pos[i] == Long.MAX_VALUE) {
					ignoredCount++;
					continue;
				}
			}
			distrib.increment(pos);
		}
	}

	private void populateBinsFromListOfIterables() {
		distrib.resetCounters();
		ignoredCount = 0;
		List<T> vals = new ArrayList<T>(mappers.size());
		List<Iterator<T>> iters = new ArrayList<Iterator<T>>();
		for (int i = 0; i < iterables.size(); i++) {
			iters.add(iterables.get(i).iterator());
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
				for (int i = 0; i < pos.length; i++) {
					if (pos[i] == Long.MIN_VALUE || pos[i] == Long.MAX_VALUE) {
						ignoredCount++;
						continue;
					}
				}
				distrib.increment(pos);
			}
		}
		while (hasNext);
	}

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
