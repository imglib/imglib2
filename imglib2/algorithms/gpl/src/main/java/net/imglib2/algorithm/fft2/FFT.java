/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */
package net.imglib2.algorithm.fft2;

import java.util.concurrent.atomic.AtomicInteger;

import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.iterator.LocalizingZeroMinIntervalIterator;
import net.imglib2.multithreading.SimpleMultiThreading;
import net.imglib2.type.numeric.ComplexType;
import net.imglib2.type.numeric.RealType;
import edu.mines.jtk.dsp.FftComplex;
import edu.mines.jtk.dsp.FftReal;

/**
 * Compute a FFT transform, either real-to-complex or complex-to-complex. It can be computed for individual dimensions or for consecutively for all
 * 
 * Unfortunately only supports a maximal size of INT in each dimension as the one-dimensional FFT is based on arrays.
 * 
 * @author Stephan Preibisch (stephan.preibisch@gmx.de)
 */
public class FFT
{
	final public static < C extends ComplexType< C >, R extends RealType< R > > boolean complexToReal( final RandomAccessibleInterval< C > input, final RandomAccessibleInterval< R > output, final int dim )
	{
		return complexToReal( input, output, dim, Runtime.getRuntime().availableProcessors() );
	}
	
	final public static < C extends ComplexType< C >, R extends RealType< R > > boolean complexToReal( final RandomAccessibleInterval< C > input, final RandomAccessibleInterval< R > output, final int dim, final int numThreads )
	{
		final int numDimensions = input.numDimensions();
		
		final int inputSize[] = new int[ numDimensions ];
		final int outputSize[] = new int[ numDimensions ];

		// the size of the input and output image
		for ( int d = 0; d < numDimensions; ++d )
		{
			inputSize[ d ] = (int)input.dimension( d );
			outputSize[ d ] = (int)output.dimension( d );
		}
		
		// test if those are valid sizes in case of real to complex
		if ( !verifyRealToComplexfftDimensions( outputSize[ dim ], inputSize[ dim ] ) )
			return false;
		
		// perform the complex-to-real fft in a dimension multithreaded if more than one dimension exisits
		final int complexSize = inputSize[ dim ];
		final int realSize = outputSize[ dim ];
		
		if ( numDimensions > 1  )
		{
			final AtomicInteger ai = new AtomicInteger( 0 );
			Thread[] threads = SimpleMultiThreading.newThreads( numThreads );
			
			for ( int ithread = 0; ithread < threads.length; ++ithread )
				threads[ithread] = new Thread(new Runnable()
				{
					public void run()
					{
						final int myNumber = ai.getAndIncrement();
						
						// the temporary 1-d arrays for the fft
						final float[] tempIn = new float[ realSize ];				
						final float[] tempOut = new float[ complexSize * 2 ];
						
						final FftReal fft = new FftReal( realSize );
						
						final RandomAccess< C > randomAccessIn = input.randomAccess();
						final RandomAccess< R > randomAccessOut = output.randomAccess(); 
						
						/**
						 * Here we use a LocalizingZeroMinIntervalIterator to iterate through all dimensions except the one we are computing the inverse fft in 
						 */	
						final int[] fakeSize = new int[ numDimensions - 1 ];
						final int[] cursorInPosition = new int[ numDimensions ];
						final int[] cursorOutPosition = new int[ numDimensions ];
						
						// get all dimensions except the one we are doing the real-to-complex fft on
						int countDim = 0;						
						for ( int d = 0; d < numDimensions; ++d )
							if ( d != dim )
								fakeSize[ countDim++ ] = inputSize[ d ];
						
						final LocalizingZeroMinIntervalIterator cursorDim = new LocalizingZeroMinIntervalIterator( fakeSize );						
						
						// iterate over all dimensions except the one we are computing the fft in, which is dim=0 here
						while ( cursorDim.hasNext() )
						{
							cursorDim.fwd();							
	
							if ( cursorDim.getIntPosition( 0 ) % numThreads == myNumber )
							{							
								// get all dimensions except the one we are currently doing the fft on
								cursorDim.localize( fakeSize );
								
								// the location on the one-dimensional vector of which we compute the fft
								// is simply the first pixel
								cursorInPosition[ dim ] = (int)input.min( dim );
								cursorOutPosition[ dim ] = (int)output.min( dim );
								
								// get the position in all dimensions except the on we compute the fft in
								// which we get from the iterator that iterates n-1 dimensions
								countDim = 0;						
								for ( int d = 0; d < numDimensions; ++d )
								{
									if ( d != dim )
									{
										cursorInPosition[ d ] = fakeSize[ countDim ] + (int)input.min( d );
										cursorOutPosition[ d ] = fakeSize[ countDim ] + (int)output.min( d );
										++countDim;
									}
								}
	
								// set the cursor to the beginning of the correct line
								randomAccessIn.setPosition( cursorInPosition );
								
								// set the cursor in the fft output image to the right line
								randomAccessOut.setPosition( cursorOutPosition );
								
								// compute the FFT along the 1d vector and write it into the output
								computeComplexToReal1dFFT( fft, randomAccessIn, randomAccessOut, dim, tempIn, tempOut );
							}
						}				
					}
				});
			
			SimpleMultiThreading.startAndJoin(threads);			
		}
		else
		{
			// the temporary 1-d arrays for the fft
			final float[] tempIn = new float[ complexSize * 2 ];				
			final float[] tempOut = new float[ realSize ];
			
			final FftReal fft = new FftReal( realSize );
			
			final RandomAccess< C > randomAccessIn = input.randomAccess();
			final RandomAccess< R > randomAccessOut = output.randomAccess(); 
			
			// set the cursor to 0 in the first (and only) dimension
			randomAccessIn.setPosition( (int)input.min( 0 ), 0 );
			
			// set the cursor in the fft output image to 0 in the first (and only) dimension
			randomAccessOut.setPosition( (int)output.min( 0 ), 0 );
			
			// compute the FFT along the 1d vector and write it into the output
			computeComplexToReal1dFFT( fft, randomAccessIn, randomAccessOut, 0, tempIn, tempOut );		
		}
		
		return true;
	}

