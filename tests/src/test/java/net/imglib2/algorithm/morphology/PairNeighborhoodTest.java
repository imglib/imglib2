package net.imglib2.algorithm.morphology;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;

import java.io.File;

import net.imglib2.algorithm.region.localneighborhood.PairShape;
import net.imglib2.algorithm.region.localneighborhood.Shape;
import net.imglib2.img.ImagePlusAdapter;
import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

public class PairNeighborhoodTest
{
	public static < T extends RealType< T > & NativeType< T >> void main( final String[] args )
	{

		ImageJ.main( args );
		final File file = new File( "/Users/tinevez/Desktop/Data/Uneven.tif" );
		// final File file = new File(
		// "/Users/JeanYves/Desktop/Data/brightblobs.tif" );
		final ImagePlus imp = IJ.openImage( file.getAbsolutePath() );
		final Img< T > img = ImagePlusAdapter.wrap( imp );

		final long start = System.currentTimeMillis();

		final Shape shape = new PairShape( new long[] { -10, 20 } );
		final Img< T > target = MorphologicalOperations.dilate( img, shape, 1 );

		final long end = System.currentTimeMillis();

		System.out.println( "Processing done in " + ( end - start ) + " ms." );// DEBUG

		ImageJFunctions.show( img );
		ImageJFunctions.show( target );

		final Shape shape2 = new PairShape( new long[] { 10, -20 } );
		final Img< T > target2 = MorphologicalOperations.dilate( img, shape2, 1 );
		ImageJFunctions.show( target2 );

		final Shape shape3 = new PairShape( new long[] { 10, 20 } );
		final Img< T > target3 = MorphologicalOperations.dilate( img, shape3, 1 );
		ImageJFunctions.show( target3 );

	}

}
