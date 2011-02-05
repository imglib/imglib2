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
 */
package mpicbg.imglib.sampler.special;

import mpicbg.imglib.Iterator;
import mpicbg.imglib.container.Img;
import mpicbg.imglib.container.ImgCursor;
import mpicbg.imglib.container.ImgRandomAccess;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.Type;

/**
 * Generic {@link Iterator} for orthogonal 2d-slices.  This implementation
 * iterates row by row from top left to bottom right mapping <em>x</em> and
 * <em>y</em> to two arbitrary dimensions using a
 * {@link ImgRandomAccess} provided either directly or through an
 * {@link Image}.  While, for most {@link Img Containers}, this is the
 * sufficient implementation, sometimes, a different iteration order is
 * required.  Such {@link Img Containers} are expected to provide their
 * own adapted implementation.
 *
 * @author Stephan Preibisch and Stephan Saalfeld
 */
public class OrthoSliceIterator< T extends Type< T > > implements ImgCursor< T >
{
	/* index of x and y dimensions */
	final protected int x, y;
	final protected long w, h, maxX, maxY;
	
	final protected ImgRandomAccess< T > sampler;
	
	private static long[] intToLong( final int[] i )
	{
		final long[] l = new long[ i.length ];
		
		for ( int d = 0; d < i.length; ++d )
			l[ d ] = i[ d ];
		
		return l;
	}
	
	public OrthoSliceIterator( final Img< T > container, final int x, final int y, final long[] position )
	{
		this( container.randomAccess(), x, y, position );		
	}

	public OrthoSliceIterator( final Img< T > container, final int x, final int y, final int[] position )
	{
		this( container.randomAccess(), x, y, intToLong( position ) );		
	}

	public OrthoSliceIterator( final ImgRandomAccess< T > sampler, final int x, final int y, final long[] position )
	{
		this.sampler = sampler;
		this.x = x;
		this.y = y;
		w = sampler.getImg().dimension( x );
		h = sampler.getImg().dimension( y );
		maxX = w - 1;
		maxY = h - 1;
		
		sampler.setPosition( position );
		reset();
	}

	@Override
	public Img< T > getImg()
	{
		return sampler.getImg();
	}

	@Override
	@Deprecated
	public T getType()
	{
		return get();
	}

	@Override
	public T get()
	{
		return sampler.get();
	}

	@Override
	public int numDimensions()
	{
		return sampler.numDimensions();
	}

	@Override
	public int getIntPosition( int dim )
	{
		return sampler.getIntPosition( dim );
	}

	@Override
	public long getLongPosition( int dim )
	{
		return sampler.getLongPosition( dim );
	}

	@Override
	public void localize( int[] position )
	{
		sampler.localize( position );
	}

	@Override
	public void localize( long[] position )
	{
		sampler.localize( position );
	}

	@Override
	public double getDoublePosition( int dim )
	{
		return sampler.getDoublePosition( dim );
	}

	@Override
	public float getFloatPosition( int dim )
	{
		return sampler.getFloatPosition( dim );
	}

	@Override
	public String toString()
	{
		return sampler.toString();
	}

	@Override
	public void localize( float[] position )
	{
		sampler.localize( position );	
	}

	@Override
	public void localize( double[] position )
	{
		sampler.localize( position );
	}

	@Override
	public void fwd()
	{
		final int xi = sampler.getIntPosition( x );
		if ( xi == maxX )
		{
			sampler.setPosition( 0, x );
			sampler.fwd( y );
		}
		else
			sampler.fwd( x );
	}

	@Override
	public void jumpFwd( long steps )
	{
		final long ySteps = steps / w;
		final long xSteps = steps - ySteps * w;
		sampler.move( ySteps, y );
		sampler.move( xSteps, x );
	}

	@Override
	public void reset()
	{
		sampler.setPosition( -1, x );
		sampler.setPosition( 0, y );
	}

	@Override
	public boolean hasNext()
	{
		return sampler.getIntPosition( y ) < maxY || sampler.getIntPosition( x ) < maxX;
	}

	@Override
	public T next()
	{
		fwd();
		return sampler.get();
	}

	@Override
	public void remove() {}

	@Override
	public T create()
	{
		return sampler.create();
	}

	@Override
	public long min( final int d )
	{
		return sampler.min( d );
	}

	@Override
	public void min( final long[] min )
	{
		sampler.min( min );
	}

	@Override
	public long max( final int d )
	{
		return sampler.max( d );
	}

	@Override
	public void max( final long[] max )
	{
		sampler.max( max );
	}

	@Override
	public void dimensions( final long[] size )
	{
		sampler.dimensions( size );
	}

	@Override
	public long dimension( final int d )
	{
		return sampler.dimension( d );
	}

	@Override
	public double realMin( final int d )
	{
		return sampler.realMax( d );
	}

	@Override
	public void realMin( final double[] min )
	{
		sampler.realMax( min );
	}

	@Override
	public double realMax( final int d )
	{
		return sampler.realMax( d );
	}

	@Override
	public void realMax( final double[] max )
	{
		sampler.realMax( max );
	}
}
