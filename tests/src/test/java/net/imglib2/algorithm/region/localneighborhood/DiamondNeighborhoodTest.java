package net.imglib2.algorithm.region.localneighborhood;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;

import java.io.File;
import java.util.Iterator;

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

	public static < T extends RealType< T > & NativeType< T >> void main( final String[] args )
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
