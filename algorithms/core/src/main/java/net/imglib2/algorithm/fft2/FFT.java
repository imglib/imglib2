/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package net.imglib2.algorithm.fft2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.imglib2.FinalDimensions;
import net.imglib2.Interval;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.type.numeric.ComplexType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.complex.ComplexFloatType;
import net.imglib2.util.Util;
import net.imglib2.view.Views;

/**
 * Compute a FFT transform, either real-to-complex, complex-to-complex, or
 * complex-to-real for an entire dataset. Unfortunately only supports a maximal
 * size of INT in each dimension as the one-dimensional FFT is based on arrays.
 * 
 * @author Stephan Preibisch (stephan.preibisch@gmx.de)
 */
public class FFT
{
	final public static < R extends RealType< R > > Img< ComplexFloatType > realToComplex( final RandomAccessibleInterval< R > input, final ImgFactory< ComplexFloatType > factory )
	{
		return realToComplex( Views.extendValue( input, Util.getTypeFromInterval( input ).createVariable() ), input, factory, new ComplexFloatType(), Runtime.getRuntime().availableProcessors() );
	}
	
	final public static < R extends RealType< R > > Img< ComplexFloatType > realToComplex( final RandomAccessibleInterval< R > input, final ImgFactory< ComplexFloatType > factory, final int numThreads )
	{
		return realToComplex( Views.extendValue( input, Util.getTypeFromInterval( input ).createVariable() ), input, factory, new ComplexFloatType(), numThreads );
	}

	final public static < R extends RealType< R > > Img< ComplexFloatType > realToComplex( final RandomAccessibleInterval< R > input, final ImgFactory< ComplexFloatType > factory, final ExecutorService service )
	{
		return realToComplex( Views.extendValue( input, Util.getTypeFromInterval( input ).createVariable() ), input, factory, new ComplexFloatType(), service );
	}

	final public static < R extends RealType< R > > Img< ComplexFloatType > realToComplex( final RandomAccessibleInterval< R > input, final OutOfBoundsFactory< R, RandomAccessibleInterval< R > > oobs, final ImgFactory< ComplexFloatType > factory )
	{
		return realToComplex( Views.extend( input, oobs ), input, factory, new ComplexFloatType(), Runtime.getRuntime().availableProcessors() );
	}

	final public static < R extends RealType< R > > Img< ComplexFloatType > realToComplex( final RandomAccessibleInterval< R > input, final OutOfBoundsFactory< R, RandomAccessibleInterval< R > > oobs, final ImgFactory< ComplexFloatType > factory, final int numThreads )
	{
		return realToComplex( Views.extend( input, oobs ), input, factory, new ComplexFloatType(), numThreads );
	}
	
	final public static < R extends RealType< R > > Img< ComplexFloatType > realToComplex( final RandomAccessibleInterval< R > input, final OutOfBoundsFactory< R, RandomAccessibleInterval< R > > oobs, final ImgFactory< ComplexFloatType > factory, final ExecutorService service )
	{
		return realToComplex( Views.extend( input, oobs ), input, factory, new ComplexFloatType(), service );
	}

	final public static < R extends RealType< R >, C extends ComplexType< C > > Img< C > realToComplex( final RandomAccessible< R > input, final Interval inputInterval, final ImgFactory< C > factory, final C type )
	{
		return realToComplex( input, inputInterval, factory, type, Runtime.getRuntime().availableProcessors() );
	}

	final public static < R extends RealType< R >, C extends ComplexType< C > > Img< C > realToComplex( final RandomAccessible< R > input, final Interval inputInterval, final ImgFactory< C > factory, final C type, final int numThreads )
	{
		final ExecutorService service = Executors.newFixedThreadPool( numThreads );
		final Img< C >  ret = realToComplex( input, inputInterval, factory, type, service );
		service.shutdown();
		
		return ret;
	}

	final public static < R extends RealType< R >, C extends ComplexType< C > > Img< C > realToComplex( final RandomAccessible< R > input, Interval inputInterval, final ImgFactory< C > factory, final C type, final ExecutorService service )
	{
		// compute the size of the complex-valued output and the required
		// padding
		final long[] paddedDimensions = new long[ input.numDimensions() ];
		final long[] fftDimensions = new long[ input.numDimensions() ];

		FFTMethods.dimensionsRealToComplexFast( inputInterval, paddedDimensions, fftDimensions );

		// create the output Img
		final Img< C > fft = factory.create( fftDimensions, type );

		// if the input size is not the right size adjust the interval
		if ( !FFTMethods.dimensionsEqual( inputInterval, paddedDimensions ) )
			inputInterval = FFTMethods.paddingIntervalCentered( inputInterval, FinalDimensions.wrap( paddedDimensions ) );

		// real-to-complex fft
		realToComplex( Views.interval( input, inputInterval ), fft, service );

		return fft;
	}

