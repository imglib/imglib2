package net.imglib2.algorithm.region.localneighborhood;

import ij.ImageJ;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.ImgPlus;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.meta.Axes;
import net.imglib2.meta.AxisType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.util.Util;

public class SphereNeighborhoodExample
{

	private static final int DIM = 100; // also N points

	public static void main( String[] args )
	{
		ImageJ.main( args );
		example1();
		example2();
		example3();
	}

	public static void example2()
	{

		final ImgFactory< UnsignedByteType > imgFactory = new ArrayImgFactory< UnsignedByteType >();
		Img< UnsignedByteType > img = imgFactory.create( new int[] { DIM, DIM, DIM / 5 }, new UnsignedByteType() );
		double[] cal = new double[] { 0.2, 0.2, 1 };
		AxisType[] axes = new AxisType[] { Axes.X, Axes.Y };
		ImgPlus< UnsignedByteType > image = new ImgPlus< UnsignedByteType >( img, "Test", axes, cal );

		long[] center = new long[ 3 ];

		RandomAccess< UnsignedByteType > ra = image.randomAccess();

		for ( int i = 0; i < DIM / 10; i++ )
		{

			center[ 0 ] = ( long ) ( Math.random() * DIM );
			center[ 1 ] = ( long ) ( Math.random() * DIM );

			ra.setPosition( center );

			double radius = Math.random() / 10 * DIM;

			SphereNeighborhood< UnsignedByteType, ImgPlus< UnsignedByteType >> sphere = new SphereNeighborhood< UnsignedByteType, ImgPlus< UnsignedByteType >>( image, radius );
			sphere.setPosition( center );
			SphereCursor< UnsignedByteType, ImgPlus< UnsignedByteType >> cursor = sphere.cursor();

			System.out.println( "Center: " + Util.printCoordinates( center ) );
			System.out.println( "Radius: " + radius );

			while ( cursor.hasNext() )
			{

				cursor.fwd();
				if ( cursor.isOutOfBounds() )
					continue;
				cursor.get().add( new UnsignedByteType( 50 ) );

			}

		}

		ImageJFunctions.show( image );

	}

	public static void example3()
	{
		long[] center = new long[] { 100, 100, 20 }; // the middle
		double radius = 15;
		final ImgFactory< UnsignedByteType > imgFactory = new ArrayImgFactory< UnsignedByteType >();
		Img< UnsignedByteType > img = imgFactory.create( new int[] { 200, 200, 40 }, new UnsignedByteType() );
		double[] cal = new double[] { 0.2, 0.2, 1 };
		AxisType[] axes = new AxisType[] { Axes.X, Axes.Y };
		ImgPlus< UnsignedByteType > image = new ImgPlus< UnsignedByteType >( img, "Radius " + radius, axes, cal );

		// Write into the image
		SphereNeighborhood< UnsignedByteType, ImgPlus< UnsignedByteType >> sphere = new SphereNeighborhood< UnsignedByteType, ImgPlus< UnsignedByteType > >( image, radius );
		sphere.setPosition( center );
		SphereCursor< UnsignedByteType, ImgPlus< UnsignedByteType > > cursor = sphere.cursor();
		while ( cursor.hasNext() )
		{
			cursor.fwd();
			cursor.get().set( ( int ) cursor.getDistanceSquared() );
		}

		ImageJFunctions.show( image );
	}

	public static void example1()
	{
		long[] center = new long[] { 50, 50, 25 }; // the middle
		double radius = 5;

		final ImgFactory< UnsignedByteType > imgFactory = new ArrayImgFactory< UnsignedByteType >();
		Img< UnsignedByteType > img = imgFactory.create( new int[] { DIM, DIM, DIM / 2 }, new UnsignedByteType() );
		double[] cal = new double[] { 0.2, 0.2, 1 };
		AxisType[] axes = new AxisType[] { Axes.X, Axes.Y };
		ImgPlus< UnsignedByteType > image = new ImgPlus< UnsignedByteType >( img, "Radius " + radius, axes, cal );

		// Write into the image
		SphereNeighborhood< UnsignedByteType, ImgPlus< UnsignedByteType >> sphere = new SphereNeighborhood< UnsignedByteType, ImgPlus< UnsignedByteType >>( image, radius );
		sphere.setPosition( center );

		UnsignedByteType val = new UnsignedByteType( 200 );
		for ( UnsignedByteType pixel : sphere )
		{
			pixel.set( val );
		}

		ImageJFunctions.show( image );
	}
}
