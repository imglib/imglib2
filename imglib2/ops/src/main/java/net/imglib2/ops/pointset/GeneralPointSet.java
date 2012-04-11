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

import net.imglib2.ops.PointSet;
import net.imglib2.ops.PointSetIterator;

/**
 * 
 * @author Barry DeZonia
 */
public class GeneralPointSet extends AbstractBoundedRegion implements PointSet {
	private long[] anchor;
	private final List<long[]> points;
	private final long[] origAnchor;
	private final long[] tmpPoint;
	private boolean boundsInvalid = true;

	public GeneralPointSet(long[] anchor, List<long[]> pts) {
		this.anchor = anchor;
		this.points = new ArrayList<long[]>();
		this.points.addAll(pts);
		this.origAnchor = anchor.clone();
		this.tmpPoint = new long[origAnchor.length];
		for (int i = 1; i < points.size(); i++) {
			long[] p = points.get(i);
			if (p.length != origAnchor.length)
				throw new IllegalArgumentException();
			for (int k = 0; k < origAnchor.length; k++)
				p[k] -= anchor[k];
		}
		boundsInvalid = true;
	}

	@Override
	public long[] getAnchor() { return anchor; }
	
	@Override
	public void setAnchor(long[] newAnchor) {
		if (newAnchor.length != origAnchor.length)
			throw new IllegalArgumentException();
		this.anchor = newAnchor;
		boundsInvalid = true;
	}
	
	@Override
	public GeneralPointSetIterator createIterator() {
		return new GeneralPointSetIterator();
	}
	
	@Override
	public int numDimensions() { return origAnchor.length; }
	
	// TODO a spatial data structure would speed this up greatly
	
	@Override
	public boolean includes(long[] point) {
		for (int i = 0; i < origAnchor.length; i++)
			tmpPoint[i] = point[i] - anchor[i];
		for (long[] p : points) {
			if (Arrays.equals(tmpPoint, p)) return true;
		}
		return false;
	}

	@Override
	public long[] findBoundMin() {
		if (boundsInvalid) calcBounds();
		return getMin();
	}
	
	@Override
	public long[] findBoundMax() {
		if (boundsInvalid) calcBounds();
		return getMax();
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
				newP[i] += origAnchor[i];
		}
		return new GeneralPointSet(origAnchor.clone(), pointsCopied);
	}
	
	private void calcBounds() {
		long[] absoluteCoord = new long[anchor.length];
		setMin(anchor);
		setMax(anchor);
		for (long[] point : points) {
			for (int i = 0; i < anchor.length; i++) {
				absoluteCoord[i] = anchor[i] + point[i];
			}
			updateMin(absoluteCoord);
			updateMax(absoluteCoord);
		}
		boundsInvalid = false;
	}
	
	private class GeneralPointSetIterator implements PointSetIterator {
		private int index;
		private long[] tmpPosition;
		
		public GeneralPointSetIterator() {
			index = -1;
			tmpPosition = new long[GeneralPointSet.this.origAnchor.length];
		}
		
		@Override
		public boolean hasNext() {
			return index+1 < points.size();
		}
		
		@Override
		public long[] next() {
			index++;
			long[] p = points.get(index);
			for (int i = 0; i < origAnchor.length; i++)
				tmpPosition[i] = p[i] + anchor[i];
			return tmpPosition;
		}
		
		@Override
		public void reset() {
			index = -1;
		}
	}
}
