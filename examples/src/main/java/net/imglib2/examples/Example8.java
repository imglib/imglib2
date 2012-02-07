package net.imglib2.examples;

import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.numeric.real.FloatType;
import ij.ImageJ;
import ij.ImagePlus;

/**
 * Perform a gaussian convolution using fourier convolution
 *
 * @author Stephan Preibisch &amp; Stephan Saalfeld
 *
 */
public class Example8
{
	public Example8()
	{
		// open with ImgOpener using an ArrayContainer
		Img<FloatType> image = new ImgOpener().openImg( "DrosophilaWing.tif", new ArrayImgFactory<FloatType>() );
		Img<FloatType> kernel = new ImgOpener().openImg( "kernelGauss.tif", new ArrayImgFactory<FloatType>() );

		// normalize the kernel
		NormalizeImageFloat<FloatType> normImage = new NormalizeImageFloat<FloatType>( kernel );

		if ( !normImage.checkInput() || !normImage.process() )
		{
			System.out.println( "Cannot normalize kernel: " + normImage.getErrorMessage() );
			return;
		}

		kernel.close();
		kernel = normImage.getResult();

		// display all
		final ImagePlus impKernel = ImageJFunctions.show( kernel );
		impKernel.resetDisplayRange();
		impKernel.setTitle( "kernel" );
		impKernel.show();

		final ImagePlus imp = ImageJFunctions.show( image );
		imp.resetDisplayRange();
		imp.show();

		// compute fourier convolution
		FourierConvolution<FloatType, FloatType> fourierConvolution = new FourierConvolution<FloatType, FloatType>( image, kernel );

		if ( !fourierConvolution.checkInput() || !fourierConvolution.process() )
		{
			System.out.println( "Cannot compute fourier convolution: " + fourierConvolution.getErrorMessage() );
			return;
		}

		Img<FloatType> convolved = fourierConvolution.getResult();

		final ImagePlus impConvolved = ImageJFunctions.show( convolved );
		impConvolved.resetDisplayRange();
		impConvolved.setTitle( "("  + fourierConvolution.getProcessingTime() + " ms) Convolution of " + image.getName() );
		impConvolved.show();

	}

	public static void main( String[] args )
	{
		// open an ImageJ window
		new ImageJ();

		// run the example
		new Example8();
	}
}
