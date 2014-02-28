/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
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

package net.imglib2.algorithm.region.localneighborhood;

import java.util.Iterator;

import net.imglib2.AbstractInterval;
import net.imglib2.Cursor;
import net.imglib2.FlatIterationOrder;
import net.imglib2.Interval;
import net.imglib2.IterableInterval;
import net.imglib2.IterableRealInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.view.Views;

/**
 * A factory for Accessibles on hyper-sphere neighboorhoods.
 * 
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class HyperSphereShape implements Shape
{
	final long radius;

	public HyperSphereShape( final long radius )
	{
		this.radius = radius;
	}

	@Override
	public < T > IterableInterval< Neighborhood< T >> neighborhoods( final RandomAccessibleInterval< T > source )
	{
		return Views.iterable( neighborhoodsRandomAccessible( source ) );
	}

	@Override
	public < T > NeighborhoodsAccessible< T > neighborhoodsRandomAccessible( final RandomAccessibleInterval< T > source )
	{
		return new NeighborhoodsAccessible< T >( source, radius, HyperSphereNeighborhoodUnsafe.< T >factory() );
	}

	@Override
	public < T > IterableInterval< Neighborhood< T >> neighborhoodsSafe( final RandomAccessibleInterval< T > source )
	{
		return Views.iterable( neighborhoodsRandomAccessible( source ) );
	}

	@Override
	public < T > NeighborhoodsAccessible< T > neighborhoodsRandomAccessibleSafe( final RandomAccessibleInterval< T > source )
	{
		return new NeighborhoodsAccessible< T >( source, radius, HyperSphereNeighborhood.< T >factory() );
	}

	public static final class NeighborhoodsAccessible< T > extends AbstractInterval implements RandomAccessibleInterval< Neighborhood< T > >, IterableInterval< Neighborhood< T > >
	{
		final RandomAccessibleInterval< T > source;

		final long radius;

		final long size;

		final HyperSphereNeighborhoodFactory< T > factory;

		public NeighborhoodsAccessible( final RandomAccessibleInterval< T > source, final long radius, final HyperSphereNeighborhoodFactory< T > factory )
		{
			super( source );
			this.source = source;
			this.radius = radius;
			this.factory = factory;

			long s = source.dimension( 0 );
			for ( int d = 1; d < n; ++d )
				s *= source.dimension( d );
			size = s;
		}

		@Override
		public RandomAccess< Neighborhood< T >> randomAccess()
		{
			return new HyperSphereNeighborhoodRandomAccess< T >( source, radius, factory );
		}

		@Override
		public RandomAccess< Neighborhood< T >> randomAccess( final Interval interval )
		{
			return randomAccess();
		}

		@Override
		public long size()
		{
			return size;
		}

		@Override
		public Neighborhood< T > firstElement()
		{
			return cursor().next();
		}

		@Override
		public Object iterationOrder()
		{
			return new FlatIterationOrder( this );
		}

		@Override
		public boolean equalIterationOrder( final IterableRealInterval< ? > f )
		{
			return iterationOrder().equals( f.iterationOrder() );
		}

		@Override
		public Iterator< Neighborhood< T >> iterator()
		{
			return cursor();
		}

		@Override
		public Cursor< Neighborhood< T >> cursor()
		{
			return new HyperSphereNeighborhoodCursor< T >( source, radius, factory );
		}

		@Override
		public Cursor< Neighborhood< T >> localizingCursor()
		{
			return cursor();
		}
	}
}
