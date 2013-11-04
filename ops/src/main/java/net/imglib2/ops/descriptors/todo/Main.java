package net.imglib2.ops.descriptors.todo;

import net.imglib2.Cursor;
import net.imglib2.Point;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.roi.EllipseRegionOfInterest;
import net.imglib2.type.numeric.real.FloatType;

public class Main
{
	public static void main( final String[] args )
	{

		// TODO: We should also test against barrys implementation.
		// (measurementtests2)
		// standard test image
		Img< FloatType > testImg = new ArrayImgFactory< FloatType >().create( new long[] { 10000, 10000 }, new FloatType() );
		Cursor< FloatType > cursor = testImg.cursor();
		while ( cursor.hasNext() )
		{
			cursor.fwd();
			float val = ( float ) Math.random();
			cursor.get().set( val );
		}

		EllipseRegionOfInterest sphere = new EllipseRegionOfInterest( 2 );
		sphere.setRadius( 100 );
		sphere.setOrigin( new Point( new long[] { 200, 200 } ) );

		// Create test factories
		ImplTests< FloatType > newFrameWorkTest = new ImplTests< FloatType >();
		// ImgLib2Tests< FloatType > test = new ImgLib2Tests< FloatType >();
		// tests

		long startTime = System.currentTimeMillis();
		for ( int i = 0; i < 20; i++ )
		{
			System.out.println( "###### Testrun " + i );
			sphere.move( i, 0 );
			newFrameWorkTest.runFirstOrderTest( sphere.getIterableIntervalOverROI( testImg ) );
			// test.runFirstOrderTest(sphere.getIterableIntervalOverROI(testImg));
		}

		long estimatedTime = System.currentTimeMillis() - startTime;
		System.out.println( "finished in " + estimatedTime + "ms" );

		// Testing against imglib
	}
}
