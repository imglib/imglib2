/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2024 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package net.imglib2.img.array;

import net.imglib2.AbstractLocalizableInt;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.type.Index;
import net.imglib2.type.NativeType;

/**
 * {@link RandomAccess} on an {@link ArrayImg}.
 *
 * @param <T>
 *
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 * @author Tobias Pietzsch
 */
public class ArrayRandomAccess< T extends NativeType< T > > extends AbstractLocalizableInt implements RandomAccess< T >
{
	protected final T type;

	private final Index typeIndex;

	final ArrayImg< T, ? > img;

	protected ArrayRandomAccess( final ArrayRandomAccess< T > randomAccess )
	{
		super( randomAccess.numDimensions() );

		this.img = randomAccess.img;
		this.type = img.createLinkedType();
		this.typeIndex = type.index();

		typeIndex.set( 0 );
		for ( int d = 0; d < n; d++ )
		{
			position[ d ] = randomAccess.position[ d ];
			typeIndex.inc( position[ d ] * img.steps[ d ] );
		}

		type.updateContainer( this );
	}

	public ArrayRandomAccess( final ArrayImg< T, ? > container )
	{
		super( container.numDimensions() );

		this.img = container;
		this.type = container.createLinkedType();
		this.typeIndex = type.index();

		typeIndex.set( 0 );
		for ( int d = 0; d < n; d++ )
			position[ d ] = 0;

		type.updateContainer( this );
	}

	@Override
	public T get()
	{
		return type;
	}

	@Override
	public T getType()
	{
		return type;
	}

	@Override
	public void fwd( final int d )
	{
		typeIndex.inc( img.steps[ d ] );
		++position[ d ];
	}

	@Override
	public void bck( final int d )
	{
		typeIndex.dec( img.steps[ d ] );
		--position[ d ];
	}

	@Override
	public void move( final int distance, final int d )
	{
		typeIndex.inc( img.steps[ d ] * distance );
		position[ d ] += distance;
	}

	@Override
	public void move( final long distance, final int d )
	{
		typeIndex.inc( img.steps[ d ] * ( int ) distance );
		position[ d ] += distance;
	}

	@Override
	public void move( final Localizable localizable )
	{
		int move = 0;
		for ( int d = 0; d < n; ++d )
		{
			final int distance = localizable.getIntPosition( d );
			position[ d ] += distance;
			move += distance * img.steps[ d ];
		}
		typeIndex.inc( move );
	}

	@Override
	public void move( final int[] distance )
	{
		int move = 0;
		for ( int d = 0; d < n; ++d )
		{
			position[ d ] += distance[ d ];
			move += distance[ d ] * img.steps[ d ];
		}
		typeIndex.inc( move );
	}

	@Override
	public void move( final long[] distance )
	{
		int move = 0;
		for ( int d = 0; d < n; ++d )
		{
			position[ d ] += distance[ d ];
			move += distance[ d ] * img.steps[ d ];
		}
		typeIndex.inc( move );
	}

	@Override
	public void setPosition( final Localizable localizable )
	{
		int i = 0;
		for ( int d = 0; d < n; ++d )
		{
			position[ d ] = localizable.getIntPosition( d );
			i += position[ d ] * img.steps[ d ];
		}
		typeIndex.set( i );
	}

	@Override
	public void setPosition( final int[] pos )
	{
		int i = 0;
		for ( int d = 0; d < n; ++d )
		{
			position[ d ] = pos[ d ];
			i += pos[ d ] * img.steps[ d ];
		}
		typeIndex.set( i );
	}

	@Override
	public void setPosition( final long[] pos )
	{
		int i = 0;
		for ( int d = 0; d < n; ++d )
		{
			final int p = ( int ) pos[ d ];
			position[ d ] = p;
			i += p * img.steps[ d ];
		}
		typeIndex.set( i );
	}

	@Override
	public void setPosition( final int pos, final int d )
	{
		typeIndex.inc( ( pos - position[ d ] ) * img.steps[ d ] );
		position[ d ] = pos;
	}

	@Override
	public void setPosition( final long pos, final int d )
	{
		typeIndex.inc( ( ( int ) pos - position[ d ] ) * img.steps[ d ] );
		position[ d ] = ( int ) pos;
	}

	@Override
	public ArrayRandomAccess< T > copy()
	{
		return new ArrayRandomAccess< T >( this );
	}

	/* Special methods for access in one-dimensional arrays only */

	/**
	 * Moves one step forward in dimension 0
	 */
	public void fwdDim0()
	{
		typeIndex.inc();
		++position[ 0 ];
	}

	/**
	 * Moves one step backward in dimension 0
	 */
	public void bckDim0()
	{
		typeIndex.dec();
		--position[ 0 ];
	}

	/**
	 * Moves n steps in dimension 0
	 *
	 * @param distance
	 *            - how many steps (positive or negative)
	 */
	public void moveDim0( final int distance )
	{
		typeIndex.inc( distance );
		position[ 0 ] += distance;
	}

	/**
	 * Moves n steps in dimension 0
	 *
	 * @param distance
	 *            - how many steps (positive or negative)
	 */
	public void move( final long distance )
	{
		typeIndex.inc( ( int ) distance );
		position[ 0 ] += distance;
	}

	/**
	 * Sets the {@link ArrayRandomAccess} to a certain position in dimension 0
	 *
	 * Careful: it assumes that it is only a one-dimensional image, all other
	 * dimensions would be set to zero (this saves one subtraction)
	 *
	 * @param pos
	 *            - the new position
	 */
	public void setPositionDim0( final int pos )
	{
		typeIndex.set( pos );
		position[ 0 ] = pos;
	}

	/**
	 * Sets the {@link ArrayRandomAccess} to a certain position in dimension 0
	 *
	 * Careful: it assumes that it is only a one-dimensional image, all other
	 * dimensions would be set to zero (this saves one subtraction)
	 *
	 * @param pos
	 *            - the new position
	 */
	public void setPositionDim0( final long pos )
	{
		typeIndex.set( ( int ) pos );
		position[ 0 ] = ( int ) pos;
	}
}
