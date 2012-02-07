package net.imglib2.examples;

import ij.ImageJ;
import ij.ImagePlus;

import java.io.File;

import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.numeric.real.FloatType;

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

		// open with ImgOpener using an ArrayContainer
		Img<FloatType> image = new ImgOpener().openImg( file.getAbsolutePath(), new ArrayImgFactory<FloatType>() );

		// display maxima
		final ImagePlus imp = ImageJFunctions.show( image );
		imp.resetDisplayRange();
		imp.show();

		// perform gaussian convolution
		GaussianConvolution<FloatType> gauss = new GaussianConvolution<FloatType>( image, new OutOfBoundsStrategyMirrorFactory<FloatType>(), new double[]{ 0, 0, 4} );

		// run the algorithm
		if( !gauss.checkInput() || !gauss.process() )
		{
			System.out.println( "Error running gaussian convolution: " + gauss.getErrorMessage() );
			return;
		}

		// get the result
		Img<FloatType> convolved = gauss.getResult();

		// display
		final ImagePlus impConvolved = ImageJFunctions.show( convolved );
		impConvolved.resetDisplayRange();
		impConvolved.show();
	}

	public static void main( String[] args )
	{
		// open an ImageJ window
		new ImageJ();

		// run the example
		new Example7();
	}
}