	final public static < R extends RealType< R >, C extends ComplexType< C > > boolean realToComplex( final RandomAccessibleInterval< R > input, final RandomAccessibleInterval< C > output, final int dim )
	{
		return realToComplex( input, output, dim, Runtime.getRuntime().availableProcessors() );
	}
	
	final public static < R extends RealType< R >, C extends ComplexType< C > > boolean realToComplex( final RandomAccessibleInterval< R > input, final RandomAccessibleInterval< C > output, final int dim, final int numThreads )
	{
		final int numDimensions = input.numDimensions();
		
		final int inputSize[] = new int[ numDimensions ];
		final int outputSize[] = new int[ numDimensions ];

		// the size of the input and output image
		for ( int d = 0; d < numDimensions; ++d )
		{
			inputSize[ d ] = (int)input.dimension( d );
			outputSize[ d ] = (int)output.dimension( d );
		}
		
		// test if those are valid sizes in case of real to complex
		if ( !verifyRealToComplexfftDimensions( inputSize[ dim ], outputSize[ dim ] ) )
		{
			System.out.println( "Unsupported combination of dimensionality of input and output" );
			return false;
		}
		
		// perform the real-to-complex fft in a dimension multithreaded if more than one dimension exisits
		final int realSize = inputSize[ dim ];
		final int complexSize = outputSize[ dim ];

		if ( numDimensions > 1 )
		{		
			final AtomicInteger ai = new AtomicInteger( 0 );
			Thread[] threads = SimpleMultiThreading.newThreads( numThreads );
			
			for ( int ithread = 0; ithread < threads.length; ++ithread )
				threads[ithread] = new Thread(new Runnable()
				{
					public void run()
					{
						final int myNumber = ai.getAndIncrement();
						
						// the temporary 1-d arrays for the fft
						final float[] tempIn = new float[ realSize ];				
						final float[] tempOut = new float[ complexSize * 2 ];
						
						final FftReal fft = new FftReal( realSize );
						
						final RandomAccess< R > randomAccessIn = input.randomAccess();
						final RandomAccess< C > randomAccessOut = output.randomAccess(); 
						
						/**
						 * Here we use a LocalizingZeroMinIntervalIterator to iterate through all dimensions except the one we are computing the inverse fft in 
						 */	
						final int[] fakeSize = new int[ numDimensions - 1 ];
						final int[] cursorInPosition = new int[ numDimensions ];
						final int[] cursorOutPosition = new int[ numDimensions ];
						
						// get all dimensions except the one we are doing the real-to-complex fft on
						int countDim = 0;						
						for ( int d = 0; d < numDimensions; ++d )
							if ( d != dim )
								fakeSize[ countDim++ ] = inputSize[ d ];
						
						final LocalizingZeroMinIntervalIterator cursorDim = new LocalizingZeroMinIntervalIterator( fakeSize );						
						
						// iterate over all dimensions except the one we are computing the fft in, which is dim=0 here
						while ( cursorDim.hasNext() )
						{
							cursorDim.fwd();							
	
							if ( cursorDim.getIntPosition( 0 ) % numThreads == myNumber )
							{							
								// get all dimensions except the one we are currently doing the fft on
								cursorDim.localize( fakeSize );
								
								// the location on the one-dimensional vector of which we compute the fft
								// is simply the first pixel
								cursorInPosition[ dim ] = (int)input.min( dim );
								cursorOutPosition[ dim ] = (int)output.min( dim );
								
								// get the position in all dimensions except the on we compute the fft in
								// which we get from the iterator that iterates n-1 dimensions
								countDim = 0;						
								for ( int d = 0; d < numDimensions; ++d )
								{
									if ( d != dim )
									{
										cursorInPosition[ d ] = fakeSize[ countDim ] + (int)input.min( d );
										cursorOutPosition[ d ] = fakeSize[ countDim ] + (int)output.min( d );
										++countDim;
									}
								}
	
								// set the cursor to the beginning of the correct line
								randomAccessIn.setPosition( cursorInPosition );
								
								// set the cursor in the fft output image to the right line
								randomAccessOut.setPosition( cursorOutPosition );
								
								// compute the FFT along the 1d vector and write it into the output
								computeRealToComplex1dFFT( fft, randomAccessIn, randomAccessOut, dim, tempIn, tempOut );
							}
						}				
					}
				});
			
			SimpleMultiThreading.startAndJoin(threads);
		}
		else
		{
			// if only one dimension exists, multithreading makes no sense here
			
			// the temporary 1-d arrays for the fft
			final float[] tempIn = new float[ realSize ];				
			final float[] tempOut = new float[ complexSize * 2 ];
			
			final FftReal fft = new FftReal( realSize );
			
			final RandomAccess< R > randomAccessIn = input.randomAccess();
			final RandomAccess< C > randomAccessOut = output.randomAccess(); 
			
			// set the cursor to 0 in the first (and only) dimension
			randomAccessIn.setPosition( (int)input.min( 0 ), 0 );
			
			// set the cursor in the fft output image to 0 in the first (and only) dimension
			randomAccessOut.setPosition( (int)output.min( 0 ), 0 );
			
			// compute the FFT along the 1d vector and write it into the output
			computeRealToComplex1dFFT( fft, randomAccessIn, randomAccessOut, 0, tempIn, tempOut );
		}
		return true;
	}

