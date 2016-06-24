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

package net.imglib2.outofbounds;

import net.imglib2.AbstractLocalizable;
import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.util.Util;

/**
 * Coordinates out of image bounds are periodically repeated.
 * 
 * <pre>
 * Example:
 * 
 * width=4
 * 
 *                                  |<-inside->|
 * x:    -9 -8 -7 -6 -5 -4 -3 -2 -1  0  1  2  3  4  5  6  7  8  9
 * f(x):  3  0  1  2  3  0  1  2  3  0  1  2  3  0  1  2  3  0  1
 * </pre>
 * 
 * @param <T>
 * 
 * @author Stephan Saalfeld
 * @author Stephan Saalfeld (saalfeld@mpi-cbg.de)
 */
public class OutOfBoundsPeriodic< T > extends AbstractLocalizable implements OutOfBounds< T >
{
	final protected RandomAccess< T > outOfBoundsRandomAccess;

	/**
	 * Dimensions of the wrapped {@link RandomAccessible}.
	 */
	final protected long[] dimension;

	/**
	 * Minimum of the wrapped {@link RandomAccessible}.
	 */
	final protected long[] min;

	final protected long[] beforeMin;

	/**
	 * Maximum of the wrapped {@link RandomAccessible}.
	 */
	final protected long[] max;

	final protected long[] pastMax;

	final protected boolean[] dimIsOutOfBounds;

	protected boolean isOutOfBounds = false;

	public OutOfBoundsPeriodic( final OutOfBoundsPeriodic< T > outOfBounds )
	{
		super( outOfBounds.numDimensions() );
		dimension = new long[ n ];
		min = new long[ n ];
		beforeMin = new long[ n ];
		max = new long[ n ];
		pastMax = new long[ n ];
		dimIsOutOfBounds = new boolean[ n ];
		for ( int d = 0; d < n; ++d )
		{
			dimension[ d ] = outOfBounds.dimension[ d ];
			min[ d ] = outOfBounds.min[ d ];
			beforeMin[ d ] = outOfBounds.beforeMin[ d ];
			max[ d ] = outOfBounds.max[ d ];
			pastMax[ d ] = outOfBounds.pastMax[ d ];
			position[ d ] = outOfBounds.position[ d ];
			dimIsOutOfBounds[ d ] = outOfBounds.dimIsOutOfBounds[ d ];
		}

		outOfBoundsRandomAccess = outOfBounds.outOfBoundsRandomAccess.copyRandomAccess();
	}

	public < F extends Interval & RandomAccessible< T > > OutOfBoundsPeriodic( final F f )
	{
		super( f.numDimensions() );
		dimension = new long[ n ];
		f.dimensions( dimension );
		min = new long[ n ];
		f.min( min );
		max = new long[ n ];
		f.max( max );
		beforeMin = new long[ n ];
		pastMax = new long[ n ];
		for ( int d = 0; d < n; ++d )
		{
			beforeMin[ d ] = min[ d ] - 1;
			pastMax[ d ] = max[ d ] + 1;
		}
		dimIsOutOfBounds = new boolean[ n ];

		outOfBoundsRandomAccess = f.randomAccess();
	}

	final protected void checkOutOfBounds()
	{
		for ( int d = 0; d < n; ++d )
		{
			if ( dimIsOutOfBounds[ d ] )
			{
				isOutOfBounds = true;
				return;
			}
		}
		isOutOfBounds = false;
	}

	/* OutOfBounds */

	@Override
	public boolean isOutOfBounds()
	{
		return isOutOfBounds;
	}

	/* Sampler */

	@Override
	public T get()
	{
		return outOfBoundsRandomAccess.get();
	}

	@Override
	final public OutOfBoundsPeriodic< T > copy()
	{
		return new OutOfBoundsPeriodic< T >( this );
	}

	/* RandomAccess */

	@Override
	final public OutOfBoundsPeriodic< T > copyRandomAccess()
	{
		return copy();
	}

	/* Positionable */

	@Override
	final public void fwd( final int d )
	{
		final long p = ++position[ d ];
		if ( p == min[ d ] )
		{
			dimIsOutOfBounds[ d ] = false;
			checkOutOfBounds();
		}
		else if ( p == pastMax[ d ] )
			dimIsOutOfBounds[ d ] = isOutOfBounds = true;

		final long q = outOfBoundsRandomAccess.getLongPosition( d );
		if ( q == max[ d ] )
			outOfBoundsRandomAccess.setPosition( min[ d ], d );
		else
			outOfBoundsRandomAccess.fwd( d );
	}

	@Override
	final public void bck( final int d )
	{
		final long p = --position[ d ];
		if ( p == beforeMin[ d ] )
			dimIsOutOfBounds[ d ] = isOutOfBounds = true;
		else if ( p == max[ d ] )
		{
			dimIsOutOfBounds[ d ] = false;
			checkOutOfBounds();
		}

		final long q = outOfBoundsRandomAccess.getLongPosition( d );
		if ( q == min[ d ] )
			outOfBoundsRandomAccess.setPosition( max[ d ], d );
		else
			outOfBoundsRandomAccess.bck( d );
	}

	@Override
	final public void setPosition( final long position, final int d )
	{
		this.position[ d ] = position;
		final long minD = min[ d ];
		final long maxD = max[ d ];
		if ( position < minD )
		{
			outOfBoundsRandomAccess.setPosition( maxD - ( maxD - position ) % dimension[ d ], d );
			dimIsOutOfBounds[ d ] = isOutOfBounds = true;
		}
		else if ( position > maxD )
		{
			outOfBoundsRandomAccess.setPosition( minD + ( position - minD ) % dimension[ d ], d );
			dimIsOutOfBounds[ d ] = isOutOfBounds = true;
		}
		else
		{
			outOfBoundsRandomAccess.setPosition( position, d );
			if ( isOutOfBounds )
			{
				dimIsOutOfBounds[ d ] = false;
				checkOutOfBounds();
			}
		}
	}

	@Override
	public void move( final long distance, final int d )
	{
		setPosition( getLongPosition( d ) + distance, d );
	}

	@Override
	public void move( final int distance, final int d )
	{
		move( ( long ) distance, d );
	}

	@Override
	public void move( final Localizable localizable )
	{
		for ( int d = 0; d < n; ++d )
			move( localizable.getLongPosition( d ), d );
	}

	@Override
	public void move( final int[] distance )
	{
		for ( int d = 0; d < n; ++d )
			move( distance[ d ], d );
	}

	@Override
	public void move( final long[] distance )
	{
		for ( int d = 0; d < n; ++d )
			move( distance[ d ], d );
	}

	@Override
	public void setPosition( final int position, final int d )
	{
		setPosition( ( long ) position, d );
	}

	@Override
	public void setPosition( final Localizable localizable )
	{
		for ( int d = 0; d < n; ++d )
			setPosition( localizable.getLongPosition( d ), d );
	}

	@Override
	public void setPosition( final int[] position )
	{
		for ( int d = 0; d < position.length; ++d )
			setPosition( position[ d ], d );
	}

	@Override
	public void setPosition( final long[] position )
	{
		for ( int d = 0; d < position.length; ++d )
			setPosition( position[ d ], d );
	}

	/* Object */

	@Override
	public String toString()
	{
		return Util.printCoordinates( position ) + " = " + get();
	}
}
