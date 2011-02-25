/**
 * Copyright (c) 2009--2011, Stephan Saalfeld
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
 *
 */
package mpicbg.imglib.location.transform;

import mpicbg.imglib.Localizable;
import mpicbg.imglib.Positionable;
import mpicbg.imglib.RealLocalizable;
import mpicbg.imglib.RealPositionable;

/**
 * Links a {@link RealPositionable} with a {@link Positionable} by
 * transferring real coordinates to floor discrete coordinates.  For practical
 * useage, the floor operation is defined as the integer smaller than the real
 * value:
 * 
 * f = r < 0 ? (long)r - 1 : (long)r
 * 
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class RealPositionableFloorPositionable< LocalizablePositionable extends RealLocalizable & RealPositionable > implements RealPositionable
{
	final protected LocalizablePositionable source;
	final protected Positionable target;
	
	final protected int n;
	
	/* temporary floor position register */
	final protected long[] floor;
	
	/* temporary position register */
	final protected double[] position;
	
	public RealPositionableFloorPositionable( final LocalizablePositionable source, final Positionable target )
	{
		this.source = source;
		this.target = target;
		
		n = source.numDimensions();
		
		position = new double[ n ];
		floor = new long[ n ];
	}
	
	final static private long floor( final double r )
	{
		return r < 0 ? ( long )r - 1 : ( long )r;
	}
	
	final static private long floor( final float r )
	{
		return r < 0 ? ( long )r - 1 : ( long )r;
	}
	
	final static private void floor( final double[] r, final long[] f )
	{
		for ( int d = 0; d < r.length; ++d )
			f[ d ] = floor( r[ d ] );
	}
	
	final static private void floor( final float[] r, final long[] f )
	{
		for ( int d = 0; d < r.length; ++d )
			f[ d ] = floor( r[ d ] );
	}
	
	final static private void floor( final RealLocalizable r, final long[] f )
	{
		for ( int d = 0; d < f.length; ++d )
			f[ d ] = floor( r.getDoublePosition( d ) );
	}
	
	
	/* EuclideanSpace */
	
	@Override
	public int numDimensions(){ return source.numDimensions(); }

	
	/* RealPositionable */
	
	@Override
	public void move( final float distance, final int d )
	{
		source.move( distance, d );
		target.setPosition( floor( source.getDoublePosition( d ) ), d );
	}

	@Override
	public void move( final double distance, final int d )
	{
		source.move( distance, d );
		target.setPosition( floor( source.getDoublePosition( d ) ), d );
	}

	@Override
	public void move( final RealLocalizable localizable )
	{
		source.move( localizable );
		floor( localizable, floor );
		target.move( floor );
	}

	@Override
	public void move( final float[] distance )
	{
		source.move( distance );
		floor( distance, floor );
		target.move( floor );
	}

	@Override
	public void move( final double[] distance )
	{
		source.move( distance );
		floor( distance, floor );
		target.move( floor );
	}

	@Override
	public void setPosition( final RealLocalizable localizable )
	{
		source.setPosition( localizable );
		floor( localizable, floor );
		target.setPosition( floor );
	}

	@Override
	public void setPosition( final float[] pos )
	{
		source.setPosition( pos );
		floor( pos, floor );
		target.setPosition( floor );
	}

	@Override
	public void setPosition( final double[] pos )
	{
		source.setPosition( pos );
		floor( pos, floor );
		target.setPosition( floor );
	}

	@Override
	public void setPosition( final float position, final int d )
	{
		source.setPosition( position, d );
		target.setPosition( floor( position ), d );
	}

	@Override
	public void setPosition( final double position, final int d )
	{
		source.setPosition( position, d );
		target.setPosition( floor( position ), d );
	}

	
	/* Positionable */
	
	@Override
	public void bck( final int d )
	{
		source.bck( d );
		target.bck( d );
	}

	@Override
	public void fwd( final int d )
	{
		source.fwd( d );
		target.fwd( d );
	}

	@Override
	public void move( final int distance, final int d )
	{
		source.move( distance, d );
		target.move( distance, d );
	}

	@Override
	public void move( final long distance, final int d )
	{
		source.move( distance, d );
		target.move( distance, d );
	}

	@Override
	public void move( final Localizable localizable )
	{
		source.move( localizable );
		target.move( localizable );
	}

	@Override
	public void move( final int[] pos )
	{
		source.move( pos );
		target.move( pos );
	}

	@Override
	public void move( final long[] pos )
	{
		source.move( pos );
		target.move( pos );
	}
	
	@Override
	public void setPosition( final Localizable localizable )
	{
		source.setPosition( localizable );
		target.setPosition( localizable );
	}
	
	@Override
	public void setPosition( final int[] position )
	{
		source.setPosition( position );
		target.setPosition( position );
	}
	
	@Override
	public void setPosition( final long[] position )
	{
		source.setPosition( position );
		target.setPosition( position );
	}

	@Override
	public void setPosition( final int position, final int d )
	{
		source.setPosition( position, d );
		target.setPosition( position, d );
	}

	@Override
	public void setPosition( final long position, final int d )
	{
		source.setPosition( position, d );
		target.setPosition( position, d );
	}
}
