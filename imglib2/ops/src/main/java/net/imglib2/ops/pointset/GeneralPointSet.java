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
 * 
 * @author Barry DeZonia
 */
public class GeneralPointSet implements PointSet {
	private long[] origin;
	private long[] boundMin;
	private long[] boundMax;
	private final List<long[]> points;
	private final long[] tmpIncludePoint;

	public GeneralPointSet(long[] origin, List<long[]> pts) {
		this.origin = origin.clone();
		this.boundMin = origin.clone();
		this.boundMax = origin.clone();
		this.points = new ArrayList<long[]>();
		this.points.addAll(pts);
		this.tmpIncludePoint = new long[origin.length];
		// calc bounds BEFORE relativizing points
		calcBounds(origin, points);
		// relativize points: this makes translate() fast
		for (int i = 0; i < points.size(); i++) {
			long[] p = points.get(i);
			if (p.length != origin.length)
				throw new IllegalArgumentException();
			for (int k = 0; k < origin.length; k++)
				p[k] -= origin[k];
		}
	}

	@Override
	public long[] getOrigin() { return origin; }
	
	@Override
	public void translate(long[] deltas) {
		for (int i = 0; i < origin.length; i++) {
			origin[i] += deltas[i];
			boundMin[i] += deltas[i];
			boundMax[i] += deltas[i];
		}
	}
	
	@Override
	public PointSetIterator createIterator() {
		return new GeneralPointSetIterator();
	}
	
	@Override
	public int numDimensions() { return origin.length; }
	
	// TODO a spatial data structure would speed this up greatly
	
	@Override
	public boolean includes(long[] point) {
		for (int i = 0; i < origin.length; i++)
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
			for (int i = 0; i < newP.length; i++)
				newP[i] += origin[i];
			pointsCopied.add(newP);
		}
		return new GeneralPointSet(origin, pointsCopied);
	}
	
	public static GeneralPointSet explode(PointSet ps) {
		final List<long[]> points = new ArrayList<long[]>();
		final PointSetIterator iter = ps.createIterator();
		while (iter.hasNext()) {
			points.add(iter.next().clone());
		}
		return new GeneralPointSet(ps.getOrigin(), points);
	}

	private void calcBounds(long[] org, List<long[]> pts) {
		for (int i = 0; i < org.length; i++) {
			boundMin[i] = org[i];
			boundMax[i] = org[i];
		}
		for (long[] point : points) {
			for (int i = 0; i < org.length; i++) {
				if (point[i] < boundMin[i]) boundMin[i] = point[i];
				if (point[i] > boundMax[i]) boundMax[i] = point[i];
			}
		}
	}
	
	private class GeneralPointSetIterator implements PointSetIterator {
		private int index;
		private long[] tmpNextPoint;
		
		public GeneralPointSetIterator() {
			index = -1;
			tmpNextPoint = new long[GeneralPointSet.this.origin.length];
		}
		
		@Override
		public boolean hasNext() {
			return index+1 < points.size();
		}
		
		@Override
		public long[] next() {
			index++;
			long[] p = points.get(index);
			for (int i = 0; i < origin.length; i++)
				tmpNextPoint[i] = p[i] + origin[i];
			return tmpNextPoint;
		}
		
		@Override
		public void reset() {
			index = -1;
		}
	}
}
