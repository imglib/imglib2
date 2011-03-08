package tests;

import ij.ImageJ;
import ij.ImagePlus;
import ij.process.ColorProcessor;
import mpicbg.imglib.Cursor;
import mpicbg.imglib.RandomAccess;
import mpicbg.imglib.display.ARGBScreenImage;
import mpicbg.imglib.display.RealARGBConverter;
import mpicbg.imglib.display.XYProjector;
import mpicbg.imglib.img.Img;
import mpicbg.imglib.img.ImgFactory;
import mpicbg.imglib.img.array.ArrayImgFactory;
import mpicbg.imglib.io.LOCI;
import mpicbg.imglib.type.Type;
import mpicbg.imglib.type.numeric.ARGBType;
import mpicbg.imglib.type.numeric.real.FloatType;
import mpicbg.imglib.view.View;
import mpicbg.imglib.view.Views;


public class OpenAndDisplayView
{
	public static < T extends Type< T > > void copy (View< T > src, Img< T > dst)
	{
		final Cursor< T > srcCursor = src.localizingCursor();
		final RandomAccess< T > dstCursor = dst.randomAccess();
		
		int[] position = new int[ src.numDimensions() ];
		while ( srcCursor.hasNext() )
		{
			srcCursor.fwd();
			srcCursor.localize( position );
			dstCursor.setPosition( position );
			dstCursor.get().set( srcCursor.get() );
		}
	}

	final static public void main( final String[] args )
	{
		new ImageJ();
		
		ImgFactory< FloatType > imgFactory = new ArrayImgFactory< FloatType >();
		
		Img< FloatType > img = LOCI.openLOCIFloatType( "/home/tobias/workspace/imglib2/imglib/DrosophilaWingMarked.tif", imgFactory );

		long[] viewDimension = new long[] {100, 100};
		View< FloatType > v1 = Views.superIntervalView( Views.view( img ), new long[] {65, 80}, viewDimension );
		View< FloatType > v2 = Views.flippedView( v1, 0 );
		View< FloatType > v3 = Views.flippedView( v2, 1 );

		viewDimension = new long[] {120, 120};
//		View< FloatType > v4 = Views.superIntervalView( Views.view( v3 ), new long[] {-10, -10}, viewDimension );
		View< FloatType > v4 = Views.superIntervalView( v3, new long[] {-10, -10}, viewDimension );

		/*
		ViewTransform t2 = new ViewTransform( 2, 2 );
		t2.setPermutation( new boolean[] {false, false}, new int[] {1, 0}, new boolean[] {false, true} );
		View< FloatType > v2 = new ViewTransformView< FloatType >( v1, t2, viewDimension );
		
		ViewTransform t3 = new ViewTransform( 2, 2 );
		t3.setTranslation( new long[] {-viewDimension[ 0 ], 0} );
		View< FloatType > v3 = new ViewTransformView< FloatType >( v2, t3, viewDimension );
		*/
			
		Img< FloatType > viewCopy = imgFactory.create( viewDimension, new FloatType () );
		copy( v4, viewCopy );
		
		final ARGBScreenImage screenImage = new ARGBScreenImage( ( int )viewCopy.dimension( 0 ), ( int )viewCopy.dimension( 1 ) );
		final XYProjector< FloatType, ARGBType > projector = new XYProjector< FloatType, ARGBType >( viewCopy, screenImage, new RealARGBConverter< FloatType >( 0, 255 ) );
		projector.map();

		final ColorProcessor cp = new ColorProcessor( screenImage.image() );
		final ImagePlus imp = new ImagePlus( "argbScreenProjection", cp );
		imp.show();		
	}
}
