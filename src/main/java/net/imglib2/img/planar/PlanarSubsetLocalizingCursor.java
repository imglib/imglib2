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
import net.imglib2.util.Intervals;

/**
 * Basic Iterator for {@link PlanarImg PlanarContainers}
 * 
 * @param <T>
 * 
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 * @author Christian Dietz
 */
public class PlanarSubsetLocalizingCursor< T extends NativeType< T > >
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
	 * Last index in plane
	 */
	protected final int lastIndexPlane;

	/**
	 * Current slice index
	 */
	protected int planeIndex;

	/**
	 * The current index of the type. It is faster to duplicate this here than
	 * to access it through type.getIndex().
	 */
	protected int indexInPlane;

	/**
	 * Total offset in container to interval position
	 */
	protected final int offsetContainer;

	/**
	 * Current index in the container
	 */
	protected int indexContainer;

	/**
	 * Size of one plane
	 */
	protected final int planeSize;

	/**
	 * Last index to be visited in the container
	 */
	protected final int lastIndexContainer;

	/**
	 * Maximum of the {@link PlanarImg} in every dimension. This is used to
	 * check isOutOfBounds().
	 */
	protected final int[] max;

	/**
	 * TODO Javadoc
	 * 
	 * @param cursor
	 */
	protected PlanarSubsetLocalizingCursor( final PlanarSubsetLocalizingCursor< T > cursor )
	{
		super( cursor.numDimensions() );

		container = cursor.container;
		this.type = container.createLinkedType();

		lastIndexPlane = cursor.lastIndexPlane;
		planeIndex = cursor.planeIndex;
		indexInPlane = cursor.indexInPlane;
		offsetContainer = cursor.offsetContainer;
		indexContainer = cursor.indexContainer;
		planeSize = cursor.planeSize;
		lastIndexContainer = cursor.lastIndexContainer;

		max = new int[ n ];
		for ( int d = 0; d < n; ++d )
		{
			max[ d ] = cursor.max[ d ];
			position[ d ] = cursor.position[ d ];
		}

		type.updateContainer( this );
		type.updateIndex( indexInPlane );
	}

	/**
	 * TODO Javadoc
	 * 
	 * @param container
	 * @param interval
	 */
	public PlanarSubsetLocalizingCursor( final PlanarImg< T, ? > container, final Interval interval )
	{
		super( container.numDimensions() );

		this.type = container.createLinkedType();

		this.container = container;

		this.offsetContainer = ( int ) offset( interval );

		this.planeSize = ( ( n > 1 ) ? ( int ) container.dimension( 1 ) : 1 ) * ( int ) container.dimension( 0 );

		this.lastIndexPlane = planeSize - 1;

		this.lastIndexContainer = ( int ) ( offsetContainer + Intervals.numElements( interval ) - 1 );

		max = new int[ n ];
		for ( int d = 0; d < n; ++d )
			max[ d ] = ( int ) container.max( d );

		reset();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getCurrentSliceIndex()
	{
		return planeIndex;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T get()
	{
		return type;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PlanarSubsetLocalizingCursor< T > copy()
	{
		return new PlanarSubsetLocalizingCursor< T >( this );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PlanarSubsetLocalizingCursor< T > copyCursor()
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
		return indexContainer < lastIndexContainer;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fwd()
	{
		++indexContainer;

		if ( ++indexInPlane > lastIndexPlane )
		{
			indexInPlane = 0;
			++planeIndex;
			type.updateContainer( this );

			type.updateIndex( indexInPlane );
		}
		else
			type.incIndex();

//		for ( int d = 0; d < n; ++d )
//		{
//			if ( ++position[ d ] > max[ d ] ) position[ d ] = 0;
//			else break;
//		}

		/*
		 * Benchmarks @ 2012-04-17 demonstrate that the less readable code below
		 * is reliably 5-10% faster than the almost equivalent commented code
		 * above. The reason is NOT simply that d=0 is executed outside the
		 * loop. We have tested that and it does not provide improved speed when
		 * done in the above version of the code. Below, it plays a role.
		 */
		if ( ++position[ 0 ] <= max[ 0 ] )
			return;
		position[ 0 ] = 0;

		for ( int d = 1; d < n; ++d )
		{
			if ( ++position[ d ] > max[ d ] )
				position[ d ] = 0;
			else
				break;
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void jumpFwd( final long steps )
	{
		long newIndex = indexInPlane + steps;
		if ( newIndex > lastIndexPlane )
		{
			final long s = newIndex / planeSize;
			newIndex -= s * planeSize;
			planeIndex += s;
			type.updateContainer( this );
		}
		indexInPlane = ( int ) newIndex;
		indexContainer += steps;
		type.updateIndex( indexInPlane );

		container.indexToGlobalPosition( planeIndex, indexInPlane, position );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reset()
	{

		// Set current slice index
		planeIndex = offsetContainer / planeSize;

		// Set index inside the slice
		indexInPlane = offsetContainer % planeSize - 1;

		// Set total index to index
		indexContainer = ( planeSize * planeIndex ) + indexInPlane;

		type.updateIndex( indexInPlane );
		type.updateContainer( this );

		container.indexToGlobalPosition( planeIndex, indexInPlane, position );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString()
	{
		return type.toString();
	}

	private long offset( final Interval interval )
	{
		final int maxDim = numDimensions() - 1;
		long i = interval.min( maxDim );
		for ( int d = maxDim - 1; d >= 0; --d )
			i = i * container.dimension( d ) + interval.min( d );

		return i;
	}
}
