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

import net.imglib2.Cursor;
import net.imglib2.EuclideanSpace;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.integer.LongType;

/**
 * This class represents an n-dimensional set of counters. Histogram
 * implementations use these for tracking value counts.
 * 
 * @author Barry DeZonia
 */
public class DiscreteFrequencyDistribution implements EuclideanSpace {

	private Img<LongType> counts;
	private RandomAccess<LongType> accessor;
	private long totalValues = 0;

	/**
	 * Construct an n-dimensional counter
	 */
	public DiscreteFrequencyDistribution(long[] binCounts)
	{
		// check inputs for issues

		for (int i = 0; i < binCounts.length; i++) {
			if (binCounts[i] <= 0) {
				throw new IllegalArgumentException("invalid bin count (<= 0)");
			}
		}

		// then build object

		counts = new ArrayImgFactory<LongType>().create(binCounts, new LongType());

		accessor = counts.randomAccess();
	}

	@Override
	public int numDimensions() {
		return counts.numDimensions();
	}

	public long dimension(int d) {
		return counts.dimension(d);
	}

	public void dimensions(long[] dims) {
		counts.dimensions(dims);
	}

	public void resetCounters() {
		Cursor<LongType> cursor = counts.cursor();
		while (cursor.hasNext()) {
			cursor.next().setZero();
		}
		totalValues = 0;
	}

	public long frequency(long[] binPos) {
		accessor.setPosition(binPos);
		return accessor.get().get();
	}

	public double relativeFrequency(long[] binPos) {
		if (totalValues == 0) return 0;
		return 1.0 * frequency(binPos) / totalValues;
	}

	public void increment(long[] binPos) {
		accessor.setPosition(binPos);
		accessor.get().inc();
		totalValues++;
	}

}
