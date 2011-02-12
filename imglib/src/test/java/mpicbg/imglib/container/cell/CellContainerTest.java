package mpicbg.imglib.container.cell;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import mpicbg.imglib.container.Img;
import mpicbg.imglib.container.array.ArrayContainerFactory;
import mpicbg.imglib.type.numeric.real.FloatType;

import org.junit.Before;
import org.junit.Test;

public class CellContainerTest
{
	int[] cellDimensions;
	long[] dimensions;
	Img< FloatType > img;
	
	@Before
	public void setUp()
	{
		cellDimensions = new int[] { 8, 16, 5, 2 };
		dimensions = new long[] { 20, 37, 12, 33 };
		img = new CellContainerFactory< FloatType >( cellDimensions ).create( dimensions, new FloatType() );
	}

	@Test
	public void equalIterationOrder()
	{
		Img< FloatType > img2 = new CellContainerFactory< FloatType >( cellDimensions ).create( dimensions, new FloatType() );
		assertTrue( img2.equalIterationOrder( img ) );		
		assertTrue( img.equalIterationOrder( img2 ) );		

		Img< FloatType > img3 = new CellContainerFactory< FloatType >( 9 ).create( dimensions, new FloatType() );
		assertFalse( img3.equalIterationOrder( img ) );		
		assertFalse( img.equalIterationOrder( img3 ) );		

		Img< FloatType > img4 = new ArrayContainerFactory< FloatType >().create( dimensions, new FloatType() );
		assertFalse( img4.equalIterationOrder( img ) );		
		assertFalse( img.equalIterationOrder( img4 ) );		
	}
}
