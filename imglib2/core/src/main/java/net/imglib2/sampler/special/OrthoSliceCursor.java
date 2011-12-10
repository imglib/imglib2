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
package net.imglib2.sampler.special;

import java.awt.Image;

import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.Iterator;
import net.imglib2.Positionable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RealPositionable;
import net.imglib2.img.Img;
import net.imglib2.img.basictypeaccess.PlanarAccess;
import net.imglib2.type.Type;

/**
 * Generic {@link Iterator} for orthogonal 2d-slices.  This implementation
 * iterates row by row from top left to bottom right mapping <em>x</em> and
 * <em>y</em> to two arbitrary dimensions using a
 * {@link RandomAccess} provided either directly or through an
 * {@link Image}.  While, for most {@link Img Containers}, this is the
 * sufficient implementation, sometimes, a different iteration order is
 * required.  Such {@link Img Containers} are expected to provide their
 * own adapted implementation.
 *
 * @author Stephan Preibisch and Stephan Saalfeld
 */
public class OrthoSliceCursor< T extends Type< T > > implements Cursor< T >, Interval
{
	/* index of x and y dimensions */
	final protected Interval interval;
	final protected int x, y;
	final protected long w, h, maxX, maxY;
	protected boolean initialState;
	
	final protected RandomAccess< T > sampler;
	
	/*
	private static long[] intToLong( final int[] i )
	{
		final long[] l = new long[ i.length ];
		
		for ( int d = 0; d < i.length; ++d )
			l[ d ] = i[ d ];
		
		return l;
	}
	*/
	
	/**
	 * @param f The {@link RandomAccessible} and {@link Interval} object, such as an {@link Img}.
	 * @param x One of the two dimensions of the orthoslice plane.
	 * @param y The other dimension of the orthoslice plane.
	 * @param position The starting point for the sampler, that is, the offset to the first value to consider.
	 */
	public < F extends RandomAccessible< T > & Interval > OrthoSliceCursor( final F f, final int x, final int y, final long[] position )
	{
		interval = f;
		this.sampler = f.randomAccess();
		this.x = x;
		this.y = y;
		w = interval.dimension( x );
		h = interval.dimension( y );
		maxX = w - 1;
		maxY = h - 1;
		
		sampler.setPosition( position );
		reset();
	}

	protected OrthoSliceCursor( final OrthoSliceCursor< T > cursor )
	{
		this.interval = cursor.interval;
		this.sampler = cursor.sampler.copyRandomAccess();
		this.x = cursor.x;
		this.y = cursor.y;
		this.w = cursor.w;
		this.h = cursor.h;
		this.maxX = cursor.maxX;
		this.maxY = cursor.maxY;
		this.initialState = cursor.initialState;
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
	public int getIntPosition( final int dim )
	{
		return sampler.getIntPosition( dim );
	}

	@Override
	public long getLongPosition( final int dim )
	{
		return sampler.getLongPosition( dim );
	}

	@Override
	public void localize( final int[] position )
	{
		sampler.localize( position );
	}

	@Override
	public void localize( final long[] position )
	{
		sampler.localize( position );
	}

	@Override
	public double getDoublePosition( final int dim )
	{
		return sampler.getDoublePosition( dim );
	}

	@Override
	public float getFloatPosition( final int dim )
	{
		return sampler.getFloatPosition( dim );
	}

	@Override
	public String toString()
	{
		return sampler.toString();
	}

	@Override
	public void localize( final float[] position )
	{
		sampler.localize( position );	
	}

	@Override
	public void localize( final double[] position )
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
			
			if ( initialState )
				initialState = false;
			else
				sampler.fwd( y );
		}
		else
			sampler.fwd( x );
	}

	@Override
	public void jumpFwd( final long steps )
	{
		final long ySteps = steps / w;
		final long xSteps = steps - ySteps * w;
		sampler.move( ySteps, y );
		sampler.move( xSteps, x );
	}

	/**
	 * We {@link PlanarAccess} to the end of the line, a state that has to be
	 * checked anyways. In the fwd() call we then check for this special case
	 * if it was maybe the initialState and set it to (0,x) (0,y)
	 */
	@Override
	public void reset()
	{
		sampler.setPosition( maxX, x );
		sampler.setPosition( 0, y );
		initialState = true;
	}

	@Override
	public boolean hasNext()
	{
		// if we do not query for the initial state, hasNext is false if the 
		// size of the second dimension is only 1
		return sampler.getIntPosition( y ) < maxY || sampler.getIntPosition( x ) < maxX || initialState;
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
	public long min( final int d )
	{
		return interval.min( d );
	}

	@Override
	public void min( final long[] min )
	{
		interval.min( min );
	}

	@Override
	public void min( final Positionable min )
	{
		interval.min( min );
	}

	@Override
	public long max( final int d )
	{
		return interval.max( d );
	}

	@Override
	public void max( final long[] max )
	{
		interval.max( max );
	}

	@Override
	public void max( final Positionable max )
	{
		interval.max( max );
	}

	@Override
	public void dimensions( final long[] size )
	{
		interval.dimensions( size );
	}

	@Override
	public long dimension( final int d )
	{
		return interval.dimension( d );
	}

	@Override
	public double realMin( final int d )
	{
		return interval.realMax( d );
	}

	@Override
	public void realMin( final double[] min )
	{
		interval.realMax( min );
	}

	@Override
	public void realMin( final RealPositionable min )
	{
		interval.realMax( min );
	}

	@Override
	public double realMax( final int d )
	{
		return interval.realMax( d );
	}

	@Override
	public void realMax( final double[] max )
	{
		interval.realMax( max );
	}

	@Override
	public void realMax( final RealPositionable max )
	{
		interval.realMax( max );
	}

	@Override
	public OrthoSliceCursor< T > copy()
	{
		return new OrthoSliceCursor< T >( this );
	}

	@Override
	public OrthoSliceCursor< T > copyCursor()
	{
		return copy();
	}
}
