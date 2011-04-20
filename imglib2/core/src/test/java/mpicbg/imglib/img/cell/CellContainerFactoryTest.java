package mpicbg.imglib.img.cell;

import static org.junit.Assert.assertArrayEquals;
import mpicbg.imglib.img.cell.CellImg;
import mpicbg.imglib.img.cell.CellImgFactory;
import mpicbg.imglib.type.NativeType;
import mpicbg.imglib.type.logic.BitType;
import mpicbg.imglib.type.numeric.integer.IntType;
import mpicbg.imglib.type.numeric.real.FloatType;

import org.junit.Test;

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

	private < T extends NativeType< T > > void testDefaultCellSize (T type)
	{
		int defaultCellSize = 43;
		int[] expectedCellDims = {43, 43, 43, 43};
		CellImgFactory< T > factory = new CellImgFactory< T >( defaultCellSize );
		long[] dimension = {100, 80, 4, 3}; 
		CellImg< T, ? > img = factory.create( dimension, type );
		assertArrayEquals( expectedCellDims, img.cellDims );
	}
	
	@Test
	public void testBitDefaultCellSize() { testDefaultCellSize( new BitType() ); }

	@Test
	public void testIntDefaultCellSize() { testDefaultCellSize( new IntType() ); }

	@Test
	public void testFloatDefaultCellSize() { testDefaultCellSize( new FloatType() ); }

	private < T extends NativeType< T > > void testDefaultCellDimensions (T type)
	{
		int[] defaultCellDims = {6, 8, 5, 3};
		int[] expectedCellDims = defaultCellDims.clone();
		CellImgFactory< T > factory = new CellImgFactory< T >( defaultCellDims );
		long[] dimension = {100, 80, 4, 3}; 
		CellImg< T, ? > img = factory.create( dimension, type );
		assertArrayEquals( expectedCellDims, img.cellDims );
	}

	@Test
	public void testBitDefaultCellDimensions() { testDefaultCellDimensions( new BitType() ); }

	@Test
	public void testIntDefaultCellDimensions() { testDefaultCellDimensions( new IntType() ); }

	@Test
	public void testFloatDefaultCellDimensions() { testDefaultCellDimensions( new FloatType() ); }
}