	final public static < C extends ComplexType< C > > boolean complexToComplex( final RandomAccessibleInterval< C > data, final int dim, final boolean forward )
	{
		return complexToComplex( data, dim, forward, Runtime.getRuntime().availableProcessors() );
	}
	
	final public static < C extends ComplexType< C > > boolean complexToComplex( final RandomAccessibleInterval< C > data, final int dim, final boolean forward, final int numThreads )
	{
		final int numDimensions = data.numDimensions();
		
		final int dataSize[] = new int[ numDimensions ];

		// the size of the input and output image
		for ( int d = 0; d < numDimensions; ++d )
			dataSize[ d ] = (int)data.dimension( d );
		
		// test if those are valid sizes in case of real to complex
		if ( !verifyComplexToComplexfftDimensions( dataSize[ dim ], dataSize[ dim ] ) )
			return false;
		
		// perform the real-to-complex fft in a dimension multithreaded if more than one dimension exisits
		final int size = dataSize[ dim ];

		if ( numDimensions > 1 )
		{		
			final AtomicInteger ai = new AtomicInteger( 0 );
			Thread[] threads = SimpleMultiThreading.newThreads( numThreads );
			
			for ( int ithread = 0; ithread < threads.length; ++ithread )
				threads[ithread] = new Thread(new Runnable()
				{
					public void run()
					{
						final int myNumber = ai.getAndIncrement();
						
						// the temporary 1-d arrays for the fft
						final float[] tempIn = new float[ size * 2 ];				
						final float[] tempOut = new float[ size * 2 ];
						
						final FftComplex fft = new FftComplex( size );
						
						final RandomAccess< C > randomAccess = data.randomAccess();
						
						/**
						 * Here we use a LocalizingZeroMinIntervalIterator to iterate through all dimensions except the one we are computing the inverse fft in 
						 */	
						final int[] fakeSize = new int[ numDimensions - 1 ];
						final int[] randomAccessPosition = new int[ numDimensions ];
						
						// get all dimensions except the one we are currently doing the fft on
						int countDim = 0;						
						for ( int d = 0; d < numDimensions; ++d )
							if ( d != dim )
								fakeSize[ countDim++ ] = dataSize[ d ];

						final LocalizingZeroMinIntervalIterator cursorDim = new LocalizingZeroMinIntervalIterator( fakeSize );						
						
						// iterate over all dimensions except the one we are computing the fft in
						while ( cursorDim.hasNext() )
						{
							cursorDim.fwd();							
	
							if ( cursorDim.getIntPosition( 0 ) % numThreads == myNumber )
							{							
								// get all dimensions except the one we are currently doing the fft on
								cursorDim.localize( fakeSize );
								
								// the location on the one-dimensional vector of which we compute the fft
								// is simply the first pixel
								randomAccessPosition[ dim ] = (int)data.min( dim );
								
								// get the position in all dimensions except the on we compute the fft in
								// which we get from the iterator that iterates n-1 dimensions
								countDim = 0;						
								for ( int d = 0; d < numDimensions; ++d )
									if ( d != dim )
										randomAccessPosition[ d ] = fakeSize[ countDim++ ] + (int)data.min( d );
	
								// set the cursor to the beginning of the correct line
								randomAccess.setPosition( randomAccessPosition );
								
								// compute the FFT along the 1d vector and write it into the output
								computeComplexToComplex1dFFT( fft, forward, randomAccess, dim, tempIn, tempOut );
							}
						}				
					}
				});
			
			SimpleMultiThreading.startAndJoin(threads);
		}
		else
		{
			// if only one dimension exists, multithreading makes no sense here
			
			// the temporary 1-d arrays for the fft
			final float[] tempIn = new float[ size * 2 ];				
			final float[] tempOut = new float[ size * 2 ];
			
			final FftComplex fft = new FftComplex( size );
			
			final RandomAccess< C > randomAccess = data.randomAccess();
			
			// set the cursor to 0 in the first (and only) dimension
			randomAccess.setPosition( (int)data.min( 0 ), 0 );
			
			// compute the FFT along the 1d vector and write it into the output
			computeComplexToComplex1dFFT( fft, forward, randomAccess, dim, tempIn, tempOut );
		}
		
		return true;
	}

