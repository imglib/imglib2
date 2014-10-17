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

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import net.imglib2.Dimensions;
import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.iterator.LocalizingZeroMinIntervalIterator;
import net.imglib2.type.numeric.ComplexType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;
import edu.mines.jtk.dsp.FftComplex;
import edu.mines.jtk.dsp.FftReal;

/**
 * Compute a FFT transform, either real-to-complex or complex-to-complex, or
 * complex-to-real for individual dimensions. Unfortunately only supports a
 * maximal size of INT in each dimension as the one-dimensional FFT is based on
 * arrays.
 * 
 * @author Stephan Preibisch (stephan.preibisch@gmx.de)
 */
public class FFTMethods
{
	/**
	 * Computes a complex-to-real inverse FFT transform of an n-dimensional
	 * dataset in a certain dimension (typically dim = 0). By default as many
	 * threads as processors are available are used. By default the real values
	 * are scaled (divided by the amount of pixels in the input dataset), this
	 * way after performing a forward and reverse FFT, the values will be
	 * exactly the same
	 * 
	 * @param input
	 *            - the complex-valued input dataset
	 * @param output
	 *            - the real-valued output dataset
	 * @param dim
	 *            - the dimension to compute the inverse FFT in
	 * @return - true if successful, false if the dimensions of input and output
	 *         are not compatible, i.e. not supported by the edu_mines_jtk 1d
	 *         fft
	 */
	final public static < C extends ComplexType< C >, R extends RealType< R >> boolean complexToReal(
			final RandomAccessibleInterval< C > input,
			final RandomAccessibleInterval< R > output, final int dim )
	{
		return complexToReal( input, output, output, dim, true );
	}

	/**
	 * Computes a complex-to-real inverse FFT transform of an n-dimensional
	 * dataset in a certain dimension (typically dim = 0). By default as many
	 * threads as processors are available are used. By default the real values
	 * are scaled (divided by the amount of pixels in the input dataset), this
	 * way after performing a forward and reverse FFT, the values will be
	 * exactly the same
	 * 
	 * @param input
	 *            - the complex-valued input dataset
	 * @param output
	 *            - the real-valued output dataset
	 * @param interval
	 *            - if just a subset of the real-values output is required it
	 *            can be defined here (otherwise it can just be equal to output)
	 * @param dim
	 *            - the dimension to compute the inverse FFT in
	 * @return - true if successful, false if the dimensions of input and output
	 *         are not compatible, i.e. not supported by the edu_mines_jtk 1d
	 *         fft
	 */
	final public static < C extends ComplexType< C >, R extends RealType< R >> boolean complexToReal(
			final RandomAccessibleInterval< C > input,
			final RandomAccessibleInterval< R > output, final Interval interval,
			final int dim )
	{
		return complexToReal( input, output, interval, dim, true );
	}

	/**
	 * Computes a complex-to-real inverse FFT transform of an n-dimensional
	 * dataset in a certain dimension (typically dim = 0). By default as many
	 * threads as processors are available are used.
	 * 
	 * @param input
	 *            - the complex-valued input dataset
	 * @param output
	 *            - the real-valued output dataset
	 * @param interval
	 *            - if just a subset of the real-values output is required it
	 *            can be defined here (otherwise it can just be equal to output)
	 * @param dim
	 *            - the dimension to compute the inverse FFT in
	 * @param scale
	 *            - define if each pixel is divided by the sum of all pixels in
	 *            the image
	 * @return - true if successful, false if the dimensions of input and output
	 *         are not compatible, i.e. not supported by the edu_mines_jtk 1d
	 *         fft
	 */
	final public static < C extends ComplexType< C >, R extends RealType< R >> boolean complexToReal(
			final RandomAccessibleInterval< C > input,
			final RandomAccessibleInterval< R > output, final Interval interval,
			final int dim, final boolean scale )
	{
		return complexToReal( input, output, interval, dim, scale, Runtime
				.getRuntime().availableProcessors() );
	}

	/**
	 * Computes a complex-to-real inverse FFT transform of an n-dimensional
	 * dataset in a certain dimension (typically dim = 0).
	 * 
	 * @param input
	 *            - the complex-valued input dataset
	 * @param output
	 *            - the real-valued output dataset
	 * @param dim
	 *            - the dimension to compute the inverse FFT in
	 * @param scale
	 *            - define if each pixel is divided by the sum of all pixels in
	 *            the image
	 * @param nThreads
	 *            - number of threads to utilize
	 * @return - true if successful, false if the dimensions of input and output
	 *         are not compatible, i.e. not supported by the edu_mines_jtk 1d
	 *         fft
	 */
	final public static < C extends ComplexType< C >, R extends RealType< R >> boolean complexToReal(
			final RandomAccessibleInterval< C > input,
			final RandomAccessibleInterval< R > output, final int dim,
			final boolean scale, int nThreads )
	{
		return complexToReal( input, output, output, dim, scale, nThreads );
	}

	/**
	 * Computes a complex-to-real inverse FFT transform of an n-dimensional
	 * dataset in a certain dimension (typically dim = 0). By default as many
	 * threads as processors are available are used.
	 * 
	 * @param input
	 *            - the complex-valued input dataset
	 * @param output
	 *            - the real-valued output dataset
	 * @param interval
	 *            - if just a subset of the real-values output is required it
	 *            can be defined here (otherwise it can just be equal to output)
	 * @param dim
	 *            - the dimension to compute the inverse FFT in
	 * @param scale
	 *            - define if each pixel is divided by the sum of all pixels in
	 *            the image
	 * @param nThreads
	 *            - number of threads to utilize
	 * @return - true if successful, false if the dimensions of input and output
	 *         are not compatible, i.e. not supported by the edu_mines_jtk 1d
	 *         fft
	 */
	final public static < C extends ComplexType< C >, R extends RealType< R >> boolean complexToReal(
			final RandomAccessibleInterval< C > input,
			final RandomAccessibleInterval< R > output, final Interval interval,
			final int dim, final boolean scale, int nThreads )
	{
		ExecutorService service = Executors.newFixedThreadPool( nThreads );
		boolean ret = complexToReal( input, output, interval, dim, scale, service );
		service.shutdown();

		return ret;
	}

