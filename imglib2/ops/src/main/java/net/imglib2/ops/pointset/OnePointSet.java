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

import java.util.Arrays;


/**
 * OnePointSet represents a {@link PointSet} that contains exactly one point.
 * 
 * @author Barry DeZonia
 */
public class OnePointSet implements PointSet {

	// -- instance varaibles --
	
	private long[] point;
	
	// -- constructor --
	
	public OnePointSet(long[] point) {
		this.point = point;
	}
	
	// -- PointSet methods --
	
	@Override
	public long[] getOrigin() {
		return point;
	}

	@Override
	public void translate(long[] deltas) {
		for (int i = 0; i < point.length; i++)
			point[i] += deltas[i];
	}

	@Override
	public int numDimensions() {
		return point.length;
	}

	@Override
	public long[] findBoundMin() {
		return point;
	}

	@Override
	public long[] findBoundMax() {
		return point;
	}

	@Override
	public boolean includes(long[] pt) {
		return Arrays.equals(this.point, pt);
	}

	@Override
	public long calcSize() {
		return 1;
	}

	@Override
	public OnePointSet copy() {
		return new OnePointSet(point.clone());
	}
	
	@Override
	public PointSetIterator iterator() {
		return new OnePointSetIterator();
	}

	// -- private helpers --
	
	private class OnePointSetIterator implements PointSetIterator {

		private boolean hasNext = true;
		
		@Override
		public boolean hasNext() {
			return hasNext;
		}

		@Override
		public long[] next() {
			hasNext = false;
			return point;
		}

		@Override
		public void reset() {
			hasNext = true;
		}
		
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
