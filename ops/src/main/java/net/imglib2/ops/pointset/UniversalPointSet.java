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

import net.imglib2.AbstractCursor;

/**
 * UniversalPointSet is a {@link PointSet} that includes all points in
 * space.
 * 
 * @author Barry DeZonia
 */
public class UniversalPointSet extends AbstractPointSet {

	// -- instance variables --
	
	private final long[] origin;

	// -- constructor --
	
	public UniversalPointSet() {
		origin = new long[0];
	}
	
	// -- PointSet methods --
	
	@Override
	public long[] getOrigin() {
		return origin;
	}

	@Override
	public void translate(long[] deltas) {
		invalidateBounds();
	}

	@Override
	public PointSetIterator iterator() {
		return new UniversalPointSetIterator();
	}

	@Override
	public int numDimensions() {
		return 0;
	}

	@Override
	protected long[] findBoundMin() {
		return origin;
	}

	@Override
	protected long[] findBoundMax() {
		return origin;
	}

	@Override
	public boolean includes(long[] point) {
		return true;
	}

	@Override
	public long size() {
		throw new UnsupportedOperationException("UniversalPointSet is infinite in size");
	}

	@Override
	public PointSet copy() {
		return new UniversalPointSet();
	}

	// -- private helpers --
	
	private class UniversalPointSetIterator extends AbstractCursor<long[]>
		implements PointSetIterator
	{

		public UniversalPointSetIterator() {
			super(0);
		}

		@Override
		public boolean hasNext() {
			return false;  // noniterable
		}

		@Override
		public long[] next() {
			throw new UnsupportedOperationException("Cannot iterate over UniversalPointSet");
		}

		@Override
		public void reset() {
			// nothing to do
		}
		
		@Override
		public long[] get() {
			throw new UnsupportedOperationException(
				"Cannot get values from a UniversalPointSet");
		}

		@Override
		public void fwd() {
			// nothing to do
		}

		@Override
		public void localize(long[] position) {
			throw new UnsupportedOperationException(
				"Cannot localize from a UniversalPointSet");
		}

		@Override
		public long getLongPosition(int d) {
			throw new UnsupportedOperationException(
				"Cannot get positions from a UniversalPointSet");
		}

		@Override
		public AbstractCursor<long[]> copy() {
			return new UniversalPointSetIterator();
		}

		@Override
		public AbstractCursor<long[]> copyCursor() {
			return copy();
		}
		
	}
}