	final public static < C extends ComplexType< C >, R extends RealType< R > > Img< R > complexToReal( final RandomAccessibleInterval< C > input, final ImgFactory< R > factory, final R type )
	{
		return complexToReal( input, input, null, factory, type, Runtime.getRuntime().availableProcessors() );
	}

	final public static < C extends ComplexType< C >, R extends RealType< R > > Img< R > complexToReal( final RandomAccessibleInterval< C > input, final ImgFactory< R > factory, final R type, final int numThreads )
	{
		return complexToReal( input, input, null, factory, type, numThreads );
	}
	
	final public static < C extends ComplexType< C >, R extends RealType< R > > Img< R > complexToReal( final RandomAccessibleInterval< C > input, final ImgFactory< R > factory, final R type, final ExecutorService service )
	{
		return complexToReal( input, input, null, factory, type, service );
	}

	final public static < C extends ComplexType< C >, R extends RealType< R > > Img< R > complexToReal( final RandomAccessibleInterval< C > input, final Interval outputDimensions, final ImgFactory< R > factory, final R type )
	{
		return complexToReal( input, input, outputDimensions, factory, type, Runtime.getRuntime().availableProcessors() );
	}
	
	final public static < C extends ComplexType< C >, R extends RealType< R > > Img< R > complexToReal( final RandomAccessibleInterval< C > input, final Interval outputDimensions, final ImgFactory< R > factory, final R type, final int numThreads )
	{
		return complexToReal( input, input, outputDimensions, factory, type, numThreads );
	}

	final public static < C extends ComplexType< C >, R extends RealType< R > > Img< R > complexToReal( final RandomAccessibleInterval< C > input, final Interval outputDimensions, final ImgFactory< R > factory, final R type, final ExecutorService service )
	{
		return complexToReal( input, input, outputDimensions, factory, type, service );
	}

	final public static < C extends ComplexType< C >, R extends RealType< R > > Img< R > complexToReal( final RandomAccessible< C > input, final Interval inputInterval, final ImgFactory< R > factory, final R type )
	{
		return complexToReal( input, inputInterval, null, factory, type, Runtime.getRuntime().availableProcessors() );
	}
	
	final public static < C extends ComplexType< C >, R extends RealType< R > > Img< R > complexToReal( final RandomAccessible< C > input, final Interval inputInterval, final ImgFactory< R > factory, final R type, final int numThreads )
	{
		return complexToReal( input, inputInterval, null, factory, type, numThreads );
	}

	final public static < C extends ComplexType< C >, R extends RealType< R > > Img< R > complexToReal( final RandomAccessible< C > input, final Interval inputInterval, final ImgFactory< R > factory, final R type, final ExecutorService service )
	{
		return complexToReal( input, inputInterval, null, factory, type, service );
	}

	final public static < C extends ComplexType< C >, R extends RealType< R > > Img< R > complexToReal( final RandomAccessible< C > input, final Interval inputInterval, final Interval outputDimensions, final ImgFactory< R > factory, final R type )
	{
		return complexToReal( input, inputInterval, outputDimensions, factory, type, Runtime.getRuntime().availableProcessors() );
	}

	final public static < C extends ComplexType< C >, R extends RealType< R > > Img< R > complexToReal( final RandomAccessible< C > input, final Interval inputInterval, final Interval outputDimensions, final ImgFactory< R > factory, final R type, final int numThreads )
	{
		final ExecutorService service = Executors.newFixedThreadPool( numThreads );
		final Img< R > ret = complexToReal( input, inputInterval, outputDimensions, factory, type, service );
		service.shutdown();

		return ret;
	}

	final public static < C extends ComplexType< C >, R extends RealType< R > > Img< R > complexToReal( final RandomAccessible< C > input, Interval inputInterval, final Interval outputDimensions, final ImgFactory< R > factory, final R type, final ExecutorService service )
	{
		final int numDimensions = input.numDimensions();

		// compute the size of the complex-valued output and the required
		// padding
		final long[] paddedDimensions = new long[ numDimensions ];
		final long[] realDimensions = new long[ numDimensions ];

		FFTMethods.dimensionsComplexToRealFast( inputInterval, paddedDimensions, realDimensions );

		// if it is not the right size adjust the interval
		if ( !FFTMethods.dimensionsEqual( inputInterval, paddedDimensions ) )
		{
			System.out.println( "adjusting complex input" );
			inputInterval = FFTMethods.paddingIntervalCentered( inputInterval, FinalDimensions.wrap( paddedDimensions ) );
		}

		final RandomAccessibleInterval< C > fft = Views.interval( input, inputInterval );

		// create the output Img
		if ( outputDimensions == null )
		{
			// without cropping
			final Img< R > output = factory.create( realDimensions, type );

			for ( int d = numDimensions - 1; d > 0; --d )
				FFTMethods.complexToComplex( fft, d, false );

			FFTMethods.complexToReal( fft, output, 0, true, service );

			return output;
		}
		// with cropping, computed based on the original size of the input image
		final Img< R > output = factory.create( outputDimensions, type );

		for ( int d = numDimensions - 1; d > 0; --d )
			FFTMethods.complexToComplex( fft, d, false, true, service );

		FFTMethods.complexToReal( fft, output, FFTMethods.unpaddingIntervalCentered( inputInterval, outputDimensions ), 0, true, service );

		return output;
	}

