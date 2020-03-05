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
import net.imglib2.util.Intervals;

/**
 * A {@link Shape} representing finite, centered, symmetric lines, that are
 * parallel to the image axes.
 * 
 * @author Jean-Yves Tinevez#
 * @author Jonathan Hale (University of Konstanz)
 */
public class HorizontalLineShape implements Shape
{
	private final long span;

	private final boolean skipCenter;

	private final int dim;

	/**
	 * Create a new line shape.
	 * 
	 * @param span
	 *            the span of the line in both directions, so that its total
	 *            extend is given by {@code 2 x span + 1}.
	 * @param dim
	 *            the dimension along which to lay this line.
	 * @param skipCenter
	 *            if {@code true}, the shape will skip the center point of
	 *            the line.
	 */
	public HorizontalLineShape( final long span, final int dim, final boolean skipCenter )
	{
		this.span = span;
		this.dim = dim;
		this.skipCenter = skipCenter;
	}

	@Override
	public < T > NeighborhoodsIterableInterval< T > neighborhoods( final RandomAccessibleInterval< T > source )
	{
		return new NeighborhoodsIterableInterval< T >( source, span, dim, skipCenter, HorizontalLineNeighborhoodUnsafe.< T >factory() );
	}

	@Override
	public < T > NeighborhoodsAccessible< T > neighborhoodsRandomAccessible( final RandomAccessible< T > source )
	{
		final HorizontalLineNeighborhoodFactory< T > f = HorizontalLineNeighborhoodUnsafe.< T >factory();
		return new NeighborhoodsAccessible< T >( source, span, dim, skipCenter, f );
	}

	@Override
	public < T > IterableInterval< Neighborhood< T >> neighborhoodsSafe( final RandomAccessibleInterval< T > source )
	{
		return new NeighborhoodsIterableInterval< T >( source, span, dim, skipCenter, HorizontalLineNeighborhood.< T >factory() );
	}

	@Override
	public < T > NeighborhoodsAccessible< T > neighborhoodsRandomAccessibleSafe( final RandomAccessible< T > source )
	{
		final HorizontalLineNeighborhoodFactory< T > f = HorizontalLineNeighborhood.< T >factory();
		return new NeighborhoodsAccessible< T >( source, span, dim, skipCenter, f );
	}

	/**
	 * @return {@code true} if {@code skipCenter} was set to true
	 *         during construction, {@code false} otherwise.
	 * @see CenteredRectangleShape#CenteredRectangleShape(int[], boolean)
	 */
	public boolean isSkippingCenter()
	{
		return skipCenter;
	}

	/**
	 * @return The span of this shape.
	 */
	public long getSpan()
	{
		return span;
	}
	
	/**
	 * @return The dimension along which the line is layed.
	 */
	public int getLineDimension()
	{
		return dim;
	}
	
	@Override
	public String toString()
	{
		return "HorizontalLineShape, span = " + span + " in dim " + dim + ", skipCenter = " + skipCenter;
	}

	public static final class NeighborhoodsIterableInterval< T > extends AbstractInterval implements IterableInterval< Neighborhood< T > >
	{
		final RandomAccessibleInterval< T > source;

		final long span;

		final HorizontalLineNeighborhoodFactory< T > factory;

		final long size;

		final int dim;

		final boolean skipCenter;

		public NeighborhoodsIterableInterval( final RandomAccessibleInterval< T > source, final long span, final int dim, final boolean skipCenter, final HorizontalLineNeighborhoodFactory< T > factory )
		{
			super( source );
			this.source = source;
			this.span = span;
			this.dim = dim;
			this.skipCenter = skipCenter;
			this.factory = factory;
			long s = source.dimension( 0 );
			for ( int d = 1; d < n; ++d )
				s *= source.dimension( d );
			size = s;
		}

		@Override
		public Cursor< Neighborhood< T >> cursor()
		{
			return new HorizontalLineNeighborhoodCursor< T >( source, span, dim, skipCenter, factory );
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
		public Iterator< Neighborhood< T >> iterator()
		{
			return cursor();
		}

		@Override
		public Cursor< Neighborhood< T >> localizingCursor()
		{
			return cursor();
		}
	}

	public static final class NeighborhoodsAccessible< T > extends AbstractEuclideanSpace implements RandomAccessible< Neighborhood< T > >
	{
		final RandomAccessible< T > source;

		final HorizontalLineNeighborhoodFactory< T > factory;

		private final long span;

		private final int dim;

		private final boolean skipCenter;

		public NeighborhoodsAccessible( final RandomAccessible< T > source, final long span, final int dim, final boolean skipCenter, final HorizontalLineNeighborhoodFactory< T > factory )
		{
			super( source.numDimensions() );
			this.source = source;
			this.span = span;
			this.dim = dim;
			this.skipCenter = skipCenter;
			this.factory = factory;
		}

		@Override
		public RandomAccess< Neighborhood< T >> randomAccess()
		{
			return new HorizontalLineNeighborhoodRandomAccess< T >( source, span, dim, skipCenter, factory );
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
		final long[] a = new long[numDimensions];
		final long[] b = new long[numDimensions];

		a[dim] = -span;
		b[dim] = span;

		return Intervals.union(new FinalInterval(a, a), new FinalInterval(b, b));
	}

}