	/**
	 * Computes a complex-to-real inverse FFT transform of an n-dimensional
	 * dataset in a certain dimension (typically dim = 0). By default as many
	 * threads as processors are available are used.
	 * 
	 * @param input
	 *            - the complex-valued input dataset
	 * @param output
	 *            - the real-valued output dataset
	 * @param dim
	 *            - the dimension to compute the inverse FFT in
	 * @param scale
	 *            - define if each pixel is divided by the sum of all pixels in
	 *            the image
	 * @param service
	 *            - service providing threads for multi-threading (if numDims >
	 *            1)
	 * @return - true if successful, false if the dimensions of input and output
	 *         are not compatible, i.e. not supported by the edu_mines_jtk 1d
	 *         fft
	 */
	final public static < C extends ComplexType< C >, R extends RealType< R >> boolean complexToReal(
			final RandomAccessibleInterval< C > input,
			final RandomAccessibleInterval< R > output, final int dim,
			final boolean scale, final ExecutorService service )
	{
		return complexToReal( input, output, output, dim, scale, service );
	}

	/**
	 * Computes a complex-to-real inverse FFT transform of an n-dimensional
	 * dataset in a certain dimension (typically dim = 0).
	 * 
	 * @param input
	 *            - the complex-valued input dataset
	 * @param output
	 *            - the real-valued output dataset
	 * @param interval
	 *            - if just a subset of the real-values output is required it
	 *            can be defined here (otherwise it can just be equal to output)
	 * @param dim
	 *            - the dimension to compute the inverse FFT in
	 * @param scale
	 *            - define if each pixel is divided by the sum of all pixels in
	 *            the image
	 * @param service
	 *            - service providing threads for multi-threading (if numDims >
	 *            1)
	 * @return - true if successful, false if the dimensions of input and output
	 *         are not compatible, i.e. not supported by the edu_mines_jtk 1d
	 *         fft
	 */
	final public static < C extends ComplexType< C >, R extends RealType< R >> boolean complexToReal(
			final RandomAccessibleInterval< C > input,
			final RandomAccessibleInterval< R > output, final Interval interval,
			final int dim, final boolean scale, final ExecutorService service )
	{
		final int numDimensions = input.numDimensions();

		final int inputSize[] = new int[ numDimensions ];

		// FIXME is there a better way to determine the number of threads?
		final int numThreads = Runtime.getRuntime().availableProcessors();
		final int numTasks = numThreads > 1 ? numThreads * 4 : 1;

		// the size of the input image
		for ( int d = 0; d < numDimensions; ++d )
			inputSize[ d ] = ( int ) input.dimension( d );

		final int complexSize = inputSize[ dim ];
		final int realSize = ( complexSize - 1 ) * 2;

		// test if those are valid sizes in case of real to complex
		if ( !verifyRealToComplexfftDimensions( realSize, complexSize ) )
		{
			System.out
					.println( "Unsupported combination of dimensionality of input and output" );
			return false;
		}

		// perform the complex-to-real fft in a dimension multithreaded if more
		// than one dimension exisits
		if ( numDimensions > 1 )
		{
			final ArrayList< Future< Void >> futures = new ArrayList< Future< Void >>();
			final AtomicInteger ai = new AtomicInteger( 0 );

			for ( int ithread = 0; ithread < numTasks; ++ithread )
			{
				final Callable< Void > callable = new Callable< Void >()
				{

					@Override
					public Void call() throws Exception
					{
						final int myNumber = ai.getAndIncrement();

						// the temporary 1-d arrays for the inverse fft
						final float[] tempIn = new float[ complexSize * 2 ];
						final float[] tempOut = new float[ realSize ];

						final FftReal fft = new FftReal( realSize );

						final RandomAccess< C > randomAccessIn = input
								.randomAccess();
						final RandomAccess< R > randomAccessOut = output
								.randomAccess();

						/**
						 * Here we use a LocalizingZeroMinIntervalIterator to
						 * iterate through all dimensions except the one we are
						 * computing the inverse fft in
						 */
						final int[] fakeSize = new int[ numDimensions - 1 ];
						final int[] cursorInPosition = new int[ numDimensions ];
						final int[] cursorOutPosition = new int[ numDimensions ];

						// get all dimensions except the one we are doing the
						// real-to-complex fft on
						int countDim = 0;
						for ( int d = 0; d < numDimensions; ++d )
							if ( d != dim )
								fakeSize[ countDim++ ] = inputSize[ d ];

						final LocalizingZeroMinIntervalIterator cursorDim = new LocalizingZeroMinIntervalIterator(
								fakeSize );

						// iterate over all dimensions except the one we are
						// computing the fft in, which is dim=0 here
						A: while ( cursorDim.hasNext() )
						{
							cursorDim.fwd();

							if ( cursorDim.getIntPosition( 0 ) % numThreads == myNumber )
							{
								// get all dimensions except the one we are
								// currently doing the fft on
								cursorDim.localize( fakeSize );

								// the location on the one-dimensional vector of
								// which we compute the fft
								// is simply the first pixel
								cursorInPosition[ dim ] = ( int ) input.min( dim );
								cursorOutPosition[ dim ] = ( int ) output.min( dim );

								// get the position in all dimensions except the
								// on we compute the fft in
								// which we get from the iterator that iterates
								// n-1 dimensions
								countDim = 0;
								for ( int d = 0; d < numDimensions; ++d )
								{
									if ( d != dim )
									{
										// check that we are not out of the
										// cropped image's bounds defined by
										// interval,
										// then we do not have to compute the
										// inverse fft here
										if ( fakeSize[ countDim ] < interval
												.min( d )
												|| fakeSize[ countDim ] > interval
														.max( d ) )
											continue A;

										cursorInPosition[ d ] = fakeSize[ countDim ]
												+ ( int ) input.min( d );
										cursorOutPosition[ d ] = fakeSize[ countDim ]
												+ ( int ) output.min( d )
												- ( int ) interval.min( d );
										++countDim;
									}
								}

								// set the cursor to the beginning of the
								// correct line
								randomAccessIn.setPosition( cursorInPosition );

								// set the cursor in the fft output image to the
								// right line
								randomAccessOut.setPosition( cursorOutPosition );

								// compute the FFT along the 1d vector and write
								// it into the output
								computeComplexToReal1dFFT( fft, randomAccessIn,
										randomAccessOut, interval, dim, tempIn,
										tempOut, scale );
							}
						}

						return null;
					}

				};
				futures.add( service.submit( callable ) );
			}

			for ( final Future< Void > future : futures )
			{
				try
				{
					future.get();
				}
				catch ( final InterruptedException e )
				{
					e.printStackTrace();
				}
				catch ( final ExecutionException e )
				{
					e.printStackTrace();
				}
			}

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
			randomAccessIn.setPosition( ( int ) input.min( 0 ), 0 );

			// set the cursor in the fft output image to 0 in the first (and
			// only) dimension
			randomAccessOut.setPosition( ( int ) output.min( 0 ), 0 );

			// compute the FFT along the 1d vector and write it into the output
			computeComplexToReal1dFFT( fft, randomAccessIn, randomAccessOut,
					interval, 0, tempIn, tempOut, scale );
		}

		return true;
	}

