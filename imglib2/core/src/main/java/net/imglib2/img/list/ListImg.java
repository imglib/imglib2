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
import java.util.Collection;

import net.imglib2.FlatIterationOrder;
import net.imglib2.IterableRealInterval;
import net.imglib2.img.AbstractImg;
import net.imglib2.img.Img;
import net.imglib2.type.Type;
import net.imglib2.util.IntervalIndexer;

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
 * @author Stephan Preibisch and Stephan Saalfeld
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class ListImg< T > extends AbstractImg< T >
{
	final protected int[] step;

	final protected int[] dim;

	final ArrayList< T > pixels;

	final private FlatIterationOrder iterationOrder;

	protected ListImg( final long[] dim, final T type )
	{
		super( dim );

		this.dim = new int[ n ];
		for ( int d = 0; d < n; ++d )
			this.dim[ d ] = ( int ) dim[ d ];

		this.step = new int[ n ];
		IntervalIndexer.createAllocationSteps( this.dim, this.step );

		this.pixels = new ArrayList< T >( ( int ) numPixels );

		if ( type instanceof Type< ? > )
		{
			final Type< ? > t = ( Type< ? > ) type;
			@SuppressWarnings( "unchecked" )
			final ArrayList< Type< ? > > tpixels = ( ArrayList< Type< ? > > ) this.pixels;
			for ( int i = 0; i < this.numPixels; ++i )
				tpixels.add( t.createVariable() );
		}
		else
		{
			for ( int i = 0; i < this.numPixels; ++i )
				pixels.add( null );
		}

		this.iterationOrder = new FlatIterationOrder( this );
	}

	public ListImg( final Collection< T > collection, final long[] dim )
	{
		super( dim );

		this.dim = new int[ n ];
		for ( int d = 0; d < n; ++d )
			this.dim[ d ] = ( int ) dim[ d ];

		this.step = new int[ n ];
		IntervalIndexer.createAllocationSteps( this.dim, this.step );
		this.numPixels = ( int ) super.numPixels;

		this.pixels = new ArrayList< T >( ( int ) numPixels );
		this.pixels.addAll( collection );

		this.iterationOrder = new FlatIterationOrder( this );
	}

	@Override
	public ListCursor< T > cursor()
	{
		return new ListCursor< T >( this );
	}

	@Override
	public ListLocalizingCursor< T > localizingCursor()
	{
		return new ListLocalizingCursor< T >( this );
	}

	@Override
	public ListRandomAccess< T > randomAccess()
	{
		return new ListRandomAccess< T >( this );
	}

	@Override
	public ListImgFactory< T > factory()
	{
		return new ListImgFactory< T >();
	}

	@Override
	public Object iterationOrder()
	{
		return iterationOrder;
	}

	@Override
	public boolean equalIterationOrder( final IterableRealInterval< ? > f )
	{
		return iterationOrder().equals( f.iterationOrder() );
	}

	private static < A extends Type< A > > ListImg< A > copyWithType( final ListImg< A > img )
	{
		final ListImg< A > copy = new ListImg< A >( img.dimension, img.firstElement().createVariable() );

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
		final T type = firstElement();
		if ( type instanceof Type< ? > )
		{
			return copyWithType( ( ListImg< Type > ) this );
		}
		else
		{
			return new ListImg< T >( this.pixels, dimension );
		}
	}
}
