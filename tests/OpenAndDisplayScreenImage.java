package tests;

import mpicbg.imglib.container.Img;
import mpicbg.imglib.container.array.ArrayContainerFactory;
import mpicbg.imglib.display.ARGBScreenImage;
import mpicbg.imglib.display.RealARGBConverter;
import mpicbg.imglib.display.XYProjector;
import mpicbg.imglib.io.LOCI;
import mpicbg.imglib.type.numeric.ARGBType;
import mpicbg.imglib.type.numeric.real.FloatType;
import ij.ImageJ;
import ij.ImagePlus;
import ij.process.ColorProcessor;

public class OpenAndDisplayScreenImage
{	
	final static public void main( final String[] args )
	{
		new ImageJ();
		
		Img< FloatType > img = LOCI.openLOCIFloatType( "/home/saalfeld/Desktop/73.tif",  new ArrayContainerFactory<FloatType>() );
		
		final ARGBScreenImage screenImage = new ARGBScreenImage( ( int )img.size( 0 ), ( int )img.size( 1 ) );
		final XYProjector< FloatType, ARGBType > projector = new XYProjector< FloatType, ARGBType >( img, screenImage, new RealARGBConverter( 0, 127 ) );
		projector.setPosition( 40, 2 );
		
		projector.map();
		
		final ColorProcessor cp = new ColorProcessor( screenImage.image() );
		
		new ImagePlus( "argbScreenProjection", cp ).show();

	}
}