	/**
	 * Computes a real-to-complex forward FFT transform of an n-dimensional
	 * dataset in a certain dimension (typically dim = 0). By default as many
	 * threads as processors are available are used. By default the complex
	 * values are not scaled (not divided by the amount of pixels in the input
	 * dataset)
	 * 
	 * @param input
	 *            - the real-valued input dataset
	 * @param output
	 *            - the complex-valued output dataset
	 * @param dim
	 *            - the dimension to compute the FFT in
	 * @return - true if successful, false if the dimensions of input and output
	 *         are not compatible, i.e. not supported by the edu_mines_jtk 1d
	 *         fft
	 */
	final public static < R extends RealType< R >, C extends ComplexType< C >> boolean realToComplex(
			final RandomAccessibleInterval< R > input,
			final RandomAccessibleInterval< C > output, final int dim )
	{
		return realToComplex( input, output, output, dim, false );
	}

	/**
	 * Computes a real-to-complex forward FFT transform of an n-dimensional
	 * dataset in a certain dimension (typically dim = 0). By default as many
	 * threads as processors are available are used. By default the complex
	 * values are not scaled (not divided by the amount of pixels in the input
	 * dataset)
	 * 
	 * @param input
	 *            - the real-valued input dataset
	 * @param output
	 *            - the complex-valued output dataset
	 * @param interval
	 *            - if just a subset of the complex-valued output is required it
	 *            can be defined here (otherwise it can just be equal to output)
	 * @param dim
	 *            - the dimension to compute the FFT in
	 * @return - true if successful, false if the dimensions of input and output
	 *         are not compatible, i.e. not supported by the edu_mines_jtk 1d
	 *         fft
	 */
	final public static < R extends RealType< R >, C extends ComplexType< C >> boolean realToComplex(
			final RandomAccessibleInterval< R > input,
			final RandomAccessibleInterval< C > output, final Interval interval,
			final int dim )
	{
		return realToComplex( input, output, dim, false );
	}

	/**
	 * Computes a real-to-complex forward FFT transform of an n-dimensional
	 * dataset in a certain dimension (typically dim = 0). By default as many
	 * threads as processors are available are used.
	 * 
	 * @param input
	 *            - the real-valued input dataset
	 * @param output
	 *            - the complex-valued output dataset
	 * @param dim
	 *            - the dimension to compute the FFT in
	 * @param scale
	 *            - define if each pixel is divided by the sum of all pixels in
	 *            the image
	 * @return - true if successful, false if the dimensions of input and output
	 *         are not compatible, i.e. not supported by the edu_mines_jtk 1d
	 *         fft
	 */
	final public static < R extends RealType< R >, C extends ComplexType< C >> boolean realToComplex(
			final RandomAccessibleInterval< R > input,
			final RandomAccessibleInterval< C > output, final int dim,
			final boolean scale )
	{
		return realToComplex( input, output, output, dim, scale );
	}

	/**
	 * Computes a real-to-complex forward FFT transform of an n-dimensional
	 * dataset in a certain dimension (typically dim = 0). By default as many
	 * threads as processors are available are used.
	 * 
	 * @param input
	 *            - the real-valued input dataset
	 * @param output
	 *            - the complex-valued output dataset
	 * @param interval
	 *            - if just a subset of the complex-valued output is required it
	 *            can be defined here (otherwise it can just be equal to output)
	 * @param dim
	 *            - the dimension to compute the FFT in
	 * @param scale
	 *            - define if each pixel is divided by the sum of all pixels in
	 *            the image
	 * @return - true if successful, false if the dimensions of input and output
	 *         are not compatible, i.e. not supported by the edu_mines_jtk 1d
	 *         fft
	 */
	final public static < R extends RealType< R >, C extends ComplexType< C >> boolean realToComplex(
			final RandomAccessibleInterval< R > input,
			final RandomAccessibleInterval< C > output, final Interval interval,
			final int dim, final boolean scale )
	{
		return realToComplex( input, output, interval, dim, scale, Runtime.getRuntime().availableProcessors() );
	}

	/**
	 * Computes a real-to-complex forward FFT transform of an n-dimensional
	 * dataset in a certain dimension (typically dim = 0). By default as many
	 * threads as processors are available are used.
	 * 
	 * @param input
	 *            - the real-valued input dataset
	 * @param output
	 *            - the complex-valued output dataset
	 * @param dim
	 *            - the dimension to compute the FFT in
	 * @param scale
	 *            - define if each pixel is divided by the sum of all pixels in
	 *            the image
	 * @param nThreads
	 *            - number of threads to utilize
	 * @return - true if successful, false if the dimensions of input and output
	 *         are not compatible, i.e. not supported by the edu_mines_jtk 1d
	 *         fft
	 */
	final public static < R extends RealType< R >, C extends ComplexType< C >> boolean realToComplex(
			final RandomAccessibleInterval< R > input,
			final RandomAccessibleInterval< C > output,
			final int dim, final boolean scale, int nThreads )
	{
		return realToComplex( input, output, output, dim, scale, nThreads );
	}

	/**
	 * Computes a real-to-complex forward FFT transform of an n-dimensional
	 * dataset in a certain dimension (typically dim = 0). By default as many
	 * threads as processors are available are used.
	 * 
	 * @param input
	 *            - the real-valued input dataset
	 * @param output
	 *            - the complex-valued output dataset
	 * @param interval
	 *            - if just a subset of the complex-valued output is required it
	 *            can be defined here (otherwise it can just be equal to output)
	 * @param dim
	 *            - the dimension to compute the FFT in
	 * @param scale
	 *            - define if each pixel is divided by the sum of all pixels in
	 *            the image
	 * @param nThreads
	 *            - number of threads to utilize
	 * @return - true if successful, false if the dimensions of input and output
	 *         are not compatible, i.e. not supported by the edu_mines_jtk 1d
	 *         fft
	 */
	final public static < R extends RealType< R >, C extends ComplexType< C >> boolean realToComplex(
			final RandomAccessibleInterval< R > input,
			final RandomAccessibleInterval< C > output, final Interval interval,
			final int dim, final boolean scale, int nThreads )
	{
		ExecutorService service = Executors.newFixedThreadPool( nThreads );
		boolean ret = realToComplex( input, output, interval, dim, scale, service );
		service.shutdown();

		return ret;
	}

