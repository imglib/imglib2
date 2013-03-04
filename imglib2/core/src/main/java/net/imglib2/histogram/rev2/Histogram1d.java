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

/**
 * @author Barry DeZonia
 */
public class Histogram1d<T> {

	private BinMapper1d<T> mapper;
	private Iterable<T> data;
	private DiscreteFrequencyDistribution distrib;
	private long[] pos;
	private long ignoredCount;

	public Histogram1d(Iterable<T> data, BinMapper1d<T> mapper) {
		this.data = data;
		this.mapper = mapper;
		this.distrib =
			new DiscreteFrequencyDistribution(new long[] { mapper.getBinCount() });
		this.pos = new long[1];
		this.ignoredCount = 0;
		populateBins();
	}

	boolean hasTails() {
		return mapper.hasTails();
	}

	long lowerTailCount() {
		if (!hasTails()) return 0;
		pos[0] = 0;
		return distrib.frequency(pos);
	}

	long upperTailCount() {
		if (!hasTails()) return 0;
		pos[0] = mapper.getBinCount() - 1;
		return distrib.frequency(pos);
	}

	long valueCount() {
		return totalCount() - lowerTailCount() - upperTailCount();
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

	long frequency(T value) {
		long bin = mapper.map(value);
		return frequency(bin);
	}

	long frequency(long binPos) {
		if (binPos < 0 || binPos >= mapper.getBinCount()) return 0;
		pos[0] = binPos;
		return distrib.frequency(pos);
	}

	double relativeFrequency(T value, boolean includeTails) {
		long bin = mapper.map(value);
		return relativeFrequency(bin, includeTails);
	}

	double relativeFrequency(long binPos, boolean includeTails) {
		double numer = frequency(binPos);
		long denom = includeTails ? totalCount() : valueCount();
		return numer / denom;
	}

	long getBinCount() {
		return mapper.getBinCount();
	}

	long getBinPosition(T value) {
		return mapper.map(value);
	}

	void recalc() {
		populateBins();
	}

	void getCenterValue(long binPos, T value) {
		mapper.getCenterValue(binPos, value);
	}

	void getLowerBound(long binPos, T value) {
		mapper.getLowerBound(binPos, value);
	}

	void getUpperBound(long binPos, T value) {
		mapper.getUpperBound(binPos, value);
	}

	boolean includesUpperBound(long binPos) {
		return mapper.includesUpperBound(binPos);
	}

	boolean includesLowerBound(long binPos) {
		return mapper.includesLowerBound(binPos);
	}

	boolean isInLowerTail(T value) {
		if (!hasTails()) return false;
		long bin = mapper.map(value);
		return bin == 0;
	}

	boolean isInUpperTail(T value) {
		if (!hasTails()) return false;
		long bin = mapper.map(value);
		return bin == getBinCount() - 1;
	}

	boolean isInMiddle(T value) {
		if (!hasTails()) return true;
		long bin = mapper.map(value);
		return (bin > 0) && (bin < getBinCount() - 1);
	}

	// -- helpers --

	private void populateBins() {
		distrib.resetCounters();
		ignoredCount = 0;
		for (T value : data) {
			long bin = mapper.map(value);
			if (bin == Long.MIN_VALUE || bin == Long.MAX_VALUE) {
				ignoredCount++;
				continue;
			}
			pos[0] = bin;
			distrib.increment(pos);
		}
	}
}
