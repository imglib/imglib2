/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2016 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
package net.imglib2.neighborhood;

import java.util.Arrays;
import java.util.Iterator;

import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.AbstractInterval;
import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.FlatIterationOrder;
import net.imglib2.Interval;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;

/**
 * TODO
 * 
 * @author Jean-Yves Tinevez
 * @author Jonathan Hale (University of Konstanz)
 */
public class DiamondShape implements Shape
{

	private final long radius;

	/**
	 * Constructor
	 * 
	 * @param radius
	 *            Radius for the diamond shape.
	 */
	public DiamondShape( final long radius )
	{
		this.radius = radius;
	}

	@Override
	public < T > IterableInterval< Neighborhood< T >> neighborhoods( final RandomAccessibleInterval< T > source )
	{
		return new NeighborhoodsIterableInterval< T >( source, radius, DiamondNeighborhoodUnsafe.< T >factory() );
	}

	@Override
	public < T > NeighborhoodsAccessible< T > neighborhoodsRandomAccessible( final RandomAccessible< T > source )
	{
		final DiamondNeighborhoodFactory< T > f = DiamondNeighborhoodUnsafe.< T >factory();
		return new NeighborhoodsAccessible< T >( source, radius, f );
	}

	@Override
	public < T > NeighborhoodsIterableInterval< T > neighborhoodsSafe( final RandomAccessibleInterval< T > source )
	{
		return new NeighborhoodsIterableInterval< T >( source, radius, DiamondNeighborhood.< T >factory() );
	}

	@Override
	public < T > NeighborhoodsAccessible< T > neighborhoodsRandomAccessibleSafe( final RandomAccessible< T > source )
	{
		return new NeighborhoodsAccessible< T >( source, radius, DiamondNeighborhood.< T >factory() );
	}

	/**
	 * @return Radius of this shape.
	 * @see DiamondShape#DiamondShape(long)
	 */
	public long getRadius()
	{
		return radius;
	}

	@Override
	public String toString()
	{
		return "DiamondShape, radius = " + radius;
	}

	public static final class NeighborhoodsIterableInterval< T > extends AbstractInterval implements IterableInterval< Neighborhood< T > >
	{
		final RandomAccessibleInterval< T > source;

		final DiamondNeighborhoodFactory< T > factory;

		final long size;

		private final long radius;

		public NeighborhoodsIterableInterval( final RandomAccessibleInterval< T > source, final long radius, final DiamondNeighborhoodFactory< T > factory )
		{
			super( source );
			this.source = source;
			this.radius = radius;
			this.factory = factory;
			long s = source.dimension( 0 );
			for ( int d = 1; d < n; ++d )
			{
				s *= source.dimension( d );
			}
			size = s;
		}

		@Override
		public Cursor< Neighborhood< T >> cursor()
		{
			return new DiamondNeighborhoodCursor< T >( source, radius, factory );
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
		public Cursor< Neighborhood< T >> localizingCursor()
		{
			return cursor();
		}

		@Override
		public Iterator< Neighborhood< T >> iterator()
		{
			return cursor();
		}
	}

	public static final class NeighborhoodsAccessible< T > extends AbstractEuclideanSpace implements RandomAccessible< Neighborhood< T > >
	{
		final RandomAccessible< T > source;

		final DiamondNeighborhoodFactory< T > factory;

		private final long radius;

		public NeighborhoodsAccessible( final RandomAccessible< T > source, final long radius, final DiamondNeighborhoodFactory< T > factory )
		{
			super( source.numDimensions() );
			this.source = source;
			this.radius = radius;
			this.factory = factory;
		}

		@Override
		public RandomAccess< Neighborhood< T >> randomAccess()
		{
			return new DiamondNeighborhoodRandomAccess< T >( source, radius, factory );
		}

		@Override
		public RandomAccess< Neighborhood< T >> randomAccess( final Interval interval )
		{
			return randomAccess();
		}

		@Override
		public int numDimensions()
		{
			return source.numDimensions();
		}

	}

	@Override
	public Interval getStructuringElementBoundingBox(final int numDimensions) {
		final long[] min = new long[numDimensions];
		Arrays.fill(min, -getRadius());

		final long[] max = new long[numDimensions];
		Arrays.fill(max, getRadius());

		return new FinalInterval(min, max);
	}
}
