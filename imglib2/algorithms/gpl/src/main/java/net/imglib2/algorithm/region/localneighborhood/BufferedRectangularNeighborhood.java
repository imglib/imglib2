package net.imglib2.algorithm.region.localneighborhood;

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

import java.util.Iterator;

import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.type.Type;

/**
 * 
 * 
 */
public class BufferedRectangularNeighborhood<T extends Type<T>, IN extends RandomAccessibleInterval<T>>
		extends AbstractNeighborhood<T, IN> {

	private int size;

	public BufferedRectangularNeighborhood(IN source,
			OutOfBoundsFactory<T, IN> outOfBounds, long[] spans) {
		this(source.numDimensions(), outOfBounds, spans);
		updateSource(source);
	}

	public BufferedRectangularNeighborhood(int numDims,
			OutOfBoundsFactory<T, IN> outOfBounds, long[] spans) {
		super(numDims, outOfBounds);
		setSpan(spans);

		size = 1;
		for (long s : spans)
			size *= s;
	}

	@Override
	public Cursor<T> cursor() {
		return new BufferedRectangularNeighborhoodCursor<T>(this);
	}

	@Override
	public Cursor<T> localizingCursor() {
		return cursor();
	}

	@Override
	public long size() {
		return size;
	}

	@Override
	public Iterator<T> iterator() {
		return cursor();
	}

	@Override
	public BufferedRectangularNeighborhood<T, IN> copy() {
		if (source != null)
			return new BufferedRectangularNeighborhood<T, IN>(source,
					outOfBounds, span);
		else
			return new BufferedRectangularNeighborhood<T, IN>(n, outOfBounds,
					span);
	}
}