	/**
	 * Computes a real-to-complex forward FFT transform of an n-dimensional
	 * dataset in a certain dimension (typically dim = 0).
	 * 
	 * @param input
	 *            - the real-valued input dataset
	 * @param output
	 *            - the complex-valued output dataset
	 * @param dim
	 *            - the dimension to compute the FFT in
	 * @param scale
	 *            - define if each pixel is divided by the sum of all pixels in
	 *            the image
	 * @param service
	 *            - service providing threads for multi-threading
	 * @return - true if successful, false if the dimensions of input and output
	 *         are not compatible, i.e. not supported by the edu_mines_jtk 1d
	 *         fft
	 */
	final public static < R extends RealType< R >, C extends ComplexType< C >> boolean realToComplex(
			final RandomAccessibleInterval< R > input,
			final RandomAccessibleInterval< C > output, final int dim,
			final boolean scale, final ExecutorService service )
	{
		return realToComplex( input, output, output, dim, scale, service );
	}

	/**
	 * Computes a real-to-complex forward FFT transform of an n-dimensional
	 * dataset in a certain dimension (typically dim = 0).
	 * 
	 * @param input
	 *            - the real-valued input dataset
	 * @param output
	 *            - the complex-valued output dataset
	 * @param interval
	 *            - if just a subset of the complex-valued output is required it
	 *            can be defined here (otherwise it can just be equal to output)
	 * @param dim
	 *            - the dimension to compute the FFT in
	 * @param scale
	 *            - define if each pixel is divided by the sum of all pixels in
	 *            the image
	 * @param service
	 *            - service providing threads for multi-threading (if numDims >
	 *            1)
	 * @return - true if successful, false if the dimensions of input and output
	 *         are not compatible, i.e. not supported by the edu_mines_jtk 1d
	 *         fft
	 */
	final public static < R extends RealType< R >, C extends ComplexType< C >> boolean realToComplex(
			final RandomAccessibleInterval< R > input,
			final RandomAccessibleInterval< C > output, final Interval interval,
			final int dim, final boolean scale, final ExecutorService service )
	{
		final int numDimensions = input.numDimensions();

		final int inputSize[] = new int[ numDimensions ];

		// FIXME is there a better way to determine the number of threads?
		final int numThreads = Runtime.getRuntime().availableProcessors();

		// the size of the input and output image
		for ( int d = 0; d < numDimensions; ++d )
			inputSize[ d ] = ( int ) input.dimension( d );

		final int realSize = inputSize[ dim ];
		final int complexSize = realSize / 2 + 1;

		// test if those are valid sizes in case of real to complex
		if ( !verifyRealToComplexfftDimensions( realSize, complexSize ) )
		{
			System.out.println( "Input dimensions not supported by FFT." );
			return false;
		}

		// perform the real-to-complex fft in a dimension multithreaded if more
		// than one dimension exisits
		if ( numDimensions > 1 )
		{
			final ArrayList< Future< Void >> futures = new ArrayList< Future< Void >>();
			final AtomicInteger ai = new AtomicInteger( 0 );

			for ( int ithread = 0; ithread < numThreads; ++ithread )
			{
				final Callable< Void > call = new Callable< Void >()
				{
					@Override
					public Void call() throws Exception
					{
						final int myNumber = ai.getAndIncrement();

						// the temporary 1-d arrays for the fft
						final float[] tempIn = new float[ realSize ];
						final float[] tempOut = new float[ complexSize * 2 ];

						final FftReal fft = new FftReal( realSize );

						final RandomAccess< R > randomAccessIn = input
								.randomAccess();
						final RandomAccess< C > randomAccessOut = output
								.randomAccess();

						/**
						 * Here we use a LocalizingZeroMinIntervalIterator to
						 * iterate through all dimensions except the one we are
						 * computing the inverse fft in
						 */
						final int[] fakeSize = new int[ numDimensions - 1 ];
						final int[] cursorInPosition = new int[ numDimensions ];
						final int[] cursorOutPosition = new int[ numDimensions ];

						// get all dimensions except the one we are doing the
						// real-to-complex fft on
						int countDim = 0;
						for ( int d = 0; d < numDimensions; ++d )
							if ( d != dim )
								fakeSize[ countDim++ ] = inputSize[ d ];

						final LocalizingZeroMinIntervalIterator cursorDim = new LocalizingZeroMinIntervalIterator(
								fakeSize );

						// iterate over all dimensions except the one we are
						// computing the fft in, which is dim=0 here
						A: while ( cursorDim.hasNext() )
						{
							cursorDim.fwd();

							if ( cursorDim.getIntPosition( 0 ) % numThreads == myNumber )
							{
								// get all dimensions except the one we are
								// currently doing the fft on
								cursorDim.localize( fakeSize );

								// the location on the one-dimensional vector of
								// which we compute the fft
								// is simply the first pixel
								cursorInPosition[ dim ] = ( int ) input.min( dim );
								cursorOutPosition[ dim ] = ( int ) output.min( dim );

								// get the position in all dimensions except the
								// on we compute the fft in
								// which we get from the iterator that iterates
								// n-1 dimensions
								countDim = 0;
								for ( int d = 0; d < numDimensions; ++d )
								{
									if ( d != dim )
									{
										// check that we are not out of the
										// cropped image's bounds defined by
										// interval,
										// then we do not have to compute the
										// fft here
										if ( fakeSize[ countDim ] < interval
												.min( d )
												|| fakeSize[ countDim ] > interval
														.max( d ) )
											continue A;

										cursorInPosition[ d ] = fakeSize[ countDim ]
												+ ( int ) input.min( d );
										cursorOutPosition[ d ] = fakeSize[ countDim ]
												+ ( int ) output.min( d )
												- ( int ) interval.min( d );
										++countDim;
									}
								}

								// set the cursor to the beginning of the
								// correct line
								randomAccessIn.setPosition( cursorInPosition );

								// set the cursor in the fft output image to the
								// right line
								randomAccessOut.setPosition( cursorOutPosition );

								// compute the FFT along the 1d vector and write
								// it into the output
								computeRealToComplex1dFFT( fft, randomAccessIn,
										randomAccessOut, interval, dim, tempIn,
										tempOut, scale );
							}
						}
						return null;
					}
				};

				futures.add( service.submit( call ) );
			}

			for ( final Future< Void > future : futures )
			{
				try
				{
					future.get();
				}
				catch ( final InterruptedException e )
				{
					e.printStackTrace();
				}
				catch ( final ExecutionException e )
				{
					e.printStackTrace();
				}
			}
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
			randomAccessIn.setPosition( ( int ) input.min( 0 ), 0 );

			// set the cursor in the fft output image to 0 in the first (and
			// only) dimension
			randomAccessOut.setPosition( ( int ) output.min( 0 ), 0 );

			// compute the FFT along the 1d vector and write it into the output
			computeRealToComplex1dFFT( fft, randomAccessIn, randomAccessOut,
					interval, 0, tempIn, tempOut, scale );
		}
		return true;
	}

