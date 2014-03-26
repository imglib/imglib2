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
package net.imglib2.algorithm.dog;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.gauss3.Gauss3;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.util.Util;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;

/**
 * Compute Difference-of-Gaussian of a {@link RandomAccessible}.
 * 
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class DifferenceOfGaussian
{
	/**
	 * Compute the difference of Gaussian for the input. Input convolved with
	 * Gaussian of sigma1 is subtracted from input convolved with Gaussian of
	 * sigma2 (where sigma2 > sigma1).
	 * 
	 * <p>
	 * Creates an appropriate temporary image and calls
	 * {@link #DoG(double[], double[], RandomAccessible, RandomAccessible, RandomAccessibleInterval, int)}.
	 * 
	 * @param sigma1
	 *            stddev (in every dimension) of smaller Gaussian.
	 * @param sigma2
	 *            stddev (in every dimension) of larger Gaussian.
	 * @param input
	 *            the input image extended to infinity (or at least covering the
	 *            same interval as the dog result image, plus borders for
	 *            convolution).
	 * @param dog
	 *            the Difference-of-Gaussian result image.
	 * @param service
	 *            service providing threads for multi-threading
	 */
	public static < T extends NumericType< T > & NativeType< T > > void DoG( final double[] sigma1, final double[] sigma2, final RandomAccessible< T > input, final RandomAccessibleInterval< T > dog, final ExecutorService service )
	{
		final T type = Util.getTypeFromInterval( dog );
		final Img< T > g1 = Util.getArrayOrCellImgFactory( dog, type ).create( dog, type );
		final long[] translation = new long[ dog.numDimensions() ];
		dog.min( translation );
		DoG( sigma1, sigma2, input, Views.translate( g1, translation ), dog, service );
	}

	/**
	 * Compute the difference of Gaussian for the input. Input convolved with
	 * Gaussian of sigma1 is subtracted from input convolved with Gaussian of
	 * sigma2 (where sigma2 > sigma1).
	 * 
	 * @param sigma1
	 *            stddev (in every dimension) of smaller Gaussian.
	 * @param sigma2
	 *            stddev (in every dimension) of larger Gaussian.
	 * @param input
	 *            the input image extended to infinity (or at least covering the
	 *            same interval as the dog result image, plus borders for
	 *            convolution).
	 * @param tmp
	 *            temporary image, must at least cover the same interval as the
	 *            dog result image.
	 * @param dog
	 *            the Difference-of-Gaussian result image.
	 * @param service
	 *            how many threads to use for the computation.
	 */
	public static < T extends NumericType< T > > void DoG( final double[] sigma1, final double[] sigma2, final RandomAccessible< T > input, final RandomAccessible< T > tmp, final RandomAccessibleInterval< T > dog, final ExecutorService service )
	{
		final IntervalView< T > tmpInterval = Views.interval( tmp, dog );
		try
		{
			Gauss3.gauss( sigma1, input, tmpInterval, service );
			Gauss3.gauss( sigma2, input, dog, service );
		}
		catch ( final IncompatibleTypeException e )
		{
			e.printStackTrace();
		}
		final IterableInterval< T > dogIterable = Views.iterable( dog );
		final IterableInterval< T > tmpIterable = Views.iterable( tmpInterval );
		final long size = dogIterable.size();
		// FIXME find better heuristic?
		final int numThreads = Runtime.getRuntime().availableProcessors();
		final int numTasks = numThreads <= 1 ? 1 : numThreads * 20;
		final long taskSize = size / numTasks;

		final ArrayList< Future< Void >> futures = new ArrayList< Future< Void >>();

		for ( int taskNum = 0; taskNum < numTasks; ++taskNum )
		{
			final long fromIndex = taskNum * taskSize;
			final long thisTaskSize = ( taskNum == numTasks - 1 ) ? size - fromIndex : taskSize;
			final Callable< Void > r;
			if ( dogIterable.iterationOrder().equals( tmpIterable.iterationOrder() ) )
			{
				r = new Callable< Void >()
				{
					@Override
					public Void call()
					{
						final Cursor< T > dogCursor = dogIterable.cursor();
						final Cursor< T > tmpCursor = tmpIterable.cursor();
						dogCursor.jumpFwd( fromIndex );
						tmpCursor.jumpFwd( fromIndex );
						for ( int i = 0; i < thisTaskSize; ++i )
							dogCursor.next().sub( tmpCursor.next() );
						return null;
					}
				};
			}
			else
			{
				r = new Callable< Void >()
				{
					@Override
					public Void call()
					{
						final Cursor< T > dogCursor = dogIterable.localizingCursor();
						final RandomAccess< T > tmpAccess = tmpInterval.randomAccess();
						dogCursor.jumpFwd( fromIndex );
						for ( int i = 0; i < thisTaskSize; ++i )
						{
							final T o = dogCursor.next();
							tmpAccess.setPosition( dogCursor );
							o.sub( tmpAccess.get() );
						}
						return null;
					}
				};
			}
			futures.add( service.submit( r ) );
		}

		for ( final Future< Void > f : futures )
		{
			try
			{
				f.get();
			}
			catch ( InterruptedException e )
			{
				e.printStackTrace();
			}
			catch ( ExecutionException e )
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * Helper function to compute per-dimension sigmas in pixel coordinates. The
	 * parameters <code>sigma1</code> and <code>sigma2</code> specify desired
	 * sigmas (scale) in image coordinates. Taking into account the sigma of the
	 * input image as well as the image calibration, the resulting sigma arrays
	 * specifiy the smoothing that has to be applied to achieve the desired
	 * sigmas.
	 * 
	 * @param imageSigma
	 *            estimated sigma of the input image, in pixel coordinates.
	 * @param minf
	 *            multiple of the <code>imageSigma</code> that smoothing with
	 *            the resulting sigma must at least achieve.
	 * @param pixelSize
	 *            calibration. Dimensions of a pixel in image units.
	 * @param sigma1
	 *            desired sigma in image coordinates.
	 * @param sigma2
	 *            desired sigma in image coordinates.
	 * @return <code>double[2][numDimensions]</code>, array of two arrays
	 *         contains resulting sigmas for sigma1, sigma2.
	 */
	public static double[][] computeSigmas( final double imageSigma, final double minf, final double[] pixelSize, final double sigma1, final double sigma2 )
	{
		final int n = pixelSize.length;
		final double k = sigma2 / sigma1;
		final double[] sigmas1 = new double[ n ];
		final double[] sigmas2 = new double[ n ];
		for ( int d = 0; d < n; ++d )
		{
			final double s1 = Math.max( minf * imageSigma, sigma1 / pixelSize[ d ] );
			final double s2 = k * s1;
			sigmas1[ d ] = Math.sqrt( s1 * s1 - imageSigma * imageSigma );
			sigmas2[ d ] = Math.sqrt( s2 * s2 - imageSigma * imageSigma );
		}
		return new double[][] { sigmas1, sigmas2 };
	}

	/**
	 * Helper function to compute the minimum sigma that can be given to
	 * {@link #computeSigmas(double, double, double[], double, double)} while
	 * still achieving isotropic smoothed images.
	 */
	public static double computeMinIsotropicSigma( final double imageSigma, final double minf, final double[] pixelSize )
	{
		final int n = pixelSize.length;
		double s = pixelSize[ 0 ];
		for ( int d = 1; d < n; ++d )
			s = Math.max( s, pixelSize[ d ] );
		return minf * imageSigma * s;
	}
}
