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
import net.imglib2.ops.condition.Condition;

/**
 * ConditionalPointSet is a {@link PointSet} implementation that constrains
 * another PointSet by a {@link Condition}. For instance you could specify
 * a set that contains all the points in a hyper volume whose Y coordinate
 * is odd.
 * <p>
 * Note that Conditions can be compound. Alternatively ConditionalPointSets
 * can be nested.
 * 
 * @author Barry DeZonia
 *
 */
public class ConditionalPointSet extends AbstractPointSet {

	// -- instance variables --
	
	private PointSet pointSet;
	private Condition<long[]> condition;
	private final BoundsCalculator calculator;
	private boolean needsCalc;
	
	// -- ConditionalPointSet methods --
	
	public ConditionalPointSet(PointSet pointSet, Condition<long[]> condition) {
		this.pointSet = pointSet;
		this.condition = condition;
		this.calculator = new BoundsCalculator();
		this.needsCalc = true;
	}
	
	public Condition<long[]> getCondition() { return condition; }
	
	public void setCondition(Condition<long[]> condition) {
		this.condition = condition;
		invalidateBounds();
		needsCalc = true;
	}
	
	public PointSet getPointSet() {
		return pointSet;
	}

	public void setPointSet(PointSet ps) {
		pointSet = ps;
		invalidateBounds();
		needsCalc = true;
	}

	// -- PointSet methods --
	
	@Override
	public long[] getOrigin() {
		return pointSet.getOrigin();
	}

	@Override
	public void translate(long[] deltas) {
		pointSet.translate(deltas);
		invalidateBounds();
		needsCalc = true;
	}

	@Override
	public PointSetIterator iterator() {
		return new ConditionalPointSetIterator();
	}

	@Override
	public int numDimensions() {
		return pointSet.numDimensions();
	}

	@Override
	protected long[] findBoundMin() {
		if (needsCalc) {
			calculator.calc(this);
			needsCalc = false;
		}
		return calculator.getMin();
	}

	@Override
	protected long[] findBoundMax() {
		if (needsCalc) {
			calculator.calc(this);
			needsCalc = false;
		}
		return calculator.getMax();
	}

	@Override
	public boolean includes(long[] point) {
		return pointSet.includes(point) && condition.isTrue(point);
	}

	@Override
	public long size() {
		long numElements = 0;
		PointSetIterator iter = iterator();
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

	// -- private helpers --
	
	private class ConditionalPointSetIterator extends AbstractCursor<long[]>
		implements PointSetIterator
	{
		private final PointSetIterator iter;
		private long[] curr;
		private long[] nextCache;
		
		public ConditionalPointSetIterator() {
			super(pointSet.numDimensions());
			iter = pointSet.iterator();
			reset();
		}

		@Override
		public boolean hasNext() {
			if (nextCache != null) return true;
			return positionToNext();
		}

		@Override
		public void reset() {
			iter.reset();
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
			return new ConditionalPointSetIterator();
		}

		@Override
		public AbstractCursor<long[]> copyCursor() {
			return copy();
		}

		private boolean positionToNext() {
			nextCache = null;
			while (iter.hasNext()) {
				long[] pos = iter.next();
				if (condition.isTrue(pos)) {
					nextCache = pos;
					return true;
				}
			}
			return false;
		}

	}
}