	/**
	 * Computes a complex-to-complex forward or inverse FFT transform of an
	 * n-dimensional dataset in a certain dimension. By default as many threads
	 * as processors are available are used. By default the real values are
	 * scaled if it is an inverse transform (divided by the amount of pixels in
	 * the input dataset), this way after performing a forward and reverse FFT,
	 * the values will be exactly the same. By default the real values are not
	 * scaled if it is a forward transform (not divided by the amount of pixels
	 * in the input dataset)
	 * 
	 * @param data
	 *            - the complex-valued dataset (FFT performed in-place)
	 * @param dim
	 *            - the dimension to compute the inverse FFT in
	 * @param forward
	 *            - true means forward FFT transform, false means inverse FFT
	 *            transform
	 * @return - true if successful, false if the dimensionality of the dataset
	 *         is not supported by the edu_mines_jtk 1d fft
	 */
	final public static < C extends ComplexType< C >> boolean complexToComplex(
			final RandomAccessibleInterval< C > data, final int dim,
			final boolean forward )
	{
		if ( forward )
			return complexToComplex( data, dim, forward, false );
		return complexToComplex( data, dim, forward, true );
	}

	/**
	 * Computes a complex-to-complex forward or inverse FFT transform of an
	 * n-dimensional dataset in a certain dimension. By default as many threads
	 * as processors are available are used.
	 * 
	 * @param data
	 *            - the complex-valued dataset (FFT performed in-place)
	 * @param dim
	 *            - the dimension to compute the inverse FFT in
	 * @param forward
	 *            - true means forward FFT transform, false means inverse FFT
	 *            transform
	 * @param scale
	 *            - define if each pixel is divided by the sum of all pixels in
	 *            the image
	 * @return - true if successful, false if the dimensionality of the dataset
	 *         is not supported by the edu_mines_jtk 1d fft
	 */
	final public static < C extends ComplexType< C >> boolean complexToComplex(
			final RandomAccessibleInterval< C > data, final int dim,
			final boolean forward, final boolean scale )
	{

		final ExecutorService service = Executors.newFixedThreadPool( Runtime.getRuntime().availableProcessors() );
		final boolean ret = complexToComplex( data, dim, forward, scale, service );
		service.shutdown();

		return ret;
	}

	/**
	 * Computes a complex-to-complex forward or inverse FFT transform of an
	 * n-dimensional dataset in a certain dimension.
	 * 
	 * @param data
	 *            - the complex-valued dataset (FFT performed in-place)
	 * @param dim
	 *            - the dimension to compute the inverse FFT in
	 * @param forward
	 *            - true means forward FFT transform, false means inverse FFT
	 *            transform
	 * @param scale
	 *            - define if each pixel is divided by the sum of all pixels in
	 *            the image
	 * @param service
	 *            - service providing threads for multi-threading (if numDims >
	 *            1)
	 * @return - true if successful, false if the dimensionality of the dataset
	 *         is not supported by the edu_mines_jtk 1d fft
	 */
	final public static < C extends ComplexType< C >> boolean complexToComplex(
			final RandomAccessibleInterval< C > data, final int dim,
			final boolean forward, final boolean scale, final ExecutorService service )
	{
		final int numDimensions = data.numDimensions();

		final int dataSize[] = new int[ numDimensions ];

		// FIXME is there a better way to determine the number of threads?
		final int numThreads = Runtime.getRuntime().availableProcessors();

		// the size of the input and output image
		for ( int d = 0; d < numDimensions; ++d )
			dataSize[ d ] = ( int ) data.dimension( d );

		// test if those are valid sizes in case of real to complex
		if ( !verifyComplexToComplexfftDimensions( dataSize[ dim ], dataSize[ dim ] ) )
		{
			System.out
					.println( "Unsupported combination of dimensionality of input and output" );
			return false;
		}

		// perform the real-to-complex fft in a dimension multithreaded if more
		// than one dimension exisits
		final int size = dataSize[ dim ];

		if ( numDimensions > 1 )
		{
			final ArrayList< Future< Void >> futures = new ArrayList< Future< Void >>();
			final AtomicInteger ai = new AtomicInteger( 0 );

			for ( int ithread = 0; ithread < numThreads; ++ithread )
			{
				Callable< Void > call = new Callable< Void >()
				{
					@Override
					public Void call() throws Exception
					{
						final int myNumber = ai.getAndIncrement();

						// the temporary 1-d arrays for the fft
						final float[] tempIn = new float[ size * 2 ];
						final float[] tempOut = new float[ size * 2 ];

						final FftComplex fft = new FftComplex( size );

						final RandomAccess< C > randomAccess = data
								.randomAccess();

						/**
						 * Here we use a LocalizingZeroMinIntervalIterator to
						 * iterate through all dimensions except the one we are
						 * computing the inverse fft in
						 */
						final int[] fakeSize = new int[ numDimensions - 1 ];
						final int[] randomAccessPosition = new int[ numDimensions ];

						// get all dimensions except the one we are currently
						// doing the fft on
						int countDim = 0;
						for ( int d = 0; d < numDimensions; ++d )
							if ( d != dim )
								fakeSize[ countDim++ ] = dataSize[ d ];

						final LocalizingZeroMinIntervalIterator cursorDim = new LocalizingZeroMinIntervalIterator(
								fakeSize );

						// iterate over all dimensions except the one we are
						// computing the fft in
						while ( cursorDim.hasNext() )
						{
							cursorDim.fwd();

							if ( cursorDim.getIntPosition( 0 ) % numThreads == myNumber )
							{
								// get all dimensions except the one we are
								// currently doing the fft on
								cursorDim.localize( fakeSize );

								// the location on the one-dimensional vector of
								// which we compute the fft
								// is simply the first pixel
								randomAccessPosition[ dim ] = ( int ) data.min( dim );

								// get the position in all dimensions except the
								// on we compute the fft in
								// which we get from the iterator that iterates
								// n-1 dimensions
								countDim = 0;
								for ( int d = 0; d < numDimensions; ++d )
									if ( d != dim )
										randomAccessPosition[ d ] = fakeSize[ countDim++ ]
												+ ( int ) data.min( d );

								// set the cursor to the beginning of the
								// correct line
								randomAccess.setPosition( randomAccessPosition );

								// compute the FFT along the 1d vector and write
								// it into the output
								computeComplexToComplex1dFFT( fft, forward,
										randomAccess, dim, tempIn, tempOut,
										scale );
							}
						}
						return null;
					}
				};
				futures.add( service.submit( call ) );
			}

			for ( final Future< Void > future : futures )
			{
				try
				{
					future.get();
				}
				catch ( final InterruptedException e )
				{
					e.printStackTrace();
				}
				catch ( final ExecutionException e )
				{
					e.printStackTrace();
				}
			}
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
			randomAccess.setPosition( ( int ) data.min( 0 ), 0 );

			// compute the FFT along the 1d vector and write it into the output
			computeComplexToComplex1dFFT( fft, forward, randomAccess, dim,
					tempIn, tempOut, scale );
		}

		return true;
	}

