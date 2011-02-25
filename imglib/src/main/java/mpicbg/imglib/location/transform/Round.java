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
 *
 */
package mpicbg.imglib.location.transform;

import mpicbg.imglib.Localizable;
import mpicbg.imglib.Positionable;
import mpicbg.imglib.RealLocalizable;
import mpicbg.imglib.RealPositionable;

/**
 * A {@link RealPositionable} that drives a {@link Positionable} to its
 * round discrete coordinates:
 * 
 * f = r < 0 ? (long)( r - 0.5 ) : (long)( r + 0.5 )
 * 
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class Round< LocalizablePositionable extends Localizable & Positionable > implements RealPositionable, RealLocalizable
{
	final protected LocalizablePositionable target;
	
	final private int n;
	
	/* current position, required for relative movement */
	final private double[] position;
	
	/* current round for temporary storage, this field does not necessarily contain the actual floor position! */
	final protected long[] round;
	
	
	public Round( final LocalizablePositionable target )
	{
		this.target = target;
		
		n = target.numDimensions();
		
		position = new double[ n ];
		round = new long[ n ];
	}
	
	public Round( final RealLocalizable origin, final LocalizablePositionable target )
	{
		this( target );
		
		origin.localize( position );
		for ( int d = 0; d < n; ++d )
			round[ d ] = round( position[ d ] );
		target.setPosition( round );
	}
	
	final static private long round( final double r )
	{
		return r < 0 ? ( long )( r - 0.5 ) : ( long )( r + 0.5 );
	}
	
	final static private long round( final float r )
	{
		return r < 0 ? ( long )( r - 0.5f ) : ( long )( r + 0.5f );
	}
	
	
	/* EuclideanSpace */
	
	@Override
	public int numDimensions(){ return n; }

	
	/* RealLocalizable */

	@Override
	public double getDoublePosition( final int d )
	{
		return position[ d ];
	}

	@Override
	public float getFloatPosition( final int d )
	{
		return ( float )position[ d ];
	}

	@Override
	public String toString()
	{
		final StringBuffer pos = new StringBuffer( "(" );
		pos.append( position[ 0 ] );

		for ( int d = 1; d < n; d++ )
			pos.append( ", " ).append( position[ d ] );

		pos.append( ")" );

		return pos.toString();
	}

	@Override
	public void localize( final float[] pos )
	{
		for ( int d = 0; d < pos.length; ++d )
			pos[ d ] = ( float )position[ d ];
	}

	@Override
	public void localize( final double[] pos )
	{
		for ( int d = 0; d < pos.length; ++d )
			pos[ d ] = position[ d ];
	}
	

	/* RealPositionable */
	
	@Override
	public void move( final float distance, final int d )
	{
		final double realPosition = position[ d ] + distance;
		final long roundPosition = round( realPosition );
		position[ d ] = realPosition;
		final long roundDistance = roundPosition - target.getIntPosition( d );
		if ( roundDistance == 0 )
			return;
		else
			target.move( roundDistance, d );
	}

	@Override
	public void move( final double distance, final int d )
	{
		final double realPosition = position[ d ] + distance;
		final long roundPosition = round( realPosition );
		position[ d ] = realPosition;
		final long roundDistance = roundPosition - target.getIntPosition( d );
		if ( roundDistance == 0 )
			return;
		else
			target.move( roundDistance, d );
	}

	@Override
	public void move( final RealLocalizable localizable )
	{
		for ( int d = 0; d < n; ++d )
		{
			final double realPosition = position[ d ] + localizable.getDoublePosition( d );
			final long floorPosition = round( realPosition );
			position[ d ] = realPosition;
			round[ d ] = floorPosition - target.getLongPosition( d );
		}
		target.move( round );
	}

	@Override
	public void move( final float[] distance )
	{
		for ( int d = 0; d < n; ++d )
		{
			final double realPosition = position[ d ] + distance[ d ];
			final long floorPosition = round( realPosition );
			position[ d ] = realPosition;
			round[ d ] = floorPosition - target.getLongPosition( d );
		}
		target.move( round );
	}

	@Override
	public void move( final double[] distance )
	{
		for ( int d = 0; d < n; ++d )
		{
			final double realPosition = position[ d ] + distance[ d ];
			final long floorPosition = round( realPosition );
			position[ d ] = realPosition;
			round[ d ] = floorPosition - target.getLongPosition( d );
		}
		target.move( round );
	}

	@Override
	public void setPosition( final RealLocalizable localizable )
	{
		for ( int d = 0; d < n; ++d )
		{
			final double realPosition = localizable.getDoublePosition( d );
			position[ d ] = realPosition;
			round[ d ] = round( realPosition );
		}
		target.setPosition( round );
	}

	@Override
	public void setPosition( final float[] pos )
	{
		for ( int d = 0; d < n; ++d )
		{
			final float realPosition = pos[ d ];
			position[ d ] = realPosition;
			round[ d ] = round( realPosition );
		}
		target.setPosition( round );
	}

	@Override
	public void setPosition( final double[] pos )
	{
		for ( int d = 0; d < n; ++d )
		{
			final double realPosition = pos[ d ];
			position[ d ] = realPosition;
			round[ d ] = round( realPosition );
		}
		target.setPosition( round );
	}

	@Override
	public void setPosition( final float position, final int d )
	{
		this.position[ d ] = position;
		target.setPosition( round( position ), d );
	}

	@Override
	public void setPosition( final double position, final int d )
	{
		this.position[ d ] = position;
		target.setPosition( round( position ), d );
	}

	
	/* Positionable */
	
	@Override
	public void bck( final int d )
	{
		position[ d ] -= 1;
		target.bck( d );
	}

	@Override
	public void fwd( final int d )
	{
		position[ d ] += 1;
		target.fwd( d );
	}

	@Override
	public void move( final int distance, final int dim )
	{
		position[ dim ] += distance;
		target.move( distance, dim );
	}

	@Override
	public void move( final long distance, final int dim )
	{
		position[ dim ] += distance;
		target.move( distance, dim );
	}

	@Override
	public void move( final Localizable localizable )
	{
		for ( int d = 0; d < n; ++d )
			position[ d ] += localizable.getDoublePosition( d ); 
			
		target.move( localizable );
	}

	@Override
	public void move( final int[] distance )
	{
		for ( int d = 0; d < n; ++d )
			position[ d ] += distance[ d ]; 
			
		target.move( distance );
	}

	@Override
	public void move( final long[] distance )
	{
		for ( int d = 0; d < n; ++d )
			position[ d ] += distance[ d ]; 
			
		target.move( distance );
	}
	
	@Override
	public void setPosition( final Localizable localizable )
	{
		localizable.localize( position );
		target.setPosition( localizable );
	}
	
	@Override
	public void setPosition( final int[] position )
	{
		for ( int d = 0; d < n; ++d )
			this.position[ d ] = position[ d ];
		
		target.setPosition( position );
	}
	
	@Override
	public void setPosition( final long[] position )
	{
		for ( int d = 0; d < n; ++d )
			this.position[ d ] = position[ d ];
		
		target.setPosition( position );
	}

	@Override
	public void setPosition( final int position, final int d )
	{
		this.position[ d ] = position;
		target.setPosition( position, d );
	}

	@Override
	public void setPosition( final long position, final int d )
	{
		this.position[ d ] = position;
		target.setPosition( position, d );
	}
}
