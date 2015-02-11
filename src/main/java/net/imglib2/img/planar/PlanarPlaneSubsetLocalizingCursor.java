/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2015 Tobias Pietzsch, Stephan Preibisch, Barry DeZonia,
 * Stephan Saalfeld, Curtis Rueden, Albert Cardona, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Jonathan Hale, Lee Kamentsky, Larry Lindsey, Mark
 * Hiner, Michael Zinsmaier, Martin Horn, Grant Harris, Aivar Grislis, John
 * Bogovic, Steffen Jaensch, Stefan Helfrich, Jan Funke, Nick Perry, Mark Longair,
 * Melissa Linkert and Dimiter Prodanov.
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
import net.imglib2.type.NativeType;
import net.imglib2.util.IntervalIndexer;

/**
 * 
 * Cursor optimized for one plane in an PlanarImg.
 * 
 * @author Jonathan Hale
 * 
 * @param <T>
 */
public class PlanarPlaneSubsetLocalizingCursor< T extends NativeType< T > >
		extends AbstractLocalizingCursorInt< T > implements PlanarImg.PlanarContainerSampler
{

	/**
	 * Access to the type
	 */
	protected final T type;

	/**
	 * Container
	 */
	protected final PlanarImg< T, ? > container;

	/**
	 * Current slice index
	 */
	protected int sliceIndex;

	/**
	 * Size of one plane
	 */
	protected final int planeSize;

	/**
	 * Last index on the plane
	 */
	protected final int lastIndexPlane;

	/**
	 * Offset index of the container
	 */
	protected final long offsetContainer;

	/**
	 * Maximum of the {@link PlanarImg} in every dimension. This is used to
	 * check isOutOfBounds().
	 */
	protected final int[] max;

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

		sliceIndex = cursor.sliceIndex;
		planeSize = cursor.planeSize;
		lastIndexPlane = cursor.lastIndexPlane;
		offsetContainer = cursor.offsetContainer;

		max = new int[ n ];
		for ( int d = 0; d < n; ++d )
		{
			max[ d ] = cursor.max[ d ];
			position[ d ] = cursor.position[ d ];
		}

		type.updateContainer( this );
		type.updateIndex( cursor.type.getIndex() );
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

		this.container = container;

		this.planeSize = ( ( n > 1 ) ? ( int ) interval.dimension( 1 ) : 1 ) * ( int ) interval.dimension( 0 );

		this.lastIndexPlane = planeSize - 1;

		// Set current slice index
		offsetContainer = offset( interval );
		sliceIndex = ( int ) ( offsetContainer / planeSize );

		max = new int[ n ];
		for ( int d = 0; d < n; ++d )
			max[ d ] = ( int ) interval.max( d );

		type.updateContainer( this ); // we're working on one container only.
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
		return type.getIndex() < lastIndexPlane;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void fwd()
	{
		type.incIndex();

//		 for ( int d = 0; d < n; ++d )
//		 {
//		 if ( ++position[ d ] > max[ d ] ) position[ d ] = 0;
//		 else break;
//		 }
		/*
		 * Benchmarks @ 2012-04-17 demonstrate that the less readable code below
		 * is reliably 5-10% faster than the almost equivalent commented code
		 * above. The reason is NOT simply that d=0 is executed outside the
		 * loop. We have tested that and it does not provide improved speed when
		 * done in the above version of the code. Below, it plays a role.
		 */
		if ( ++position[ 0 ] <= max[ 0 ] )
		{
			return;
		}
		else
		{
			position[ 0 ] = 0;

			for ( int d = 1; d < n; ++d )
			{
				if ( ++position[ d ] <= max[ d ] )
					break;
				else
					position[ d ] = 0;
			}

			return;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void jumpFwd( final long steps )
	{
		type.incIndex( ( int ) steps );

		IntervalIndexer.indexToPosition( ( int ) offsetContainer + type.getIndex(), container.dimensions, position );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void reset()
	{
		type.updateIndex( -1 );

		IntervalIndexer.indexToPosition( ( int ) offsetContainer + type.getIndex(), container.dimensions, position );
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
