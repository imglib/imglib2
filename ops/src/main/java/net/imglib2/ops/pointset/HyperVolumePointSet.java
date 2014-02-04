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
import net.imglib2.FlatIterationOrder;

/**
 * HyperVolumePointSet is a {@link PointSet} that spans a contiguous region of
 * space. It can be thought of as a regularly spaced n-dimensional grid.
 * 
 * @author Barry DeZonia
 */
public class HyperVolumePointSet extends AbstractPointSet {
	
	// -- instance variables --
	
	private final long[] origin;
	private final long[] boundMin;
	private final long[] boundMax;
	private final long size;

	// -- constructors --
	
	/**
	 * Constructor taking an anchor point and offsets relative to that anchor
	 * point. These parameters define a hypervolume.
	 * 
	 * @param origin The key point around which the hypervolume is defined.
	 * @param negOffsets Span in the negative direction beyond the anchor point
	 *          (note: must be positive values).
	 * @param posOffsets - Span in the positive direction beyond the anchor point
	 *          (note: must be positive values).
	 */
	public HyperVolumePointSet(long[] origin, long[] negOffsets, long[] posOffsets)
	{
		if (origin.length != negOffsets.length)
			throw new IllegalArgumentException();
		if (origin.length != posOffsets.length)
			throw new IllegalArgumentException();
		for (int i = 0; i < origin.length; i++) {
			if (negOffsets[i] < 0)
				throw new IllegalArgumentException("all offsets must be >= 0");
			if (posOffsets[i] < 0)
				throw new IllegalArgumentException("all offsets must be >= 0");
		}
		this.origin = origin.clone();
		this.boundMin = new long[origin.length];
		this.boundMax = new long[origin.length];
		for (int i = 0; i < origin.length; i++) {
			boundMin[i] = origin[i] - negOffsets[i];
			boundMax[i] = origin[i] + posOffsets[i];
		}
		this.size = calcSize();
	}
	
	/**
	 * Constructor that takes a minimum point and a maximum point. The
	 * hypervolume is defined between and including these points.
	 * 
	 * @param pt1
	 * @param pt2
	 */
	public HyperVolumePointSet(long[] pt1, long[] pt2) {
		if (pt1.length != pt2.length)
			throw new IllegalArgumentException();
		this.boundMin = new long[pt1.length];
		this.boundMax = new long[pt1.length];
		for (int i = 0; i < pt1.length; i++) {
			boundMin[i] = Math.min(pt1[i], pt2[i]);
			boundMax[i] = Math.max(pt1[i], pt2[i]);
		}
		this.origin = pt1.clone();
		this.size = calcSize();
	}
	
	/**
	 * Constructor that takes a span array. The hypervolume is defined from the
	 * origin extending for the given span. This means that if a span component
	 * equals X then the hypervolume goes from 0 to X-1.
	 * 
	 * @param span The values specifying width, height, depth, etc. of the
	 *          hypervolume.
	 */
	public HyperVolumePointSet(long[] span) {
		this(new long[span.length], lastPoint(span));
	}
	
	// -- PointSet methods --
	
	@Override
	public long[] getOrigin() { return origin; }
	
	@Override
	public void translate(long[] deltas) {
		for (int i = 0; i < origin.length; i++) {
			long delta = deltas[i];
			origin[i] += delta;
			boundMin[i] += delta;
			boundMax[i] += delta;
		}
		invalidateBounds();
		//for (PointSetIterator iter : iters) iter.reset();
	}
	
	@Override
	public PointSetIterator iterator() {
		return new HyperVolumePointSetIterator();
	}
	
	@Override
	public int numDimensions() { return origin.length; }
	
	@Override
	protected long[] findBoundMin() {
		return boundMin;
	}

	@Override
	protected long[] findBoundMax() {
		return boundMax;
	}
	
	@Override
	public boolean includes(long[] point) {
		for (int i = 0; i < origin.length; i++) {
			if (point[i] < boundMin[i]) return false;
			if (point[i] > boundMax[i]) return false;
		}
		return true;
	}
	
	@Override
	public long size() {
		return size;
	}

	@Override
	public HyperVolumePointSet copy() {
		return new HyperVolumePointSet(findBoundMin(), findBoundMax());
	}
	
	@Override
	public Object iterationOrder() {
		return new FlatIterationOrder(this);
	}

	// -- private helpers --
	
	private static long[] lastPoint(long[] span) {
		long[] lastPoint = new long[span.length];
		for (int i = 0; i < span.length; i++) {
			lastPoint[i] = span[i] - 1;
		}
		return lastPoint;
	}

	private long calcSize() {
		long numElements = 1;
		for (int i = 0; i < origin.length; i++) {
			numElements *= boundMax[i] - boundMin[i] + 1;
		}
		return numElements;
	}

	private class HyperVolumePointSetIterator extends AbstractCursor<long[]>
		implements PointSetIterator
	{
		final long[] pos;
		
		public HyperVolumePointSetIterator() {
			super(origin.length);
			pos = new long[n];
			reset();
		}
		
		@Override
		public void reset() {
			for (int i = 0; i < n; i++) {
				pos[i] = boundMin[i];
			}
			pos[0]--; // NB - will fail if boundMin[0] == Long.MIN_VALUE
			// Note however that this weakness allows us to run much more quickly for
			// all other points.
		}
		
		@Override
		public boolean hasNext() {
			for (int i = 0; i < n; i++) {
				if (pos[i] < boundMax[i]) return true;
			}
			return false;
		}
		
		@Override
		public long[] get() {
			return pos;
		}

		@Override
		public void fwd() {
			for (int i = 0; i < n; i++) {
				pos[i]++;
				if (pos[i] <= boundMax[i]) return;
				pos[i] = boundMin[i];
			}
			
			throw new IllegalArgumentException(
				"can't call fwd() beyond last position");
		}

		@Override
		public void localize(long[] position) {
			for (int i = 0; i < n; i++) {
				position[i] = pos[i];
			}
		}

		@Override
		public long getLongPosition(int d) {
			return pos[d];
		}

		@Override
		public AbstractCursor<long[]> copy() {
			return new HyperVolumePointSetIterator();
		}

		@Override
		public AbstractCursor<long[]> copyCursor() {
			return copy();
		}

	}

}
