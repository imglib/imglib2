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
package net.imglib2.img.list;

import java.util.ArrayList;

import net.imglib2.AbstractLocalizable;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.Sampler;

/**
 *
 * @param <T>
 *
 * @author Stephan Preibisch and Stephan Saalfeld
 */
public class ListRandomAccess< T > extends AbstractLocalizable implements RandomAccess< T >
{
	private int i;

	private final ArrayList< T > pixels;
	private final ListImg< T > container;

	public ListRandomAccess( final ListRandomAccess< T > randomAccess )
	{
		super( randomAccess.numDimensions() );

		container = randomAccess.container;
		this.pixels = randomAccess.pixels;

		for ( int d = 0; d < n; ++d )
			position[ d ] = randomAccess.position[ d ];

		i = randomAccess.i;
	}

	public ListRandomAccess( final ListImg< T > container )
	{
		super( container.numDimensions() );

		this.container = container;
		this.pixels = container.pixels;

		i = 0;
	}

	@Override
	public T get()
	{
		return pixels.get( i );
	}

	public void set( final T t )
	{
		pixels.set( i, t );
	}

	@Override
	public void fwd( final int dim )
	{
		i += container.step[ dim ];
		++position[ dim ];
	}

	@Override
	public void bck( final int dim )
	{
		i -= container.step[ dim ];
		--position[ dim ];
	}

	@Override
	public void move( final int distance, final int d )
	{
		i += container.step[ d ] * distance;
		position[ d ] += distance;
	}

	@Override
	public void move( final long distance, final int d )
	{
		move( ( int ) distance, d );
	}

	@Override
	public void move( final Localizable localizable )
	{
		for ( int d = 0; d < n; ++d )
			move( localizable.getIntPosition( d ), d );
	}

	@Override
	public void move( final int[] distance )
	{
		for ( int d = 0; d < n; ++d )
			move( distance[ d ], d );
	}

	@Override
	public void move( final long[] distance )
	{
		for ( int d = 0; d < n; ++d )
			move( ( int ) distance[ d ], d );
	}

	/* (non-Javadoc)
	 * @see net.imglib2.Positionable#setPosition(net.imglib2.Localizable)
	 */
	@Override
	public void setPosition( final Localizable localizable )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setPosition( final int[] position )
	{
		i = position[ 0 ];
		this.position[ 0 ] = i;
		for ( int d = 1; d < n; ++d )
		{
			final int p = position[ d ];
			i += p * container.step[ d ];
			this.position[ d ] = p;
		}
	}

	@Override
	public void setPosition( final long[] position )
	{
		i = ( int ) position[ 0 ];
		this.position[ 0 ] = i;
		for ( int d = 1; d < n; ++d )
		{
			final int p = ( int ) position[ d ];
			i += p * container.step[ d ];
			this.position[ d ] = p;
		}
	}

	/* (non-Javadoc)
	 * @see net.imglib2.Positionable#setPosition(int, int)
	 */
	@Override
	public void setPosition( final int position, final int d )
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.imglib2.Positionable#setPosition(long, int)
	 */
	@Override
	public void setPosition( final long position, final int d )
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.imglib2.Sampler#copy()
	 */
	@Override
	public Sampler< T > copy()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.imglib2.RandomAccess#copyRandomAccess()
	 */
	@Override
	public RandomAccess< T > copyRandomAccess()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	@Override
	public void setPosition( final int position, final int dim )
	{
		this.position[ dim ] = position;

		i = container.getPos( this.position );
	}

	@Override
	public void setPosition( final long position, final int dim )
	{
		setPosition( ( int )position, dim );
	}

	@Override
	public ListRandomAccess< T > copy()
	{
		return new ListRandomAccess< T >( this );
	}

	@Override
	public ListRandomAccess< T > copyRandomAccess()
	{
		return copy();
	}
	*/
}
