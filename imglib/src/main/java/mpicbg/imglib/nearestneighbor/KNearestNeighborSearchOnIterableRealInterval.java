/**
 * Copyright (c) 2011, Stephan Saalfeld
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  Neither the name of the imglib project nor
 * the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package mpicbg.imglib.nearestneighbor;

import java.util.ArrayList;

import mpicbg.imglib.IterableRealInterval;
import mpicbg.imglib.RealCursor;
import mpicbg.imglib.RealLocalizable;

/**
 * <em>k</em>-nearest-neighbor search on {@link IterableRealInterval}
 * implemented as linear search.
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class KNearestNeighborSearchOnIterableRealInterval< T > implements KNearestNeighborSearch< T >
{
	final protected IterableRealInterval< T > iterable;
	
	final protected int k;
	final protected RealCursor< T >[] elements;
	final protected double[] squareDistances;
	
	final protected double[] referenceLocation;
	
	/**
	 * Calculate the square Euclidean distance of a query location to the
	 * location stored in referenceLocation.
	 * 
	 * @param query
	 * @return
	 */
	final protected double squareDistance( final RealLocalizable query )
	{
		double squareSum = 0;
		for ( int d = 0; d < k; ++d )
		{
			final double distance = query.getDoublePosition( d ) - referenceLocation[ d ];
			squareSum += distance * distance;
		}
		return squareSum;
	}
	
	@SuppressWarnings( "unchecked" )
	public KNearestNeighborSearchOnIterableRealInterval( final IterableRealInterval< T > iterable, final int k )
	{
		this.iterable = iterable;
		this.k = k;
		
		elements = ( RealCursor< T >[] )( new RealCursor[ k ] );
		squareDistances = new double[ k ];
		referenceLocation = new double[ iterable.numDimensions() ];
	}
	
	@Override
	public void search( final RealLocalizable reference )
	{
		for ( int i = 0; i < k; ++i )
			squareDistances[ i ] = Double.MAX_VALUE;
		
		reference.localize( referenceLocation );
		
		final RealCursor< T > cursor = iterable.localizingCursor();
		while ( cursor.hasNext() )
		{
			cursor.fwd();
			final double squareDistance = squareDistance( cursor );
			int i = k - 1;
			if ( squareDistances[ i ] > squareDistance )
			{
				final RealCursor< T > candidate = cursor.copy();
				
				for ( int j = i - 1; i > 0 && squareDistances[ j ] > squareDistance; --i, --j )
				{
					squareDistances[ i ] = squareDistances[ j ];
					elements[ i ] = elements[ j ];
				}
				squareDistances[ i ] = squareDistance;
				elements[ i ] = candidate;
			}
		}
	}

	@Override
	public RealLocalizable getPosition( final int i )
	{
		return elements[ i ];
	}

	@Override
	public double getSquareDistance( final int i )
	{
		return squareDistances[ i ];
	}

	@Override
	public double getDistance( final int i )
	{
		return Math.sqrt( squareDistances[ i ] );
	}

	@Override
	public RealCursor< T > getSampler( final int i )
	{
		return elements.get( i );
	}
}
