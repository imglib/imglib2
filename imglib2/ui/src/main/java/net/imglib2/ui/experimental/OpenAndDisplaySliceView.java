package net.imglib2.ui.experimental;

//import ij.ImageJ;
//import ij.ImagePlus;
//import ij.process.ColorProcessor;
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


public class OpenAndDisplaySliceView
{
	final static public void main( final String[] args )
	{
//		new ImageJ();
		
		Img< FloatType > img = null;
		try
		{
			ImgFactory< FloatType > imgFactory = new ArrayImgFactory< FloatType >();
			final ImgOpener io = new ImgOpener();
			img = io.openImg( "/home/tobias/Desktop/73.tif", imgFactory, new FloatType() ).getImg();
		}
		catch ( Exception e )
		{
			e.printStackTrace();
			return;
		}

		RandomAccessibleInterval< FloatType > view = Views.hyperSlice( Views.hyperSlice( img, 2, 0 ), 0, 50 );

		final ARGBScreenImage screenImage = new ARGBScreenImage( ( int )view.dimension( 0 ), ( int )view.dimension( 1 ) );
		final XYProjector< FloatType, ARGBType > projector = new XYProjector< FloatType, ARGBType >( view, screenImage, new RealARGBConverter< FloatType >( 0, 255 ) );
		projector.map();

//		final ColorProcessor cp = new ColorProcessor( screenImage.image() );
//		final ImagePlus imp = new ImagePlus( "argbScreenProjection", cp );
//		imp.show();	
	}
}
