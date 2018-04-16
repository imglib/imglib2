/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2016 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package net.imglib2.img.sparse;

import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.img.sparse.NtreeImg.PositionProvider;
import net.imglib2.type.NativeType;
import net.imglib2.util.Util;

/**
 * @author Tobias Pietzsch
 * 
 */
public final class NtreeRandomAccess< T extends NativeType< T > > implements PositionProvider, RandomAccess< T >
{
	private final NtreeImg< T, ? > img;

	private final T type;

	private final int n;

	private final long[] position;

	public NtreeRandomAccess( final NtreeImg< T, ? > img )
	{

		this.n = img.numDimensions();
		this.position = new long[ img.numDimensions() ];
		this.img = img;
		this.type = img.createLinkedType();

		for ( int d = 0; d < n; d++ )
			position[ d ] = 0;

		type.updateContainer( this );
	}

	private NtreeRandomAccess( final NtreeRandomAccess< T > randomAccess )
	{
		this.n = randomAccess.numDimensions();
		this.position = new long[ randomAccess.numDimensions() ];
		this.img = randomAccess.img;
		this.type = img.createLinkedType();

		for ( int d = 0; d < n; d++ )
			position[ d ] = randomAccess.position[ d ];

		type.updateContainer( this );
	}

	@Override
	public void fwd( final int d )
	{
		++position[ d ];
	}

	@Override
	public void bck( final int d )
	{
		--position[ d ];
	}

	@Override
	public void move( final long distance, final int d )
	{
		position[ d ] += distance;
	}

	@Override
	public void setPosition( final int[] pos )
	{
		for ( int d = 0; d < n; ++d )
			position[ d ] = pos[ d ];
	}

	@Override
	public void setPosition( final long[] pos )
	{
		for ( int d = 0; d < n; ++d )
			position[ d ] = pos[ d ];
	}

	@Override
	public void setPosition( final long pos, final int d )
	{
		position[ d ] = pos;
	}

	@Override
	public T get()
	{
		return type;
	}

	@Override
	public NtreeRandomAccess< T > copy()
	{
		return new NtreeRandomAccess<>( this );
	}

	@Override
	public NtreeRandomAccess< T > copyRandomAccess()
	{
		return copy();
	}

	@Override
	public long[] getPosition()
	{
		return position;
	}

	@Override
	public void move( final int distance, final int dim )
	{
		move( ( long ) distance, dim );
	}

	@Override
	public void setPosition( final int position, final int dim )
	{
		setPosition( ( long ) position, dim );
	}

	@Override
	public void move( final int[] distance )
	{
		for ( int d = 0; d < n; ++d )
		{
			final long dist = distance[ d ];

			if ( dist != 0 )
				move( dist, d );
		}
	}

	@Override
	public void move( final long[] distance )
	{
		for ( int d = 0; d < n; ++d )
		{
			final long dist = distance[ d ];

			if ( dist != 0 )
				move( dist, d );
		}
	}

	@Override
	public void move( final Localizable localizable )
	{
		for ( int d = 0; d < n; ++d )
		{
			final long dist = localizable.getLongPosition( d );

			if ( dist != 0 )
				move( dist, d );
		}
	}

	@Override
	public void setPosition( final Localizable localizable )
	{
		for ( int d = 0; d < n; ++d )
		{
			setPosition( localizable.getLongPosition( d ), d );
		}
	}

	@Override
	public void localize( final float[] pos )
	{
		for ( int d = 0; d < n; d++ )
			pos[ d ] = this.position[ d ];
	}

	@Override
	public void localize( final double[] pos )
	{
		for ( int d = 0; d < n; d++ )
			pos[ d ] = this.position[ d ];
	}

	@Override
	public void localize( final int[] pos )
	{
		for ( int d = 0; d < n; d++ )
			pos[ d ] = ( int ) this.position[ d ];
	}

	@Override
	public void localize( final long[] pos )
	{
		for ( int d = 0; d < n; d++ )
			pos[ d ] = this.position[ d ];
	}

	@Override
	public float getFloatPosition( final int dim )
	{
		return position[ dim ];
	}

	@Override
	public double getDoublePosition( final int dim )
	{
		return position[ dim ];
	}

	@Override
	public int getIntPosition( final int dim )
	{
		return ( int ) position[ dim ];
	}

	@Override
	public long getLongPosition( final int dim )
	{
		return position[ dim ];
	}

	@Override
	public String toString()
	{
		return Util.printCoordinates( position ) + " = " + get();
	}

	@Override
	public int numDimensions()
	{
		return n;
	}
}
