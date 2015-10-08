package net.imglib2.img.array;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.img.AbstractSubIntervalIterableCursorTest;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.util.IntervalIndexer;
import net.imglib2.view.Views;

import org.junit.Before;
import org.junit.Test;

/**
 * ArrayIterableSubIntervalCursorTest
 * 
 * TODO Javadoc
 * 
 */
public class ArrayIterableSubIntervalCursorTest extends AbstractSubIntervalIterableCursorTest< ArrayImg< IntType, ? >>
{
	int numValues;

	/*
	 * Interval to ensure optimized cursors are not created when not possible.
	 */
	Interval intervalFastPart;

	long intDataSum;

	@Before
	public void createSourceData()
	{
		dimensions = new long[] { 23, 31, 11, 7, 3 };

		intervalFast = new FinalInterval( new long[] { dimensions[ 0 ], dimensions[ 1 ], 5, 1, 1 } );

		intervalFastPart = new FinalInterval( new long[] { dimensions[ 0 ], 2, 3, 1, 1 } );

		intervalShifted = new FinalInterval( new long[] { 0, 0, 3, 5, 1 }, new long[] { dimensions[ 0 ] - 1, dimensions[ 1 ] - 1, 4, 5, 1 } );

		numValues = 1;
		for ( int d = 0; d < dimensions.length; ++d )
			numValues *= dimensions[ d ];

		intData = new int[ numValues ];
		intDataSum = 0;
		Random random = new Random( 0 );
		for ( int i = 0; i < numValues; ++i )
		{
			intData[ i ] = random.nextInt();
			intDataSum += intData[ i ];
		}

		img = ( ArrayImg< IntType, ? > ) new ArrayImgFactory< IntType >().create( dimensions, new IntType() );

		long[] pos = new long[ dimensions.length ];
		RandomAccess< IntType > a = img.randomAccess();

		for ( int i = 0; i < numValues; ++i )
		{
			IntervalIndexer.indexToPosition( i, dimensions, pos );
			a.setPosition( pos );
			a.get().set( intData[ i ] );
		}
	}

	@Test
	public void testOptimizable()
	{

		// Testing Cursor
		assertTrue( ( Views.interval( img, intervalFast ).cursor() instanceof ArraySubIntervalCursor ) );

		// Testing Localizing Cursor
		assertTrue( ( Views.interval( img, intervalFast ).localizingCursor() instanceof AbstractArrayLocalizingCursor ) );

		// Testing Cursor
		assertFalse( ( Views.interval( img, intervalFastPart ).cursor() instanceof ArraySubIntervalCursor ) );

		// Testing Localizing Cursor
		assertFalse( ( Views.interval( img, intervalFastPart ).localizingCursor() instanceof AbstractArrayLocalizingCursor ) );

		// Testing Cursor
		assertTrue( ( Views.interval( img, intervalShifted ).cursor() instanceof ArraySubIntervalCursor ) );

		// Testing Localizing Cursor
		assertTrue( ( Views.interval( img, intervalShifted ).localizingCursor() instanceof AbstractArrayLocalizingCursor ) );
	}

}
