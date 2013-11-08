package net.imglib2.algorithm.region.localneighborhood;


import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import net.imglib2.Cursor;
import net.imglib2.EuclideanSpace;
import net.imglib2.algorithm.morphology.MorphologicalOperations;
import net.imglib2.algorithm.morphology.StructuringElements;
import net.imglib2.img.ImagePlusAdapter;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.array.ArrayRandomAccess;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedByteType;


public class DiamondNeighborhoodTest
{

	public static void main6( final String[] args )
	{
		final List< Shape > strels = StructuringElements.diamond( 4, true );

		for ( final Shape strel : strels )
		{
			final String str = StructuringElements.printNeighborhood( strel, new EuclideanSpace()
			{
				@Override
				public int numDimensions()
				{
					return 2;
				}
			} );
			System.out.println( str );
		}

	}

	/**
	 * Tune optimized 3D cases
	 *
	 * @param args
	 */
	public static void main( final String[] args )
	{
		ImageJ.main( args );

		final ArrayImg< UnsignedByteType, ByteArray > img = ArrayImgs.unsignedBytes( 40, 40, 40 );
		final ArrayRandomAccess< UnsignedByteType > ra = img.randomAccess();
		ra.setPosition( new long[] { 19, 19, 19 } );
		ra.get().set( 255 );

		final long start = System.currentTimeMillis();
		final List< Shape > strels = StructuringElements.diamond( 4, true );
		final Img< UnsignedByteType > dilated = MorphologicalOperations.dilate( img, strels, 1 );
		final long end = System.currentTimeMillis();
		System.out.println( "Optimized processing time: " + ( end - start ) + " ms." );

		ImageJFunctions.show( dilated, "Decomposed strel" );

	}

	/**
	 * Performance comparison between optimized & standard strel, 2D case.
	 */
	public static void main3( final String[] args )
	{
		ImageJ.main( args );

		final ArrayImg< UnsignedByteType, ByteArray > img = ArrayImgs.unsignedBytes( 100, 100 );
		final ArrayRandomAccess< UnsignedByteType > ra = img.randomAccess();
		ra.setPosition( new long[] { 49, 49 } );
		ra.get().set( 255 );

		final ArrayImg< UnsignedByteType, ByteArray > target1 = ArrayImgs.unsignedBytes( 100, 100, 40 );
		System.out.println( "Full strel" );
		System.out.println( "radius\ttime(ms)" );
		for ( int i = 0; i < 40; i++ )
		{
			final long start = System.currentTimeMillis();
			final List< Shape > strels = StructuringElements.diamond( i, false );
			final Img< UnsignedByteType > dilated = MorphologicalOperations.dilate( img, strels, 1 );
			final long end = System.currentTimeMillis();
			System.out.println( "" + i + '\t' + ( end - start ) );

			// copy to target
			final ArrayRandomAccess< UnsignedByteType > ra1 = target1.randomAccess();
			ra1.setPosition( i, 2 );
			final Cursor< UnsignedByteType > c = dilated.cursor();
			while ( c.hasNext() )
			{
				c.fwd();
				ra1.setPosition( c );
				ra1.get().set( c.get() );
			}
		}

		ImageJFunctions.show( target1, "Full strel" );

		final ArrayImg< UnsignedByteType, ByteArray > target2 = ArrayImgs.unsignedBytes( 100, 100, 40 );
		System.out.println( "Decomposed strel" );
		System.out.println( "radius\ttime(ms)" );
		for ( int i = 0; i < 40; i++ )
		{
			final long start = System.currentTimeMillis();
			final List< Shape > strels = StructuringElements.diamond( i, true );
			final Img< UnsignedByteType > dilated = MorphologicalOperations.dilate( img, strels, 1 );
			final long end = System.currentTimeMillis();
			System.out.println( "" + i + '\t' + ( end - start ) );

			// copy to target
			final ArrayRandomAccess< UnsignedByteType > ra1 = target2.randomAccess();
			ra1.setPosition( i, 2 );
			final Cursor< UnsignedByteType > c = dilated.cursor();
			while ( c.hasNext() )
			{
				c.fwd();
				ra1.setPosition( c );
				ra1.get().set( c.get() );
			}
		}

		ImageJFunctions.show( target2, "Decomposed strel" );

	}

	/**
	 * 2D & 3D processing.
	 */
	public static void main2( final String[] args )
	{
		ImageJ.main( args );

		final ArrayImg< UnsignedByteType, ByteArray > img1 = ArrayImgs.unsignedBytes( 20, 20 );
		final DiamondNeighborhood< UnsignedByteType > diamond1 = new DiamondNeighborhood< UnsignedByteType >( new long[] { 10, 10 }, 3, img1.randomAccess() );
		final Iterator< UnsignedByteType > c1 = diamond1.iterator();
		while ( c1.hasNext() )
		{
			c1.next().set( 255 );
		}

		final ArrayRandomAccess< UnsignedByteType > ra = img1.randomAccess();
		ra.setPosition( new int[] { 10, 10 } );
		ra.get().set( 55 );

		ImageJFunctions.show( img1 );

		//

		final ArrayImg< UnsignedByteType, ByteArray > img2 = ArrayImgs.unsignedBytes( 20, 20, 20 );
		final DiamondNeighborhood< UnsignedByteType > diamond2 = new DiamondNeighborhood< UnsignedByteType >( new long[] { 10, 10, 10 }, 5, img2.randomAccess() );
		final Iterator< UnsignedByteType > c2 = diamond2.iterator();
		while ( c2.hasNext() )
		{
			c2.next().set( 255 );
		}

		final ArrayRandomAccess< UnsignedByteType > ra2 = img2.randomAccess();
		ra2.setPosition( new int[] { 10, 10, 10 } );
		ra2.get().set( 55 );

		ImageJFunctions.show( img2 );

	}

	/**
	 * Basic processing.
	 */
	public static < T extends RealType< T > & NativeType< T >> void main4( final String[] args )
	{

		ImageJ.main( args );
		final File file = new File( "/Users/tinevez/Desktop/Data/Uneven.tif" );
		// final File file = new File(
		// "/Users/JeanYves/Desktop/Data/brightblobs.tif" );
		final ImagePlus imp = IJ.openImage( file.getAbsolutePath() );
		final Img< T > img = ImagePlusAdapter.wrap( imp );

		final long start = System.currentTimeMillis();

		final Shape shape = new DiamondShape( 5 );
		final Img< T > target = PeriodicLineNeighborhoodTest.dilate( img, shape );

		final long end = System.currentTimeMillis();

		System.out.println( "Processing done in " + ( end - start ) + " ms." );// DEBUG

		ImageJFunctions.show( img );
		ImageJFunctions.show( target );

	}

}
