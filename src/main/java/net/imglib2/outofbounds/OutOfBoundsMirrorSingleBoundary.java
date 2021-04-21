/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2021 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

import net.imglib2.Interval;
import net.imglib2.RandomAccessible;

/**
 * Coordinates out of image bounds are mirrored at boundary coordinates.
 * Boundary pixels are not repeated.  Note that this requires that all
 * dimensions of the source (F f) must be &gt; 1.
 *
 * <pre>
 * {@code
 * Example:
 *
 * width=4
 *
 *                                  |<-inside->|
 * x:    -9 -8 -7 -6 -5 -4 -3 -2 -1  0  1  2  3  4  5  6  7  8  9
 * f(x):  3  2  1  0  1  2  3  2  1  0  1  2  3  2  1  0  1  2  3
 * }
 * </pre>
 *
 * @param <T>
 *
 * @author Stephan Saalfeld
 */
public class OutOfBoundsMirrorSingleBoundary< T > extends AbstractOutOfBoundsMirror< T >
{
	public OutOfBoundsMirrorSingleBoundary( final OutOfBoundsMirrorSingleBoundary< T > outOfBounds )
	{
		super( outOfBounds );
	}

	public < F extends Interval & RandomAccessible< T > > OutOfBoundsMirrorSingleBoundary( final F f )
	{
		super( f );

		for ( int i = 0; i < dimension.length; ++i )
			p[ i ] = 2 * dimension[ i ] - 2;
	}

	/* Sampler */

	@Override
	public OutOfBoundsMirrorSingleBoundary< T > copy()
	{
		return new OutOfBoundsMirrorSingleBoundary< T >( this );
	}

	/* RandomAccess */

	@Override
	public OutOfBoundsMirrorSingleBoundary< T > copyRandomAccess()
	{
		return copy();
	}

	/* Positionable */

	@Override
	final public void fwd( final int d )
	{
		final long x = ++zeroMinPos[ d ];
		if ( x == 0 )
		{
			dimIsOutOfBounds[ d ] = false;
			if ( isOutOfBounds )
				checkOutOfBounds();
		}
		else if ( x == dimension[ d ] )
			dimIsOutOfBounds[ d ] = isOutOfBounds = true;

		final long y = outOfBoundsRandomAccess.getLongPosition( d ) - min[ d ];
		if ( inc[ d ] )
		{
			if ( y + 1 == dimension[ d ] )
			{
				inc[ d ] = false;
				outOfBoundsRandomAccess.bck( d );
			}
			else
				outOfBoundsRandomAccess.fwd( d );
		}
		else
		{
			if ( y == 0 )
			{
				inc[ d ] = true;
				outOfBoundsRandomAccess.fwd( d );
			}
			else
				outOfBoundsRandomAccess.bck( d );
		}
	}

	@Override
	final public void bck( final int d )
	{
		final long x = zeroMinPos[ d ]--;
		if ( x == 0 )
			dimIsOutOfBounds[ d ] = isOutOfBounds = true;
		else if ( x == dimension[ d ] )
		{
			dimIsOutOfBounds[ d ] = false;
			if ( isOutOfBounds )
				checkOutOfBounds();
		}

		final long y = outOfBoundsRandomAccess.getLongPosition( d ) - min[ d ];
		if ( inc[ d ] )
		{
			if ( y == 0 )
			{
				inc[ d ] = false;
				outOfBoundsRandomAccess.fwd( d );
			}
			else
				outOfBoundsRandomAccess.bck( d );
		}
		else
		{
			if ( y + 1 == dimension[ d ] )
			{
				inc[ d ] = true;
				outOfBoundsRandomAccess.bck( d );
			}
			else
				outOfBoundsRandomAccess.fwd( d );
		}
	}

	@Override
	final public void setPosition( long position, final int d )
	{
		position -= min[ d ];
		this.zeroMinPos[ d ] = position;
		final long mod = dimension[ d ];
		final boolean pos;
		if ( position < 0 )
		{
			dimIsOutOfBounds[ d ] = isOutOfBounds = true;
			position = -position;
			pos = false;
		}
		else
			pos = true;

		if ( position >= mod )
		{
			dimIsOutOfBounds[ d ] = isOutOfBounds = true;
			final long x = this.p[ d ];

			if ( position <= x )
			{
				position = x - position;
				inc[ d ] = !pos;
			}
			else
			{
				/* catches mod == 1 to no additional cost */
				try
				{
					position %= x;
					if ( position >= mod )
					{
						position = x - position;
						inc[ d ] = !pos;
					}
					else
						inc[ d ] = pos;
				}
				catch ( final ArithmeticException e )
				{
					position = 0;
				}
			}
		}
		else
		{
			if ( pos )
			{
				dimIsOutOfBounds[ d ] = false;
				if ( isOutOfBounds )
					checkOutOfBounds();
			}

			inc[ d ] = pos;
		}

		outOfBoundsRandomAccess.setPosition( position += min[ d ], d );
	}
}
