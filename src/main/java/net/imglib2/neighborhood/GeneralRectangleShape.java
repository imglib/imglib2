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
import net.imglib2.neighborhood.RectangleShape.NeighborhoodsAccessible;
import net.imglib2.neighborhood.RectangleShape.NeighborhoodsIterableInterval;

/**
 * A factory for Accessibles on rectangular neighboorhoods.
 *
 * @author Tobias Pietzsch
 * @author Jonathan Hale (University of Konstanz)
 * @author John Bogovic
 */
public class GeneralRectangleShape extends RectangleShape
{
	final int offset;

	final int size;

	/**
	 * @param span
	 * @param skipCenter
	 */
	public GeneralRectangleShape( final int size, final int offset, boolean skipCenter )
	{
		super( -1, skipCenter );
		this.offset = offset;
		this.size = size;
	}

	@Override
	public < T > NeighborhoodsIterableInterval< T > neighborhoods( final RandomAccessibleInterval< T > source )
	{
		final RectangleNeighborhoodFactory< T > f = skipCenter ? RectangleNeighborhoodSkipCenterUnsafe.< T >factory() : RectangleNeighborhoodUnsafe.< T >factory();
		final Interval spanInterval = createSpan( source.numDimensions() );
		return new NeighborhoodsIterableInterval< T >( source, spanInterval, f );
	}

	@Override
	public < T > NeighborhoodsAccessible< T > neighborhoodsRandomAccessible( final RandomAccessible< T > source )
	{
		final RectangleNeighborhoodFactory< T > f = skipCenter ? RectangleNeighborhoodSkipCenterUnsafe.< T >factory() : RectangleNeighborhoodUnsafe.< T >factory();
		final Interval spanInterval = createSpan( source.numDimensions() );
		return new NeighborhoodsAccessible< T >( source, spanInterval, f );
	}

	@Override
	public < T > NeighborhoodsIterableInterval< T > neighborhoodsSafe( final RandomAccessibleInterval< T > source )
	{
		final RectangleNeighborhoodFactory< T > f = skipCenter ? RectangleNeighborhoodSkipCenter.< T >factory() : RectangleNeighborhood.< T >factory();
		final Interval spanInterval = createSpan( source.numDimensions() );
		return new NeighborhoodsIterableInterval< T >( source, spanInterval, f );
	}

	@Override
	public < T > NeighborhoodsAccessible< T > neighborhoodsRandomAccessibleSafe( final RandomAccessible< T > source )
	{
		final RectangleNeighborhoodFactory< T > f = skipCenter ? RectangleNeighborhoodSkipCenter.< T >factory() : RectangleNeighborhood.< T >factory();
		final Interval spanInterval = createSpan( source.numDimensions() );
		return new NeighborhoodsAccessible< T >( source, spanInterval, f );
	}

	private Interval createSpan( final int n )
	{
		final long[] min = new long[ n ];
		final long[] max = new long[ n ];
		for ( int d = 0; d < n; ++d )
		{
			min[ d ] = offset;
			max[ d ] = size + offset - 1;
		}
		return new FinalInterval( min, max );
	}

	/**
	 * @return The size of this shape.
	 */
	public int getSize()
	{
		return size;
	}

	/**
	 * @return The offset of this shape.
	 */
	public int getOffset()
	{
		return offset;
	}
	
	@Override
	public String toString()
	{
		return String.format( "GeneralRectangleShape, size = %d, offset = %d  ", size, offset );
	}

	@Override
	public Interval getStructuringElementBoundingBox( final int numDimensions )
	{
		final long[] min = new long[ numDimensions ];
		Arrays.fill( min, getOffset() );

		final long[] max = new long[ numDimensions ];
		Arrays.fill( max, getSize() + getOffset() - 1  );

		return new FinalInterval( min, max );
	}
}
