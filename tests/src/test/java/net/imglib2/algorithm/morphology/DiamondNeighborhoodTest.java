package net.imglib2.algorithm.morphology;


import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;

import java.io.File;
import java.util.List;

import net.imglib2.algorithm.region.localneighborhood.DiamondShape;
import net.imglib2.algorithm.region.localneighborhood.Shape;
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

	public static void main( final String[] args )
	{
		ImageJ.main( args );
		final ArrayImg< UnsignedByteType, ByteArray > img = ArrayImgs.unsignedBytes( 19, 19, 19 );
		final ArrayRandomAccess< UnsignedByteType > ra = img.randomAccess();
		ra.setPosition( new long[] { 9, 9, 9 } );
		ra.get().set( 255 );

		final int[] radiuses = new int[] { 2, 3, 5 };
		for ( final int radius : radiuses )
		{
			final List< Shape > strelStd = StructuringElements.diamond( radius, img.numDimensions(), false );
			final Img< UnsignedByteType > std = MorphologicalOperations.dilate( img, strelStd, 1 );
			final List< Shape > strelOpt = StructuringElements.diamond( radius, img.numDimensions(), true );
			final Img< UnsignedByteType > opt = MorphologicalOperations.dilate( img, strelOpt, 1 );

			ImageJFunctions.show( std, "Std " + radius );
			ImageJFunctions.show( opt, "Opt " + radius );
		}
	}

	/**
	 * Performance comparison between optimized & standard strel, 3D case.
	 */
	public static void main1( final String[] args )
	{
		final ArrayImg< UnsignedByteType, ByteArray > img = ArrayImgs.unsignedBytes( 39, 39, 39 );
		final ArrayRandomAccess< UnsignedByteType > ra = img.randomAccess();
		ra.setPosition( new long[] { 19, 19, 19 } );
		ra.get().set( 255 );

		System.out.println( "Full strel" );
		System.out.println( "radius\ttime(ms)" );
		// Warm up
		MorphologicalOperations.dilate( img, StructuringElements.diamond( 1, img.numDimensions(), false ), 1 );
		MorphologicalOperations.dilate( img, StructuringElements.diamond( 1, img.numDimensions(), false ), 1 );
		for ( int i = 1; i < 19; i++ )
		{
			final long start = System.currentTimeMillis();
			final List< Shape > strels = StructuringElements.diamond( i, img.numDimensions(), false );
			MorphologicalOperations.dilate( img, strels, 1 );
			final long end = System.currentTimeMillis();
			System.out.println( "" + i + '\t' + ( end - start ) );
		}

		System.out.println( "Decomposed strel" );
		System.out.println( "radius\ttime(ms)" );
		// Warm up
		MorphologicalOperations.dilate( img, StructuringElements.diamond( 1, img.numDimensions(), true ), 1 );
		MorphologicalOperations.dilate( img, StructuringElements.diamond( 1, img.numDimensions(), true ), 1 );
		for ( int i = 1; i < 19; i++ )
		{
			final long start = System.currentTimeMillis();
			final List< Shape > strels = StructuringElements.diamond( i, img.numDimensions(), true );
			MorphologicalOperations.dilate( img, strels, 1 );
			final long end = System.currentTimeMillis();
			System.out.println( "" + i + '\t' + ( end - start ) );
		}
	}

	/**
	 * Tune optimized 3D cases
	 *
	 * @param args
	 */
	public static void main7( final String[] args )
	{
		ImageJ.main( args );

		final ArrayImg< UnsignedByteType, ByteArray > img = ArrayImgs.unsignedBytes( 19, 19, 19 );
		final ArrayRandomAccess< UnsignedByteType > ra = img.randomAccess();
		ra.setPosition( new long[] { 9, 9, 9 } );
		ra.get().set( 255 );

		final long start = System.currentTimeMillis();
		final List< Shape > strels = StructuringElements.diamond( 6, img.numDimensions(), true );

		Img< UnsignedByteType > dilated = img;
		for ( final Shape strel : strels )
		{
			final String str = MorphologyUtils.printNeighborhood( strel, img.numDimensions() );
			System.out.println( str );
			dilated = MorphologicalOperations.dilate( dilated, strel, 1 );
			ImageJFunctions.show( dilated, "Decomposed strel: " + strel );
		}

		final long end = System.currentTimeMillis();
		System.out.println( "Optimized processing time: " + ( end - start ) + " ms." );


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

		System.out.println( "Full strel" );
		System.out.println( "radius\ttime(ms)" );
		// Warm up
		MorphologicalOperations.dilate( img, StructuringElements.diamond( 1, img.numDimensions(), false ), 1 );
		MorphologicalOperations.dilate( img, StructuringElements.diamond( 1, img.numDimensions(), false ), 1 );
		for ( int i = 0; i < 40; i++ )
		{
			final long start = System.currentTimeMillis();
			final List< Shape > strels = StructuringElements.diamond( i, img.numDimensions(), false );
			MorphologicalOperations.dilate( img, strels, 1 );
			final long end = System.currentTimeMillis();
			System.out.println( "" + i + '\t' + ( end - start ) );
		}

		System.out.println( "Decomposed strel" );
		System.out.println( "radius\ttime(ms)" );
		// Warm up
		MorphologicalOperations.dilate( img, StructuringElements.diamond( 1, img.numDimensions(), true ), 1 );
		MorphologicalOperations.dilate( img, StructuringElements.diamond( 1, img.numDimensions(), true ), 1 );
		for ( int i = 0; i < 40; i++ )
		{
			final long start = System.currentTimeMillis();
			final List< Shape > strels = StructuringElements.diamond( i, img.numDimensions(), true );
			MorphologicalOperations.dilate( img, strels, 1 );
			final long end = System.currentTimeMillis();
			System.out.println( "" + i + '\t' + ( end - start ) );
		}
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

		System.out.println( "Processing done in " + ( end - start ) + " ms." );

		ImageJFunctions.show( img );
		ImageJFunctions.show( target );

	}

}
