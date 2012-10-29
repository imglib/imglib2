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
 * UniversalPointSet is a {@link PointSet} that includes all points in
 * space.
 * 
 * @author Barry DeZonia
 */
public class UniversalPointSet implements PointSet {

	// -- instance variables --
	
	private final long[] origin;

	// -- constructor --
	
	public UniversalPointSet() {
		origin = new long[0];
	}
	
	// -- PointSet methods --
	
	@Override
	public long[] getOrigin() {
		return origin;
	}

	@Override
	public void translate(long[] deltas) {
		// do nothing
	}

	@Override
	public PointSetIterator iterator() {
		return new UniversalPointSetIterator();
	}

	@Override
	public int numDimensions() {
		return 0;
	}

	@Override
	public long[] findBoundMin() {
		return origin;
	}

	@Override
	public long[] findBoundMax() {
		return origin;
	}

	@Override
	public boolean includes(long[] point) {
		return true;
	}

	@Override
	public long calcSize() {
		throw new UnsupportedOperationException("UniversalPointSet is infinite in size");
	}

	@Override
	public PointSet copy() {
		return new UniversalPointSet();
	}

	// -- private helpers --
	
	private class UniversalPointSetIterator implements PointSetIterator {

		@Override
		public boolean hasNext() {
			return false;  // noniterable
		}

		@Override
		public long[] next() {
			throw new UnsupportedOperationException("Cannot iterate over UniversalPointSet");
		}

		@Override
		public void reset() {
			// nothing to do
		}
		
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
	}
}
