package net.imglib2.examples;

import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import ij.ImageJ;

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
		// open with LOCI using an ArrayContainer
		Img<FloatType> image = LOCI.openLOCIFloatType( "DrosophilaWing.tif", new ArrayContainerFactory() );
		Img<FloatType> kernel = LOCI.openLOCIFloatType( "kernelGauss.tif", new ArrayContainerFactory() );

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
		kernel.getDisplay().setMinMax();
		kernel.setName( "kernel" );
		ImageJFunctions.copyToImagePlus( kernel ).show();

		image.getDisplay().setMinMax();
		ImageJFunctions.copyToImagePlus( image ).show();

		// compute fourier convolution
		FourierConvolution<FloatType, FloatType> fourierConvolution = new FourierConvolution<FloatType, FloatType>( image, kernel );

		if ( !fourierConvolution.checkInput() || !fourierConvolution.process() )
		{
			System.out.println( "Cannot compute fourier convolution: " + fourierConvolution.getErrorMessage() );
			return;
		}

		Img<FloatType> convolved = fourierConvolution.getResult();
		convolved.setName( "("  + fourierConvolution.getProcessingTime() + " ms) Convolution of " + image.getName() );

		convolved.getDisplay().setMinMax();
		ImageJFunctions.copyToImagePlus( convolved ).show();

	}

	public static void main( String[] args )
	{
		// open an ImageJ window
		new ImageJ();

		// run the example
		new Example8();
	}
}
