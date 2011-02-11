package mpicbg.imglib.container.cell;

import static org.junit.Assert.*;

import org.junit.Test;

import mpicbg.imglib.container.basictypecontainer.array.ArrayDataAccess;
import mpicbg.imglib.container.basictypecontainer.array.BitArray;
import mpicbg.imglib.container.cell.Cell;
import mpicbg.imglib.container.cell.CellContainer;
import mpicbg.imglib.container.cell.CellContainerFactory;
import mpicbg.imglib.type.NativeType;
import mpicbg.imglib.type.logic.BitType;
import mpicbg.imglib.type.numeric.integer.IntType;
import mpicbg.imglib.type.numeric.real.FloatType;

public class CellContainerFactoryTest
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

	public < T extends NativeType< T > > void testDefaultCellSize (T type)
	{
		CellContainerFactory< T > factory = new CellContainerFactory< T >( 43 );
		long[] dim = {100, 80, 4, 3}; 
		CellContainer< T, ? > img = factory.create( dim, type );
		int[] expectedCellDims = {43, 43, 43, 43};
		assertArrayEquals( expectedCellDims, img.cellDims );
	}
	
	@Test
	public void testBitDefaultCellSize() { testDefaultCellSize( new BitType() ); }

	@Test
	public void testIntDefaultCellSize() { testDefaultCellSize( new IntType() ); }

	@Test
	public void testFloatDefaultCellSize() { testDefaultCellSize( new FloatType() ); }
}
