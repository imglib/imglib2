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

package net.imglib2.ops.input;

import net.imglib2.ops.pointset.PointSet;

/**
 * This class is an {@link InputIterator} that iterates a region (given as a
 * {@link PointSet}) a point at a time returning each PointSet anchored at the
 * point of iteration.
 * 
 * @author Barry DeZonia
 */
public class PointSetInputIterator implements InputIterator<PointSet> {

	// -- instance variables --
	
	private final PointSet subspace;
	private final PointInputIterator iter;
	private long[] pos;
	private long[] deltas;
	
	// -- constructor --
	
	public PointSetInputIterator(PointSet space, PointSet subspace) {
		iter = new PointInputIterator(space);
		this.subspace = subspace;
		pos = null;
		deltas = new long[space.numDimensions()];
	}
	
	// -- InputIterator methods --
	
	@Override
	public boolean hasNext() {
		return iter.hasNext();
	}

	@Override
	public PointSet next(PointSet currVal) {
		pos = iter.next(pos);

		// translate subspace through space
		long[] oldPosition = subspace.getOrigin();
		for (int i = 0; i < deltas.length; i++) {
			deltas[i] = pos[i] - oldPosition[i];
		}
		subspace.translate(deltas);
		
		return subspace;
	}

	@Override
	public long[] getCurrentPoint() {
		return iter.getCurrentPoint();
	}
}
