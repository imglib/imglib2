package net.imglib2.examples;

import ij.ImageJ;

import java.io.File;

import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgOpener;

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

		// open with ImgOpener using an ArrayContainer
		Img<FloatType> image = new ImgOpener().openLOCIFloatType( file.getAbsolutePath(), new ArrayContainerFactory() );

		// perform gaussian convolution
		GaussianConvolution<FloatType> gauss = new GaussianConvolution<FloatType>( image, new OutOfBoundsStrategyValueFactory<FloatType>(), 4 );

		// run the algorithm
		if( !gauss.checkInput() || !gauss.process() )
		{
			System.out.println( "Error running gaussian convolution: " + gauss.getErrorMessage() );
			return;
		}

		// get the result
		Img<FloatType> convolved = gauss.getResult();

		// display
		convolved.getDisplay().setMinMax();
		ImageJFunctions.show( convolved ).show();

		// find maxima again
		final Img<ByteType> maxima = Example4.findAndDisplayLocalMaxima( convolved, new ByteType() );

		// display maxima
		maxima.getDisplay().setMinMax();
		ImageJFunctions.show( maxima ).show();
	}

	public static void main( String[] args )
	{
		// open an ImageJ window
		new ImageJ();

		// run the example
		new Example6();
	}
}
