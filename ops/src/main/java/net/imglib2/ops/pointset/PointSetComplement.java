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

/*
* This is not a textbook definition of complement. Complement usually
* is defined as a set difference. But we handle set difference below.
* The set difference is a relation between two sets A and B. A is the
* containing space and B is the set of outside of which we are
* interested in. In this implementation our Complement treats a given
* set as both A & B in a set difference. For a full region this will
* be the empty set. But for a sparse set it will be all the points not
* contained in the sparse set.
*/

/**
 * PointSetComplement is a {@link PointSet} consisting of all the points not
 * in an input PointSet. This complement is relative to the input PointSet's
 * bounds. Thus for a {@link HyperVolumePointSet} it's complement is the
 * empty set. To make a complement of a PointSet that includes all of space
 * use {@link PointSetDifference}.
 * 
 * @author Barry DeZonia
 */
public class PointSetComplement extends AbstractPointSet {
	
	// -- instance variables --
	
	private final PointSet a;
	private final PointSetDifference diff;
	
	// -- constructor --
	
	public PointSetComplement(PointSet a) {
		this.a = a;
		long[] min = new long[a.numDimensions()];
		long[] max = min.clone();
		a.min(min);
		a.max(max);
		HyperVolumePointSet hyper = new HyperVolumePointSet(min, max);
		diff = new PointSetDifference(hyper, a);
	}
	
	// -- PointSet methods --
	
	@Override
	public long[] getOrigin() {
		return diff.getOrigin();
	}
	
	@Override
	public void translate(long[] deltas) {
		diff.translate(deltas);
		invalidateBounds();
	}
	
	@Override
	public PointSetIterator iterator() {
		return diff.iterator();
	}
	
	@Override
	public int numDimensions() { return diff.numDimensions(); }
	
	@Override
	public boolean includes(long[] point) {
		return diff.includes(point);
	}
	
	@Override
	protected long[] findBoundMin() {
		return diff.findBoundMin();
	}

	@Override
	protected long[] findBoundMax() {
		return diff.findBoundMax();
	}
	
	@Override
	public long size() { return diff.size(); }
	
	@Override
	public PointSetComplement copy() {
		return new PointSetComplement(a.copy());
	}
}