	final private static < R extends RealType< R >, C extends ComplexType< C >> void computeRealToComplex1dFFT(
			final FftReal fft, final RandomAccess< R > randomAccessIn,
			final RandomAccess< C > randomAccessOut, final Interval range,
			final int dim, final float[] tempIn, final float[] tempOut,
			final boolean scale )
	{
		final int realSize = tempIn.length;
		final int complexSize = tempOut.length / 2;
		final int realMax = realSize - 1;
		final int complexMax = complexSize - 1;

		// fill the input array with image data
		for ( int i = 0; i < realMax; ++i )
		{
			tempIn[ i ] = randomAccessIn.get().getRealFloat();
			randomAccessIn.fwd( dim );
		}
		tempIn[ realMax ] = randomAccessIn.get().getRealFloat();

		// compute the fft in dimension dim ( real -> complex )
		fft.realToComplex( -1, tempIn, tempOut );

		// write back the fft data

		// check if only a certain portion of the data is desired (maybe if
		// windowing was applied that should undone)
		final int min, max;

		if ( range == null )
		{
			min = 0;
			max = complexMax;
		}
		else
		{
			min = ( int ) range.min( dim );
			max = ( int ) range.max( dim );
		}

		final int complexMax2 = max * 2;

		if ( scale )
		{
			for ( int i = min; i < max; ++i )
			{
				final int j = i * 2;

				randomAccessOut.get().setComplexNumber( tempOut[ j ] / realSize,
						tempOut[ j + 1 ] / realSize );
				randomAccessOut.fwd( dim );
			}
			randomAccessOut.get().setComplexNumber(
					tempOut[ complexMax2 ] / realSize,
					tempOut[ complexMax2 + 1 ] / realSize );
		}
		else
		{
			for ( int i = min; i < max; ++i )
			{
				final int j = i * 2;

				randomAccessOut.get().setComplexNumber( tempOut[ j ],
						tempOut[ j + 1 ] );
				randomAccessOut.fwd( dim );
			}
			randomAccessOut.get().setComplexNumber( tempOut[ complexMax2 ],
					tempOut[ complexMax2 + 1 ] );
		}
	}

	final private static < C extends ComplexType< C >, R extends RealType< R >> void computeComplexToReal1dFFT(
			final FftReal fft, final RandomAccess< C > randomAccessIn,
			final RandomAccess< R > randomAccessOut, final Interval range,
			final int dim, final float[] tempIn, final float[] tempOut,
			final boolean scale )
	{
		final int complexSize = tempIn.length / 2;
		final int realSize = tempOut.length;
		final int complexMax = complexSize - 1;
		final int realMax = realSize - 1;
		final int complexMax2 = complexMax * 2;

		// get the input data
		// fill the input array with complex image data
		for ( int i = 0; i < complexMax; ++i )
		{
			final int j = i * 2;

			tempIn[ j ] = randomAccessIn.get().getRealFloat();
			tempIn[ j + 1 ] = randomAccessIn.get().getImaginaryFloat();
			randomAccessIn.fwd( 0 );
		}
		tempIn[ complexMax2 ] = randomAccessIn.get().getRealFloat();
		tempIn[ complexMax2 + 1 ] = randomAccessIn.get().getImaginaryFloat();

		// compute the fft in dimension 0 ( complex -> real )
		fft.complexToReal( 1, tempIn, tempOut );

		// write back the real data

		// check if only a certain portion of the data is desired (maybe if
		// windowing was applied that should undone)
		final int min, max;

		if ( range == null )
		{
			min = 0;
			max = realMax;
		}
		else
		{
			min = ( int ) range.min( dim );
			max = ( int ) range.max( dim );
		}

		if ( scale )
		{
			for ( int x = min; x < max; ++x )
			{
				randomAccessOut.get().setReal( tempOut[ x ] / realSize );
				randomAccessOut.fwd( 0 );
			}
			randomAccessOut.get().setReal( tempOut[ max ] / realSize );
		}
		else
		{
			for ( int x = min; x < max; ++x )
			{
				randomAccessOut.get().setReal( tempOut[ x ] );
				randomAccessOut.fwd( 0 );
			}
			randomAccessOut.get().setReal( tempOut[ max ] );
		}
	}