	final private static < R extends RealType< R >, C extends ComplexType< C > > void computeRealToComplex1dFFT( final FftReal fft, final RandomAccess< R > randomAccessIn, final RandomAccess< C > randomAccessOut, final int dim, final float[] tempIn, final float[] tempOut )
	{
		final int realSize = tempIn.length;
		final int complexSize = tempOut.length / 2;
		final int realMax = realSize - 1;
		final int complexMax = complexSize - 1;
		
		// fill the input array with image data
		for ( int x = 0; x < realMax; ++x )
		{
			tempIn[ x ] = randomAccessIn.get().getRealFloat();									
			randomAccessIn.fwd( dim );
		}
		tempIn[ realMax ] = randomAccessIn.get().getRealFloat();

		// compute the fft in dimension dim ( real -> complex )
		fft.realToComplex( -1, tempIn, tempOut );

		// write back the fft data
		for ( int x = 0; x < complexMax; ++x )
		{
			randomAccessOut.get().setComplexNumber( tempOut[ x * 2 ] / realSize, tempOut[ x * 2 + 1 ] / realSize );									
			randomAccessOut.fwd( dim );
		}
		randomAccessOut.get().setComplexNumber( tempOut[ complexMax * 2 ] / realSize, tempOut[ complexMax * 2 + 1 ] / realSize );		
	}

