package net.imglib2.img.planar;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Random;

import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.util.IntervalIndexer;
import net.imglib2.view.Views;

import org.junit.Before;
import org.junit.Test;

public class PlanarIterableSubIntervalCursorTest
{
	long[] dimensions = new long[] { 23, 31, 11, 7, 3 };

	Interval intervalA, intervalB, intervalC, intervalD;

	int numValues, fastintervalsize_A, shiftedintervalsize_C, singleplaneintervalsize_D;

	int[] intData;

	PlanarImg< IntType, ? > planarImg;

	@Before
	public void createSourceData()
	{
		
		// fastA -> d1,d2 full, subset of d3, position in d4,d5  => iterate over multiple planes  
		intervalA = new FinalInterval( new long[] { 23, 31, 5, 1, 1 } );

		// not optimized -> d1 full, subset of d2,d3 position in d4, d5 => cannot simply iterate over planes
		intervalB = new FinalInterval( new long[] { 23, 2, 3, 1, 1 } );

		// fastCShifted -> d1, d2 full, subset of d3, position in d4,d5 => iterate over multiple planes
		intervalC = new FinalInterval( new long[] { 0, 0, 3, 5, 1 }, new long[] { 22, 30, 4, 5, 1 } );
		
		// fastDSinglePlane -> d1,d2 full position for the rest => iterate over one plane
		intervalD = new FinalInterval( new long[] { 23, 31, 1, 1, 1 } );	

		// Size of the planes which can be iterated fast
		fastintervalsize_A = 23 * 31 * 5;
				
		shiftedintervalsize_C = 23 * 31 * 2;

		singleplaneintervalsize_D = 23 * 31;
		
		

		//create random data for all dims and fill the planar img
		numValues = 1;
		for ( int d = 0; d < dimensions.length; ++d )
			numValues *= dimensions[ d ];

		intData = new int[ numValues ];
		Random random = new Random( 0 );
		for ( int i = 0; i < numValues; ++i )
		{
			intData[ i ] = random.nextInt();
		}

		planarImg = ( PlanarImg< IntType, ? > ) new PlanarImgFactory< IntType >().create( dimensions, new IntType() );

		long[] pos = new long[ dimensions.length ];
		RandomAccess< IntType > a = planarImg.randomAccess();

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
		assertTrue( ( Views.interval( planarImg, intervalA ).cursor() instanceof PlanarSubsetCursor ) );

		// Testing Localizing Cursor
		assertTrue( ( Views.interval( planarImg, intervalA ).localizingCursor() instanceof PlanarSubsetLocalizingCursor ) );

		// Testing Cursor
		assertFalse( ( Views.interval( planarImg, intervalB ).cursor() instanceof PlanarSubsetCursor ) );

		// Testing Localizing Cursor
		assertFalse( ( Views.interval( planarImg, intervalB ).localizingCursor() instanceof PlanarSubsetLocalizingCursor ) );

		// Testing Cursor
		assertTrue( ( Views.interval( planarImg, intervalC ).cursor() instanceof PlanarSubsetCursor ) );

		// Testing Localizing Cursor
		assertTrue( ( Views.interval( planarImg, intervalC ).localizingCursor() instanceof PlanarSubsetLocalizingCursor ) );
		
		// Testing Cursor
		assertTrue( ( Views.interval( planarImg, intervalD ).cursor() instanceof PlanarSubsetCursor ) );

		// Testing Localizing Cursor
		assertTrue( ( Views.interval( planarImg, intervalD ).localizingCursor() instanceof PlanarSubsetLocalizingCursor ) );

	}

	@Test
	public void testIterationFast()
	{
		Cursor< IntType > cursor = planarImg.cursor( intervalA );

		long[] position = new long[ cursor.numDimensions() ];
		long[] max = new long[ cursor.numDimensions() ];

		int ctr = 0;
		long sum = 0;

		while ( cursor.hasNext() )
		{
			cursor.next();
			cursor.localize( position );
			sum += cursor.get().get();
			ctr++;
		}

		intervalA.max( max );

		assertTrue( Arrays.equals( max, position ) );
		assertTrue( ctr == fastintervalsize_A );
		assertTrue( sum == getSum(intervalA));

	}