	final private static < C extends ComplexType< C >> void computeComplexToComplex1dFFT(
			final FftComplex fft, final boolean forward,
			final RandomAccess< C > randomAccess, final int dim,
			final float[] tempIn, final float[] tempOut, final boolean scale )
	{
		final int size = tempIn.length / 2;
		final int max = size - 1;
		final int max2 = max * 2;

		// get the input line
		for ( int i = 0; i < max; ++i )
		{
			final int j = i * 2;

			tempIn[ j ] = randomAccess.get().getRealFloat();
			tempIn[ j + 1 ] = randomAccess.get().getImaginaryFloat();
			randomAccess.fwd( dim );
		}
		tempIn[ max2 ] = randomAccess.get().getRealFloat();
		tempIn[ max2 + 1 ] = randomAccess.get().getImaginaryFloat();

		// compute the fft in dimension dim (complex -> complex)
		if ( forward )
			fft.complexToComplex( -1, tempIn, tempOut );
		else
			fft.complexToComplex( 1, tempIn, tempOut );

		// move the randomAccess back
		randomAccess.move( -max, dim );

		// write back result
		if ( scale )
		{
			for ( int i = 0; i < max; ++i )
			{
				final int j = i * 2;

				randomAccess.get().setComplexNumber( tempOut[ j ] / size,
						tempOut[ j + 1 ] / size );
				randomAccess.fwd( dim );
			}
			randomAccess.get().setComplexNumber( tempOut[ max2 ] / size,
					tempOut[ max2 + 1 ] / size );
		}
		else
		{
			for ( int i = 0; i < max; ++i )
			{
				final int j = i * 2;

				randomAccess.get().setComplexNumber( tempOut[ j ], tempOut[ j + 1 ] );
				randomAccess.fwd( dim );
			}
			randomAccess.get().setComplexNumber( tempOut[ max2 ],
					tempOut[ max2 + 1 ] );
		}
	}

	/**
	 * Computes the padding interval required to perform an FFT when the padding
	 * dimensions are known. It will define the padding area around the input.
	 * If the extension is not even, it will add the one pixel more on the right
	 * side.
	 * 
	 * @param input
	 *            - the input interval
	 * @param paddingDimensions
	 *            - the dimensions of the padding
	 * @return - a new Interval with the correct dimensions.
	 */
	final public static Interval paddingIntervalCentered( final Interval input,
			final Dimensions paddingDimensions )
	{
		final long[] min = new long[ input.numDimensions() ];
		final long[] max = new long[ input.numDimensions() ];

		for ( int d = 0; d < input.numDimensions(); ++d )
		{
			final long difference = paddingDimensions.dimension( d )
					- input.dimension( d );

			if ( difference % 2 == 0 )
			{
				// left and right the same amount of pixels
				min[ d ] = input.min( d ) - difference / 2;
				max[ d ] = input.max( d ) + difference / 2;
			}
			else
			{
				// right side gets on more pixel than left side
				min[ d ] = input.min( d ) - difference / 2;
				max[ d ] = input.max( d ) + difference / 2 + 1;
			}
		}

		return new FinalInterval( min, max );
	}

	/**
	 * Computes the un-padding interval required to extract the original sized
	 * image from an inverse FFT when padding was applying upon the FFT. It
	 * assumes that the original padding was computed using the method
	 * paddingIntervalCentered( final Interval input, final int[]
	 * paddingDimensions ).
	 * 
	 * Therefore, it will define the padding area around the input. If the
	 * extension is not even on all sides, it will assume the one pixel to be on
	 * the right side.
	 * 
	 * @param fftDimensions
	 *            - the input interval
	 * @param originalDimensions
	 *            - the dimensions of the padding
	 * @return - a new Interval with the correct dimensions.
	 */
	final public static Interval unpaddingIntervalCentered(
			final Interval fftDimensions, final Dimensions originalDimensions )
	{
		final long[] min = new long[ fftDimensions.numDimensions() ];
		final long[] max = new long[ fftDimensions.numDimensions() ];

		// first dimension is different as its size changes
		final long realSize = ( fftDimensions.dimension( 0 ) - 1 ) * 2;
		long difference = realSize - originalDimensions.dimension( 0 );

		min[ 0 ] = fftDimensions.min( 0 ) + difference / 2;
		max[ 0 ] = min[ 0 ] + originalDimensions.dimension( 0 ) - 1;

		for ( int d = 1; d < fftDimensions.numDimensions(); ++d )
		{
			difference = fftDimensions.dimension( d )
					- originalDimensions.dimension( d );

			// left and right the same amount of pixels
			min[ d ] = fftDimensions.min( d ) + difference / 2;
			max[ d ] = min[ d ] + originalDimensions.dimension( d ) - 1;
		}

		return new FinalInterval( min, max );
	}

	/**
	 * Computes the supported dimensionality of an input dataset (of complex
	 * numbers) for an inverse FFT of the entire dataset AS SMALL AS POSSIBLE
	 * 
	 * @param inputDimensions
	 *            - the dimensions of the complex-valued input
	 * @param paddedDimensions
	 *            - the required dimensions of the complex-valued input
	 *            (computed)
	 * @param realSize
	 *            - the dimensions of the real-valued output after the inverse
	 *            fast fourier transform (computed), i.e. which dimensions are
	 *            required for the output
	 */
	final static public void dimensionsComplexToRealSmall(
			final Dimensions inputDimensions, final long[] paddedDimensions,
			final long[] realSize )
	{
		// compute what the dimensionality corresponds to in real-valued pixels
		final int d0 = ( ( int ) inputDimensions.dimension( 0 ) - 1 ) * 2;

		// compute which dimensionality we could get from that
		paddedDimensions[ 0 ] = FftReal.nfftSmall( d0 ) / 2 + 1;

		// and which final dimensionality this will give in real space
		realSize[ 0 ] = ( paddedDimensions[ 0 ] - 1 ) * 2;

		for ( int d = 1; d < inputDimensions.numDimensions(); ++d )
			realSize[ d ] = paddedDimensions[ d ] = FftComplex
					.nfftSmall( ( int ) inputDimensions.dimension( d ) );
	}

