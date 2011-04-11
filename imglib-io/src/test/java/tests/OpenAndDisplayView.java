package tests;

import ij.ImageJ;
import ij.ImagePlus;
import ij.process.ColorProcessor;
import mpicbg.imglib.RandomAccessible;
import mpicbg.imglib.RandomAccessibleInterval;
import mpicbg.imglib.display.ARGBScreenImage;
import mpicbg.imglib.display.RealARGBConverter;
import mpicbg.imglib.display.XYProjector;
import mpicbg.imglib.img.Img;
import mpicbg.imglib.img.ImgFactory;
import mpicbg.imglib.img.array.ArrayImgFactory;
import mpicbg.imglib.io.ImgOpener;
import mpicbg.imglib.type.numeric.ARGBType;
import mpicbg.imglib.type.numeric.real.FloatType;
import mpicbg.imglib.view.Views;


public class OpenAndDisplayView
{
	final static public void main( final String[] args )
	{
		new ImageJ();
		
		Img< FloatType > img = null;
		try
		{
			ImgFactory< FloatType > imgFactory = new ArrayImgFactory< FloatType >();
			final ImgOpener io = new ImgOpener();
			img = io.openImg( "/home/tobias/workspace/imglib2/imglib/wingclip.tif", imgFactory, new FloatType() );
		}
		catch ( Exception e )
		{
			e.printStackTrace();
			return;
		}

		RandomAccessible< FloatType >         view1 = Views.extend( img );	
		RandomAccessibleInterval< FloatType > view2 = Views.superIntervalView( view1, new long[] {-20, -20}, new long[] {157, 157} );		
		RandomAccessible< FloatType >         view3 = Views.extend( view2 );	
		RandomAccessibleInterval< FloatType > view4 = Views.superIntervalView( view3, new long[] {-100, -100}, new long[] {357, 357} );		
		RandomAccessibleInterval< FloatType > view5 = Views.superIntervalView( view4, new long[] {120, 120}, new long[] {117, 117} );		
		
		RandomAccessibleInterval< FloatType > finalView = view5;
		
		final ARGBScreenImage screenImage = new ARGBScreenImage( ( int )finalView.dimension( 0 ), ( int )finalView.dimension( 1 ) );
		final XYProjector< FloatType, ARGBType > projector = new XYProjector< FloatType, ARGBType >( finalView, screenImage, new RealARGBConverter< FloatType >( 0, 255 ) );
		projector.map();

		final ColorProcessor cp = new ColorProcessor( screenImage.image() );
		final ImagePlus imp = new ImagePlus( "argbScreenProjection", cp );
		imp.show();		
	}
}
