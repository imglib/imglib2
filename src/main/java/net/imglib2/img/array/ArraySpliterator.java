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

import java.util.function.Consumer;
import net.imglib2.Positionable;
import net.imglib2.RealPositionable;
import net.imglib2.stream.LocalizableSpliterator;
import net.imglib2.type.Index;
import net.imglib2.type.NativeType;
import net.imglib2.util.IntervalIndexer;

/**
 * LocalizableSpliterator for ArrayImg.
 * Localizes on demand.
 *
 * @param <T> pixel type
 */
class ArraySpliterator< T extends NativeType< T > > implements LocalizableSpliterator< T >
{
	private final ArrayImg< T, ? > img;

	private final T type;

	private final Index index;

	private final int fence;  // one past last index

	ArraySpliterator( ArrayImg< T, ? > img, int origin, int fence )
	{
		this.img = img;
		type = img.createLinkedType();
		index = type.index();
		this.fence = fence;

		type.updateContainer( this );
		index.set( origin - 1 );
	}

	@Override
	public boolean tryAdvance( final Consumer< ? super T > action )
	{
		if ( action == null )
			throw new NullPointerException();
		if ( index.get() < fence - 1 )
		{
			index.inc();
			action.accept( type );
			return true;
		}
		return false;
	}

	@Override
	public void forEachRemaining( final Consumer< ? super T > action )
	{
		if ( action == null )
			throw new NullPointerException();

		final int len = fence - index.get() - 1;
		for ( int i = 0; i < len; i++ )
		{
			index.inc();
			action.accept( type );
		}
	}

	@Override
	public ArraySpliterator< T > trySplit()
	{
		int lo = index.get() + 1, mid = ( lo + fence ) >>> 1;
		if ( lo >= mid )
			return null;
		else
		{
			final ArraySpliterator< T > prefix = new ArraySpliterator<>( img, lo, mid );
			index.set( mid - 1 );
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
	public ArraySpliterator< T > copy()
	{
		return new ArraySpliterator<>( img, index.get() + 1, fence );
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


	// -----------------------------------------------------------
	//   Localizable

	@Override
	public int numDimensions()
	{
		return img.numDimensions();
	}

	@Override
	public void localize( final int[] position )
	{
		IntervalIndexer.indexToPosition( index.get(), img.dim, position );
	}

	@Override
	public void localize( final float[] position )
	{
		IntervalIndexer.indexToPosition( index.get(), img.dim, position );
	}

	@Override
	public void localize( final double[] position )
	{
		IntervalIndexer.indexToPosition( index.get(), img.dim, position );
	}

	@Override
	public void localize( final long[] position )
	{
		IntervalIndexer.indexToPosition( index.get(), img.dim, position );
	}

	@Override
	public void localize( final Positionable position )
	{
		IntervalIndexer.indexToPosition( index.get(), img.dim, position );
	}

	@Override
	public void localize( final RealPositionable position )
	{
		IntervalIndexer.indexToPosition( index.get(), img.dim, position );
	}

	@Override
	public int getIntPosition( final int d )
	{
		if ( d == 0 )
			return index.get() % img.dim[ 0 ];
		else
			return IntervalIndexer.indexToPosition( index.get(), img.dim, img.steps, d );
	}

	@Override
	public long getLongPosition( final int d )
	{
		return getIntPosition( d );
	}

	@Override
	public float getFloatPosition( final int d )
	{
		return getIntPosition( d );
	}

	@Override
	public double getDoublePosition( final int d )
	{
		return getIntPosition( d );
	}
}
