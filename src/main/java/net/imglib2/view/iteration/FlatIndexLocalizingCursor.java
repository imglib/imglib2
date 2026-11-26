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

package net.imglib2.view.iteration;

import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.util.IntervalIndexer;

/**
 * Like {@link FlatIndexCursor}, but keeps track of the position, which in flat
 * order only ever increments and carries, and therefore needs no division.
 *
 * @param <T>
 */
public class FlatIndexLocalizingCursor< T > extends FlatIndexCursor< T >
{
	private final long[] position;

	private final long[] max;

	/**
	 * @param sourceCursor
	 *            iterates the source elements of {@code interval}, in flat
	 *            order.
	 * @param interval
	 *            the interval of the view that is iterated.
	 */
	public FlatIndexLocalizingCursor( final Cursor< T > sourceCursor, final Interval interval )
	{
		super( sourceCursor, interval );
		position = new long[ n ];
		max = interval.maxAsLongArray();
		resetPosition();
	}

	private FlatIndexLocalizingCursor( final FlatIndexLocalizingCursor< T > cursor )
	{
		super( cursor );
		position = cursor.position.clone();
		max = cursor.max;
	}

	/**
	 * The position "before" flat index 0, so that the first fwd() lands on it.
	 */
	private void resetPosition()
	{
		System.arraycopy( min, 0, position, 0, n );
		position[ 0 ] = min[ 0 ] - 1;
	}

	@Override
	public void fwd()
	{
		super.fwd();
		if ( ++position[ 0 ] > max[ 0 ] )
		{
			position[ 0 ] = min[ 0 ];
			for ( int d = 1; d < n; ++d )
			{
				if ( ++position[ d ] <= max[ d ] )
					break;
				position[ d ] = min[ d ];
			}
		}
	}

	@Override
	public void jumpFwd( final long steps )
	{
		super.jumpFwd( steps );
		if ( index < 0 )
			resetPosition();
		else
			IntervalIndexer.indexToPositionWithOffset( index, dimensions, min, position );
	}

	@Override
	public void reset()
	{
		super.reset();
		resetPosition();
	}

	@Override
	public void localize( final long[] pos )
	{
		System.arraycopy( position, 0, pos, 0, n );
	}

	@Override
	public long getLongPosition( final int d )
	{
		return position[ d ];
	}

	@Override
	public FlatIndexLocalizingCursor< T > copy()
	{
		return new FlatIndexLocalizingCursor<>( this );
	}
}
