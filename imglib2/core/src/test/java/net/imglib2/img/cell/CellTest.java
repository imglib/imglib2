package net.imglib2.img.cell;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.img.basictypeaccess.array.BitArray;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.img.basictypeaccess.array.CharArray;
import net.imglib2.img.basictypeaccess.array.DoubleArray;
import net.imglib2.img.basictypeaccess.array.FloatArray;
import net.imglib2.img.basictypeaccess.array.IntArray;
import net.imglib2.img.basictypeaccess.array.ShortArray;

import org.junit.Test;


public class CellTest
{
	int[][] dim = {
			{10, 12},
			{200, 30, 2, 384},
			{12, 3, 4, 1, 9}
		};
	long[][] offset = {
			{0, 0},
			{0, 912389123123l, 1231238214214367l, 2},
			{321, 3, 1, 0, 0}
		};
	int[] expectedLength = {
			120,
			4608000,
			1296
		};

	public < A extends ArrayDataAccess< A > > void testConstruction( final A creator )
	{
		for ( int i = 0; i < dim.length; ++i ) {
			final AbstractCell< A > cell = new DefaultCell< A >( creator, dim[ i ], offset[ i ], 2);
			assertTrue( creator.getClass().isInstance( cell.getData() ) );
			assertTrue( cell.size() == expectedLength[ i ] );
		}
	}

	@Test
	public void testBitConstruction() { testConstruction( new BitArray( 1 ) ); }

	@Test
	public void testByteConstruction() { testConstruction( new ByteArray( 1 ) ); }

	@Test
	public void testCharConstruction() { testConstruction( new CharArray( 1 ) ); }

	@Test
	public void testShortConstruction() { testConstruction( new ShortArray( 1 ) ); }

	@Test
	public void testIntConstruction() { testConstruction( new IntArray( 1 ) ); }

	@Test
	public void testFloatConstruction() { testConstruction( new FloatArray( 1 ) ); }

	@Test
	public void testDoubleConstruction() { testConstruction( new DoubleArray( 1 ) ); }

	@Test
	public void testLocalIndexCalculation()
	{
		final AbstractCell< FloatArray > cell = new DefaultCell< FloatArray >( new FloatArray( 1 ), new int[] {20, 8, 10}, new long[] { 0, 9876543210l, 222 } , 2);
		final long[][] position = { {3, 4, 5}, {12, 0, 3}, {3, 2, 0} };
		final int[] expectedIndex = { 883, 492, 43 };
		for ( int i = 0; i < position.length; ++i ) {
			assertTrue( cell.localPositionToIndex( position[ i ] ) == expectedIndex[ i ]);
		}
	}

	@Test
	public void testGlobalPositionCalculation()
	{
		final AbstractCell< FloatArray > cell = new DefaultCell< FloatArray >( new FloatArray( 1 ), new int[] {20, 8, 10}, new long[] { 0, 9876543210l, 222 } , 2);
		final int[] index = { 883, 492, 43 };
		final long[][] expectedPosition = { {3, 9876543214l, 227}, {12, 9876543210l, 225}, {3, 9876543212l, 222} };
		for ( int i = 0; i < index.length; ++i ) {
			final long[] position = new long[ 3 ];
			cell.indexToGlobalPosition( index[ i ], position );
			assertArrayEquals( expectedPosition[ i ], position );
			for ( int d = 0; d < position.length; ++d )
				assertTrue( cell.indexToGlobalPosition( index[ i ], d ) == expectedPosition[ i ][ d ] );
		}
	}
}
