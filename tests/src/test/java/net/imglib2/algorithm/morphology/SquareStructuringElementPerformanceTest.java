package net.imglib2.algorithm.morphology;

import ij.ImageJ;

import java.util.List;

import net.imglib2.Cursor;
import net.imglib2.algorithm.region.localneighborhood.Shape;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.array.ArrayRandomAccess;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.UnsignedByteType;

public class SquareStructuringElementPerformanceTest
{

	public static void main( final String[] args )
	{
		ImageJ.main( args );

		/*
		 * 2D
		 */

		{

			final ArrayImg< UnsignedByteType, ByteArray > img = ArrayImgs.unsignedBytes( 100, 100 );
			final ArrayRandomAccess< UnsignedByteType > ra = img.randomAccess();
			ra.setPosition( new int[] { 49, 49 } );
			ra.get().set( 255 );

			{
				final ArrayImg< UnsignedByteType, ByteArray > target = ArrayImgs.unsignedBytes( 100, 100, 48 );
				final ArrayRandomAccess< UnsignedByteType > randomAccess = target.randomAccess();

				System.out.println( "Square structuring element, 2D, standard, 1 thread." );
				System.out.println( "Radius\tTime (ms)" );
				// warm up
				perform( img, 1, false, 1 );
				perform( img, 1, false, 1 );
				// run test
				for ( int r = 1; r < 49; r++ )
				{
					final Object[] objects = perform( img, r, false, 1 );
					final Long dt = ( Long ) objects[ 0 ];
					@SuppressWarnings( "unchecked" )
					final Img< UnsignedByteType > dilated = ( Img< UnsignedByteType > ) objects[ 1 ];
					System.out.println( "" + r + '\t' + dt );

					// copy
					randomAccess.setPosition( r - 1, 2 );
					final Cursor< UnsignedByteType > cursor = dilated.cursor();
					while ( cursor.hasNext() )
					{
						cursor.fwd();
						randomAccess.setPosition( cursor );
						randomAccess.get().set( cursor.get() );
					}
				}

				ImageJFunctions.show( target, "standard" );
			}

			{
				final ArrayImg< UnsignedByteType, ByteArray > target = ArrayImgs.unsignedBytes( 100, 100, 48 );
				final ArrayRandomAccess< UnsignedByteType > randomAccess = target.randomAccess();

				System.out.println();
				System.out.println( "Square structuring element, 2D, optimized, 1 thread." );
				System.out.println( "Radius\tTime (ms)" );
				// warm up
				perform( img, 1, true, 1 );
				perform( img, 1, true, 1 );
				// run test
				for ( int r = 1; r < 49; r++ )
				{
					final Object[] objects = perform( img, r, true, 1 );
					final Long dt = ( Long ) objects[ 0 ];
					@SuppressWarnings( "unchecked" )
					final Img< UnsignedByteType > dilated = ( Img< UnsignedByteType > ) objects[ 1 ];
					System.out.println( "" + r + '\t' + dt );

					// copy
					randomAccess.setPosition( r - 1, 2 );
					final Cursor< UnsignedByteType > cursor = dilated.cursor();
					while ( cursor.hasNext() )
					{
						cursor.fwd();
						randomAccess.setPosition( cursor );
						randomAccess.get().set( cursor.get() );
					}
				}

				ImageJFunctions.show( target, "optimized" );
			}

		}

		{

			final ArrayImg< UnsignedByteType, ByteArray > img = ArrayImgs.unsignedBytes( 40, 40, 40 );
			final ArrayRandomAccess< UnsignedByteType > ra = img.randomAccess();
			ra.setPosition( new int[] { 19, 19, 19 } );
			ra.get().set( 255 );

			{
				System.out.println( "Square structuring element, 3D, standard, 1 thread." );
				System.out.println( "Radius\tTime (ms)" );
				// warm up
				perform( img, 1, false, 1 );
				perform( img, 1, false, 1 );
				// run test
				for ( int r = 1; r < 20; r++ )
				{
					final Object[] objects = perform( img, r, false, 1 );
					final Long dt = ( Long ) objects[ 0 ];
					System.out.println( "" + r + '\t' + dt );
				}
			}

			{
				System.out.println();
				System.out.println( "Square structuring element, 3D, optimized, 1 thread." );
				System.out.println( "Radius\tTime (ms)" );
				// warm up
				perform( img, 1, true, 1 );
				perform( img, 1, true, 1 );
				// run test
				for ( int r = 1; r < 20; r++ )
				{
					final Object[] objects = perform( img, r, true, 1 );
					final Long dt = ( Long ) objects[ 0 ];
					System.out.println( "" + r + '\t' + dt );
				}
			}
		}
	}

	public static final Object[] perform( final Img< UnsignedByteType > img, final int radius, final boolean optimize, final int numThreads )
	{
		final long start = System.currentTimeMillis();
		final List< Shape > strels = StructuringElements.square( radius, img.numDimensions(), optimize );
		final Img< UnsignedByteType > dilated = MorphologicalOperations.dilate( img, strels, numThreads );
		final long end = System.currentTimeMillis();
		return new Object[] { Long.valueOf( end - start ), dilated };
	}

}
