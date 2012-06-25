package net.imglib2.algorithm.fft2;

import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.complex.ComplexFloatType;
import net.imglib2.view.Views;

public class FFTConvolution 
{
	final public static < R extends RealType< R > > void convolve( final RandomAccessible< R > img, final Interval imgInterval, final RandomAccessible< R > kernel, final Interval kernelInterval, final ImgFactory< ComplexFloatType > factory )
	{
		final int numDimensions = imgInterval.numDimensions();
		
		// the image has to be extended at least by kernelDimensions/2-1 in each dimension so that
		// the pixels outside of the interval are used for the convolution.
		final int[] newDimensions = new int[ numDimensions ];
		
		for ( int d = 0; d < numDimensions; ++d )
		{
			newDimensions[ d ] = (int)imgInterval.dimension( d ) + (int)kernelInterval.dimension( d ) - 1;
			System.out.println( newDimensions[ d ] );
		}
		
		// compute the size of the complex-valued output and the required padding
		// based on the prior extended input image
		final int[] paddedDimensions = new int[ numDimensions ];
		final int[] fftDimensions = new int[ numDimensions ];
		
		FFTMethods.dimensionsRealToComplexFast( newDimensions, paddedDimensions, fftDimensions );

		// compute the new interval for the input image
		final Interval imgConvolutionInterval = FFTMethods.paddingIntervalCentered( imgInterval, paddedDimensions );
		
		// compute the new interval for the kernel image
		final Interval kernelConvolutionInterval = FFTMethods.paddingIntervalCentered( kernelInterval, paddedDimensions );

		// compute where to place the final Interval for the kernel so that the coordinate in the center
		// of the kernel is at position (0,0)
		long[] min = new long[ numDimensions ];
		long[] max = new long[ numDimensions ];
		
		for ( int d = 0; d < numDimensions; ++d )
		{
			min[ d ] = kernelInterval.min( d ) + kernelInterval.dimension( d ) / 2;
			max[ d ] = min[ d ] + kernelConvolutionInterval.dimension( d ) - 1;
		}
		
		// assemble the correct kernel (size of the input + extended periodic + top left at center of input kernel)
		final RandomAccessibleInterval< R > kernelInput = Views.interval( Views.extendPeriodic( Views.interval( kernel, kernelConvolutionInterval ) ), new FinalInterval( min, max ) );
		final RandomAccessibleInterval< R > imgInput = Views.interval( img, imgConvolutionInterval );
		
		// compute the FFT's
		final Img<ComplexFloatType> fftImg = FFT.realToComplex( imgInput, factory );
		final Img<ComplexFloatType> fftKernel = FFT.realToComplex( kernelInput, factory );
		
		// multiply in place
		multiplyComplex( fftImg, fftKernel );
		
		// inverse FFT in place
		FFT.complexToRealUnpad( fftImg, Views.interval( img, imgInterval ) );
	}
	
	final public static void multiplyComplex( final RandomAccessibleInterval< ComplexFloatType > img, final RandomAccessibleInterval< ComplexFloatType > kernel )
	{
		final IterableInterval< ComplexFloatType > iterableImg = Views.iterable( img );
		final IterableInterval< ComplexFloatType > iterableKernel = Views.iterable( kernel );
		
		if ( iterableImg.iterationOrder().equals( iterableKernel.iterationOrder() ) )
		{
			final Cursor< ComplexFloatType > cursorA = iterableImg.cursor();
			final Cursor< ComplexFloatType > cursorB = iterableKernel.cursor();
			
			while ( cursorA.hasNext() )
				cursorA.next().mul( cursorB.next() );
		}
		else
		{
			final Cursor< ComplexFloatType > cursorA = iterableImg.localizingCursor();
			final RandomAccess< ComplexFloatType > randomAccess = kernel.randomAccess();
			
			while ( cursorA.hasNext() )
			{
				final ComplexFloatType t = cursorA.next();
				randomAccess.setPosition( cursorA );
				
				t.mul( randomAccess.get() );
			}						
		}
	}

}
