package net.imglib2.algorithm.region.localneighborhood;

import ij.ImageJ;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.ImgPlus;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.util.Util;

public class EllipsoidNeighborhoodExample
{

	private static final int DIM = 100; // also N points

	public static void main( String[] args )
	{
		ImageJ.main( args );
		example1();
		// example2();
		// example3();
		// testBounds();
	}

	public static void example2()
	{

		final ImgFactory< UnsignedByteType > imgFactory = new ArrayImgFactory< UnsignedByteType >();
		Img< UnsignedByteType > image = imgFactory.create( new int[] { DIM, DIM, DIM }, new UnsignedByteType() );

		long[] center = new long[ 3 ];
		long[] span = new long[ 3 ];

		for ( int i = 0; i < DIM; i++ )
		{

			center[ 0 ] = ( long ) ( Math.random() * DIM );
			center[ 1 ] = ( long ) ( Math.random() * DIM );
			center[ 2 ] = ( long ) ( Math.random() * DIM );

			span[ 0 ] = ( long ) ( Math.random() / 10 * DIM );
			span[ 1 ] = ( long ) ( Math.random() / 10 * DIM );
			span[ 2 ] = ( long ) ( Math.random() / 10 * DIM );

			EllipsoidNeighborhood< UnsignedByteType, Img< UnsignedByteType >> ellipsoid = new EllipsoidNeighborhood< UnsignedByteType, Img< UnsignedByteType >>( image );
			ellipsoid.setPosition( center );
			ellipsoid.setSpan( span );

			System.out.println( "Center: " + Util.printCoordinates( center ) );// DEBUG
			System.out.println( "Span: " + Util.printCoordinates( span ) );// DEBUG

			int val = ( int ) ( Math.random() * 200 );
			for ( UnsignedByteType pixel : ellipsoid )
			{
				pixel.set( val );
				// val++;
			}

		}

		ImageJFunctions.show( image );

	}

	public static void example1()
	{
		final ImgFactory< UnsignedShortType > imgFactory = new ArrayImgFactory< UnsignedShortType >();
		Img< UnsignedShortType > image = imgFactory.create( new int[] { DIM, DIM, DIM }, new UnsignedShortType() );

		long[] center = new long[] { 50, 50, 50 }; // the middle
		long[] span = new long[] { 0, 10, 10 };

		// Write into the image
		EllipsoidNeighborhood< UnsignedShortType, Img< UnsignedShortType >> ellipsoid = new EllipsoidNeighborhood< UnsignedShortType, Img< UnsignedShortType >>( image );
		ellipsoid.setPosition( center );
		ellipsoid.setSpan( span );

		int val = 250;
		for ( UnsignedShortType pixel : ellipsoid )
		{
			pixel.set( val );
			// val++;
		}

		ImageJFunctions.show( image );
	}

	public static void example3()
	{
		final ImgFactory< UnsignedByteType > imgFactory = new ArrayImgFactory< UnsignedByteType >();
		Img< UnsignedByteType > image = imgFactory.create( new int[] { DIM, DIM, DIM }, new UnsignedByteType() );

		long[][] centers = new long[][] { { 50, 50, 50 }, { 20, 20, 50 }, { 20, 70, 50 }, { 70, 70, 50 }, { 70, 20, 50 }

		};
		long[][] spans = new long[][] { { 20, 10, 15 }, { 20, 15, 1 }, { 1, 20, 15 }, { 20, 1, 15 }, { 20, 1, 1 } };

		EllipsoidNeighborhood< UnsignedByteType, Img< UnsignedByteType >> ellipsoid = new EllipsoidNeighborhood< UnsignedByteType, Img< UnsignedByteType > >( image );

		for ( int i = 0; i < spans.length; i++ )
		{

			ellipsoid.setPosition( centers[ i ] );
			ellipsoid.setSpan( spans[ i ] );

			// Write into the image
			int val = 200;
			for ( UnsignedByteType pixel : ellipsoid )
			{
				pixel.set( val );
				// val++;
			}
		}

		ImageJFunctions.show( image );
	}

	public static void testBounds()
	{

		int sy = 1;
		int sz = 7;

		int[] rys = new int[ sz + 1 ];

		Utils.getXYEllipseBounds( sy, sz, rys );

		for ( int i = 0; i < rys.length; i++ )
		{
			System.out.println( i + ": " + rys[ i ] );
		}
	}

}
