package net.imglib2.view;

import ij.ImageJ;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.real.FloatType;

public class CopyViews
{
	public static < T extends Type< T > > void copy( RandomAccessible< T > src, RandomAccessibleInterval< T > dst )
	{
		final RandomAccessibleZeroMinIntervalCursor< T > dstCursor = new RandomAccessibleZeroMinIntervalCursor< T >( dst );
		final RandomAccess< T > srcAccess = src.randomAccess( dst );		
		while ( dstCursor.hasNext() )
		{
			dstCursor.fwd();
			srcAccess.setPosition( dstCursor );
			dstCursor.get().set( srcAccess.get() );
		}	
	}

	final static public void main( final String[] args )
	{
		new ImageJ();
		ImgFactory< FloatType > imgFactory = new ArrayImgFactory< FloatType >();

		Img< FloatType > inputImg = null;
		try
		{
			final ImgOpener io = new ImgOpener();
			inputImg = io.openImg( "/home/tobias/workspace/data/wingclip.tif", imgFactory, new FloatType() );
		}
		catch ( Exception e )
		{
			e.printStackTrace();
			return;
		}

		final long w = inputImg.dimension( 0 );
		final long h = inputImg.dimension( 1 );

		final long[] dim = new long[] { w * 2, h * 2 };
		Img< FloatType > outputImg = imgFactory.create( dim, new FloatType() );
		
		copy( inputImg, Views.superIntervalView( outputImg, new long[] {0,0}, new long[] {w,h} ) );
		copy( Views.flippedView( inputImg, 0 ), Views.superIntervalView( outputImg, new long[] {w,0}, new long[] {w,h} ) );
		copy( Views.flippedView( inputImg, 1 ), Views.superIntervalView( outputImg, new long[] {0,h}, new long[] {w,h} ) );
		copy( Views.flippedView( Views.flippedView( inputImg, 1 ), 0 ), Views.superIntervalView( outputImg, new long[] {w,h}, new long[] {w,h} ) );

		ImageJFunctions.show( outputImg );
//		final ARGBScreenImage screenImage = new ARGBScreenImage( ( int )outputImg.dimension( 0 ), ( int )outputImg.dimension( 1 ) );
//		final XYProjector< FloatType, ARGBType > projector = new XYProjector< FloatType, ARGBType >( outputImg, screenImage, new RealARGBConverter< FloatType >( 0, 255 ) );
//		projector.map();
//
//		final ColorProcessor cp = new ColorProcessor( screenImage.image() );
//		final ImagePlus imp = new ImagePlus( "argbScreenProjection", cp );
//		imp.show();		
	}
}
