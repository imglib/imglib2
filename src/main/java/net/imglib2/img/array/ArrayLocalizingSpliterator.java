/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2023 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

import java.util.function.Consumer;
import net.imglib2.AbstractLocalizableInt;
import net.imglib2.stream.LocalizableSpliterator;
import net.imglib2.type.Index;
import net.imglib2.type.NativeType;
import net.imglib2.util.IntervalIndexer;

/**
 * LocalizableSpliterator for ArrayImg.
 * Keeps track of location on every step.
 *
 * @param <T> pixel type
 */
class ArrayLocalizingSpliterator< T extends NativeType< T > > extends AbstractLocalizableInt implements LocalizableSpliterator< T >
{
	private final ArrayImg< T, ? > img;

	private final T type;

	private final Index index;

	private final int fence;  // one past last index

	ArrayLocalizingSpliterator( ArrayImg< T, ? > img, int origin, int fence )
	{
		super( img.numDimensions() );
		this.img = img;
		type = img.createLinkedType();
		index = type.index();
		this.fence = fence;

		type.updateContainer( this );
		index.set( origin - 1 );

		IntervalIndexer.indexToPosition( origin, img.dim, position );
		position[ 0 ]--;
	}

	@Override
	public boolean tryAdvance( final Consumer< ? super T > action )
	{
		if ( action == null )
			throw new NullPointerException();
		if ( index.get() < fence - 1 )
		{
			fwd();
			action.accept( type );
			return true;
		}
		return false;
	}

	private void fwd()
	{
		index.inc();
		if ( ++position[ 0 ] > img.dim[ 0 ] - 1 )
		{
			position[ 0 ] = 0;

			for ( int d = 1; d < n; ++d )
			{
				if ( ++position[ d ] <= img.dim[ d ] - 1 )
					break;
				else
					position[ d ] = 0;
			}
		}
	}

	@Override
	public void forEachRemaining( final Consumer< ? super T > action )
	{
		if ( action == null )
			throw new NullPointerException();

		int len = fence - index.get() - 1;
		while ( len > 0 )
		{
			final int lenX = Math.min(
					len,
					img.dim[ 0 ] - 1 - position[ 0 ] );
			for ( int x = 0; x < lenX; ++x )
			{
				++position[ 0 ];
				index.inc();
				action.accept( type );
			}
			len -= lenX;
			if ( len > 0 )
				nextLine();
		}
	}

	private void nextLine()
	{
		position[ 0 ] = -1;
		for ( int d = 1; d < n; ++d )
		{
			if ( ++position[ d ] <= img.dim[ d ] - 1 )
				break;
			else
				position[ d ] = 0;
		}
	}

	@Override
	public ArrayLocalizingSpliterator< T > trySplit()
	{
		int lo = index.get() + 1, mid = ( lo + fence ) >>> 1;
		if ( lo >= mid )
			return null;
		else
		{
			final ArrayLocalizingSpliterator< T > prefix = new ArrayLocalizingSpliterator<>( img, lo, mid );
			index.set( mid - 1 );
			IntervalIndexer.indexToPosition( mid, img.dim, position );
			position[ 0 ]--;
			return prefix;
		}
	}

	@Override
	public long estimateSize()
	{
		return fence - index.get() - 1;
	}

	@Override
	public int characteristics()
	{
		return IMMUTABLE | NONNULL | ORDERED | SIZED | SUBSIZED;
	}

	@Override
	public ArrayLocalizingSpliterator< T > copy()
	{
		return new ArrayLocalizingSpliterator<>( img, index.get() + 1, fence );
	}

	@Override
	public T get()
	{
		return type;
	}
}
