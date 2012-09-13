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
public class PointSetUnion extends AbstractBoundedRegion implements PointSet {
	private final PointSet a, b;
	private boolean minInvalid, maxInvalid;
	
	public PointSetUnion(PointSet a, PointSet b) {
		if (a.numDimensions() != b.numDimensions())
			throw new IllegalArgumentException();
		this.a = a;
		this.b = b;
		minInvalid = true;
		maxInvalid = true;
	}
	
	@Override
	public long[] getAnchor() {
		return a.getAnchor();
	}
	
	@Override
	public void setAnchor(long[] newAnchor) {
		long[] currAnchor = getAnchor();
		if (currAnchor.length != newAnchor.length)
			throw new IllegalArgumentException();
		//long[] anchor1 = a.getAnchor();
		long[] anchor2 = b.getAnchor();
		long[] deltas = new long[currAnchor.length];
		for (int i = 0; i < currAnchor.length; i++) {
			deltas[i] = newAnchor[i] - currAnchor[i];
		}
		long[] newAnchor2 = new long[currAnchor.length];
		for (int i = 0; i < currAnchor.length; i++) {
			newAnchor2[i] = anchor2[i] + deltas[i];
		}
		a.setAnchor(newAnchor);
		b.setAnchor(newAnchor2);
		minInvalid = true;
		maxInvalid = true;
	}
	
	@Override
	public PointSetIterator createIterator() {
		return new PointSetUnionIterator();
	}
	
	@Override
	public int numDimensions() { return getAnchor().length; }
	
	@Override
	public boolean includes(long[] point) {
		return a.includes(point) || b.includes(point);
	}
	
	@Override
	public long[] findBoundMin() {
		if (minInvalid) {
			minInvalid = false;
			setMin(a.findBoundMin());
			updateMin(b.findBoundMin());
		}
		return getMin();
	}

	@Override
	public long[] findBoundMax() {
		if (maxInvalid) {
			maxInvalid = false;
			setMax(a.findBoundMax());
			updateMax(b.findBoundMax());
		}
		return getMax();
	}
	
	@Override
	public long calcSize() {
		long numElements = 0;
		PointSetIterator iter = createIterator();
		while (iter.hasNext()) {
			iter.next();
			numElements++;
		}
		return numElements;
	}
	
	@Override
	public PointSetUnion copy() {
		return new PointSetUnion(a.copy(), b.copy());
	}

	private class PointSetUnionIterator implements PointSetIterator {
		
		private final PointSetIterator aIter;
		private final PointSetIterator bIter;
		private long[] bNext;
		
		public PointSetUnionIterator() {
			aIter = a.createIterator();
			bIter = b.createIterator();
			bNext = null;
		}
		
		@Override
		public boolean hasNext() {
			bNext = null;
			if (aIter.hasNext()) return true;
			while (bIter.hasNext()) {
				bNext = bIter.next();
				if (!a.includes(bNext)) return true;
			}
			return false;
		}
		
		@Override
		public long[] next() {
			if (bNext != null) return bNext;
			return aIter.next();
		}
		
		@Override
		public void reset() {
			aIter.reset();
			bIter.reset();
		}
	}
}

