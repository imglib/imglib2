package net.imglib2.streamifiedview;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.interpolation.randomaccess.NLinearInterpolatorFactory;
import net.imglib2.type.numeric.integer.IntType;

public class StreamifiedViewsExample
{
	public static void main( String[] args )
	{
		RandomAccessibleInterval< IntType > img = ArrayImgs.ints( 100, 100 );

		RandomAccess< IntType > access = img.view()
				.extendBorder()
				.interpolate( new NLinearInterpolatorFactory<>() )
				.raster()
				.interval( img )
				.randomAccess();

		Cursor< IntType > cursor = img.view()
				.interval( img )
				.cursor();
	}
}
