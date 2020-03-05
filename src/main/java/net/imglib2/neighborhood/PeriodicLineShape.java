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
import net.imglib2.util.Util;

/**
 * A factory for Accessibles on {@link PeriodicLineNeighborhood}s, that iterate
 * over what is termed "Periodic lines", and is best explained in Ronald Jones
 * and Pierre Soilles publication:
 * <p>
 * <tt>Jones and Soilles. Periodic lines: Definition, cascades, and application
 * to granulometries. Pattern Recognition Letters (1996) vol. 17 (10) pp. 1057-1063</tt>
 * 
 * @author Jean-Yves Tinevez, 2013
 * @author Jonathan Hale (University of Konstanz)
 */
public class PeriodicLineShape implements Shape
{
	private final long span;

	private final int[] increments;

	/**
	 * Creates a new periodic line shape, that will iterate over
	 * {@code 2 × span + 1} pixels as follow:
	 * 
	 * <pre>
	 * position - span x increments,
	 * ...
	 * position - 2 × increments,
	 * position - increments,
	 * position,
	 * position + increments,
	 * position + 2 × increments,
	 * ...
	 * position + span x increments
	 * </pre>
	 * 
	 * @param span
	 *            the span of the neighborhood, so that it will iterate over
	 *            {@code 2 × span + 1} pixels. Must be positive.
	 * @param increments
	 *            the values by which each element of the position vector is to
	 *            be incremented when iterating.
	 */
	public PeriodicLineShape( final long span, final int[] increments )
	{
		if ( span < 0 ) { throw new IllegalArgumentException( "Span cannot be negative." ); }
		this.span = span;
		this.increments = increments;
	}

	@Override
	public < T > NeighborhoodsIterableInterval< T > neighborhoods( final RandomAccessibleInterval< T > source )
	{
		final PeriodicLineNeighborhoodFactory< T > f = PeriodicLineNeighborhoodUnsafe.< T >factory();
		return new NeighborhoodsIterableInterval< T >( source, span, increments, f );
	}

	@Override
	public < T > NeighborhoodsAccessible< T > neighborhoodsRandomAccessible( final RandomAccessible< T > source )
	{
		final PeriodicLineNeighborhoodFactory< T > f = PeriodicLineNeighborhoodUnsafe.< T >factory();
		return new NeighborhoodsAccessible< T >( source, span, increments, f );
	}

	@Override
	public < T > IterableInterval< Neighborhood< T >> neighborhoodsSafe( final RandomAccessibleInterval< T > source )
	{
		final PeriodicLineNeighborhoodFactory< T > f = PeriodicLineNeighborhood.< T >factory();
		return new NeighborhoodsIterableInterval< T >( source, span, increments, f );
	}

	@Override
	public < T > NeighborhoodsAccessible< T > neighborhoodsRandomAccessibleSafe( final RandomAccessible< T > source )
	{
		final PeriodicLineNeighborhoodFactory< T > f = PeriodicLineNeighborhood.< T >factory();
		return new NeighborhoodsAccessible< T >( source, span, increments, f );
	}

	/**
	 * @return The span of this shape.
	 */
	public long getSpan()
	{
		return span;
	}
	
	/**
	 * @return Copy of the increments of this shape.
	 */
	public int[] getIncrements()
	{
		return increments.clone();
	}
	
	@Override
	public String toString()
	{
		return "PeriodicLineShape, span = " + span + ", increments = " + Util.printCoordinates( increments );
	}

	public static final class NeighborhoodsIterableInterval< T > extends AbstractInterval implements IterableInterval< Neighborhood< T > >
	{
		final RandomAccessibleInterval< T > source;

		final long span;

		final PeriodicLineNeighborhoodFactory< T > factory;

		final long size;

		final int[] increments;

		public NeighborhoodsIterableInterval( final RandomAccessibleInterval< T > source, final long span, final int[] increments, final PeriodicLineNeighborhoodFactory< T > factory )
		{
			super( source );
			this.source = source;
			this.span = span;
			this.increments = increments;
			this.factory = factory;
			long s = source.dimension( 0 );
			for ( int d = 1; d < n; ++d )
				s *= source.dimension( d );
			size = s;
		}

		@Override
		public Cursor< Neighborhood< T >> cursor()
		{
			return new PeriodicLineNeighborhoodCursor< T >( source, span, increments, factory );
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

		final PeriodicLineNeighborhoodFactory< T > factory;

		private final long span;

		private final int[] increments;

		public NeighborhoodsAccessible( final RandomAccessible< T > source, final long span, final int[] increments, final PeriodicLineNeighborhoodFactory< T > factory )
		{
			super( source.numDimensions() );
			this.source = source;
			this.span = span;
			this.increments = increments;
			this.factory = factory;
		}

		@Override
		public RandomAccess< Neighborhood< T >> randomAccess()
		{
			return new PeriodicLineNeighborhoodRandomAccess< T >( source, span, increments, factory );
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

		for (int i = 0; i < numDimensions; ++i) {
			a[i] = increments[i] * -getSpan();
			b[i] = increments[i] * getSpan();
		}

		return Intervals.union(new FinalInterval(a, a), new FinalInterval(b, b));
	}

}
