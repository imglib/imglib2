package net.imglib2.img.cell;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.type.numeric.real.FloatType;

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
		img = new CellImgFactory< FloatType >( cellDimensions ).create( dimensions, new FloatType() );
	}

	@Test
	public void equalIterationOrder()
	{
		Img< FloatType > img2 = new CellImgFactory< FloatType >( cellDimensions ).create( dimensions, new FloatType() );
		assertTrue( img2.equalIterationOrder( img ) );		
		assertTrue( img.equalIterationOrder( img2 ) );		

		Img< FloatType > img3 = new CellImgFactory< FloatType >( 9 ).create( dimensions, new FloatType() );
		assertFalse( img3.equalIterationOrder( img ) );		
		assertFalse( img.equalIterationOrder( img3 ) );		

		Img< FloatType > img4 = new ArrayImgFactory< FloatType >().create( dimensions, new FloatType() );
		assertFalse( img4.equalIterationOrder( img ) );		
		assertFalse( img.equalIterationOrder( img4 ) );		
	}
}