	final private static < C extends ComplexType< C >, R extends RealType< R > > void computeComplexToReal1dFFT( final FftReal fft, final RandomAccess< C > randomAccessIn, final RandomAccess< R > randomAccessOut, final int dim, final float[] tempIn, final float[] tempOut )
	{
		final int complexSize = tempIn.length / 2;
		final int realSize = tempOut.length;
		final int complexMax = complexSize - 1;
		final int realMax = realSize - 1;
		
		// get the input data
		// fill the input array with complex image data
		for ( int i = 0; i < complexMax; ++i )
		{
			tempIn[ i * 2 ] = randomAccessIn.get().getRealFloat();
			tempIn[ i * 2 + 1 ] = randomAccessIn.get().getImaginaryFloat();
			randomAccessIn.fwd( 0 );
		}
		tempIn[ complexMax * 2 ] = randomAccessIn.get().getRealFloat();
		tempIn[ complexMax * 2 + 1 ] = randomAccessIn.get().getImaginaryFloat();
		
		// compute the fft in dimension 0 ( real -> complex )
		fft.complexToReal( 1, tempIn, tempOut );
		
		// set the cursor in the fft output image to 0 in the first (and only) dimension
		randomAccessOut.setPosition( 0, 0 );
		
		// write back the real data
		for ( int x = 0; x < realMax; ++x )
		{
			randomAccessOut.get().setReal( (tempOut[ x ] / realSize) );
			randomAccessOut.fwd( 0 );
		}
		randomAccessOut.get().setReal( (tempOut[ realMax ] / realSize) );
	}
	
	final private static < C extends ComplexType< C > > void computeComplexToComplex1dFFT( final FftComplex fft, final boolean forward, final RandomAccess< C > randomAccess, final int dim, final float[] tempIn, final float[] tempOut )
	{
		final int size = tempIn.length;
		final int max = size - 1;

		// get the input line
		for ( int i = 0; i < max; ++i )
		{
			tempIn[ i * 2 ] = randomAccess.get().getRealFloat();
			tempIn[ i * 2 + 1 ] = randomAccess.get().getImaginaryFloat();
			randomAccess.fwd( dim );
		}
		tempIn[ max * 2 ] = randomAccess.get().getRealFloat();
		tempIn[ max * 2 + 1 ] = randomAccess.get().getImaginaryFloat();
		
		// compute the fft in dimension dim (complex -> complex)
		if ( forward )
			fft.complexToComplex( -1, tempIn, tempOut);
		else
			fft.complexToComplex( 1, tempIn, tempOut);
		
		// move the randomAccess back
		randomAccess.move( -max, dim );
		
		// write back result
		for ( int i = 0; i < max; ++i )
		{
			randomAccess.get().setComplexNumber( tempOut[ i * 2 ] / size, tempOut[ i * 2 + 1 ] / size );
			randomAccess.fwd( dim );
		}
		randomAccess.get().setComplexNumber( tempOut[ max * 2 ] / size, tempOut[ max * 2 + 1 ] / size );
	}

