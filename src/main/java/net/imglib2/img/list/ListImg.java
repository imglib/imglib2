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

package net.imglib2.img.list;

import java.util.ArrayList;
import java.util.Collection;

import java.util.List;
import net.imglib2.img.Img;
import net.imglib2.type.Type;

/**
 * This {@link Img} stores an image in a single linear {@link ArrayList}. Each
 * pixel is stored as an individual object, so {@link ListImg} should only be
 * used for images with relatively few pixels. In principle, the number of
 * entities stored is limited to {@link Integer#MAX_VALUE}.
 *
 * @param <T>
 *            The value type of the pixels. You can us {@link Type}s or
 *            arbitrary {@link Object}s. If you use non-{@link Type} pixels,
 *            note, that you cannot use {@link Type#set(Type)} to change the
 *            value stored in every reference. Instead, you can use the
 *            {@link ListCursor#set(Object)} and
 *            {@link ListRandomAccess#set(Object)} methods to alter the
 *            underlying {@link ArrayList}.
 *
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 * @author Tobias Pietzsch
 */
public class ListImg< T > extends AbstractListImg< T >
{
	private final List< T > pixels;

	private final T type;

	public ListImg( final long[] dim, final T type )
	{
		super( dim );
		this.type = type;
		pixels = new ArrayList< T >( ( int ) numPixels );
		if ( type instanceof Type< ? > )
		{
			final Type< ? > t = ( Type< ? > ) type;
			@SuppressWarnings( "unchecked" )
			final ArrayList< Type< ? > > tpixels = ( ArrayList< Type< ? > > ) pixels;
			for ( int i = 0; i < numPixels; ++i )
				tpixels.add( t.createVariable() );
		}
		else
		{
			for ( int i = 0; i < numPixels; ++i )
				pixels.add( null );
		}
	}

	public ListImg( final T type, final Collection< T > collection, final long... dim )
	{
		super( dim );
		this.type = type;
		assert numPixels == collection.size() : "Dimensions do not match number of pixels.";
		pixels = new ArrayList<>( collection );
	}

	public ListImg( final Collection< T > collection, final long... dim )
	{
		this( collection.iterator().next(), collection, dim );
	}

	@Override
	protected T get( final int index )
	{
		return pixels.get( index );
	}

	@Override
	public T getType()
	{
		return type;
	}

	@Override
	protected void set( final int index, final T value )
	{
		pixels.set( index, value );
	}

	private static < A extends Type< A > > ListImg< A > copyWithType( final ListImg< A > img )
	{
		final ListImg< A > copy = new ListImg<>( img.dimension, img.type );

		final ListCursor< A > source = img.cursor();
		final ListCursor< A > target = copy.cursor();

		while ( source.hasNext() )
			target.next().set( source.next() );

		return copy;
	}

	@SuppressWarnings( { "unchecked", "rawtypes" } )
	@Override
	public ListImg< T > copy()
	{
		if ( type instanceof Type< ? > )
		{
			final ListImg< ? > copy = copyWithType( ( ListImg< Type > ) this );
			return ( ListImg< T > ) copy;
		}
		return new ListImg<>( this.type, this.pixels, dimension );
	}

	private ListImg( final List< T > pixels, final boolean dummy, final long... dim )
	{
		super( dim );
		this.type = pixels.isEmpty() ? null : pixels.get( 0 );
		assert numPixels == pixels.size() : "Dimensions do not match number of pixels.";
		this.pixels = pixels;
	}

	public static < T > ListImg< T > wrap( List< T > pixels, final long... dim )
	{
		return new ListImg<>( pixels, false, dim );
	}
}
