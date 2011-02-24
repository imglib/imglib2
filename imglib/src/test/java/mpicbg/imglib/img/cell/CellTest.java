package mpicbg.imglib.img.cell;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;
import mpicbg.imglib.img.basictypeaccess.array.ArrayDataAccess;
import mpicbg.imglib.img.basictypeaccess.array.BitArray;
import mpicbg.imglib.img.basictypeaccess.array.ByteArray;
import mpicbg.imglib.img.basictypeaccess.array.CharArray;
import mpicbg.imglib.img.basictypeaccess.array.DoubleArray;
import mpicbg.imglib.img.basictypeaccess.array.FloatArray;
import mpicbg.imglib.img.basictypeaccess.array.IntArray;
import mpicbg.imglib.img.basictypeaccess.array.ShortArray;
import mpicbg.imglib.img.cell.Cell;

import org.junit.Test;


public class CellTest
{
	int[][] dim = {
			{10, 12},
			{200, 30, 2, 2384},
			{12, 3, 4, 1, 9}
		};
	long[][] offset = {
			{0, 0},
			{0, 912389123123l, 1231238214214367l, 2},
			{321, 3, 1, 0, 0}
		};
	int[] expectedLength = {
			120,
			28608000,
			1296
		};

	public < A extends ArrayDataAccess< A > > void testConstruction( A creator )
	{
		for ( int i = 0; i < dim.length; ++i ) {
			Cell< A > cell = new Cell< A >( creator, dim[ i ], offset[ i ], 2);			
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
		Cell< FloatArray > cell = new Cell< FloatArray >( new FloatArray( 1 ), new int[] {20, 8, 10}, new long[] { 0, 9876543210l, 222 } , 2);
		long[][] position = { {3, 4, 5}, {12, 0, 3}, {3, 2, 0} };
		int[] expectedIndex = { 883, 492, 43 };
		for ( int i = 0; i < position.length; ++i ) {
			assertTrue( cell.localPositionToIndex( position[ i ] ) == expectedIndex[ i ]);
		}
	}

	@Test
	public void testGlobalPositionCalculation()
	{
		Cell< FloatArray > cell = new Cell< FloatArray >( new FloatArray( 1 ), new int[] {20, 8, 10}, new long[] { 0, 9876543210l, 222 } , 2);
		int[] index = { 883, 492, 43 };
		long[][] expectedPosition = { {3, 9876543214l, 227}, {12, 9876543210l, 225}, {3, 9876543212l, 222} };
		for ( int i = 0; i < index.length; ++i ) {
			long[] position = new long[ 3 ];
			cell.indexToGlobalPosition( index[ i ], position );
			assertArrayEquals( expectedPosition[ i ], position );
			for ( int d = 0; d < position.length; ++d )
				assertTrue( cell.indexToGlobalPosition( index[ i ], d ) == expectedPosition[ i ][ d ] );
		}
	}
}
