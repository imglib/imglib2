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


/**
 * 
 * @author Barry DeZonia
 */
public class HyperVolumePointSet implements PointSet {
	
	// -- instance variables --
	
	private final long[] anchor;
	private final long[] negOffsets;
	private final long[] posOffsets;
	private final long[] boundMin;
	private final long[] boundMax;

	// -- constructors --
	
	/**
	 * Constructor taking an anchor point and offsets relative to that anchor
	 * point. These parameters define a hypervolume.
	 * 
	 * @param anchor The key point around which the hypervolume is defined.
	 * @param negOffsets Span in the negative direction beyond the anchor point (note: must be positive values).
	 * @param posOffsets - Span in the positive direction beyond the anchor point (note: must be positive values).
	 */
	public HyperVolumePointSet(long[] anchor, long[] negOffsets, long[] posOffsets) {
		if (anchor.length != negOffsets.length)
			throw new IllegalArgumentException();
		if (anchor.length != posOffsets.length)
			throw new IllegalArgumentException();
		for (int i = 0; i < anchor.length; i++) {
			if (negOffsets[i] < 0)
				throw new IllegalArgumentException("all offsets must be >= 0");
			if (posOffsets[i] < 0)
				throw new IllegalArgumentException("all offsets must be >= 0");
		}
		this.anchor = anchor.clone();
		this.negOffsets = negOffsets.clone();
		this.posOffsets = posOffsets.clone();
		this.boundMin = new long[anchor.length];
		this.boundMax = new long[anchor.length];
		for (int i = 0; i < anchor.length; i++) {
			boundMin[i] = anchor[i] - negOffsets[i];
			boundMax[i] = anchor[i] + posOffsets[i];
		}
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
		this.negOffsets = new long[pt1.length];
		this.posOffsets = new long[pt1.length];
		for (int i = 0; i < pt1.length; i++) {
			boundMin[i] = Math.min(pt1[i], pt2[i]);
			boundMax[i] = Math.max(pt1[i], pt2[i]);
		}
		for (int i = 0; i < pt1.length; i++) {
			posOffsets[i] = boundMax[i] - boundMin[i];
		}
		this.anchor = boundMin.clone();
	}
	
	/**
	 * Constructor that takes a span array. The hypervolume is defined from the
	 * origin extending for the given span. This means that if a span component
	 * equals X then the hypervolume goes from 0 to X-1.
	 * 
	 * @param span The values specifying width, height, depth, etc. of the hypervolume.
	 */
	public HyperVolumePointSet(long[] span) {
		this(new long[span.length], lastPoint(span));
	}
	
	// -- public api --
	
	@Override
	public long[] getAnchor() { return anchor; }
	
	@Override
	public void setAnchor(long[] newAnchor) {
		if (anchor != newAnchor)
			if (anchor.length != newAnchor.length)
				throw new IllegalArgumentException();
		for (int i = 0; i < anchor.length; i++) {
			anchor[i] = newAnchor[i];
			boundMin[i] = newAnchor[i] - negOffsets[i];
			boundMax[i] = newAnchor[i] + posOffsets[i];
		}
		//for (PointSetIterator iter : iters) iter.reset();
	}
	
	@Override
	public PointSetIterator createIterator() {
		return new HyperVolumePointSetIterator();
	}
	
	@Override
	public int numDimensions() { return anchor.length; }
	
	@Override
	public long[] findBoundMin() { return boundMin; }

	@Override
	public long[] findBoundMax() { return boundMax; }
	
	@Override
	public boolean includes(long[] point) {
		for (int i = 0; i < anchor.length; i++) {
			if (point[i] < boundMin[i]) return false;
			if (point[i] > boundMax[i]) return false;
		}
		return true;
	}
	
	@Override
	public long calcSize() {
		long numElements = 1;
		for (int i = 0; i < anchor.length; i++) {
			numElements *= 1 + negOffsets[i] + posOffsets[i];
		}
		return numElements;
	}

	@Override
	public HyperVolumePointSet copy() {
		return new HyperVolumePointSet(findBoundMin(), findBoundMax());
	}
	
	// -- private helpers --
	
	private static long[] lastPoint(long[] span) {
		long[] lastPoint = new long[span.length];
		for (int i = 0; i < span.length; i++) {
			lastPoint[i] = span[i] - 1;
		}
		return lastPoint;
	}

	private class HyperVolumePointSetIterator implements PointSetIterator {
		final long[] pos;
		boolean outOfBounds;
		final boolean emptySpace;
		
		HyperVolumePointSetIterator() {
			emptySpace = anchor.length == 0;
			outOfBounds = true;
			pos = new long[anchor.length];
		}
		
		@Override
		public void reset() {
			outOfBounds = true;
		}
		
		@Override
		public boolean hasNext() {
			if (emptySpace) return false;
			if (outOfBounds) return true;
			for (int i = 0; i < anchor.length; i++) {
				if (pos[i] < boundMax[i]) return true;
			}
			return false;
		}
		
		@Override
		public long[] next() {
			if (outOfBounds) {
				outOfBounds = false;
				for (int i = 0; i < anchor.length; i++) {
					pos[i] = boundMin[i];
				}
				return pos;
			}
			
			// else outOfBounds == false
			for (int i = 0; i < anchor.length; i++) {
				pos[i]++;
				if (pos[i] <= boundMax[i]) return pos;
				pos[i] = boundMin[i];
			}
			
			throw new IllegalArgumentException("can't call next() beyond last position");
		}
	}
}

