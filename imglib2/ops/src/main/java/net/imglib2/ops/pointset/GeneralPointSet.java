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


package net.imglib2.ops.pointset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Treats a general collection of points as a PointSet.
 * 
 * @author Barry DeZonia
 */
public class GeneralPointSet implements PointSet {
	
	// -- instance variables --
	
	private final int numD;
	private long[] origin;
	private long[] boundMin;
	private long[] boundMax;
	private final List<long[]> points;
	private final long[] tmpIncludePoint;

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
		this.tmpIncludePoint = new long[numD];
		// calc bounds BEFORE relativizing points
		calcBounds(points);
		// relativize points: this makes translate() fast
		for (int i = 0; i < points.size(); i++) {
			long[] p = points.get(i);
			if (p.length != numD)
				throw new IllegalArgumentException(
						"points have differing dimensions");
			for (int k = 0; k < numD; k++)
				p[k] -= origin[k];
		}
	}

	// -- PointSet methods --
	
	@Override
	public long[] getOrigin() { return origin; }
	
	@Override
	public void translate(long[] deltas) {
		for (int i = 0; i < numD; i++) {
			long delta = deltas[i];
			origin[i] += delta;
			boundMin[i] += delta;
			boundMax[i] += delta;
		}
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
		for (int i = 0; i < numD; i++)
			tmpIncludePoint[i] = point[i] - origin[i];
		for (long[] p : points) {
			if (Arrays.equals(tmpIncludePoint, p)) return true;
		}
		return false;
	}

	@Override
	public long[] findBoundMin() {
		return boundMin;
	}
	
	@Override
	public long[] findBoundMax() {
		return boundMax;
	}
	
	@Override
	public long calcSize() {
		return points.size();
	}

	@Override
	public GeneralPointSet copy() {
		ArrayList<long[]> pointsCopied = new ArrayList<long[]>();
		for (long[] p : points) {
			long[] newP = p.clone();
			for (int i = 0; i < numD; i++)
				newP[i] += origin[i];
			pointsCopied.add(newP);
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
		for (int i = 0; i < numD; i++) {
			long val = pts.get(0)[i];
			boundMin[i] = val;
			boundMax[i] = val;
		}
		for (int i = 1; i < pts.size(); i++) {
			long[] point = pts.get(i);
			for (int j = 0; j < numD; j++) {
				if (point[j] < boundMin[j]) boundMin[j] = point[j];
				if (point[j] > boundMax[j]) boundMax[j] = point[j];
			}
		}
	}
	
	private class GeneralPointSetIterator implements PointSetIterator {
		private int index;
		private long[] tmpNextPoint;
		
		public GeneralPointSetIterator() {
			index = -1;
			tmpNextPoint = new long[numD];
		}
		
		@Override
		public boolean hasNext() {
			return index+1 < points.size();
		}
		
		@Override
		public long[] next() {
			index++;
			long[] p = points.get(index);
			for (int i = 0; i < numD; i++)
				tmpNextPoint[i] = p[i] + origin[i];
			return tmpNextPoint;
		}
		
		@Override
		public void reset() {
			index = -1;
		}
		
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
