package net.imglib2.view;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.ImgPlus;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Intervals;
import net.imglib2.util.Util;

public class SubsetIterableExample
{
	public static <T> void testSlice( final RandomAccessibleInterval<T> img )
	{
		final RandomAccessibleInterval< T > slice = Views.hyperSlice( img, 2, 10 );
		final IterableInterval< T > iterable = Views.iterable(slice);
		final Cursor<T> cursor = iterable.cursor();

		cursor.fwd();
		System.out.println( Util.printCoordinates( cursor ) );
	}

	public static <T> void testInterval( final RandomAccessibleInterval<T> img )
	{
		final RandomAccessibleInterval< T > slice = Views.interval( img, Intervals.expand( img, -10, 2 ));
		final IterableInterval< T > iterable = Views.iterable(slice);
		final Cursor<T> cursor = iterable.cursor();

		cursor.fwd();
		System.out.println( Util.printCoordinates( cursor ) );
	}

	public static void main( final String[] args ) throws ImgIOException
	{
		final String filename = "/home/tobias/workspace/advanced-imglib2/images/t1-head.tif";
		final ImgPlus< FloatType > img = new ImgOpener().openImg( filename, new ArrayImgFactory< FloatType >(), new FloatType() );
		testInterval( img );
		testSlice( img );
	}
}