	/**
	 * Computes the supported dimensionality of an input dataset (of complex numbers) for an inverse FFT of the entire dataset AS SMALL AS POSSIBLE
	 * 
	 * @param interval - the dimensions of the complex-valued input
	 * @param paddedDimensions - the required dimensions of the complex-valued input (computed)
	 * @param realSize - the dimensions of the real-valued output after the inverse fast fourier transform (computed), i.e. which dimensions are required for the output
	 */
	final static public void dimensionsComplexToRealSmall( final Interval interval, final int[] paddedDimensions, final int[] realSize )
	{
		// compute what the dimensionality corresponds to in real-valued pixels
		final int d0 = ( (int)interval.dimension( 0 ) - 1 ) * 2;
		
		// compute which dimensionality we could get from that
		paddedDimensions[ 0 ] = FftReal.nfftSmall( d0 ) / 2 + 1;
		
		// and which final dimensionality this will give in real space
		realSize[ 0 ] = ( paddedDimensions[ 0 ] - 1 ) * 2;
		
		for ( int d = 1; d < interval.numDimensions(); ++d )
			realSize[ d ] = paddedDimensions[ d ] = FftComplex.nfftSmall( (int)interval.dimension( d ) );				
	}

	/**
	 * Computes the supported dimensionality of an input dataset (of complex numbers) for an inverse FFT of the entire dataset AS FAST AS POSSIBLE
	 * 
	 * @param interval - the dimensions of the complex-valued input
	 * @param paddedDimensions - the required dimensions of the complex-valued input (computed)
	 * @param realSize - the dimensions of the real-valued output after the inverse fast fourier transform (computed), i.e. which dimensions are required for the output
	 */
	final static public void dimensionsComplexToRealFast( final Interval interval, final int[] paddedDimensions, final int[] realSize )
	{
		// compute what the dimensionality corresponds to in real-valued pixels
		final int d0 = ( (int)interval.dimension( 0 ) - 1 ) * 2;
		
		// compute which dimensionality we could get from that
		paddedDimensions[ 0 ] = FftReal.nfftFast( d0 ) / 2 + 1;
		
		// and which final dimensionality this will give in real space
		realSize[ 0 ] = ( paddedDimensions[ 0 ] - 1 ) * 2;
		
		for ( int d = 1; d < interval.numDimensions(); ++d )
			realSize[ d ] = paddedDimensions[ d ] = FftComplex.nfftFast( (int)interval.dimension( d ) );		
	}

	/**
	 * Computes the supported dimensionality of an input dataset (of complex numbers) for an inverse FFT of the entire dataset AS SMALL AS POSSIBLE
	 * 
	 * @param intervalDimensions - the dimensions of the complex-valued input
	 * @param paddedDimensions - the required dimensions of the complex-valued input (computed)
	 * @param realSize - the dimensions of the real-valued output after the inverse fast fourier transform (computed), i.e. which dimensions are required for the output
	 */
	final static public void dimensionsComplexToRealSmall( final int[] intervalDimensions, final int[] paddedDimensions, final int[] realSize )
	{
		// compute what the dimensionality corresponds to in real-valued pixels
		final int d0 = ( intervalDimensions[ 0 ] - 1 ) * 2;
		
		// compute which dimensionality we could get from that
		paddedDimensions[ 0 ] = FftReal.nfftSmall( d0 ) / 2 + 1;
		
		// and which final dimensionality this will give in real space
		realSize[ 0 ] = ( paddedDimensions[ 0 ] - 1 ) * 2;
		
		for ( int d = 1; d < intervalDimensions.length; ++d )
			realSize[ d ] = paddedDimensions[ d ] = FftComplex.nfftSmall( intervalDimensions[ d ] );		
	}

