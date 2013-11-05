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

		// Creating a testimg
		Img< FloatType > testImg = new ArrayImgFactory< FloatType >().create( new long[] { 10000, 10000 }, new FloatType() );
		Cursor< FloatType > cursor = testImg.cursor();
		while ( cursor.hasNext() )
		{
			cursor.fwd();
			float val = ( float ) Math.random();
			cursor.get().set( val );
		}

		// Set up any ROI
		EllipseRegionOfInterest sphere = new EllipseRegionOfInterest( 2 );
		sphere.setRadius( 100 );
		sphere.setOrigin( new Point( new long[] { 200, 200 } ) );

		// Create test factories
		SimpleTesting< FloatType > testClass = new SimpleTesting< FloatType >();

		long startTime = System.currentTimeMillis();
		for ( int i = 0; i < 1; i++ )
		{
			System.out.println( "###### BEGIN " + i );
			
			sphere.move( i, 0 );
			testClass.test( sphere.getIterableIntervalOverROI( testImg ) );

			System.out.println( "###### END \n \n" );
		}

		long estimatedTime = System.currentTimeMillis() - startTime;
		System.out.println( "finished in " + estimatedTime + "ms" );

		// Testing against imglib
	}
}
