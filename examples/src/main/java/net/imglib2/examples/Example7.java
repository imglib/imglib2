package net.imglib2.examples;

import ij.ImageJ;

import java.io.File;

import mpicbg.imglib.algorithm.gauss.GaussianConvolution;
import mpicbg.imglib.container.array.ArrayContainerFactory;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.display.imagej.ImageJFunctions;
import mpicbg.imglib.io.LOCI;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyMirrorFactory;
import mpicbg.imglib.type.numeric.real.FloatType;

/**
 * Use of Gaussian Convolution on the Image
 *
 * @author Stephan Preibisch &amp; Stephan Saalfeld
 *
 */
public class Example7
{
	public Example7()
	{
		// define the file to open
		File file = new File( "street_bw.tif" );

		// open with LOCI using an ArrayContainer
		Image<FloatType> image = LOCI.openLOCIFloatType( file.getAbsolutePath(), new ArrayContainerFactory() );

		// display maxima
		image.getDisplay().setMinMax();
		ImageJFunctions.copyToImagePlus( image ).show();

		// perform gaussian convolution
		GaussianConvolution<FloatType> gauss = new GaussianConvolution<FloatType>( image, new OutOfBoundsStrategyMirrorFactory<FloatType>(), new double[]{ 0, 0, 4} );

		// run the algorithm
		if( !gauss.checkInput() || !gauss.process() )
		{
			System.out.println( "Error running gaussian convolution: " + gauss.getErrorMessage() );
			return;
		}

		// get the result
		Image<FloatType> convolved = gauss.getResult();

		// display
		convolved.getDisplay().setMinMax();
		ImageJFunctions.displayAsVirtualStack( convolved ).show();
	}

	public static void main( String[] args )
	{
		// open an ImageJ window
		new ImageJ();

		// run the example
		new Example7();
	}
}
