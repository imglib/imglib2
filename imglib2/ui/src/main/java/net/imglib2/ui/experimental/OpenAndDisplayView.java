package net.imglib2.ui.experimental;

import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.display.ARGBScreenImage;
import net.imglib2.display.RealARGBConverter;
import net.imglib2.display.XYProjector;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;


public class OpenAndDisplayView
{
	final static public void main( final String[] args )
	{
		//new ImageJ();
		
		Img< FloatType > img = null;
		try
		{
			ImgFactory< FloatType > imgFactory = new ArrayImgFactory< FloatType >();
			final ImgOpener io = new ImgOpener();
			img = io.openImg( "C:/TestImages/Cells.tif", imgFactory, new FloatType() ).getImg();
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
		
		

//		final ColorProcessor cp = new ColorProcessor( screenImage.image() );
//		final ImagePlus imp = new ImagePlus( "argbScreenProjection", cp );
//		imp.show();		
	}
}
