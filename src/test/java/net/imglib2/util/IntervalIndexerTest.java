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
					dim.length,
					new FinalInterval( min, max ) );
			testIndexToPosition( interval );
			testPositionToIndex( interval );
		}
	}

	public void testIndexToPosition( final Interval interval )
	{
		final long numElements = Intervals.numElements( interval );
		final long[] pos = new long[ dim.length ];
		final long[] min = Intervals.minAsLongArray( interval );
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
		final long[] min = Intervals.minAsLongArray( interval );
		for ( final Cursor< ? > cursor = Views.flatIterable( interval ).localizingCursor(); cursor.hasNext(); )
		{
			cursor.fwd();
			cursor.localize( pos );
			Assert.assertEquals( IntervalIndexer.positionWithOffsetToIndex( pos, dim, min ), IntervalIndexer.positionToIndexForInterval( cursor, interval ) );
		}
	}

}
