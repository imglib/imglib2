/**
 * Copyright (c) 2009--2011, Stephan Preibisch & Stephan Saalfeld
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
package mpicbg.imglib.outofbounds;

import mpicbg.imglib.AbstractSampler;
import mpicbg.imglib.Bounded;
import mpicbg.imglib.Localizable;
import mpicbg.imglib.RealLocalizable;
import mpicbg.imglib.RealRandomAccess;

/**
 * @author Tobias Pietzsch
 *
 * @param <T>
 */
public final class RealOutOfBoundsRealRandomAccess< T > extends AbstractSampler< T > implements RealRandomAccess< T >, Bounded
{
	/**
	 *  performs the actual moves and generates/queries a Type
	 */
	final protected RealOutOfBounds< T > outOfBounds;

	/**
	 * @param n number of dimensions in the {@link RealRandomAccessible}.
	 * @param outOfBounds
	 */
	public RealOutOfBoundsRealRandomAccess( final int n, final RealOutOfBounds< T > outOfBounds )
	{
		super( n );
		this.outOfBounds = outOfBounds;
	}

	
	/* Bounded */
	
	@Override
	public boolean isOutOfBounds()
	{
		return outOfBounds.isOutOfBounds();
	}

	
	/* Sampler */
	
	@Override
	public T get()
	{
		return outOfBounds.get();
	}

	
	/* RealLocalizable */

	@Override
	final public void localize( final float[] position ){ outOfBounds.localize( position ); }
	
	@Override
	final public void localize( final double[] position ){ outOfBounds.localize( position ); }
	
	@Override
	final public double getDoublePosition( final int dim ){ return outOfBounds.getDoublePosition( dim ); }
	
	@Override
	final public float  getFloatPosition( final int dim ){ return outOfBounds.getFloatPosition( dim ); }


	/* RealPositionable */

	@Override
	public void move( float distance, int d ) { outOfBounds.move( distance, d ); }

	@Override
	public void move( double distance, int d ) { outOfBounds.move( distance, d ); }

	@Override
	public void move( RealLocalizable localizable ) { outOfBounds.move( localizable ); }

	@Override
	public void move( float[] distance ) { outOfBounds.move( distance ); }

	@Override
	public void move( double[] distance ) { outOfBounds.move( distance ); }

	@Override
	public void setPosition( RealLocalizable localizable ) { outOfBounds.setPosition( localizable ); }

	@Override
	public void setPosition( float[] position ) { outOfBounds.setPosition( position ); }

	@Override
	public void setPosition( double[] position ) { outOfBounds.setPosition( position ); }

	@Override
	public void setPosition( float position, int d ) { outOfBounds.setPosition( position, d ); }

	@Override
	public void setPosition( double position, int d ) { outOfBounds.setPosition( position, d ); }

	
	/* Positionable */

	@Override
	public void fwd( int d ) { outOfBounds.fwd( d ); }

	@Override
	public void bck( int d ) { outOfBounds.bck( d ); }

	@Override
	public void move( int distance, int d ) { outOfBounds.move( distance, d ); }

	@Override
	public void move( long distance, int d ) { outOfBounds.move( distance, d ); }

	@Override
	public void move( Localizable localizable ) { outOfBounds.move( localizable ); }

	@Override
	public void move( int[] distance ) { outOfBounds.move( distance ); }

	@Override
	public void move( long[] distance ) { outOfBounds.move( distance ); }

	@Override
	public void setPosition( Localizable localizable ) { outOfBounds.setPosition( localizable ); }

	@Override
	public void setPosition( int[] position ) { outOfBounds.setPosition( position ); }

	@Override
	public void setPosition( long[] position ) { outOfBounds.setPosition( position ); }

	@Override
	public void setPosition( int position, int d ) { outOfBounds.setPosition( position, d ); }

	@Override
	public void setPosition( long position, int d ) { outOfBounds.setPosition( position, d ); }
}
