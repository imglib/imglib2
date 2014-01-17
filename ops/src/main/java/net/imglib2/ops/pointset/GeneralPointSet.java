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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.imglib2.AbstractCursor;

/**
 * Treats a general collection of points as a PointSet.
 * 
 * @author Barry DeZonia
 */
public class GeneralPointSet extends AbstractPointSet {
	
	// -- instance variables --
	
	private final int numD;
	private long[] origin;
	private long[] boundMin;
	private long[] boundMax;
	private final List<long[]> points;

	// -- constructor --
	
	public GeneralPointSet(long[] origin, List<long[]> pts) {
		if (pts.size() == 0)
			throw new IllegalArgumentException("no points specified!");
		this.numD = origin.length;
		this.origin = origin.clone();
		this.boundMin = new long[numD];
		this.boundMax = new long[numD];
		this.points = new ArrayList<long[]>();
		this.points.addAll(pts);
		calcBounds(pts);
	}

	// -- PointSet methods --
	
	@Override
	public long[] getOrigin() { return origin; }
	
	@Override
	public void translate(long[] deltas) {
		// translate all points in the set
		for (long[] pt : points) {
			for (int i = 0; i < numD; i++)
				pt[i] += deltas[i];
		}
		// translate other related fields
		for (int i = 0; i < numD; i++) {
			long delta = deltas[i];
			origin[i] += delta;
			boundMin[i] += delta;
			boundMax[i] += delta;
		}
		invalidateBounds();
	}
	
	@Override
	public PointSetIterator iterator() {
		return new GeneralPointSetIterator();
	}
	
	@Override
	public int numDimensions() { return numD; }
	
	// TODO a spatial data structure would speed this up greatly
	
	@Override
	public boolean includes(long[] point) {
		for (long[] p : points) {
			if (Arrays.equals(point, p)) return true;
		}
		return false;
	}

	@Override
	protected long[] findBoundMin() {
		return boundMin;
	}
	
	@Override
	protected long[] findBoundMax() {
		return boundMax;
	}
	
	@Override
	public long size() {
		return points.size();
	}

	@Override
	public GeneralPointSet copy() {
		ArrayList<long[]> pointsCopied = new ArrayList<long[]>();
		for (long[] p : points) {
			pointsCopied.add(p.clone());
		}
		return new GeneralPointSet(origin, pointsCopied);
	}
	
	// -- GeneralPointSet methods --
	
	public static GeneralPointSet explode(PointSet ps) {
		final List<long[]> points = new ArrayList<long[]>();
		final PointSetIterator iter = ps.iterator();
		while (iter.hasNext()) {
			points.add(iter.next().clone());
		}
		return new GeneralPointSet(ps.getOrigin(), points);
	}

	// -- private helpers --
	
	private void calcBounds(List<long[]> pts) {
		// init bounds to first point's values
		for (int i = 0; i < numD; i++) {
			long val = pts.get(0)[i];
			boundMin[i] = val;
			boundMax[i] = val;
		}
		// modify bounds to include the rest of the points
		for (int i = 1; i < pts.size(); i++) {
			long[] point = pts.get(i);
			for (int j = 0; j < numD; j++) {
				if (point[j] < boundMin[j]) boundMin[j] = point[j];
				if (point[j] > boundMax[j]) boundMax[j] = point[j];
			}
		}
	}
	
	private class GeneralPointSetIterator extends AbstractCursor<long[]>
		implements PointSetIterator
	{
		private int index;
		
		public GeneralPointSetIterator() {
			super(GeneralPointSet.this.numD);
			index = -1;
		}
		
		@Override
		public boolean hasNext() {
			return index+1 < points.size();
		}
		
		@Override
		public void reset() {
			index = -1;
		}

		@Override
		public long[] get() {
			if (index < 0) return null;
			return points.get(index);
		}

		@Override
		public void fwd() {
			index++;
		}

		@Override
		public void localize(long[] position) {
			for (int i = 0; i < numD; i++) {
				position[i] = getLongPosition(i);
			}
		}

		@Override
		public long getLongPosition(int d) {
			return get()[d];
		}

		@Override
		public AbstractCursor<long[]> copy() {
			return new GeneralPointSetIterator();
		}

		@Override
		public AbstractCursor<long[]> copyCursor() {
			return copy();
		}
	}
}
