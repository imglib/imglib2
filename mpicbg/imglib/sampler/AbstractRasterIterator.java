/**
 * Copyright (c) 2009--2010, Stephan Preibisch & Stephan Saalfeld
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
 * @author Stephan Preibisch & Stephan Saalfeld
 */
package mpicbg.imglib.sampler;

import java.util.Iterator;

import mpicbg.imglib.algorithm.math.MathLib;
import mpicbg.imglib.container.Container;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.location.RasterLocalizable;
import mpicbg.imglib.type.Type;

/**
 * Generic implementation of {@link Iterator} mapping to abstract {@link #fwd()} and
 * {@link #type()}.  
 * 
 * @author Stephan Preibisch and Stephan Saalfeld
 * 
 * @param <T>
 */
public abstract class AbstractRasterIterator< T extends Type< T > > extends AbstractRasterSampler< T > implements RasterIterator< T >
{
	final private long[] position;
	
	public AbstractRasterIterator( final Container< T > container, final Image< T > image )
	{
		super( container, image );
		position = new long[ numDimensions ];
	}

	@Override
	public void remove()
	{}

	@Override
	public T next()
	{
		fwd();
		return type();
	}

	@Override
	public void jumpFwd( final long steps )
	{
		for ( long j = 0; j < steps; ++j )
			fwd();
	}

	/* RasterLocalizable */

	@Override
	public void localize( float[] position )
	{
		localize( this.position );
		for ( int d = 0; d < numDimensions; d++ )
			position[ d ] = this.position[ d ];
	}

	@Override
	public void localize( double[] position )
	{
		localize( this.position );
		for ( int d = 0; d < numDimensions; d++ )
			position[ d ] = this.position[ d ];
	}

	@Override
	public void localize( int[] position )
	{
		localize( this.position );
		for ( int d = 0; d < numDimensions; d++ )
			position[ d ] = ( int ) this.position[ d ];
	}

	@Override
	public float getFloatPosition( final int d )
	{
		return getLongPosition( d );
	}

	@Override
	public double getDoublePosition( final int d )
	{
		return getLongPosition( d );
	}

	@Override
	public int getIntPosition( final int d )
	{
		return ( int ) getLongPosition( d );
	}

	@Override
	public String getLocationAsString()
	{
		final int[] position = new int[ numDimensions ];
		localize( position );
		return MathLib.printCoordinates( position );
	}
}
