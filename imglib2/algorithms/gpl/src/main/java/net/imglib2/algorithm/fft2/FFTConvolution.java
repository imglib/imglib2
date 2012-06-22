package net.imglib2.algorithm.fft2;

import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.complex.ComplexFloatType;
import net.imglib2.view.Views;

public class FFTConvolution 
{
	final public static < R extends RealType< R > > void convolve( final RandomAccessible< R > img, final Interval imgDimensions, final RandomAccessible< R > kernel, final Interval kernelDimensions )
	{
		final int numDimensions = imgDimensions.numDimensions();
		
		// the image has to be extended at least by kernelDimensions/2-1 in each dimension so that
		// the pixels outside of the interval are used for the convolution.
		final int[] newDimensions = new int[ numDimensions ];
		
		for ( int d = 0; d < numDimensions; ++d )
			newDimensions[ d ] = (int)imgDimensions.dimension( d ) + (int)kernelDimensions.dimension( d ) - 1;

		// compute the size of the complex-valued output and the required padding
		// based on the prior extended input image
		final int[] paddedDimensions = new int[ numDimensions ];
		final int[] fftDimensions = new int[ numDimensions ];
		
		FFTMethods.dimensionsRealToComplexFast( newDimensions, paddedDimensions, fftDimensions );

		// compute the new interval for the input image
		final Interval imgConvolutionInterval = FFTMethods.paddingIntervalCentered( imgDimensions, paddedDimensions );
		
		// compute the new interval for the kernel image
		final Interval kernelConvolutionInterval = FFTMethods.paddingIntervalCentered( kernelDimensions, paddedDimensions );
	}
	
	final private static long[] maxDimensions( final Interval i1, final Interval i2 )
	{
		final long[] max = new long[ i1.numDimensions() ];
		
		for ( int d = 0; d < i1.numDimensions(); ++d )
			max[ d ] = Math.max( i1.dimension( d ), i2.dimension( d ) );
		
		return max;
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