	final public static < R extends RealType< R >, C extends ComplexType< C > > void realToComplex( final RandomAccessibleInterval< R > input, final RandomAccessibleInterval< C > output )
	{
		realToComplex( input, output, Runtime.getRuntime().availableProcessors() );
	}
	
	final public static < R extends RealType< R >, C extends ComplexType< C > > void realToComplex( final RandomAccessibleInterval< R > input, final RandomAccessibleInterval< C > output, final int numThreads )
	{
		final ExecutorService service = Executors.newFixedThreadPool(numThreads);
		realToComplex(input, output, service);
		service.shutdown();
	}

	final public static < R extends RealType< R >, C extends ComplexType< C > > void realToComplex( final RandomAccessibleInterval< R > input, final RandomAccessibleInterval< C > output, final ExecutorService service )
	{
		FFTMethods.realToComplex( input, output, 0, false, service );

		for ( int d = 1; d < input.numDimensions(); ++d )
			FFTMethods.complexToComplex( output, d, true, false, service );
	}

	final public static < C extends ComplexType< C > > void complexToComplexForward( final RandomAccessibleInterval< C > data )
	{
		complexToComplexForward( data, Runtime.getRuntime().availableProcessors() );
	}

	final public static < C extends ComplexType< C > > void complexToComplexForward( final RandomAccessibleInterval< C > data, final int numThreads )
	{
		final ExecutorService service = Executors.newFixedThreadPool(numThreads);
		complexToComplexForward(data, service);
		service.shutdown();
	}
	
	final public static < C extends ComplexType< C > > void complexToComplexForward( final RandomAccessibleInterval< C > data, final ExecutorService service )
	{
		for ( int d = 0; d < data.numDimensions(); ++d )
			FFTMethods.complexToComplex( data, d, true, false, service );
	}

	final public static < C extends ComplexType< C > > void complexToComplexInverse( final RandomAccessibleInterval< C > data )
	{
		complexToComplexInverse( data, Runtime.getRuntime().availableProcessors() );
	}

	final public static < C extends ComplexType< C > > void complexToComplexInverse( final RandomAccessibleInterval< C > data, final int numThreads )
	{
		final ExecutorService service = Executors.newFixedThreadPool(numThreads);
		complexToComplexInverse(data, service);
		service.shutdown();
	}
	
	final public static < C extends ComplexType< C > > void complexToComplexInverse( final RandomAccessibleInterval< C > data, final ExecutorService service )
	{
		for ( int d = 0; d < data.numDimensions(); ++d )
			FFTMethods.complexToComplex( data, d, false, true, service );
	}

	final public static < C extends ComplexType< C >, R extends RealType< R > > void complexToReal( final RandomAccessibleInterval< C > input, final RandomAccessibleInterval< R > output )
	{
		complexToReal( input, output, Runtime.getRuntime().availableProcessors() );
	}

	final public static < C extends ComplexType< C >, R extends RealType< R > > void complexToReal( final RandomAccessibleInterval< C > input, final RandomAccessibleInterval< R > output, final int numThreads )
	{
		final ExecutorService service = Executors.newFixedThreadPool( numThreads );
		complexToReal( input, output, service );
		service.shutdown();
	}

	final public static < C extends ComplexType< C >, R extends RealType< R > > void complexToReal( final RandomAccessibleInterval< C > input, final RandomAccessibleInterval< R > output, final ExecutorService service )
	{
		for ( int d = 1; d < input.numDimensions(); ++d )
			FFTMethods.complexToComplex( input, d, false, true, service );

		FFTMethods.complexToReal( input, output, 0, true, service );
	}

	final public static < C extends ComplexType< C >, R extends RealType< R > > void complexToRealUnpad( final RandomAccessibleInterval< C > input, final RandomAccessibleInterval< R > output )
	{
		complexToRealUnpad( input, output, Runtime.getRuntime().availableProcessors() );
	}

	final public static < C extends ComplexType< C >, R extends RealType< R > > void complexToRealUnpad( final RandomAccessibleInterval< C > input, final RandomAccessibleInterval< R > output, final int numThreads )
	{
		final ExecutorService service = Executors.newFixedThreadPool(numThreads);
		complexToRealUnpad(input, output, service);
		service.shutdown();
	}

	final public static < C extends ComplexType< C >, R extends RealType< R > > void complexToRealUnpad( final RandomAccessibleInterval< C > input, final RandomAccessibleInterval< R > output, final ExecutorService service )
	{
		for ( int d = 1; d < input.numDimensions(); ++d )
			FFTMethods.complexToComplex( input, d, false, true, service );

		FFTMethods.complexToReal( input, output, FFTMethods.unpaddingIntervalCentered( input, output ), 0, true, service );
	}
}
