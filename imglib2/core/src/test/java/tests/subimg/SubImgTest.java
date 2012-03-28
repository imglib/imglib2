package tests.subimg;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;
import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.subimg.SubImg;
import net.imglib2.type.numeric.integer.UnsignedByteType;

import org.junit.Test;

public class SubImgTest
{

	@Test
	public void testSubImg()
	{
		// Random source img with pixel at (0,0,0) = 1!
		Img< UnsignedByteType > sourceImg = new ArrayImgFactory< UnsignedByteType >().create( new long[] { 10, 10, 10 }, new UnsignedByteType() );
		sourceImg.firstElement().set( 1 );

		// SubImg from [1,0,0] to [9,9,9] NOT including the first pixel
		SubImg< UnsignedByteType > subImg = new SubImg< UnsignedByteType >( sourceImg, new FinalInterval( new long[] { 1, 0, 0 }, new long[] { 9, 9, 9 } ), false );

		// Cursor over SUBIMG
		Cursor< UnsignedByteType > subCursor = subImg.cursor();

		long[] pos = new long[ subCursor.numDimensions() ];
		subCursor.localize( pos );

		// Cursor position should clearly be 0, as this cursor should only
		// return values from [1,0,0] to [9,9,9] and only the first value is set
		assertTrue( subCursor.next().get() == 0 );

		// Pos should be at [0,0,0] as the SubImg should act like an img
		assertArrayEquals( pos, new long[ sourceImg.numDimensions() ] );

	}
}
