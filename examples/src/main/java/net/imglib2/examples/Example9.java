package net.imglib2.examples;

import mpicbg.imglib.algorithm.fft.FourierConvolution;
import mpicbg.imglib.algorithm.fft.FourierTransform;
import mpicbg.imglib.algorithm.fft.InverseFourierTransform;
import mpicbg.imglib.algorithm.math.NormalizeImageFloat;
import mpicbg.imglib.container.array.ArrayContainerFactory;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.display.imagej.ImageJFunctions;
import mpicbg.imglib.io.LOCI;
import mpicbg.imglib.type.numeric.complex.ComplexFloatType;
import mpicbg.imglib.type.numeric.real.FloatType;
import ij.ImageJ;

/**
 * Perform template matching through Convolution int the Fourier domain
 *
 * @author Stephan Preibisch &amp; Stephan Saalfeld
 *
 */
public class Example9
{
	public Example9()
	{
		// open with LOCI using an ArrayContainer
		Image<FloatType> image = LOCI.openLOCIFloatType( "JohannesAndAlbert.jpg", new ArrayContainerFactory() );
		Image<FloatType> kernel = LOCI.openLOCIFloatType( "kernelAlbert.tif", new ArrayContainerFactory() );

		final FourierTransform< FloatType, ComplexFloatType > fft = new FourierTransform< FloatType, ComplexFloatType >( kernel, new ComplexFloatType() );
		final Image< ComplexFloatType > kernelFFT;
		if ( fft.checkInput() && fft.process() )
			kernelFFT = fft.getResult();
		else
		{
			System.err.println( "Cannot compute fourier transform: " + fft.getErrorMessage() );
			return;
		}

		// complex invert the kernel
		final ComplexFloatType c = new ComplexFloatType();
		for ( final ComplexFloatType t : kernelFFT.createCursor() )
		{
			c.set( t );
			t.complexConjugate();
			c.mul( t );
			t.div( c );
		}

		// compute inverse fourier transform of the kernel
		final InverseFourierTransform< FloatType, ComplexFloatType > ifft = new InverseFourierTransform< FloatType, ComplexFloatType >( kernelFFT, fft );
		final Image< FloatType > kernelInverse;
		if ( ifft.checkInput() && ifft.process() )
			kernelInverse = ifft.getResult();
		else
		{
			System.err.println( "Cannot compute inverse fourier transform: " + ifft.getErrorMessage() );
			return;
		}

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

		kernelInverse.getDisplay().setMinMax();
		kernelInverse.setName( "inverse kernel" );
		ImageJFunctions.copyToImagePlus( kernelInverse ).show();

		image.getDisplay().setMinMax();
		ImageJFunctions.copyToImagePlus( image ).show();

		// compute fourier convolution
		FourierConvolution<FloatType, FloatType> fourierConvolution = new FourierConvolution<FloatType, FloatType>( image, kernelInverse );

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
		new Example9();
	}
}
