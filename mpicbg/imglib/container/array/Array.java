/**
 * Copyright (c) 2009--2010, Stephan Preibisch & Stephan Saalfeld
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  Neither the name of the Fiji project nor
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
package mpicbg.imglib.container.array;

import mpicbg.imglib.container.AbstractDirectAccessContainer;
import mpicbg.imglib.container.Container;
import mpicbg.imglib.container.basictypecontainer.DataAccess;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.sampler.RasterSampler;
import mpicbg.imglib.sampler.array.ArrayIterableCursor;
import mpicbg.imglib.sampler.array.ArrayLocalizableCursor;
import mpicbg.imglib.sampler.array.ArrayLocalizablePlaneCursor;
import mpicbg.imglib.sampler.array.ArrayPositionableCursor;
import mpicbg.imglib.sampler.array.ArrayPositionableOutOfBoundsCursor;
import mpicbg.imglib.type.Type;

/**
 * This {@link Container} stores an image in a single linear array of basic
 * types.  By that, it provides the fastest possible access to data while
 * limiting the number of basic types stored to {@link Integer#MAX_VALUE}.
 * Keep in mind that this does not necessarily reflect the number of pixels,
 * because a pixel can be stored in less than or more than a basic type entry.
 * 
 * @param <T>
 * @param <A>
 *
 * @author Stephan Preibisch and Stephan Saalfeld
 */
public class Array< T extends Type< T >, A extends DataAccess > extends AbstractDirectAccessContainer< T, A >
{
	final protected int[] step;

	final ArrayContainerFactory factory;

	// the DataAccess created by the ArrayContainerFactory
	final A data;

	public Array( final ArrayContainerFactory factory, final A data, final int[] dim, final int entitiesPerPixel )
	{
		super( factory, dim, entitiesPerPixel );

		step = Array.createAllocationSteps( dim );
		this.factory = factory;
		this.data = data;
	}

	@Override
	public A update( final RasterSampler< ? > c )
	{
		return data;
	}

	@Override
	public ArrayContainerFactory getFactory()
	{
		return factory;
	}

	@Override
	public ArrayIterableCursor< T > createIterableCursor( final Image< T > image )
	{
		ArrayIterableCursor< T > c = new ArrayIterableCursor< T >( this, image );
		return c;
	}

	@Override
	public ArrayLocalizableCursor< T > createLocalizableCursor( final Image< T > image )
	{
		ArrayLocalizableCursor< T > c = new ArrayLocalizableCursor< T >( this, image );
		return c;
	}

	@Override
	public ArrayLocalizablePlaneCursor< T > createLocalizablePlaneCursor( final Image< T > image )
	{
		ArrayLocalizablePlaneCursor< T > c = new ArrayLocalizablePlaneCursor< T >( this, image );
		return c;
	}

	@Override
	public ArrayPositionableCursor< T > createPositionableCursor( final Image< T > image )
	{
		ArrayPositionableCursor< T > c = new ArrayPositionableCursor< T >( this, image );
		return c;
	}

	@Override
	public ArrayPositionableOutOfBoundsCursor< T > createPositionableCursor( final Image< T > image, final OutOfBoundsStrategyFactory< T > outOfBoundsFactory )
	{
		ArrayPositionableOutOfBoundsCursor< T > c = new ArrayPositionableOutOfBoundsCursor< T >( this, image, outOfBoundsFactory );
		return c;
	}

	public static int[] createAllocationSteps( final int[] dim )
	{
		int[] steps = new int[ dim.length ];
		createAllocationSteps( dim, steps );
		return steps;
	}

	public static void createAllocationSteps( final int[] dim, final int[] steps )
	{
		steps[ 0 ] = 1;
		for ( int d = 1; d < dim.length; ++d )
			steps[ d ] = steps[ d - 1 ] * dim[ d - 1 ];
	}

	public final int getPos( final int[] l )
	{
		int i = l[ 0 ];
		for ( int d = 1; d < numDimensions; ++d )
			i += l[ d ] * step[ d ];

		return i;
	}

	final public void indexToPosition( int i, final int[] l )
	{
		for ( int d = numDimensions - 1; d >= 0; --d )
		{
			final int ld = i / step[ d ];
			l[ d ] = ld;
			i -= ld * step[ d ];
			// i %= step[ d ];
		}
	}

	final public void indexToPosition( int i, final long[] l )
	{
		for ( int d = numDimensions - 1; d >= 0; --d )
		{
			final int ld = i / step[ d ];
			l[ d ] = ld;
			i -= ld * step[ d ];
			// i %= step[ d ];
		}
	}

	final public int indexToPosition( int i, final int dim )
	{
		for ( int d = numDimensions - 1; d > dim; --d )
			i %= step[ d ];

		return i / step[ dim ];
	}

	@Override
	public void close()
	{
		data.close();
	}
}
