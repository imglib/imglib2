package net.imglib2.img;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.util.IntervalIndexer;
import net.imglib2.view.Views;
import net.imglib2.view.iteration.SubIntervalIterable;

import org.junit.Test;

public abstract class AbstractSubIntervalIterableCursorTest< T extends Img< IntType > & SubIntervalIterable< IntType >>
{
	/** dimensions of the tested Image. dimension 0 of all should be at least 18 */
	protected long[] dimensions;

	protected int[] intData;

	/** Img to test on */
	protected T img;

	/** Interval which is optimizable with a special SubIntervalCursor */
	protected Interval intervalFast;

	/** Interval which is not optimizable */
	protected Interval intervalShifted;

	/*
	 * Cursor
	 */

	@Test
	public void testIterationFast()
	{
		Cursor< IntType > cursor = img.cursor( intervalFast );

		testCursorIteration( cursor, intervalFast );
	}

	@Test
	public void testIterationShifted()
	{
		Cursor< IntType > cursor = img.cursor( intervalShifted );

		testCursorIteration( cursor, intervalShifted );
	}

	@Test
	public void testJumpFwdFast()
	{
		Cursor< IntType > cursor = Views.interval( img, intervalFast ).cursor();

		testCursorJumpFwd( cursor, intervalFast );
	}

	@Test
	public void testJumpFwdShifted()
	{
		Cursor< IntType > cursor = Views.interval( img, intervalShifted ).cursor();

		testCursorJumpFwd( cursor, intervalShifted );
	}

	/*
	 * Localizing Cursor
	 */

	@Test
	public void testLocalizingIterationFast()
	{
		Cursor< IntType > cursor = img.localizingCursor( intervalFast );

		testCursorIteration( cursor, intervalFast );
	}

	@Test
	public void testLocalizingIterationShifted()
	{
		Cursor< IntType > cursor = img.localizingCursor( intervalShifted );

		testCursorIteration( cursor, intervalShifted );
	}

	@Test
	public void testLocalizingJumpFwdFast()
	{
		Cursor< IntType > cursor = Views.interval( img, intervalFast ).localizingCursor();

		testCursorJumpFwd( cursor, intervalFast );
	}
	
	@Test
	public void testLocalizingJumpFwdShifted()
	{
		Cursor< IntType > cursor = Views.interval( img, intervalShifted ).localizingCursor();

		testCursorJumpFwd( cursor, intervalShifted );
	}

	protected void testCursorIteration( Cursor< IntType > cursor, Interval i )
	{
		long[] position = new long[ cursor.numDimensions() ];
		long[] min = new long[ cursor.numDimensions() ];
		long[] max = new long[ cursor.numDimensions() ];

		i.min( min );

		cursor.fwd();
		cursor.localize( position );
		assertArrayEquals( "start position was incorrect.", min, position );

		cursor.reset();

		int ctr = 0;
		long sum = 0;

		while ( cursor.hasNext() )
		{
			cursor.fwd();
			cursor.localize( position );
			sum += cursor.get().get();
			ctr++;
		}

		i.max( max );

		assertEquals( "wrong number of elements accessed.", getIntervalSize( i ), ctr );
		assertArrayEquals( "end position incorrect.", max, position );
		assertEquals( "sum of elements incorrect.", sum, getSum( i ) );
	}

	protected void testCursorJumpFwd( Cursor< IntType > cursor, Interval i )
	{
		long[] position = new long[ cursor.numDimensions() ];
		long[] ref = new long[ cursor.numDimensions() ];

		i.min( ref );

		ref[ 0 ] += 17;
		cursor.jumpFwd( 18 );
		cursor.localize( position );

		assertArrayEquals( "jumpFwd position incorrect.", ref, position );
	}

	// HELPER

	protected final long getSum( Interval interval )
	{
		long[] pos = new long[ interval.numDimensions() ];
		long sum = 0;

		for ( int i = 0; i < intData.length; ++i )
		{
			IntervalIndexer.indexToPosition( i, dimensions, pos );

			boolean in = true;
			for ( int j = 0; j < pos.length; j++ )
			{
				if ( pos[ j ] < interval.min( j ) || pos[ j ] > interval.max( j ) )
				{
					in = false;
					break;
				}
			}

			if ( in )
			{
				sum += intData[ i ];
			}
		}

		return sum;
	}

	protected final long getIntervalSize( Interval interval )
	{
		long size = interval.dimension( 0 );

		for ( int i = 1; i < interval.numDimensions(); ++i )
		{
			size *= interval.dimension( i );
		}

		return size;
	}

}
