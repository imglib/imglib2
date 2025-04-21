/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2025 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
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

package net.imglib2.neighborsearch;

import net.imglib2.RealLocalizable;
import net.imglib2.Sampler;

/**
 * <em>k</em>-nearest-neighbor search in an Euclidean space. The interface
 * describes implementations that perform the search for a specified location
 * and provide access to the data, location and distance of the found nearest
 * neighbors until the next search is performed. In a multi-threaded
 * application, each thread will thus need its own
 * {@link KNearestNeighborSearch}.
 * 
 * @author Stephan Saalfeld
 */
public interface KNearestNeighborSearch< T > extends NearestNeighborSearch< T >
{
	/**
	 * Perform <em>k</em>-nearest-neighbor search for a reference coordinate.
	 * 
	 * @param reference
	 */
	@Override
	void search( RealLocalizable reference );

	/**
	 * Get the of k nearest neighbor points used in this search
	 * 
	 * @return the number of nearest neighbor points k used for this search
	 */
	int getK();

	/**
	 * Access the data of the <em>i</em><sup>th</sup> nearest neighbor, ordered
	 * by square Euclidean distance. Data is accessed through a {@link Sampler}
	 * that guarantees write access if the underlying data set is writable.
	 */
	Sampler< T > getSampler( int i );

	/**
	 * Access the position of the <em>i</em><sup>th</sup> nearest neighbor,
	 * ordered by square Euclidean distance.
	 */
	RealLocalizable getPosition( int i );

	/**
	 * Access the square Euclidean distance between the reference location as
	 * used for the last search and the <em>i</em><sup>th</sup> nearest
	 * neighbor, ordered by square Euclidean distance.
	 */
	double getSquareDistance( int i );

	/**
	 * Access the Euclidean distance between the reference location as used for
	 * the last search and the <em>i</em><sup>th</sup> nearest neighbor, ordered
	 * by square Euclidean distance.
	 */
	default double getDistance( final int i )
	{
		return Math.sqrt( getSquareDistance( i ) );
	}

	/**
	 * Create a copy.
	 */
	@Override
	KNearestNeighborSearch< T > copy();
}
