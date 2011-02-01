/**
 * Copyright (c) 2009--2010, Stephan Saalfeld
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  Neither the name of the Fiji project nor
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
package mpicbg.imglib.outofbounds;

import mpicbg.imglib.container.PositionableContainerSampler;
import mpicbg.imglib.type.Type;

/**
 * Coordinates out of image bounds are mirrored between boundary coordinates.
 * So boundary pixels are repeated.
 * 
 * <pre>
 * Example:
 * 
 * width=4
 * 
 *                                  |<-inside->|
 * x:    -9 -8 -7 -6 -5 -4 -3 -2 -1  0  1  2  3  4  5  6  7  8  9
 * f(x):  0  0  1  2  3  3  2  1  0  0  1  2  3  3  2  1  0  0  1
 * </pre>
 * 
 * @param <T>
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class OutOfBoundsMirrorDoubleBoundary< T extends Type< T > > extends AbstractOutOfBoundsMirror< T >
{
	OutOfBoundsMirrorDoubleBoundary( final PositionableContainerSampler< T > source )
	{
		this( source, source.getImage().createPositionableRasterSampler() );
	}
	
	OutOfBoundsMirrorDoubleBoundary(
			final PositionableContainerSampler< T > source,
			final PositionableContainerSampler< T > outOfBoundsPositionable )
	{
		super( source, outOfBoundsPositionable );
		
		for ( int i = 0; i < dimension.length; ++i )
			p[ i ] = 2 * dimension[ i ];
	}
	
	/* RasterPositionable */
	
	@Override
	final public void fwd( final int dim ) 
	{
		isOutOfBounds = ( ++position[ dim ] >= dimension[ dim ] );
		final int x = outOfBoundsPositionable.getIntPosition( dim );
		if ( inc[ dim ] )
		{
			if ( x + 1 == dimension[ dim ] )
			{
				inc[ dim ] = false;
				outOfBoundsPositionable.bck( dim );
			}
			else
				outOfBoundsPositionable.fwd( dim );
		}
		else
		{
			if ( x == 0 )
			{
				inc[ dim ] = true;
				outOfBoundsPositionable.fwd( dim  );
			}
			else
				outOfBoundsPositionable.bck( dim );
		}
	}
	
	@Override
	final public void bck( final int dim ) 
	{
		isOutOfBounds = ( --position[ dim ] < 0 );
		final int x = outOfBoundsPositionable.getIntPosition( dim );
		if ( inc[ dim ] )
		{
			if ( x == 0 )
			{
				inc[ dim ] = false;
				outOfBoundsPositionable.fwd( dim );
			}
			else
				outOfBoundsPositionable.bck( dim );
		}
		else
		{
			if ( x + 1 == dimension[ dim ] )
			{
				inc[ dim ] = true;
				outOfBoundsPositionable.bck( dim  );
			}
			else
				outOfBoundsPositionable.fwd( dim );
		}
	}
	
	@Override
	final public void setPosition( int position, final int dim )
	{
		this.position[ dim ] = position;
		final int x = this.p[ dim ];
		final int mod = dimension[ dim ];
		final boolean pos;
		if ( position < 0 )
		{
			isOutOfBounds = true;
			position = -position;
			pos = false;
		}
		else
			pos = true;	
		
		if ( position >= mod )
		{
			isOutOfBounds = true;
			if ( position <= x )
			{
				position = x - position;
				inc[ dim ] = !pos;
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
						inc[ dim ] = !pos;
					}
					else
						inc[ dim ] = pos;
				}
				catch ( ArithmeticException e ){ position = 0; }
			}
		}
		else
		{
			isOutOfBounds = false;
			inc[ dim ] = pos;
		}
		
		outOfBoundsPositionable.setPosition( position, dim );
	}
}