	/**
	 * Computes the supported dimensionality of an input dataset (of complex
	 * numbers) for an inverse FFT of the entire dataset AS FAST AS POSSIBLE
	 * 
	 * @param inputDimensions
	 *            - the dimensions of the complex-valued input
	 * @param paddedDimensions
	 *            - the required dimensions of the complex-valued input
	 *            (computed)
	 * @param realSize
	 *            - the dimensions of the real-valued output after the inverse
	 *            fast fourier transform (computed), i.e. which dimensions are
	 *            required for the output
	 */
	final static public void dimensionsComplexToRealFast(
			final Dimensions inputDimensions, final long[] paddedDimensions,
			final long[] realSize )
	{
		// compute what the dimensionality corresponds to in real-valued pixels
		final int d0 = ( ( int ) inputDimensions.dimension( 0 ) - 1 ) * 2;

		// compute which dimensionality we could get from that
		paddedDimensions[ 0 ] = FftReal.nfftFast( d0 ) / 2 + 1;

		// and which final dimensionality this will give in real space
		realSize[ 0 ] = ( paddedDimensions[ 0 ] - 1 ) * 2;

		for ( int d = 1; d < inputDimensions.numDimensions(); ++d )
			realSize[ d ] = paddedDimensions[ d ] = FftComplex
					.nfftFast( ( int ) inputDimensions.dimension( d ) );
	}

	/**
	 * A helper method to test if padding is actually necessary
	 * 
	 * @param interval
	 *            - the dimensions of a dataset
	 * @param paddedDimensions
	 *            - the dimensions of a dataset
	 * @return true if the dimensions are equal, otherwise false
	 */
	final public static boolean dimensionsEqual( final Interval interval,
			final long[] paddedDimensions )
	{
		for ( int d = 0; d < interval.numDimensions(); ++d )
			if ( interval.dimension( d ) != paddedDimensions[ d ] )
				return false;

		return true;
	}

	/**
	 * A helper method to test if padding is actually necessary
	 * 
	 * @param interval
	 *            - the dimensions of a dataset
	 * @param padded
	 *            - the dimensions of a dataset
	 * @return true if the dimensions are equal, otherwise false
	 */
	final public static boolean dimensionsEqual( final Dimensions interval,
			final Dimensions padded )
	{
		for ( int d = 0; d < interval.numDimensions(); ++d )
			if ( interval.dimension( d ) != padded.dimension( d ) )
				return false;

		return true;
	}

	/**
	 * Computes the supported dimensionality of an input dataset (of real
	 * numbers) for a forward FFT of the entire dataset AS FAST AS POSSIBLE
	 * 
	 * @param inputDimensions
	 *            - the dimensions of the real-valued input
	 * @param paddedDimensions
	 *            - the required dimensions of the real-valued input (computed)
	 * @param fftDimensions
	 *            - the dimensions of the complex-valued fft after the fast
	 *            fourier transform (computed), i.e. which dimensions are
	 *            required for the output
	 */
	final static public void dimensionsRealToComplexFast(
			final Dimensions inputDimensions, final long[] paddedDimensions,
			final long[] fftDimensions )
	{
		paddedDimensions[ 0 ] = FftReal.nfftFast( ( int ) inputDimensions
				.dimension( 0 ) );
		fftDimensions[ 0 ] = ( paddedDimensions[ 0 ] / 2 + 1 );

		for ( int d = 1; d < inputDimensions.numDimensions(); ++d )
			fftDimensions[ d ] = paddedDimensions[ d ] = FftComplex
					.nfftFast( ( int ) inputDimensions.dimension( d ) );
	}

	/**
	 * Computes the supported dimensionality of an input dataset (of real
	 * numbers) for a forward FFT of the entire dataset AS SMALL AS POSSIBLE
	 * 
	 * @param inputDimensions
	 *            - the dimensions of the real-valued input
	 * @param paddedDimensions
	 *            - the required dimensions of the real-valued input (computed)
	 * @param fftDimensions
	 *            - the dimensions of the complex-valued fft after the fast
	 *            fourier transform (computed), i.e. which dimensions are
	 *            required for the output
	 */
	final static public void dimensionsRealToComplexSmall(
			final Dimensions inputDimensions, final long[] paddedDimensions,
			final long[] fftDimensions )
	{
		paddedDimensions[ 0 ] = FftReal.nfftSmall( ( int ) inputDimensions
				.dimension( 0 ) );
		fftDimensions[ 0 ] = ( paddedDimensions[ 0 ] / 2 + 1 );

		for ( int d = 1; d < inputDimensions.numDimensions(); ++d )
			fftDimensions[ d ] = paddedDimensions[ d ] = FftComplex
					.nfftSmall( ( int ) inputDimensions.dimension( d ) );
	}

	/**
	 * Computes the supported dimensionality of an input dataset (of complex
	 * numbers) for a forward/inverse FFT of the entire dataset AS FAST AS
	 * POSSIBLE
	 * 
	 * @param inputDimensions
	 *            - the dimensions of the input
	 * @param paddedDimensions
	 *            - the required dimensions of the input/output (computed)
	 */
	final static public void dimensionsComplexToComplexFast(
			final Dimensions inputDimensions, final long[] paddedDimensions )
	{
		for ( int d = 0; d < inputDimensions.numDimensions(); ++d )
			paddedDimensions[ d ] = FftComplex.nfftFast( ( int ) inputDimensions
					.dimension( d ) );
	}

	/**
	 * Computes the supported dimensionality of an input dataset (of complex
	 * numbers) for a forward/inverse FFT of the entire dataset AS SMALL AS
	 * POSSIBLE
	 * 
	 * @param inputDimensions
	 *            - the dimensions of the input
	 * @param paddedDimensions
	 *            - the required dimensions of the input/output (computed)
	 */
	final static public void dimensionsComplexToComplexSmall(
			final Dimensions inputDimensions, final long[] paddedDimensions )
	{
		for ( int d = 0; d < inputDimensions.numDimensions(); ++d )
			paddedDimensions[ d ] = FftComplex.nfftSmall( ( int ) inputDimensions
					.dimension( d ) );
	}

	final protected static boolean verifyRealToComplexfftDimensions(
			final int inputSize, final int outputSize )
	{
		if ( FftReal.nfftFast( inputSize ) / 2 + 1 == outputSize
				|| FftReal.nfftSmall( inputSize ) / 2 + 1 == outputSize )
			return true;
		return false;
	}

	final protected static boolean verifyComplexToComplexfftDimensions(
			final int inputSize, final int outputSize )
	{
		if ( FftComplex.nfftFast( inputSize ) == outputSize
				|| FftComplex.nfftSmall( inputSize ) == outputSize )
			return true;
		return false;
	}

	final public static < T extends ComplexType< T >> void complexConjugate(
			final RandomAccessibleInterval< T > complexData )
	{
		for ( final T type : Views.iterable( complexData ) )
			type.complexConjugate();
	}
}
