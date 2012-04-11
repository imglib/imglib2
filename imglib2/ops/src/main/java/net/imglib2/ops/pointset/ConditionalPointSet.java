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

import net.imglib2.ops.Condition;
import net.imglib2.ops.PointSet;
import net.imglib2.ops.PointSetIterator;

/**
 * 
 * @author Barry DeZonia
 *
 */
public class ConditionalPointSet extends AbstractBoundedRegion implements PointSet {

	private final PointSet pointSet;
	private final Condition<long[]> condition;
	private boolean boundsInvalid;
	
	public ConditionalPointSet(PointSet pointSet, Condition<long[]> condition) {
		this.pointSet = pointSet;
		this.condition = condition;
		this.boundsInvalid = true;
	}
	
	@Override
	public long[] getAnchor() {
		return pointSet.getAnchor();
	}

	@Override
	public void setAnchor(long[] anchor) {
		pointSet.setAnchor(anchor);
		this.boundsInvalid = true;
	}

	@Override
	public ConditionalPointSetIterator createIterator() {
		return new ConditionalPointSetIterator();
	}

	@Override
	public int numDimensions() {
		return pointSet.numDimensions();
	}

	// TODO - is this too inaccurate? need to iterate first?
	// Maybe need to get rid of the whole bounds concept.
	// For instance PointSetIntersection is also iffy on this.
	// Or maybe rename to calcBoundMin() and calcBoundMax().
	// This way it is clear it can be expensive. Then each
	// PointSet can calc using iterators if needed.
	
	@Override
	public long[] findBoundMin() {
		if (boundsInvalid) calcBounds();
		return getMin();
	}

	// TODO - comment for getBoundMin() applies here too
	
	@Override
	public long[] findBoundMax() {
		if (boundsInvalid) calcBounds();
		return getMax();
	}

	@Override
	public boolean includes(long[] point) {
		return pointSet.includes(point) && condition.isTrue(point);
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
	public ConditionalPointSet copy() {
		return new ConditionalPointSet(pointSet.copy(), condition.copy());
	}
	
	private void calcBounds() {
		PointSetIterator iter = createIterator();
		while (iter.hasNext()) {
			long[] point = iter.next();
			if (boundsInvalid) {
				boundsInvalid = false;
				setMax(point);
				setMin(point);
			}
			else {
				updateMax(point);
				updateMin(point);
			}
		}
	}
	
	private class ConditionalPointSetIterator implements PointSetIterator {
		private final PointSetIterator iter;
		private long[] next;
		
		public ConditionalPointSetIterator() {
			iter = pointSet.createIterator();
		}

		@Override
		public boolean hasNext() {
			while (iter.hasNext()) {
				next = iter.next();
				if (condition.isTrue(next)) return true;
			}
			return false;
		}

		@Override
		public long[] next() {
			return next;
		}

		@Override
		public void reset() {
			iter.reset();
		}
	}
}
