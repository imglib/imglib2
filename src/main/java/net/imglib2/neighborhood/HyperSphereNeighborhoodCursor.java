/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2016 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package net.imglib2.neighborhood;

import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.util.IntervalIndexer;

public final class HyperSphereNeighborhoodCursor< T > extends HypersphereNeighborhoodLocalizableSampler< T > implements Cursor< Neighborhood< T > >
{
	private final long[] dimensions;

	private final long[] min;

	private final long[] max;

	private long index;

	private final long maxIndex;

	private long maxIndexOnLine;

	public HyperSphereNeighborhoodCursor( final RandomAccessibleInterval< T > source, final long radius, final HyperSphereNeighborhoodFactory< T > factory )
	{
		super( source, radius, factory, source );

		dimensions = new long[ n ];
		min = new long[ n ];
		max = new long[ n ];
		source.dimensions( dimensions );
		source.min( min );
		source.max( max );
		long size = dimensions[ 0 ];
		for ( int d = 1; d < n; ++d )
			size *= dimensions[ d ];
		maxIndex = size - 1;
		reset();
	}

	private HyperSphereNeighborhoodCursor( final HyperSphereNeighborhoodCursor< T > c )
	{
		super( c );
		dimensions = c.dimensions.clone();
		min = c.min.clone();
		max = c.max.clone();
		maxIndex = c.maxIndex;
		index = c.index;
		maxIndexOnLine = c.maxIndexOnLine;
	}

	@Override
	public void fwd()
	{
		++currentPos[ 0 ];
		if ( ++index > maxIndexOnLine )
			nextLine();
	}

	private void nextLine()
	{
		currentPos[ 0 ] = min[ 0 ];
		maxIndexOnLine += dimensions[ 0 ];
		for ( int d = 1; d < n; ++d )
		{
			++currentPos[ d ];
			if ( currentPos[ d ] > max[ d ] )
			{
				currentPos[ d ] = min[ d ];
			}
			else
				break;
		}
	}

	@Override
	public void reset()
	{
		index = -1;
		maxIndexOnLine = -1;
		System.arraycopy( max, 0, currentPos, 0, n );
	}

	@Override
	public boolean hasNext()
	{
		return index < maxIndex;
	}

	@Override
	public void jumpFwd( final long steps )
	{
		index += steps;
		if ( index < 0 )
		{
			maxIndexOnLine = ( ( 1 + index ) / dimensions[ 0 ] ) * dimensions[ 0 ] - 1;
			final long size = maxIndex + 1;
			IntervalIndexer.indexToPositionWithOffset( size - ( -index % size ), dimensions, min, currentPos );
		}
		else
		{
			maxIndexOnLine = ( 1 + index / dimensions[ 0 ] ) * dimensions[ 0 ] - 1;
			IntervalIndexer.indexToPositionWithOffset( index, dimensions, min, currentPos );
		}
	}

	@Override
	public Neighborhood< T > next()
	{
		fwd();
		return get();
	}

	@Override
	public void remove()
	{
		// NB: no action.
	}

	@Override
	public HyperSphereNeighborhoodCursor< T > copy()
	{
		return new HyperSphereNeighborhoodCursor< T >( this );
	}

	@Override
	public HyperSphereNeighborhoodCursor< T > copyCursor()
	{
		return copy();
	}

}
