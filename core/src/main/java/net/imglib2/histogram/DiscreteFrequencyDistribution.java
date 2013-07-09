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

import java.util.Iterator;

import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.IterableRealInterval;
import net.imglib2.Positionable;
import net.imglib2.RandomAccess;
import net.imglib2.RealPositionable;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.integer.LongType;

/**
 * This class represents an n-dimensional set of counters. Histogram
 * implementations use these for tracking value counts.
 * 
 * @author Barry DeZonia
 */
public class DiscreteFrequencyDistribution implements Img<LongType> {

	// -- instance variables --

	private final Img<LongType> counts;
	private final RandomAccess<LongType> accessor;
	private long totalValues;

	// -- public api --

	/**
	 * Construct an n-dimensional counter with the given number of bins
	 */
	public DiscreteFrequencyDistribution(long[] binCounts) {
		// check inputs for issues

		for (int i = 0; i < binCounts.length; i++) {
			if (binCounts[i] <= 0) {
				throw new IllegalArgumentException("invalid bin count (<= 0)");
			}
		}

		// then build object

		counts = new ArrayImgFactory<LongType>().create(binCounts, new LongType());

		accessor = counts.randomAccess();

		totalValues = 0;
	}

	/**
	 * Construct an n-dimensional counter using a provided Img<LongType> to store
	 * counts.
	 */
	public DiscreteFrequencyDistribution(Img<LongType> img) {
		counts = img;
		accessor = counts.randomAccess();
		resetCounters();
	}

	/**
	 * Resets all frequency counts to zero.
	 */
	public void resetCounters() {
		Cursor<LongType> cursor = counts.cursor();
		while (cursor.hasNext()) {
			cursor.next().setZero();
		}
		totalValues = 0;
	}

	/**
	 * Returns the frequency count associated with a given bin.
	 */
	public long frequency(long[] binPos) {
		for (int i = 0; i < accessor.numDimensions(); i++) {
			if (binPos[i] < 0 || binPos[i] >= dimension(i)) return 0;
		}
		accessor.setPosition(binPos);
		return accessor.get().get();
	}

	/**
	 * Returns the relative frequency (0 <= f <= 1) associated with a given bin.
	 */
	public double relativeFrequency(long[] binPos) {
		if (totalValues == 0) return 0;
		return 1.0 * frequency(binPos) / totalValues;
	}

	/**
	 * Increments the frequency count of a specified bin.
	 */
	public void increment(long[] binPos) {
		accessor.setPosition(binPos);
		accessor.get().inc();
		totalValues++;
	}

	/**
	 * Decrements the frequency count of a specified bin.
	 */
	public void decrement(long[] binPos) {
		accessor.setPosition(binPos);
		accessor.get().dec();
		totalValues--;
	}

	/**
	 * Returns the total number of values counted by this distribution.
	 */
	public long totalValues() {
		return totalValues;
	}

	// -- Img methods --

	@Override
	public RandomAccess<LongType> randomAccess() {
		return counts.randomAccess();
	}

	@Override
	public RandomAccess<LongType> randomAccess(Interval interval) {
		return counts.randomAccess(interval);
	}

	@Override
	public int numDimensions() {
		return counts.numDimensions();
	}

	@Override
	public long min(int d) {
		return counts.min(d);
	}

	@Override
	public void min(long[] min) {
		counts.min(min);
	}

	@Override
	public void min(Positionable min) {
		counts.min(min);
	}

	@Override
	public long max(int d) {
		return counts.max(d);
	}

	@Override
	public void max(long[] max) {
		counts.max(max);
	}

	@Override
	public void max(Positionable max) {
		counts.max(max);
	}

	@Override
	public double realMin(int d) {
		return counts.realMin(d);
	}

	@Override
	public void realMin(double[] min) {
		counts.realMin(min);
	}

	@Override
	public void realMin(RealPositionable min) {
		counts.realMin(min);
	}

	@Override
	public double realMax(int d) {
		return counts.realMax(d);
	}

	@Override
	public void realMax(double[] max) {
		counts.realMax(max);
	}

	@Override
	public void realMax(RealPositionable max) {
		counts.realMax(max);
	}

	@Override
	public void dimensions(long[] dimensions) {
		counts.dimensions(dimensions);
	}

	@Override
	public long dimension(int d) {
		return counts.dimension(d);
	}

	@Override
	public Cursor<LongType> cursor() {
		return counts.cursor();
	}

	@Override
	public Cursor<LongType> localizingCursor() {
		return counts.localizingCursor();
	}

	@Override
	public long size() {
		return counts.size();
	}

	@Override
	public LongType firstElement() {
		return counts.firstElement();
	}

	@Override
	public Object iterationOrder() {
		return counts.iterationOrder();
	}

	@Override
	@Deprecated
	public boolean equalIterationOrder(IterableRealInterval<?> f) {
		return counts.equalIterationOrder(f);
	}

	@Override
	public Iterator<LongType> iterator() {
		return counts.iterator();
	}

	@Override
	public ImgFactory<LongType> factory() {
		return counts.factory();
	}

	@Override
	public DiscreteFrequencyDistribution copy() {
		return new DiscreteFrequencyDistribution(counts.copy());
	}

}
