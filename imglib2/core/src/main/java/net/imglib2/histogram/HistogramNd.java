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

// Limitation: all Iterables must be of same type. For instance this assumes
// a 3d coord would have three iterators of same type (like ubyte for RGB).
// However Larry's code may have intended to allow classification of a tuple
// such as this: (5.0,'a',Colors.RED). Look at HistogramKey to learn more.

/**
 * @author Barry DeZonia
 * @param <T>
 */
public class HistogramNd<T> {

	// -- instance variables --

	private final List<Iterable<T>> iterables;
	private final BinMapper<T> binMapper;
	private final DiscreteFrequencyDistribution binDistrib;
	private final long[] tmpPos;
	private final List<T> vals;

	// -- public api --

	public HistogramNd(List<Iterable<T>> iterables, BinMapper<T> binMapper,
		List<T> values)
	{
		this.iterables = iterables;
		this.binMapper = binMapper;
		this.vals = values;
		long[] binDims = new long[binMapper.numDimensions()];
		binMapper.getBinDimensions(binDims);
		this.binDistrib = new DiscreteFrequencyDistribution(binDims);
		this.tmpPos = new long[binMapper.numDimensions()];
		populateBins();
	}

	public void getBinPosition(List<T> values, long[] binPos) {
		binMapper.getBinPosition(values, binPos);
	}

	public long frequency(long[] binPos) {
		return binDistrib.frequency(binPos);
	}

	public double relativeFrequency(long[] binPos) {
		return binDistrib.relativeFrequency(binPos);
	}

	public void recalc() {
		populateBins();
	}

	public void getCenterValues(long[] binPos, List<T> values) {
		binMapper.getCenterValues(binPos, values);
	}

	public void getMinValues(long[] binPos, List<T> values) {
		binMapper.getMinValues(binPos, values);
	}

	public void getMaxValues(long[] binPos, List<T> values) {
		binMapper.getMaxValues(binPos, values);
	}

	// -- private helpers --

	private void populateBins() {
		binDistrib.resetCounters();
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
				binMapper.getBinPosition(vals, tmpPos);
				binDistrib.increment(tmpPos);
			}
		}
		while (hasNext);
	}
}
