package net.imglib2.examples;

import ij.ImageJ;

import java.io.File;

/**
 * Use of Gaussian Convolution on the Image
 *
 * @author Stephan Preibisch &amp; Stephan Saalfeld
 *
 */
public class Example6
{
	public Example6()
	{
		// define the file to open
		File file = new File( "DrosophilaWing.tif" );

		// open with LOCI using an ArrayContainer
		Image<FloatType> image = LOCI.openLOCIFloatType( file.getAbsolutePath(), new ArrayContainerFactory() );

		// perform gaussian convolution
		GaussianConvolution<FloatType> gauss = new GaussianConvolution<FloatType>( image, new OutOfBoundsStrategyValueFactory<FloatType>(), 4 );

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

		// find maxima again
		final Image<ByteType> maxima = Example4.findAndDisplayLocalMaxima( convolved, new ByteType() );

		// display maxima
		maxima.getDisplay().setMinMax();
		ImageJFunctions.displayAsVirtualStack( maxima ).show();
	}

	public static void main( String[] args )
	{
		// open an ImageJ window
		new ImageJ();

		// run the example
		new Example6();
	}
}
