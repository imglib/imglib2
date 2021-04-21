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
import net.imglib2.Interval;
import net.imglib2.type.Index;
import net.imglib2.type.NativeType;

/**
 * 
 * Cursor optimized for one plane in an PlanarImg.
 * 
 * @author Jonathan Hale
 * @author Tobias Pietzsch
 *
 * @param <T>
 */
public class PlanarPlaneSubsetLocalizingCursor< T extends NativeType< T > >
		extends AbstractLocalizingCursorInt< T > implements PlanarImg.PlanarContainerSampler
{

	/**
	 * Access to the type
	 */
	private final T type;

	private final Index typeIndex;

	/**
	 * Container
	 */
	private final PlanarImg< T, ? > container;

	/**
	 * Current slice index
	 */
	private final int sliceIndex;

	/**
	 * Last index on the plane
	 */
	private final int lastIndexPlane;

	private final int maxX;

	private final int dimX;

	/**
	 * Copy Constructor
	 *
	 * @param cursor
	 *            PlanarPlaneSubsetLocalizingCursor to copy from
	 */
	protected PlanarPlaneSubsetLocalizingCursor( final PlanarPlaneSubsetLocalizingCursor< T > cursor )
	{
		super( cursor.numDimensions() );

		container = cursor.container;
		this.type = container.createLinkedType();
		typeIndex = type.index();

		sliceIndex = cursor.sliceIndex;
		lastIndexPlane = cursor.lastIndexPlane;

		maxX = cursor.maxX;
		dimX = cursor.dimX;
		for ( int d = 0; d < n; ++d )
			position[ d ] = cursor.position[ d ];

		type.updateContainer( this );
		typeIndex.set( cursor.typeIndex.get() );
	}

	/**
	 * Constructor
	 * 
	 * @param container
	 *            PlanarImg this cursor shall work on.
	 * @param interval
	 *            Interval over which shall be iterated.
	 */
	public PlanarPlaneSubsetLocalizingCursor( final PlanarImg< T, ? > container, final Interval interval )
	{
		super( container.numDimensions() );

		this.type = container.createLinkedType();
		typeIndex = type.index();

		this.container = container;

		final int planeSize = ( ( n > 1 ) ? ( int ) interval.dimension( 1 ) : 1 ) * ( int ) interval.dimension( 0 );
		this.lastIndexPlane = planeSize - 1;

		// Set current slice index
		sliceIndex = ( int ) ( offset( interval ) / planeSize );

		maxX = ( int ) interval.max( 0 );
		dimX = ( int ) container.dimension( 0 );

		type.updateContainer( this ); // we're working on one container only.
		for ( int d = 2; d < n; ++d )
			position[ d ] = ( int ) interval.min( d );
		reset();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getCurrentSliceIndex()
	{
		return sliceIndex;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final T get()
	{
		return type;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PlanarPlaneSubsetLocalizingCursor< T > copy()
	{
		return new PlanarPlaneSubsetLocalizingCursor< T >( this );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PlanarPlaneSubsetLocalizingCursor< T > copyCursor()
	{
		return copy();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean hasNext()
	{
		return typeIndex.get() < lastIndexPlane;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void fwd()
	{
		typeIndex.inc();
		if ( ++position[ 0 ] > maxX && n > 1 )
		{
			position[ 0 ] = 0;
			++position[ 1 ];
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void jumpFwd( final long steps )
	{
		typeIndex.inc( ( int ) steps );
		updatePositionFromIndex( typeIndex.get() );
	}

	private void updatePositionFromIndex( final int index )
	{
		if ( n == 1 )
			position[ 0 ] = index;
		else
		{
			final int j = index / dimX;
			position[ 0 ] = index - j * dimX;
			position[ 1 ] = j;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void reset()
	{
		typeIndex.set( -1 );
		updatePositionFromIndex( typeIndex.get() );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString()
	{
		return type.toString();
	}

	/*
	 * Computes global offset of the interval in the Img
	 */
	private long offset( final Interval interval )
	{
		final int maxDim = numDimensions() - 1;
		long i = interval.min( maxDim );
		for ( int d = maxDim - 1; d >= 0; --d )
			i = i * container.dimension( d ) + interval.min( d );

		return i;
	}

}