	@Test
	public void testIterationShifted()
	{
		Cursor< IntType > cursor = Views.interval( planarImg, intervalC ).cursor();

		long[] position = new long[ cursor.numDimensions() ];
		long[] tmp = new long[ cursor.numDimensions() ];

		intervalC.min( tmp );

		cursor.fwd();
		cursor.localize( position );
		assertTrue( Arrays.equals( tmp, position ) );

		cursor.reset();
		int ctr = 0;
		long sum = 0;
		while ( cursor.hasNext() )
		{
			cursor.next();
			cursor.localize( position );
			sum += cursor.get().get();
			ctr++;
		}

		intervalC.max( tmp );

		assertTrue( Arrays.equals( tmp, position ) );
		assertTrue( ctr == shiftedintervalsize_C );
		assertTrue( sum == getSum(intervalC));
	}

	@Test
	public void testIterationSinglePlane()
	{
		Cursor< IntType > cursor = Views.interval( planarImg, intervalD ).cursor();

		long[] position = new long[ cursor.numDimensions() ];
		long[] tmp = new long[ cursor.numDimensions() ];

		intervalD.min( tmp );

		cursor.fwd();
		cursor.localize( position );
		assertTrue( Arrays.equals( tmp, position ) );

		cursor.reset();
		int ctr = 0;
		long sum = 0;
		while ( cursor.hasNext() )
		{
			cursor.next();
			cursor.localize( position );
			sum += cursor.get().get();
			ctr++;
		}

		intervalD.max( tmp );

		assertTrue( Arrays.equals( tmp, position ) );
		assertTrue( ctr == singleplaneintervalsize_D );
		assertTrue( sum == getSum(intervalD));
	}
	
	@Test
	public void testJumpFwdFast()
	{
		Cursor< IntType > cursor = Views.interval( planarImg, intervalA ).cursor();

		long[] position = new long[ cursor.numDimensions() ];
		long[] ref = new long[ cursor.numDimensions() ];

		intervalA.min( ref );

		ref[ 0 ] += 17;
		cursor.jumpFwd( 18 );
		cursor.localize( position );

		assertTrue( Arrays.equals( ref, position ) );
	}

	@Test
	public void testJumpFwdShifted()
	{
		Cursor< IntType > cursor = Views.interval( planarImg, intervalC ).cursor();

		long[] position = new long[ cursor.numDimensions() ];
		long[] ref = new long[ cursor.numDimensions() ];

		intervalC.min( ref );

		ref[ 0 ] += 17;
		cursor.jumpFwd( 18 );
		cursor.localize( position );

		assertTrue( Arrays.equals( ref, position ) );
	}
	
	@Test
	public void testJumpFwdSinglePlane()
	{
		Cursor< IntType > cursor = Views.interval( planarImg, intervalD ).cursor();

		long[] position = new long[ cursor.numDimensions() ];
		long[] ref = new long[ cursor.numDimensions() ];

		intervalD.min( ref );

		ref[ 0 ] += 17;
		cursor.jumpFwd( 18 );
		cursor.localize( position );

		assertTrue( Arrays.equals( ref, position ) );
	}
	
	//HELPER
	
	private long getSum(Interval inter) {
		long[] pos = new long[dimensions.length];
		long sum = 0;
		
		
		for ( int i = 0; i < intData.length; ++i )
		{
			IntervalIndexer.indexToPosition( i, dimensions, pos );

			boolean in = true;
			for (int j = 0; j < pos.length; j++) {
				if (pos[j] < inter.min(j) || pos[j] > inter.max(j)) {
					in = false;
					break;
				}
			}
			
			if (in) {
				sum += intData[i];
			}
		}
		
		return sum;
	}
}
