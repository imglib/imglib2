package net.imglib2.img.cell;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.cell.CellImg;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.util.IntervalIndexer;

import org.junit.Before;
import org.junit.Test;

public class CellCursorTest
{
	long[] dimensions;

	int numValues;

	int[] intData;

	long intDataSum;

	CellImg< IntType, ? > intImg;

	@Before
	public void createSourceData()
	{
		dimensions = new long[] { 16, 37, 5, 13 };

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
		
		intImg = new CellImgFactory< IntType >( 4 ).create( dimensions, new IntType() );

		long[] pos = new long[ dimensions.length ];
		RandomAccess< IntType > a = intImg.randomAccess();

		for ( int i = 0; i < numValues; ++i )
		{
			IntervalIndexer.indexToPosition( i, dimensions, pos );
			a.setPosition( pos );
			a.get().set( intData[ i ] );
		}
	}

	@Test
	public void testSumWithCursor()
	{
		long sum = 0;
		Cursor< IntType > cursor = intImg.cursor();
		while ( cursor.hasNext() ) {
			sum += cursor.next().get();
		}

		assertTrue( sum == intDataSum );
	}

	@Test
	public void testResetWithCursor()
	{
		Cursor< IntType > cursor = intImg.cursor();
		int v1 = cursor.next().get();
		long[] p1 = new long[ dimensions.length ];
		cursor.localize( p1 );

		cursor.reset(); 
		int v2 = cursor.next().get();
		long[] p2 = new long[ dimensions.length ];
		cursor.localize( p2 );

		assertTrue( v1 == v2 );
		assertArrayEquals( p1, p2 );
	}

	@Test
	public void testJmpWithCursor()
	{
		int steps = 43;
		Cursor< IntType > cursor1 = intImg.cursor();
		for ( int i = 0; i < steps; ++i )
			cursor1.fwd();
		int v1 = cursor1.next().get();
		long[] p1 = new long[ dimensions.length ];
		cursor1.localize( p1 );

		Cursor< IntType > cursor2 = intImg.cursor();
		cursor2.jumpFwd( steps );
		int v2 = cursor2.next().get();
		long[] p2 = new long[ dimensions.length ];
		cursor2.localize( p2 );

		assertTrue( v1 == v2 );
		assertArrayEquals( p1, p2 );
	}

	@Test
	public void testSumWithLocalizingCursor()
	{
		long sum = 0;
		Cursor< IntType > cursor = intImg.localizingCursor();
		while ( cursor.hasNext() ) {
			sum += cursor.next().get();
		}

		assertTrue( sum == intDataSum );
	}
	@Test
	public void testResetWithLocalizingCursor()
	{
		Cursor< IntType > cursor = intImg.localizingCursor();
		int v1 = cursor.next().get();
		long[] p1 = new long[ dimensions.length ];
		cursor.localize( p1 );

		cursor.reset(); 
		int v2 = cursor.next().get();
		long[] p2 = new long[ dimensions.length ];
		cursor.localize( p2 );

		assertTrue( v1 == v2 );
		assertArrayEquals( p1, p2 );
	}

	@Test
	public void testJmpWithLocalizingCursor()
	{
		int steps = 43;
		Cursor< IntType > cursor1 = intImg.localizingCursor();
		for ( int i = 0; i < steps; ++i )
			cursor1.fwd();
		int v1 = cursor1.next().get();
		long[] p1 = new long[ dimensions.length ];
		cursor1.localize( p1 );

		Cursor< IntType > cursor2 = intImg.localizingCursor();
		cursor2.jumpFwd( steps );
		int v2 = cursor2.next().get();
		long[] p2 = new long[ dimensions.length ];
		cursor2.localize( p2 );

		assertTrue( v1 == v2 );
		assertArrayEquals( p1, p2 );
	}


	@Test
	public void testSumWithRandomAccess()
	{
		long sum = 0;
		RandomAccess< IntType > access = intImg.randomAccess();
		long[] position = new long[ dimensions.length ];
		for ( int d = 0; d < dimensions.length; ++d )
			position[ d ] = 0;

		for ( int i = 0; i < numValues; ++i )
		{
			access.setPosition( position );
			sum += access.get().get();
			for ( int d = 0; d < dimensions.length; ++d )
				if ( ++position[ d ] >= dimensions[ d ] ) position[ d ] = 0;
				else break;
		}
			
		assertTrue( sum == intDataSum );
	}
}
