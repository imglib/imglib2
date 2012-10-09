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


// TODO - calc bounds could walk resulting set and generate min bounds

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
 * 
 * @author Barry DeZonia
 */
public class PointSetComplement implements PointSet {
	private final PointSet a;
	private final PointSetDifference diff;
	
	public PointSetComplement(PointSet a) {
		this.a = a;
		final HyperVolumePointSet hyper =
				new HyperVolumePointSet(a.findBoundMin(), a.findBoundMax());
		diff = new PointSetDifference(hyper, a);
	}
	
	@Override
	public long[] getOrigin() {
		return diff.getOrigin();
	}
	
	@Override
	public void translate(long[] deltas) {
		diff.translate(deltas);
	}
	
	@Override
	public PointSetIterator createIterator() {
		return diff.createIterator();
	}
	
	@Override
	public int numDimensions() { return diff.numDimensions(); }
	
	@Override
	public boolean includes(long[] point) {
		return diff.includes(point);
	}
	
	@Override
	public long[] findBoundMin() { return diff.findBoundMin(); }

	@Override
	public long[] findBoundMax() { return diff.findBoundMax(); }
	
	@Override
	public long calcSize() { return diff.calcSize(); }
	
	@Override
	public PointSetComplement copy() {
		return new PointSetComplement(a.copy());
	}
}