	/**
	 * Computes the supported dimensionality of an input dataset (of complex numbers) for an inverse FFT of the entire dataset AS FAST AS POSSIBLE
	 * 
	 * @param intervalDimensions - the dimensions of the complex-valued input
	 * @param paddedDimensions - the required dimensions of the complex-valued input (computed)
	 * @param realSize - the dimensions of the real-valued output after the inverse fast fourier transform (computed), i.e. which dimensions are required for the output
	 */
	final static public void dimensionsComplexToRealFast( final int[] intervalDimensions, final int[] paddedDimensions, final int[] realSize )
	{
		// compute what the dimensionality corresponds to in real-valued pixels
		final int d0 = ( intervalDimensions[ 0 ] - 1 ) * 2;
		
		// compute which dimensionality we could get from that
		paddedDimensions[ 0 ] = FftReal.nfftFast( d0 ) / 2 + 1;
		
		// and which final dimensionality this will give in real space
		realSize[ 0 ] = ( paddedDimensions[ 0 ] - 1 ) * 2;
		
		for ( int d = 1; d < intervalDimensions.length; ++d )
			realSize[ d ] = paddedDimensions[ d ] = FftComplex.nfftFast( intervalDimensions[ d ] );
	}

	/**
	 * Computes the supported dimensionality of an input dataset (of real numbers) for a forward FFT of the entire dataset AS FAST AS POSSIBLE
	 * 
	 * @param interval - the dimensions of the real-valued input
	 * @param paddedDimensions - the required dimensions of the real-valued input (computed)
	 * @param fftDimensions - the dimensions of the complex-valued fft after the fast fourier transform (computed), i.e. which dimensions are required for the output
	 */
	final static public void dimensionsRealToComplexFast( final Interval interval, final int[] paddedDimensions, final int[] fftDimensions )
	{
		paddedDimensions[ 0 ] = FftReal.nfftFast( (int)interval.dimension( 0 ) );
		fftDimensions[ 0 ] = ( paddedDimensions[ 0 ]  / 2 + 1 );
		
		for ( int d = 1; d < interval.numDimensions(); ++d )
			fftDimensions[ d ] = paddedDimensions[ d ] = FftComplex.nfftFast( (int)interval.dimension( d ) );
	}

	/**
	 * Computes the supported dimensionality of an input dataset (of real numbers) for a forward FFT of the entire dataset AS SMALL AS POSSIBLE
	 * 
	 * @param interval - the dimensions of the real-valued input
	 * @param paddedDimensions - the required dimensions of the real-valued input (computed)
	 * @param fftDimensions - the dimensions of the complex-valued fft after the fast fourier transform (computed), i.e. which dimensions are required for the output
	 */
	final static public void dimensionsRealToComplexSmall( final Interval interval, final int[] paddedDimensions, final int[] fftDimensions )
	{
		paddedDimensions[ 0 ] = FftReal.nfftSmall( (int)interval.dimension( 0 ) );
		fftDimensions[ 0 ] = ( paddedDimensions[ 0 ]  / 2 + 1 );
		
		for ( int d = 1; d < interval.numDimensions(); ++d )
			fftDimensions[ d ] = paddedDimensions[ d ] = FftComplex.nfftSmall( (int)interval.dimension( d ) );
	}

	/**
	 * Computes the supported dimensionality of an input dataset (of real numbers) for a forward FFT of the entire dataset AS FAST AS POSSIBLE
	 * 
	 * @param interval - the dimensions of the real-valued input
	 * @param paddedDimensions - the required dimensions of the real-valued input (computed)
	 * @param fftDimensions - the dimensions of the complex-valued fft after the fast fourier transform (computed), i.e. which dimensions are required for the output
	 */
	final static public void dimensionsRealToComplexFast( final int[] intervalDimensions, final int[] paddedDimensions, final int[] fftDimensions )
	{
		paddedDimensions[ 0 ] = FftReal.nfftFast( intervalDimensions[ 0 ] );
		fftDimensions[ 0 ] = ( paddedDimensions[ 0 ]  / 2 + 1 );
		
		for ( int d = 1; d < intervalDimensions.length; ++d )
			fftDimensions[ d ] = paddedDimensions[ d ] = FftComplex.nfftFast( intervalDimensions[ d ] );
	}

