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

import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.neighborhood.RectangleShape.NeighborhoodsAccessible;
import net.imglib2.neighborhood.RectangleShape.NeighborhoodsIterableInterval;
import net.imglib2.util.Intervals;
import net.imglib2.util.Util;

/**
 * A factory for Accessibles on rectangular neighborhoods.
 * <p>
 * This specific factory differs to {@link RectangleShape} in that it allows
 * non-isotropic rectangular shapes. However, it constrains the the neighborhood
 * to be symmetric by its origin.
 * <p>
 * The size of the neighborhood is specified by an {@code int[]} span
 * array, so that in every dimension {@code d}, the extent of the
 * neighborhood is given by {@code 2 × span[d] + 1}.
 * <p>
 * This factory exists because {@link RectangleShape} which is based on the same
 * components, only allows for square neighborhoods.
 *
 * @author Tobias Pietzsch
 * @author Jean-Yves Tinevez
 * @author Jonathan Hale (University of Konstanz)
 */
public class CenteredRectangleShape implements Shape
{
	final int[] span;

	final boolean skipCenter;

	/**
	 * Constructs a factory for symmetric, non-isotropic rectangle
	 * neighborhoods.
	 * <p>
	 * The size of the neighborhood is specified by an {@code int[]} span
	 * array, so that in every dimension {@code d}, the extent of the
	 * neighborhood is given by {@code 2 × span[d] + 1}.
	 *
	 * @param span
	 *            the span of the neighborhood.
	 * @param skipCenter
	 *            whether we should skip the central pixel or not.
	 */
	public CenteredRectangleShape( final int[] span, final boolean skipCenter )
	{
		this.span = span;
		this.skipCenter = skipCenter;
	}

	@Override
	public < T > NeighborhoodsIterableInterval< T > neighborhoods( final RandomAccessibleInterval< T > source )
	{
		final RectangleNeighborhoodFactory< T > f = skipCenter ? RectangleNeighborhoodSkipCenterUnsafe.< T >factory() : RectangleNeighborhoodUnsafe.< T >factory();
		final Interval spanInterval = createSpan();
		return new NeighborhoodsIterableInterval< T >( source, spanInterval, f );
	}

	@Override
	public < T > NeighborhoodsAccessible< T > neighborhoodsRandomAccessible( final RandomAccessible< T > source )
	{
		final RectangleNeighborhoodFactory< T > f = skipCenter ? RectangleNeighborhoodSkipCenterUnsafe.< T >factory() : RectangleNeighborhoodUnsafe.< T >factory();
		final Interval spanInterval = createSpan();
		return new NeighborhoodsAccessible< T >( source, spanInterval, f );
	}

	@Override
	public < T > NeighborhoodsIterableInterval< T > neighborhoodsSafe( final RandomAccessibleInterval< T > source )
	{
		final RectangleNeighborhoodFactory< T > f = skipCenter ? RectangleNeighborhoodSkipCenter.< T >factory() : RectangleNeighborhood.< T >factory();
		final Interval spanInterval = createSpan();
		return new NeighborhoodsIterableInterval< T >( source, spanInterval, f );
	}

	@Override
	public < T > NeighborhoodsAccessible< T > neighborhoodsRandomAccessibleSafe( final RandomAccessible< T > source )
	{
		final RectangleNeighborhoodFactory< T > f = skipCenter ? RectangleNeighborhoodSkipCenter.< T >factory() : RectangleNeighborhood.< T >factory();
		final Interval spanInterval = createSpan();
		return new NeighborhoodsAccessible< T >( source, spanInterval, f );
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
	 * @return Copy of the span of this shape.
	 */
	public int[] getSpan()
	{
		return span.clone();
	}

	@Override
	public String toString()
	{
		return "CenteredRectangleShape, span = " + Util.printCoordinates( span );
	}

	private Interval createSpan()
	{
		final long[] min = new long[ span.length ];
		final long[] max = new long[ span.length ];
		for ( int d = 0; d < span.length; ++d )
		{
			min[ d ] = -span[ d ];
			max[ d ] = span[ d ];
		}
		return new FinalInterval( min, max );
	}

	@Override
	public Interval getStructuringElementBoundingBox(final int numDimensions) {
		final long[] a = new long[numDimensions];
		final long[] b = new long[numDimensions];

		for (int i = 0; i < numDimensions; ++i) {
			a[i] = span[i] * -1;
			b[i] = span[i];
		}

		return Intervals.union(new FinalInterval(a, a), new FinalInterval(b, b));
	}
}
