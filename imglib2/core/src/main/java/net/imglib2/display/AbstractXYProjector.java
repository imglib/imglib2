/**
 * Copyright (c) 2009--2012, ImgLib2 developers
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
package net.imglib2.display;

import net.imglib2.Localizable;
import net.imglib2.Positionable;
import net.imglib2.RandomAccessible;
import net.imglib2.converter.Converter;

/**
 * 
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
abstract public class AbstractXYProjector< A, B > implements Projector< A, B >, Positionable, Localizable
{
	final protected RandomAccessible< A > source;
	final protected Converter< A, B > converter;
	final protected long[] position;
	final long[] min;
	final long[] max;
	
	public AbstractXYProjector( final RandomAccessible< A > source, final Converter< A, B > converter )
	{
		this.source = source;
		this.converter = converter;
		
		// as this is an XY projector, we need at least two dimensions,
		// even if the source is one-dimensional
		position = new long[ Math.max( 2, source.numDimensions() ) ];
		min = new long[ position.length ];
		max = new long[ position.length ];
	}

	@Override
	public void bck( final int d )
	{
		position[ d ] -= 1;
	}

	@Override
	public void fwd( final int d )
	{
		position[ d ] += 1;
	}

	@Override
	public void move( final int distance, final int d )
	{
		position[ d ] += distance;
	}

	@Override
	public void move( final long distance, final int d )
	{
		position[ d ] += distance;
	}

	@Override
	public void move( final Localizable localizable )
	{
		for ( int d = 0; d < position.length; ++d )
			position[ d ] += localizable.getLongPosition( d );
	}

	@Override
	public void move( final int[] p )
	{
		for ( int d = 0; d < position.length; ++d )
			position[ d ] += p[ d ];
	}

	@Override
	public void move( final long[] p )
	{
		for ( int d = 0; d < position.length; ++d )
			position[ d ] += p[ d ];
	}

	@Override
	public void setPosition( final Localizable localizable )
	{
		for ( int d = 0; d < position.length; ++d )
			position[ d ] = localizable.getLongPosition( d );
	}

	@Override
	public void setPosition( final int[] p )
	{
		for ( int d = 0; d < position.length; ++d )
			position[ d ] = p[ d ];
	}

	@Override
	public void setPosition( final long[] p )
	{
		for ( int d = 0; d < position.length; ++d )
			position[ d ] = p[ d ];
	}

	@Override
	public void setPosition( final int p, final int d )
	{
		position[ d ] = p;
	}

	@Override
	public void setPosition( final long p, final int d )
	{
		position[ d ] = p;
	}

	@Override
	public int numDimensions()
	{
		return position.length;
	}

	@Override
	public int getIntPosition( final int d )
	{
		return ( int )position[ d ];
	}

	@Override
	public long getLongPosition( final int d )
	{
		return position[ d ];
	}

	@Override
	public void localize( final int[] p )
	{
		for ( int d = 0; d < p.length; ++d )
			p[ d ] = ( int )position[ d ];
	}

	@Override
	public void localize( final long[] p )
	{
		for ( int d = 0; d < p.length; ++d )
			p[ d ] = position[ d ];
	}

	@Override
	public double getDoublePosition( final int d )
	{
		return position[ d ];
	}

	@Override
	public float getFloatPosition( final int d )
	{
		return position[ d ];
	}

	@Override
	public void localize( final float[] p )
	{
		for ( int d = 0; d < p.length; ++d )
			p[ d ] = position[ d ];
	}

	@Override
	public void localize( final double[] p )
	{
		for ( int d = 0; d < p.length; ++d )
			p[ d ] = position[ d ];
	}
}