	/**
	 * Computes the supported dimensionality of an input dataset (of real numbers) for a forward FFT of the entire dataset AS SMALL AS POSSIBLE
	 * 
	 * @param interval - the dimensions of the real-valued input
	 * @param paddedDimensions - the required dimensions of the real-valued input (computed)
	 * @param fftDimensions - the dimensions of the complex-valued fft after the fast fourier transform (computed), i.e. which dimensions are required for the output
	 */
	final static public void dimensionsRealToComplexSmall( final int[] intervalDimensions, final int[] paddedDimensions, final int[] fftDimensions )
	{
		paddedDimensions[ 0 ] = FftReal.nfftSmall( intervalDimensions[ 0 ] );
		fftDimensions[ 0 ] = ( paddedDimensions[ 0 ]  / 2 + 1 );
		
		for ( int d = 1; d < intervalDimensions.length; ++d )
			fftDimensions[ d ] = paddedDimensions[ d ] = FftComplex.nfftSmall( intervalDimensions[ d ] );
	}

	/**
	 * Computes the supported dimensionality of an input dataset (of complex numbers) for a forward/inverse FFT of the entire dataset AS FAST AS POSSIBLE
	 * 
	 * @param intervalDimensions - the dimensions of the input
	 * @param paddedDimensions - the required dimensions of the input/output (computed)
	 */
	final static public void dimensionsComplexToComplexFast( final Interval interval, final int[] paddedDimensions )
	{
		for ( int d = 0; d < interval.numDimensions(); ++d )
			paddedDimensions[ d ] = FftComplex.nfftFast( (int)interval.dimension( d ) );
	}

	/**
	 * Computes the supported dimensionality of an input dataset (of complex numbers) for a forward/inverse FFT of the entire dataset AS SMALL AS POSSIBLE
	 * 
	 * @param intervalDimensions - the dimensions of the input
	 * @param paddedDimensions - the required dimensions of the input/output (computed)
	 */
	final static public void dimensionsComplexToComplexSmall( final Interval interval, final int[] paddedDimensions )
	{
		for ( int d = 0; d < interval.numDimensions(); ++d )
			paddedDimensions[ d ] = FftComplex.nfftSmall( (int)interval.dimension( d ) );
	}

	/**
	 * Computes the supported dimensionality of an input dataset (of complex numbers) for a forward/inverse FFT of the entire dataset AS FAST AS POSSIBLE
	 * 
	 * @param intervalDimensions - the dimensions of the input
	 * @param paddedDimensions - the required dimensions of the input/output (computed)
	 */
	final static public void dimensionsComplexToComplexFast( final int[] intervalDimensions, final int[] paddedDimensions )
	{
		for ( int d = 0; d < intervalDimensions.length; ++d )
			paddedDimensions[ d ] = FftComplex.nfftFast( intervalDimensions[ d ] );
	}

	/**
	 * Computes the supported dimensionality of an input dataset (of complex numbers) for a forward/inverse FFT of the entire dataset AS SMALL AS POSSIBLE
	 * 
	 * @param intervalDimensions - the dimensions of the input
	 * @param paddedDimensions - the required dimensions of the input/output (computed)
	 */
	final static public void dimensionsComplexToComplexSmall( final int[] intervalDimensions, final int[] paddedDimensions )
	{
		for ( int d = 0; d < intervalDimensions.length; ++d )
			paddedDimensions[ d ] = FftComplex.nfftSmall( intervalDimensions[ d ] );
	}

	final protected static boolean verifyRealToComplexfftDimensions( final int inputSize, final int outputSize )
	{
		if ( FftReal.nfftFast( inputSize ) / 2 + 1 == outputSize || FftReal.nfftSmall( inputSize ) / 2 + 1 == outputSize )
			return true;
		else
			return false;
	}

	final protected static boolean verifyComplexToComplexfftDimensions( final int inputSize, final int outputSize )
	{
		if ( FftComplex.nfftFast( inputSize ) == outputSize || FftComplex.nfftSmall( inputSize ) == outputSize )
			return true;
		else
			return false;
	}
}
