/*-
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2025 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
package net.imglib2.util;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.Point;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.view.Views;

/**
 *
 * @author Philipp Hanslovsky
 *
 *
 *         This tests only consistency of the newly added methods
 *         {@link IntervalIndexer#indexToPositionForInterval} and
 *         {@link IntervalIndexer#positionToIndexForInterval} with previously
 *         existing methods.
 */
public class IntervalIndexerTest
{

	private final long[] dim = new long[] { 6, 7, 8, 9 };

	private final int numOffsets = 10;

	private final Random rng = new Random( 100 );

	@Test
	public void test()
	{
		for ( int i = 0; i < numOffsets; ++i )
		{

			final long[] min = new long[ dim.length ];
			final long[] max = new long[ dim.length ];
			for ( int k = 0; k < min.length; ++k )
			{
				min[ k ] = rng.nextInt();
				max[ k ] = min[ k ] + dim[ k ] - 1;
			}
			final RandomAccessibleInterval< DoubleType > interval = ConstantUtils.constantRandomAccessibleInterval(
					new DoubleType(),
					new FinalInterval( min, max ) );
			testIndexToPosition( interval );
			testPositionToIndex( interval );
		}
	}

	public void testIndexToPosition( final Interval interval )
	{
		final long numElements = Intervals.numElements( interval );
		final long[] pos = new long[ dim.length ];
		final long[] min = interval.minAsLongArray();
		final long[] store = new long[ dim.length ];
		final Point positionable = Point.wrap( store );
		for ( long index = 0; index < numElements; ++index )
		{
			IntervalIndexer.indexToPositionWithOffset( index, dim, min, pos );
			IntervalIndexer.indexToPositionForInterval( index, interval, positionable );
			Assert.assertArrayEquals( pos, store );
		}
	}

	public void testPositionToIndex( final RandomAccessibleInterval< ? > interval )
	{
		final long[] pos = new long[ dim.length ];
		final long[] min = interval.minAsLongArray();
		for ( final Cursor< ? > cursor = Views.flatIterable( interval ).localizingCursor(); cursor.hasNext(); )
		{
			cursor.fwd();
			cursor.localize( pos );
			Assert.assertEquals( IntervalIndexer.positionWithOffsetToIndex( pos, dim, min ), IntervalIndexer.positionToIndexForInterval( cursor, interval ) );
		}
	}

}
