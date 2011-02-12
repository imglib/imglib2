package mpicbg.imglib.container.cell;

import static org.junit.Assert.assertArrayEquals;
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
		CellContainerFactory< T > factory = new CellContainerFactory< T >( defaultCellSize );
		long[] dim = {100, 80, 4, 3}; 
		CellContainer< T, ? > img = factory.create( dim, type );
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
		CellContainerFactory< T > factory = new CellContainerFactory< T >( defaultCellDims );
		long[] dim = {100, 80, 4, 3}; 
		CellContainer< T, ? > img = factory.create( dim, type );
		assertArrayEquals( expectedCellDims, img.cellDims );
	}

	@Test
	public void testBitDefaultCellDimensions() { testDefaultCellDimensions( new BitType() ); }

	@Test
	public void testIntDefaultCellDimensions() { testDefaultCellDimensions( new IntType() ); }

	@Test
	public void testFloatDefaultCellDimensions() { testDefaultCellDimensions( new FloatType() ); }
}
