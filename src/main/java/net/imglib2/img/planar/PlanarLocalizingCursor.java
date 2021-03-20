/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2020 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

import net.imglib2.AbstractLocalizingCursorInt;
import net.imglib2.type.Index;
import net.imglib2.type.NativeType;

/**
 * Localizing Iterator for a {@link PlanarImg PlanarContainers}
 * 
 * @param <T>
 * 
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public class PlanarLocalizingCursor< T extends NativeType< T > > extends AbstractLocalizingCursorInt< T > implements PlanarImg.PlanarContainerSampler
{
	protected final T type;

	protected final Index i;

	protected final PlanarImg< T, ? > container;

	protected final int lastIndex, lastSliceIndex;

	protected int sliceIndex;

	/**
	 * The current index of the type. It is faster to duplicate this here than
	 * to access it through type.getIndex().
	 */
	protected int index;

	/**
	 * Maximum of the {@link PlanarImg} in every dimension. This is used to
	 * check isOutOfBounds().
	 */
	protected final int[] max;

	protected PlanarLocalizingCursor( final PlanarLocalizingCursor< T > cursor )
	{
		super( cursor.numDimensions() );

		container = cursor.container;
		this.type = container.createLinkedType();
		i = type.index();

		lastIndex = cursor.lastIndex;
		lastSliceIndex = cursor.lastSliceIndex;

		max = new int[ n ];
		for ( int d = 0; d < n; ++d )
		{
			max[ d ] = cursor.max[ d ];
			position[ d ] = cursor.position[ d ];
		}

		sliceIndex = cursor.sliceIndex;
		index = cursor.index;

		type.updateContainer( this );
		i.set( index );
	}

	public PlanarLocalizingCursor( final PlanarImg< T, ? > container )
	{
		super( container.numDimensions() );

		this.type = container.createLinkedType();
		i = type.index();
		this.container = container;

		lastIndex = ( ( n > 1 ) ? container.dimensions[ 1 ] : 1 ) * container.dimensions[ 0 ] - 1;
		lastSliceIndex = container.numSlices() - 1;

		max = new int[ n ];
		for ( int d = 0; d < n; ++d )
			max[ d ] = ( int ) container.max( d );

		reset();
	}

	@Override
	public int getCurrentSliceIndex()
	{
		return sliceIndex;
	}

	@Override
	public T get()
	{
		return type;
	}

	@Override
	public PlanarLocalizingCursor< T > copy()
	{
		return new PlanarLocalizingCursor< T >( this );
	}

	@Override
	public PlanarLocalizingCursor< T > copyCursor()
	{
		return copy();
	}

	/**
	 * Note: This test is fragile in a sense that it returns true for elements
	 * after the last element as well.
	 *
	 * @return false for the last element
	 */
	@Override
	public boolean hasNext()
	{
		return ( sliceIndex < lastSliceIndex ) || ( index < lastIndex );
	}

	@Override
	public void fwd()
	{
		if ( ++index > lastIndex )
		{
			index = 0;
			++sliceIndex;
			type.updateContainer( this );
		}
		i.set( index );

		for ( int d = 0; d < n; ++d )
		{
			if ( ++position[ d ] > max[ d ] )
				position[ d ] = 0;
			else
				break;
		}
	}

	@Override
	public void jumpFwd( final long steps )
	{
		long newIndex = index + steps;
		if ( newIndex > lastIndex )
		{
			final long s = newIndex / ( lastIndex + 1 );
			newIndex -= s * ( lastIndex + 1 );
			sliceIndex += s;
			type.updateContainer( this );
		}
		index = ( int ) newIndex;
		i.set( index );
		container.indexToGlobalPosition( sliceIndex, index, position );
	}

	@Override
	public void reset()
	{
		position[ 0 ] = -1;
		for ( int d = 1; d < n; d++ )
			position[ d ] = 0;

		sliceIndex = 0;
		index = -1;
		i.set( -1 );
		type.updateContainer( this );
	}
}
