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

package net.imglib2.img.planar;

import java.util.function.Consumer;
import net.imglib2.Positionable;
import net.imglib2.RealPositionable;
import net.imglib2.stream.LocalizableSpliterator;
import net.imglib2.type.Index;
import net.imglib2.type.NativeType;

/**
 * LocalizableSpliterator for {@link PlanarImg}.
 * Localizes on demand.
 *
 * @param <T> pixel type
 */
class PlanarSpliterator< T extends NativeType< T > > implements LocalizableSpliterator< T >, PlanarImg.PlanarContainerSampler
{
	private final PlanarImg< T, ? > img;

	private final T type;

	private final long elementsPerSlice;

	private final int lastSlice;

	private final int lastIndexInSlice;

	private final int lastIndexInLastSlice;

	private int slice;

	private final Index index;

	PlanarSpliterator( PlanarImg< T, ? > img, long origin, long fence )
	{
		this.img = img;
		this.type = img.createLinkedType();
		this.elementsPerSlice = img.elementsPerSlice;

		final long last = fence - 1;
		lastSlice = ( int ) ( last / elementsPerSlice );
		lastIndexInSlice = img.elementsPerSlice - 1;
		lastIndexInLastSlice = ( int ) ( last - lastSlice * elementsPerSlice );

		index = type.index();
		slice = ( int ) ( origin / elementsPerSlice );
		index.set( ( int ) ( origin - slice * elementsPerSlice ) );
		index.dec();

		type.updateContainer( this );

		tmp = new int[ img.numDimensions() ];
	}

	private PlanarSpliterator( PlanarSpliterator< T > o )
	{
		img = o.img;
		type = img.createLinkedType();
		elementsPerSlice = o.elementsPerSlice;
		lastSlice = o.lastSlice;
		lastIndexInSlice = o.lastIndexInSlice;
		lastIndexInLastSlice = o.lastIndexInLastSlice;
		slice = o.slice;
		index = type.index();
		index.set( o.index.get() );
		type.updateContainer( this );
		tmp = new int[ img.numDimensions() ];
	}

	@Override
	public int getCurrentSliceIndex()
	{
		return slice;
	}

	@Override
	public int numDimensions()
	{
		return img.numDimensions();
	}

	@Override
	public boolean tryAdvance( final Consumer< ? super T > action )
	{
		if ( action == null )
			throw new NullPointerException();

		if ( slice >= lastSlice && index.get() >= lastIndexInLastSlice )
			return false;

		if ( index.get() < lastIndexInSlice )
		{
			index.inc();
		}
		else
		{
			index.set( 0 );
			++slice;
			type.updateContainer( this );
		}

		action.accept( type );
		return true;
	}

	@Override
	public void forEachRemaining( final Consumer< ? super T > action )
	{
		if ( action == null )
			throw new NullPointerException();

		while ( slice < lastSlice )
		{
			forEachRemainingInSlice( action );
			index.set( -1 );
			++slice;
			type.updateContainer( this );
		}
		forEachRemainingInSlice( action );
	}

	private void forEachRemainingInSlice(final Consumer< ? super T > action)
	{
		final int len = ( slice == lastSlice ? lastIndexInLastSlice : lastIndexInSlice ) - index.get();
		for ( int i = 0; i < len; i++ )
		{
			index.inc();
			action.accept( type );
		}
	}

	private long globalIndex( int slice, int index )
	{
		return slice * elementsPerSlice + index;
	}

	@Override
	public PlanarSpliterator< T > trySplit()
	{
		long lo = globalIndex( slice, index.get() ) + 1;
		long mid = ( lo + globalIndex( lastSlice, lastIndexInLastSlice ) + 1 ) >>> 1;
		if ( lo >= mid )
			return null;
		else
		{
			final PlanarSpliterator< T > prefix = new PlanarSpliterator<>( img, lo, mid );
			slice = ( int ) ( mid / elementsPerSlice );
			index.set( ( int ) ( mid - 1 - slice * elementsPerSlice ) );
			type.updateContainer( this );
			return prefix;
		}
	}

	@Override
	public long estimateSize()
	{
		return ( lastSlice - slice ) * elementsPerSlice + lastIndexInLastSlice - index.get();
	}

	@Override
	public int characteristics()
	{
		return IMMUTABLE | NONNULL | ORDERED | SIZED | SUBSIZED;
	}

	@Override
	public PlanarSpliterator< T > copy()
	{
		return new PlanarSpliterator<>( this );
	}

	@Override
	public T get()
	{
		return type;
	}




	// -- Localizable --

	@Override
	public void localize( final int[] pos )
	{
		img.indexToGlobalPosition( slice, index.get(), pos );
	}

	@Override
	public int getIntPosition( final int d )
	{
		return img.indexToGlobalPosition( slice, index.get(), d );
	}

	/**
	 * used internally to forward all localize() versions to the (abstract)
	 * int[] version.
	 */
	final private int[] tmp;

	@Override
	public void localize( final float[] pos )
	{
		localize( tmp );
		for ( int d = 0; d < tmp.length; d++ )
			pos[ d ] = tmp[ d ];
	}

	@Override
	public void localize( final double[] pos )
	{
		localize( tmp );
		for ( int d = 0; d < tmp.length; d++ )
			pos[ d ] = tmp[ d ];
	}

	@Override
	public void localize( final Positionable position )
	{
		localize( tmp );
		position.setPosition( tmp );
	}

	@Override
	public void localize( final RealPositionable position )
	{
		localize( tmp );
		position.setPosition( tmp );
	}

	@Override
	public void localize( final long[] pos )
	{
		localize( tmp );
		for ( int d = 0; d < tmp.length; d++ )
			pos[ d ] = tmp[ d ];
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

	@Override
	public long getLongPosition( final int d )
	{
		return getIntPosition( d );
	}
}
