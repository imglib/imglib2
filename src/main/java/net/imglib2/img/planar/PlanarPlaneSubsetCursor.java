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

package net.imglib2.img.planar;

import net.imglib2.AbstractCursorInt;
import net.imglib2.Interval;
import net.imglib2.type.Index;
import net.imglib2.type.NativeType;

/**
 * Basic Iterator for {@link PlanarImg PlanarContainers}
 * 
 * @param <T>
 * 
 * @author Christian Dietz
 * @author Jonathan Hale
 */
public class PlanarPlaneSubsetCursor< T extends NativeType< T >> extends
		AbstractCursorInt< T > implements PlanarImg.PlanarContainerSampler
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
	 * Size of one plane
	 */
	private final int planeSize;

	/**
	 * Last index on plane
	 */
	private final int lastPlaneIndex;

	/**
	 * Copy Constructor
	 *
	 * @param cursor - the cursor to copy from.
	 */
	protected PlanarPlaneSubsetCursor( final PlanarPlaneSubsetCursor< T > cursor )
	{
		super( cursor.numDimensions() );

		container = cursor.container;
		this.type = container.createLinkedType();
		typeIndex = type.index();

		sliceIndex = cursor.sliceIndex;
		planeSize = cursor.planeSize;
		lastPlaneIndex = cursor.lastPlaneIndex;

		type.updateContainer( this );
		typeIndex.set( cursor.typeIndex.get() );
	}

	/**
	 * Constructor
	 *
	 * @param container - the container this cursor shall work on.
	 * @param interval - the interval to iterate over.
	 */
	public PlanarPlaneSubsetCursor( final PlanarImg< T, ? > container, final Interval interval )
	{
		super( container.numDimensions() );

		this.type = container.createLinkedType();
		typeIndex = type.index();

		this.container = container;

		this.planeSize = ( ( n > 1 ) ? ( int ) interval.dimension( 1 ) : 1 )
				* ( int ) interval.dimension( 0 );

		this.lastPlaneIndex = planeSize - 1;

		this.sliceIndex = ( int ) ( offset( interval ) / planeSize );

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
	public PlanarPlaneSubsetCursor< T > copy()
	{
		return new PlanarPlaneSubsetCursor< T >( this );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean hasNext()
	{
		return typeIndex.get() < lastPlaneIndex;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void fwd()
	{
		typeIndex.inc();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void jumpFwd( final long steps )
	{
		typeIndex.inc( ( int ) steps );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void reset()
	{
		// Set index inside the slice
		typeIndex.set( -1 );
		type.updateContainer( this );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString()
	{
		return type.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void localize( final int[] position )
	{
		container.indexToGlobalPosition( sliceIndex, typeIndex.get(), position );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getIntPosition( final int dim )
	{
		return container.indexToGlobalPosition( sliceIndex, typeIndex.get(), dim );
	}

	private long offset(final Interval interval) {
		final int maxDim = numDimensions() - 1;
		long i = interval.min(maxDim);
		for (int d = maxDim - 1; d >= 0; --d)
			i = i * container.dimension(d) + interval.min(d);

		return i;
	}
}
