package mpicbg.imglib.workshop.prewritten;

import ij.ImageJ;

import java.io.File;

import mpicbg.imglib.algorithm.CanvasImage;
import mpicbg.imglib.container.array.ArrayContainerFactory;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.display.imagej.ImageJFunctions;
import mpicbg.imglib.io.LOCI;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyMirrorExpWindowingFactory;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyMirrorFactory;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyPeriodicFactory;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyValueFactory;
import mpicbg.imglib.type.numeric.RealType;
import mpicbg.imglib.type.numeric.real.FloatType;
import mpicbg.imglib.util.Util;

/**
 * Illustrate what the outside strategies do
 * 
 * @author Stephan Preibisch & Stephan Saalfeld
 *
 */
public class Example5
{
	public Example5()
	{
		// define the file to open
		File file = new File( "DrosophilaWingSmall.tif" );

		// open with LOCI using an ArrayContainer
		Image<FloatType> image = LOCI.openLOCIFloatType( file.getAbsolutePath(), new ArrayContainerFactory() );

		// test serveral out of bounds strategies
		testCanvas( image, new OutOfBoundsStrategyValueFactory<FloatType>() );
		testCanvas( image, new OutOfBoundsStrategyValueFactory<FloatType>( new FloatType( 128 ) ) );
		testCanvas( image, new OutOfBoundsStrategyMirrorFactory<FloatType>() );
		testCanvas( image, new OutOfBoundsStrategyPeriodicFactory<FloatType>() );
		testCanvas( image, new OutOfBoundsStrategyMirrorExpWindowingFactory<FloatType>( 0.5f ) );
	}
	
	public <T extends RealType<T>> void testCanvas( final Image<T> img, final OutOfBoundsStrategyFactory<T> outofboundsFactory )
	{
		final int[] newSize = new int[ img.getNumDimensions() ];
		
		for ( int d = 0; d < img.getNumDimensions(); ++d )
			newSize[ d ] = Util.round( img.getDimension( d ) * 3 );
		
		final CanvasImage<T> canvas = new CanvasImage<T>( img, newSize, outofboundsFactory );
		
		if ( canvas.checkInput() && canvas.process() )
		{
			Image<T> out = canvas.getResult();
			
			out.setName( outofboundsFactory.getClass().getSimpleName() + " took " + canvas.getProcessingTime() + " ms." );
			out.getDisplay().setMinMax();			
			ImageJFunctions.displayAsVirtualStack( out ).show();
		}
		else
		{
			System.out.println( canvas.getErrorMessage() );
		}
	}
	
	
	public static void main( String[] args )
	{
		// open an ImageJ window
		new ImageJ();
		
		// run the example
		new Example5();
	}
}
