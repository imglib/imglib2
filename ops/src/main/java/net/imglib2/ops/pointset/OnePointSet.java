/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
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
 * #L%
 */

package net.imglib2.ops.pointset;

import java.util.Arrays;

import net.imglib2.AbstractCursor;

/**
 * OnePointSet represents a {@link PointSet} that contains exactly one point.
 * 
 * @author Barry DeZonia
 */
public class OnePointSet extends AbstractPointSet {

	// -- instance varaibles --
	
	private long[] point;
	
	// -- constructor --
	
	public OnePointSet(long[] point) {
		this.point = point;
	}
	
	// -- PointSet methods --
	
	@Override
	public long[] getOrigin() {
		return point;
	}

	@Override
	public void translate(long[] deltas) {
		for (int i = 0; i < point.length; i++)
			point[i] += deltas[i];
		invalidateBounds();
	}

	@Override
	public int numDimensions() {
		return point.length;
	}

	@Override
	protected long[] findBoundMin() {
		return point;
	}

	@Override
	protected long[] findBoundMax() {
		return point;
	}

	@Override
	public boolean includes(long[] pt) {
		return Arrays.equals(this.point, pt);
	}

	@Override
	public long size() {
		return 1;
	}

	@Override
	public OnePointSet copy() {
		return new OnePointSet(point.clone());
	}
	
	@Override
	public PointSetIterator iterator() {
		return new OnePointSetIterator();
	}

	// -- private helpers --
	
	private class OnePointSetIterator extends AbstractCursor<long[]> implements
		PointSetIterator
	{

		private boolean hasNext = true;

		public OnePointSetIterator() {
			super(point.length);
		}
		
		@Override
		public boolean hasNext() {
			return hasNext;
		}

		@Override
		public long[] next() {
			hasNext = false;
			return point;
		}

		@Override
		public void reset() {
			hasNext = true;
		}

		@Override
		public long[] get() {
			return point;
		}

		@Override
		public void fwd() {
			if (hasNext) hasNext = false;
			else throw new IllegalArgumentException("Cannot fwd() beyond end.");
		}

		@Override
		public void localize(long[] position) {
			for (int i = 0; i < n; i++)
				position[i] = point[i];
		}

		@Override
		public long getLongPosition(int d) {
			return point[d];
		}

		@Override
		public AbstractCursor<long[]> copy() {
			return new OnePointSetIterator();
		}

		@Override
		public AbstractCursor<long[]> copyCursor() {
			return copy();
		}
	}
}
