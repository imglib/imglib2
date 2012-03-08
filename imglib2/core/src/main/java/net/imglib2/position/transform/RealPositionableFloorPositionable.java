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
package net.imglib2.position.transform;

import net.imglib2.Localizable;
import net.imglib2.Positionable;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPositionable;

/**
 * Moves a {@link RealLocalizable} & {@link RealPositionable} and a
 * {@link Positionable} in synchrony.  The position of the latter is at the
 * floor coordinates of the former.  For practical useage, the floor operation
 * is defined as the integer smaller than the real value:
 * 
 * f = r < 0 ? (long)r - 1 : (long)r
 * 
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class RealPositionableFloorPositionable< P extends RealLocalizable & RealPositionable > implements RealPositionable, RealLocalizable
{
	final protected P source;
	final protected Positionable target;
	
	final protected int n;
	
	/* temporary floor position register */
	final protected long[] floor;
	
	public RealPositionableFloorPositionable( final P source, final Positionable target )
	{
		this.source = source;
		this.target = target;
		
		n = source.numDimensions();
		
		floor = new long[ n ];
	}
	
	final static protected long floor( final double r )
	{
		return r < 0 ? ( long )r - 1 : ( long )r;
	}
	
	final static protected long floor( final float r )
	{
		return r < 0 ? ( long )r - 1 : ( long )r;
	}
	
	final static protected void floor( final double[] r, final long[] f )
	{
		for ( int d = 0; d < r.length; ++d )
			f[ d ] = floor( r[ d ] );
	}
	
	final static protected void floor( final float[] r, final long[] f )
	{
		for ( int d = 0; d < r.length; ++d )
			f[ d ] = floor( r[ d ] );
	}
	
	final static protected void floor( final RealLocalizable r, final long[] f )
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
		floor( source, floor );
		target.setPosition( floor );
	}

	@Override
	public void move( final float[] distance )
	{
		source.move( distance );
		floor( source, floor );
		target.setPosition( floor );
	}

	@Override
	public void move( final double[] distance )
	{
		source.move( distance );
		floor( source, floor );
		target.setPosition( floor );
	}

	@Override
	public void setPosition( final RealLocalizable localizable )
	{
		source.setPosition( localizable );
		floor( localizable, floor );
		target.setPosition( floor );
	}

	@Override
	public void setPosition( final float[] position )
	{
		source.setPosition( position );
		floor( position, floor );
		target.setPosition( floor );
	}

	@Override
	public void setPosition( final double[] position )
	{
		source.setPosition( position );
		floor( position, floor );
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
	public void move( final int[] position )
	{
		source.move( position );
		target.move( position );
	}

	@Override
	public void move( final long[] position )
	{
		source.move( position );
		target.move( position );
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
	
	
	/* RealLocalizable */

	@Override
	public double getDoublePosition( final int d )
	{
		return source.getDoublePosition( d );
	}

	@Override
	public float getFloatPosition( final int d )
	{
		return source.getFloatPosition( d );
	}

	@Override
	public void localize( final float[] position )
	{
		source.localize( position );
	}

	@Override
	public void localize( final double[] position )
	{
		source.localize( position );
	}
}
