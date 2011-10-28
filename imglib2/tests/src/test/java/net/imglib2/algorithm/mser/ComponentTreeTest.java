package net.imglib2.algorithm.mser;

import net.imglib2.Cursor;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.LongType;

public class ComponentTreeTest
{
	public static final int[][] testData = new int[][] {
		{ 8, 7, 6, 7, 1 },
		{ 8, 8, 5, 8, 1 },
		{ 2, 3, 4, 3, 2 },
		{ 1, 8, 3, 8, 1 },
		{ 1, 2, 2, 2, 1 } };

//	public static final int[][] testData = new int[][] {
//		{ 4, 1, 0, 1, 4 },
//		{ 2, 1, 3, 4, 5 },
//		{ 1, 0, 2, 1, 2 },
//		{ 3, 1, 2, 0, 1 },
//		{ 3, 3, 3, 3, 2 } };

//	public static final int[][] testData = new int[][] {
//		{ 0, 9, 0, 1, 4, 0, 2 },
//		{ 8, 9, 3, 4, 5, 0, 3 },
//		{ 7, 0, 3, 1, 2, 0, 4 },
//		{ 5, 1, 5, 5, 5, 5, 5 },
//		{ 7, 1, 2, 3, 4, 5, 6 },
//		{ 3, 3, 1, 0, 1, 5, 1 },
//		{ 3, 3, 0, 8, 2, 1, 1 } };

	public static void main( String[] args )
	{
		final long[] dimensions = new long[] { testData[ 0 ].length, testData.length };
		ImgFactory< IntType > imgFactory = new ArrayImgFactory< IntType >();
		Img< IntType > input = imgFactory.create( dimensions, new IntType() );

		// fill input image with test data
		int[] pos = new int[ 2 ];
		Cursor< IntType > c = input.localizingCursor();
		while ( c.hasNext() )
		{
			c.fwd();
			c.localize( pos );
			c.get().set( testData[ pos[ 1 ] ][ pos[ 0 ] ] );
		}

		try
		{
			PixelListComponentGenerator< IntType > generator = new PixelListComponentGenerator< IntType >( new IntType( Integer.MAX_VALUE ), input, imgFactory.imgFactory( new LongType() ) );
			final PixelListComponentHandler< IntType > handler = new PixelListComponentHandler< IntType >( dimensions );
			new ComponentTree< IntType, PixelListComponent< IntType > >( input, generator, handler );
		}
		catch ( IncompatibleTypeException e )
		{
			e.printStackTrace();
		}
	}
}
