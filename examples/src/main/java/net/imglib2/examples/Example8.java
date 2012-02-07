package net.imglib2.examples;

import ij.ImageJ;
import mpicbg.imglib.algorithm.fft.FourierConvolution;
import mpicbg.imglib.algorithm.math.NormalizeImageFloat;
import mpicbg.imglib.container.array.ArrayContainerFactory;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.display.imagej.ImageJFunctions;
import mpicbg.imglib.io.LOCI;
import mpicbg.imglib.type.numeric.real.FloatType;

/**
 * Perform a gaussian convolution using fourier convolution
 *
 * @author Stephan Preibisch & Stephan Saalfeld
 *
 */
public class Example8
{
	public Example8()
	{
		// open with LOCI using an ArrayContainer
		Image<FloatType> image = LOCI.openLOCIFloatType( "DrosophilaWing.tif", new ArrayContainerFactory() );
		Image<FloatType> kernel = LOCI.openLOCIFloatType( "kernelGauss.tif", new ArrayContainerFactory() );

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

		Image<FloatType> convolved = fourierConvolution.getResult();
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
