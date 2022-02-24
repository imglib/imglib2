/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2022 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

import net.imglib2.IterableRealInterval;
import net.imglib2.RealCursor;
import net.imglib2.RealLocalizable;
import net.imglib2.Sampler;

/**
 * <em>k</em>-nearest-neighbor search on {@link IterableRealInterval}
 * implemented as linear search.
 * 
 * @author Stephan Saalfeld
 */
public class NearestNeighborSearchOnIterableRealInterval< T > implements NearestNeighborSearch< T >
{
	final protected IterableRealInterval< T > iterable;

	final protected int n;

	protected RealCursor< T > element = null;

	protected double squareDistance = Double.MAX_VALUE;

	final protected double[] referenceLocation;

	/**
	 * Calculate the square Euclidean distance of a query location to the
	 * location stored in referenceLocation.
	 */
	final protected double squareDistance( final RealLocalizable query )
	{
		double squareSum = 0;
		for ( int d = 0; d < n; ++d )
		{
			final double distance = query.getDoublePosition( d ) - referenceLocation[ d ];
			squareSum += distance * distance;
		}
		return squareSum;
	}

	public NearestNeighborSearchOnIterableRealInterval( final IterableRealInterval< T > iterable )
	{
		this.iterable = iterable;
		n = iterable.numDimensions();

		referenceLocation = new double[ n ];
	}

	@Override
	public int numDimensions()
	{
		return n;
	}

	@Override
	public void search( final RealLocalizable reference )
	{
		squareDistance = Double.MAX_VALUE;

		reference.localize( referenceLocation );

		final RealCursor< T > cursor = iterable.localizingCursor();
		while ( cursor.hasNext() )
		{
			cursor.fwd();
			final double cursorSquareDistance = squareDistance( cursor );
			if ( squareDistance > cursorSquareDistance )
			{
				squareDistance = cursorSquareDistance;
				element = cursor.copyCursor();
			}
		}
	}

	/* NearestNeighborSearch */

	@Override
	public RealLocalizable getPosition()
	{
		return element;
	}

	@Override
	public Sampler< T > getSampler()
	{
		return element;
	}

	@Override
	public double getSquareDistance()
	{
		return squareDistance;
	}

	@Override
	public double getDistance()
	{
		return Math.sqrt( squareDistance );
	}

	@Override
	public NearestNeighborSearchOnIterableRealInterval< T > copy()
	{
		final NearestNeighborSearchOnIterableRealInterval< T > copy = new NearestNeighborSearchOnIterableRealInterval< T >( iterable );
		System.arraycopy( referenceLocation, 0, copy.referenceLocation, 0, referenceLocation.length );
		copy.element = element;
		copy.squareDistance = squareDistance;
		return copy;
	}
}
