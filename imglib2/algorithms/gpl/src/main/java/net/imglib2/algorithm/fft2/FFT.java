package net.imglib2.algorithm.fft2;

import net.imglib2.Interval;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.type.numeric.ComplexType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;

public class FFT 
{
	final public static < R extends RealType< R >, C extends ComplexType< C > > Img< C > realToComplex( final RandomAccessible< R > input, Interval inputInterval, final ImgFactory< C > factory, final C type )
	{
		// compute the size of the complex-valued output and the required padding
		final int[] paddedDimensions = new int[ input.numDimensions() ];
		final int[] fftDimensions = new int[ input.numDimensions() ];
		
		FFTMethods.dimensionsRealToComplexFast( inputInterval, paddedDimensions, fftDimensions );
		
		// if it is not the right size adjust the interval
		if ( !FFTMethods.dimensionsEqual( inputInterval, paddedDimensions ) )
			inputInterval = FFTMethods.paddingIntervalCentered( inputInterval, paddedDimensions );
		
		// create the output Img 
		final Img< C > fft = factory.create( fftDimensions, type );
		
		// real-to-complex fft
		realToComplex( Views.interval( input, inputInterval ), fft );
		
		return fft;
	}	
	
	final public static < R extends RealType< R >, C extends ComplexType< C > > void realToComplex( final RandomAccessibleInterval< R > input, final RandomAccessibleInterval< C > output )
	{
		FFTMethods.realToComplex( input, output, 0 );
		
		for ( int d = 1; d < input.numDimensions(); ++d )
			FFTMethods.complexToComplex( output, d, true );
	}

	final public static < C extends ComplexType< C > > void complexToComplexForward( final RandomAccessibleInterval< C > data )
	{
		for ( int d = 0; d < data.numDimensions(); ++d )
			FFTMethods.complexToComplex( data, d, true );
	}

	final public static < C extends ComplexType< C > > void complexToComplexInverse( final RandomAccessibleInterval< C > data )
	{
		for ( int d = 0; d < data.numDimensions(); ++d )
			FFTMethods.complexToComplex( data, d, false );
	}

	final public static < C extends ComplexType< C >, R extends RealType< R > > void complexToReal( final RandomAccessibleInterval< C > input, final RandomAccessibleInterval< R > output )
	{
		for ( int d = 1; d < input.numDimensions(); ++d )
			FFTMethods.complexToComplex( input, d, false );
		
		FFTMethods.complexToReal( input, output, 0 );
	}
}
