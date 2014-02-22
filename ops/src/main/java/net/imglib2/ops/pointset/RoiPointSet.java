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
import net.imglib2.roi.RegionOfInterest;

// TODO - when the efforts of people working with SciJava resolves Roi
// implementations (real and integer, RegionOfInterest and PointSet)
// this class can go away.

/**
 * Wraps a (real based) {@link RegionOfInterest} as a (integer based)
 * {@link PointSet}. An adapter class that brings the functionality
 * of PointSets to RegionOfInterests.
 * 
 * @author Barry DeZonia
 *
 */
public class RoiPointSet extends AbstractPointSet {

	// -- instance variables --
	
	private final int numD;
	private final RegionOfInterest roi;
	private final long[] origin;
	private final long[] boundMin;
	private final long[] boundMax;
	private final double[] tmpCoord;
	
	// -- constructor --
	
	public RoiPointSet(RegionOfInterest roi) {
		this.roi = roi;
		numD = roi.numDimensions();
		origin = new long[numD];
		boundMin = new long[numD];
		boundMax = new long[numD];
		tmpCoord = new double[numD];
	}
	
	// -- PointSet methods --
	
	@Override
	public long[] getOrigin() {
		for (int i = 0; i < numD; i++)
			origin[i] = (long) Math.floor(roi.realMin(i));
		return origin;
	}

	@Override
	public void translate(long[] delta) {
		for (int i = 0; i < numD; i++) {
			roi.move(delta[i], i);
		}
		invalidateBounds();
	}

	@Override
	public PointSetIterator iterator() {
		return new RoiPointSetIterator();
	}

	@Override
	public int numDimensions() {
		return roi.numDimensions();
	}

	@Override
	protected long[] findBoundMin() {
		for (int i = 0; i < numD; i++) {
			boundMin[i] = (long) Math.floor(roi.realMin(i));
		}
		return boundMin;
	}

	@Override
	protected long[] findBoundMax() {
		for (int i = 0; i < numD; i++) {
			boundMax[i] = (long) Math.ceil(roi.realMax(i));
		}
		return boundMax;
	}

	@Override
	public boolean includes(long[] point) {
		for (int i = 0; i < numD; i++) {
			tmpCoord[i] = point[i];
		}
		return roi.contains(tmpCoord);
	}

	@Override
	public long size() {
		long numElems = 0;
		PointSetIterator iter = iterator();
		while (iter.hasNext()) {
			iter.next();
			numElems++;
		}
		return numElems;
	}

	@Override
	public PointSet copy() {
		return new RoiPointSet(roi); // TODO - no copying possible. threading issues?
	}

	// -- private helpers --

	// TODO - internally it could instead make a ConditionalPointSet with a custom
	// RoiContainsPoint condition and use its iterator.
	
	private class RoiPointSetIterator extends AbstractCursor<long[]> implements
		PointSetIterator
	{
		private PointSetIterator iter;
		private long[] curr;
		private long[] nextCache;
		
		public RoiPointSetIterator() {
			super(roi.numDimensions());
			reset();
		}
		
		@Override
		public boolean hasNext() {
			if (nextCache != null) return true;
			return positionToNext();
		}

		@Override
		public void reset() {
			// can't just reset iterator as Roi may have moved. recalc bounds.
			HyperVolumePointSet vol =
				new HyperVolumePointSet(findBoundMin(), findBoundMax());
			iter = vol.iterator();
			curr = null;
			nextCache = null;
		}
		
		@Override
		public long[] get() {
			return curr;
		}

		@Override
		public void fwd() {
			if ((nextCache != null) || (positionToNext())) {
				if (curr == null) curr = new long[n];
				for (int i = 0; i < n; i++)
					curr[i] = nextCache[i];
				nextCache = null;
				return;
			}
			throw new IllegalArgumentException("fwd() cannot go beyond end");
		}

		@Override
		public void localize(long[] position) {
			for (int i = 0; i < n; i++) {
				position[i] = curr[i];
			}
		}

		@Override
		public long getLongPosition(int d) {
			return curr[d];
		}

		@Override
		public AbstractCursor<long[]> copy() {
			return new RoiPointSetIterator();
		}

		@Override
		public AbstractCursor<long[]> copyCursor() {
			return copy();
		}

		private boolean positionToNext() {
			nextCache = null;
			while (iter.hasNext()) {
				long[] pos = iter.next();
				if (includes(pos)) {
					nextCache = pos;
					return true;
				}
			}
			return false;
		}

	}
}
