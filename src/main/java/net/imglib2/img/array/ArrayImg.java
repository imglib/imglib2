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

import net.imglib2.Cursor;
import net.imglib2.FlatIterationOrder;
import net.imglib2.Interval;
import net.imglib2.img.AbstractNativeImg;
import net.imglib2.img.Img;
import net.imglib2.img.basictypeaccess.DataAccess;
import net.imglib2.stream.LocalizableSpliterator;
import net.imglib2.type.NativeType;
import net.imglib2.util.Fraction;
import net.imglib2.util.IntervalIndexer;
import net.imglib2.util.Intervals;
import net.imglib2.view.iteration.SubIntervalIterable;

/**
 * This {@link Img} stores an image in a single linear array of basic types. By
 * that, it provides the fastest possible access to data while limiting the
 * number of basic types stored to {@link Integer#MAX_VALUE}. Keep in mind that
 * this does not necessarily reflect the number of pixels, because a pixel can
 * be stored in less than or more than a basic type entry.
 *
 * @param <T>
 * @param <A>
 *
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public class ArrayImg< T extends NativeType< T >, A extends DataAccess > extends AbstractNativeImg< T, A > implements SubIntervalIterable< T >
{
	final int[] steps, dim;

	// the DataAccess created by the ArrayContainerFactory
	final private A data;

	// TODO check for the size of numPixels being < Integer.MAX_VALUE?
	// TODO Type is suddenly not necessary anymore
	public ArrayImg( final A data, final long[] dim, final Fraction entitiesPerPixel )
	{
		super( dim, entitiesPerPixel );
		this.dim = new int[ n ];
		for ( int d = 0; d < n; ++d )
			this.dim[ d ] = ( int ) dim[ d ];

		this.steps = new int[ n ];
		IntervalIndexer.createAllocationSteps( this.dim, this.steps );
		this.data = data;
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public A update( final Object o )
	{
		return ( A ) data.createView( o );
	}

	@Override
	public ArrayCursor< T > cursor()
	{
		return new ArrayCursor< T >( this );
	}

	@Override
	public ArrayLocalizingCursor< T > localizingCursor()
	{
		return new ArrayLocalizingCursor< T >( this );
	}

	@Override
	public ArrayRandomAccess< T > randomAccess()
	{
		return new ArrayRandomAccess< T >( this );
	}

	@Override
	public ArrayRandomAccess< T > randomAccess( final Interval interval )
	{
		return randomAccess();
	}

	@Override
	public FlatIterationOrder iterationOrder()
	{
		return new FlatIterationOrder( this );
	}

	@Override
	public ArrayImgFactory< T > factory()
	{
		return new ArrayImgFactory<>( linkedType );
	}

	@Override
	public ArrayImg< T, ? > copy()
	{
		final ArrayImg< T, ? > copy = factory().create( dimension );

		final ArrayCursor< T > source = this.cursor();
		final ArrayCursor< T > target = copy.cursor();

		while ( source.hasNext() )
			target.next().set( source.next() );

		return copy;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Cursor< T > cursor( final Interval interval )
	{
		final int dimLength = fastCursorAvailable( interval );

		assert dimLength > 0;

		return new ArraySubIntervalCursor< T >( this, ( int ) offset( interval ), ( int ) size( interval, dimLength ) );
	}

	private long size( final Interval interval, final int length )
	{
		long size = interval.dimension( 0 );
		for ( int d = 1; d < length; ++d )
		{
			size *= interval.dimension( d );
		}

		return size;
	}

	private long offset( final Interval interval )
	{
		final int maxDim = numDimensions() - 1;
		long i = interval.min( maxDim );
		for ( int d = maxDim - 1; d >= 0; --d )
		{
			i = i * dimension( d ) + interval.min( d );
		}

		return i;
	}

	/**
	 * If method returns -1 no fast cursor is available, else the amount of dims
	 * (starting from zero) which can be iterated fast are returned.
	 */
	private int fastCursorAvailable( final Interval interval )
	{
		// first check whether the interval is completely contained.
		if ( !Intervals.contains( this, interval ) )
			return -1;

		// find the first dimension in which image and interval differ
		int dimIdx = 0;
		for ( ; dimIdx < n; ++dimIdx )
			if ( interval.dimension( dimIdx ) != dimension( dimIdx ) )
				break;

		if ( dimIdx == n )
			return dimIdx;

		// in the dimension after that, image and interval may differ
		++dimIdx;

		// but image extents of all higher dimensions must equal 1
		for ( int d = dimIdx; d < n; ++d )
			if ( interval.dimension( d ) != 1 )
				return -1;

		return dimIdx;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Cursor< T > localizingCursor( final Interval interval )
	{
		final int dimLength = fastCursorAvailable( interval );

		assert dimLength > 0;

		return new ArrayLocalizingSubIntervalCursor< T >( this, ( int ) offset( interval ), ( int ) size( interval, dimLength ) );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean supportsOptimizedCursor( final Interval interval )
	{
		return fastCursorAvailable( interval ) > 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object subIntervalIterationOrder( final Interval interval )
	{
		return new FlatIterationOrder( interval );
	}

	@Override
	public LocalizableSpliterator< T > spliterator()
	{
		return new ArraySpliterator<>( this, 0, ( int ) size() );
	}

	/**
	 * Deprecated constructor for binary compatibility when A was not bounded by
	 * DataAccess
	 * 
	 * @param data
	 *            - will be cast to DataAccess type A
	 * @param dim
	 * @param entitiesPerPixel
	 */
	@SuppressWarnings( "unchecked" )
	@Deprecated
	public ArrayImg( final Object data, final long[] dim, final Fraction entitiesPerPixel )
	{
		this( ( A ) data, dim, entitiesPerPixel );
	}
}
