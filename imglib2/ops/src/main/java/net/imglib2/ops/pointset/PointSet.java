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

import net.imglib2.EuclideanSpace;

/**
 * PointSets define a set of point indices (long[]). PointSets can be moved
 * to new locations in space. This allows one to do sliding window type of
 * analyses. But a PointSet can be irregularly shaped.
 * 
 * @author Barry DeZonia
 */
public interface PointSet extends EuclideanSpace, Iterable<long[]> {
	
	/**
	 * Gets the current origin point of the PointSet
	 */
	long[] getOrigin();

	/**
	 * Moves the PointSet by a set of deltas. Any existing
	 * PointSetIterators will automatically iterate the new bounds.
	 */
	void translate(long[] delta);
	
	/**
	 * Creates an iterator that can be used to pull point indices out of the
	 * PointSet.
	 */
	@Override
	PointSetIterator iterator();

	/**
	 * Returns the dimensionality of the points contained in the PointSet
	 */
	@Override
	int numDimensions();
	
	/**
	 * Returns the lower bound of the space containing the PointSet. This
	 * can be an expensive operation (potentially iterating the whole set
	 * to calculate). These results are cached when possible. Subsequent
	 * calls to setAnchor() will invalidate bounds.
	 */
	long[] findBoundMin();

	/**
	 * Returns the upper bound of the space containing the PointSet. This
	 * can be an expensive operation (potentially iterating the whole set
	 * to calculate). These results are cached when possible. Subsequent
	 * calls to setAnchor() will invalidate bounds.
	 */
	long[] findBoundMax();
	
	/**
	 * Returns true if a given point is a member of the PointSet
	 */
	boolean includes(long[] point);
	
	/**
	 * Calculates the number of elements in the PointSet. This can be an
	 * expensive operation (potentially iterating the whole set to count).
	 */
	long calcSize();
	
	/**
	 * Make a copy of self. This is useful for multithreaded parallel computation
	 */
	PointSet copy();
}

