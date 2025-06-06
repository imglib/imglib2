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

import net.imglib2.EuclideanSpace;
import net.imglib2.RealLocalizable;
import net.imglib2.Sampler;

/**
 * Nearest-neighbor search in an Euclidean space. The interface describes
 * implementations that perform the search for a specified location and provide
 * access to the data, location and distance of the found nearest neighbor until
 * the next search is performed. In a multi-threaded application, each thread
 * will thus need its own {@link NearestNeighborSearch}.
 * 
 * @author Stephan Saalfeld
 */
public interface NearestNeighborSearch< T > extends EuclideanSpace
{
	/**
	 * Perform nearest-neighbor search for a reference coordinate.
	 * 
	 * @param reference
	 */
	void search( RealLocalizable reference );

	/**
	 * Access the data of the nearest neighbor. Data is accessed through a
	 * {@link Sampler} that guarantees write access if the underlying data set
	 * is writable.
	 */
	Sampler< T > getSampler();

	/**
	 * Access the position of the nearest neighbor, ordered by square Euclidean
	 * distance.
	 */
	RealLocalizable getPosition();

	/**
	 * Access the square Euclidean distance between the reference location as
	 * used for the last search and the nearest neighbor, ordered by square
	 * Euclidean distance.
	 */
	double getSquareDistance();

	/**
	 * Access the Euclidean distance between the reference location as used for
	 * the last search and the nearest neighbor, ordered by square Euclidean
	 * distance.
	 */
	default double getDistance()
	{
		return Math.sqrt( getSquareDistance() );
	}

	/**
	 * Create a copy.
	 */
	NearestNeighborSearch< T > copy();
}
