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

package net.imglib2.algorithm.gauss;

import java.util.concurrent.atomic.AtomicInteger;

import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.Iterator;
import net.imglib2.Localizable;
import net.imglib2.Positionable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.Sampler;
import net.imglib2.converter.Converter;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.iterator.LocalizingZeroMinIntervalIterator;
import net.imglib2.multithreading.SimpleMultiThreading;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.util.Intervals;
import net.imglib2.util.Util;

/**
 * 
 * @param <T>
 *            - Defines the {@link Type} in which the actual computation is
 *            performed
 * 
 * @author Stephan Preibisch
 */
public abstract class AbstractGauss< T extends NumericType< T > >
{
	final Interval inputInterval;

	final Localizable outputOffset;

	/* final */RandomAccessible< T > input, output;

	final ImgFactory< T > factory;

	final Img< T > tmp1, tmp2;

	final T type;

	final int numDimensions;

	final double[] sigma;

	final double[][] kernel;

	int numThreads;

	public AbstractGauss( final double[] sigma, final RandomAccessible< T > input, final Interval inputInterval,
			final RandomAccessible< T > output, final Localizable outputOffset,
			final ImgFactory< T > factory, final T type )
	{
		this.numThreads = Runtime.getRuntime().availableProcessors();
		this.numDimensions = sigma.length;
		this.input = input;
		this.output = output;
		this.factory = factory;
		this.type = type;

		this.sigma = sigma;
		this.kernel = new double[ numDimensions ][];
		this.inputInterval = inputInterval;
		this.outputOffset = outputOffset;

		computeKernel();

		// allocate the temp images
		final Interval intervalTmp = getTemporaryImgSize();

		if ( numDimensions > 1 )
			tmp1 = factory.create( intervalTmp, getProcessingType() );
		else
			tmp1 = null;

		if ( numDimensions > 2 )
			tmp2 = factory.create( intervalTmp, getProcessingType() );
		else
			tmp2 = null;
	}

	public double[] getSigma()
	{
		return sigma;
	}

	public double[][] getKernel()
	{
		return kernel;
	}

	public int numDimensions()
	{
		return numDimensions;
	}

	public RandomAccessible< T > getInput()
	{
		return input;
	}

	public RandomAccessible< T > getOutput()
	{
		return output;
	}

	public ImgFactory< T > getFactory()
	{
		return factory;
	}

	public Interval getInputInterval()
	{
		return inputInterval;
	}

	public Localizable getOutputOffset()
	{
		return outputOffset;
	}

	public T type()
	{
		return type;
	}

	public Img< T > getTmp1()
	{
		return tmp1;
	}

	public Img< T > getTmp2()
	{
		return tmp2;
	}

	protected T getProcessingType()
	{
		return type.createVariable();
	}

	protected abstract Img< T > getProcessingLine( final long size );

	/**
	 * @return the result of the convolution
	 */
	public RandomAccessible< T > getResult()
	{
		return output;
	}

	/**
	 * The area for the output/temp that needs to be convolved, always relative
	 * to the input of the next convolution operation. The area is larger than
	 * the input, defined by the size of the kernel in each dimension.
	 * 
	 * @param dim
	 *            - The dimension that is currently processed
	 * @return - the {@link Interval} defining the output size for the current
	 *         dimension that is processed
	 */
	protected Interval getRange( final int dim )
	{
		// this is a special case, only the area defined by the input interval
		// needs to be convolved once
		if ( numDimensions == 1 )
			return inputInterval;

		final long[] min = new long[ numDimensions ];
		final long[] max = new long[ numDimensions ];

		// the first convolution is relative to the input RandomAccessible
		// which is not necessarily zero-bounded
		if ( dim == 0 )
		{
			min[ 0 ] = inputInterval.min( 0 );
			max[ 0 ] = inputInterval.max( 0 );

			// all other dimensions except for the first one need to be
			// convolved with an extra size defined by the kernel size
			// the only exception would be if a OutOfBoundsMirrorStrategy
			// on top of a Img would be used, this could be checked in a
			// special Factory class
			for ( int d = 1; d < numDimensions; ++d )
			{
				min[ d ] = inputInterval.min( d ) - kernel[ d ].length / 2;
				max[ d ] = inputInterval.max( d ) + kernel[ d ].length / 2;
			}
		}
		else
		{
			// now everything is relative to the temp images that have to be
			// used in the special implementations of the Gaussian convolutions
			for ( int d = 0; d < numDimensions; ++d )
			{
				// all dimensions that have been convolved already are processed
				// with their normal size
				if ( d < dim )
				{
					min[ d ] = 0;
					max[ d ] = inputInterval.dimension( d ) - 1;
				}
				else if ( d == dim ) // the current dimension starts at kernel/2
										// and has the size of the original
										// input
				{
					min[ d ] = kernel[ d ].length / 2;
					max[ d ] = inputInterval.dimension( d ) - 1 + kernel[ d ].length / 2;
				}
				else
				// all other dimensions that have not been convolved yet have
				// the size of image + half the kernel size on both ends
				{
					min[ d ] = 0;
					max[ d ] = inputInterval.dimension( d ) - 1 + kernel[ d ].length - 1;
				}
			}
		}
		return new FinalInterval( min, max );
	}

	/**
	 * An {@link Iterator} that samples a one dimensional line of input data for
	 * the current dimension.
	 * 
	 * @param dim
	 *            - The current dimension
	 * @param range
	 *            - the size of the output/temp image
	 * @return - A {@link SamplingLineIterator} which provides the input
	 */
	protected SamplingLineIterator< T > createInputLineSampler( final int dim, final Interval range )
	{
		// the random access on the input data
		final RandomAccess< T > randomAccess;

		if ( dim == 0 )
			randomAccess = input.randomAccess( Intervals.expand( range, kernel[ dim ].length / 2, dim ) );
		else if ( dim % 2 == 1 )
			randomAccess = tmp1.randomAccess(); // odd dimensions
		else
			randomAccess = tmp2.randomAccess(); // even dimensions

		// place the randomAccess at the right location and return the
		// size of the SamplingLineIterator we need to compute the processLine

		// the size of the 1-d line
		final long sizeProcessLine = range.dimension( dim );

		// the size of the SamplingLineIterator we need to compute the
		// processLine
		final long sizeInputData = sizeProcessLine + kernel[ dim ].length - 1;

		// use the input image if it is the first dimension
		if ( dim == 0 )
		{
			// put the randomAccess into the correct location, the range is
			// relative to the input for dim 0
			range.min( randomAccess );

			// this also sticks out half because we need more input pixels in x
			// due to the convolution (kernel size)
			randomAccess.move( -( kernel[ 0 ].length / 2 ), 0 );
		}
		else
		{
			// now put the randomAccess into the correct location, the range is
			// relative to the temporary images
			// and therefore zero-bounded
			range.min( randomAccess );

			// all dimensions start at 0 relative to the temp images, except the
			// dimension that is currently convolved
			randomAccess.move( -( kernel[ dim ].length / 2 ), dim );
		}

		// return a new SamplingLineIterator that also keeps the instance of the
		// processing line,
		// which is important for multithreading so that each
		// SamplingLineIterator has its own
		// temporary space
		return new SamplingLineIterator< T >( dim, sizeInputData, randomAccess, getProcessingLine( sizeProcessLine ), getProcessingType(), getProcessingType() );
	}

	/**
	 * An {@link Iterator} that samples a one dimensional line of output data
	 * for the current dimension.
	 * 
	 * @param dim
	 *            - The current dimension
	 * @param range
	 *            - the size of the output/temp image
	 * @param inputLineSampler
	 *            - the input line sampler which knows all the stuff already
	 * @return - A {@link SamplingLineIterator} which provides the output
	 */
	protected WritableLineIterator< T > createOutputLineWriter( final int dim, final Interval range, final SamplingLineIterator< T > inputLineSampler )
	{
		// the random access on the output data
		final RandomAccess< T > randomAccess;

		if ( dim == numDimensions - 1 )
			randomAccess = output.randomAccess();
		else if ( dim % 2 == 0 )
			randomAccess = tmp1.randomAccess(); // even dimensions
		else
			randomAccess = tmp2.randomAccess(); // odd dimensions

		// the size of the 1-d line, same as the input
		final long sizeProcessLine = inputLineSampler.getProcessLine().size();

		if ( dim == numDimensions - 1 )
		{
			// put the randomAccess into the correct location, the range is
			// relative to the input for dim=numDimensions-1
			randomAccess.setPosition( outputOffset );
		}
		else
		{
			if ( dim % 2 == 0 )
				tmp1.min( randomAccess );
			else
				tmp2.min( randomAccess );
		}

		return new WritableLineIterator< T >( dim, sizeProcessLine, randomAccess );
	}

	/**
	 * Compute the current line. It is up to the implementation howto really do
	 * that. The idea is to only iterate over the input once (that's why it is
	 * an {@link Iterator}) as it is potentially an expensive operation (e.g. a
	 * {@link Converter} might be involved or we are computing on a rendered
	 * input)
	 * 
	 * @param input
	 *            - the {@link Iterator}/{@link Sampler} over the current input
	 *            line.
	 */
	protected void processLine( final SamplingLineIterator< T > input, final double[] kernel )
	{
		final int kernelSize = kernel.length;
		final int kernelSizeMinus1 = kernelSize - 1;
		final int kernelSizeHalf = kernelSize / 2;
		final int kernelSizeHalfMinus1 = kernelSizeHalf - 1;

		// where the result is written to, at least a size of 1
		final RandomAccess< T > randomAccessLeft = input.randomAccessLeft; // processingLine.randomAccess();
		final RandomAccess< T > randomAccessRight = input.randomAccessRight; // processingLine.randomAccess();
		final T copy = input.copy; // getProcessingType();
		final T tmp = input.tmp; // getProcessingType();

		final long imgSize = input.getProcessLine().size();

		// do we have a "normal" convolution where the image is at least
		// as big as the kernel, i.e. is the output big enough
		// so that we have left and kernel.length-1 incomplete convolutions
		// and at least one convolution in the middle where the input
		// contributes
		// to kernel.size pixels?
		if ( imgSize >= kernelSize )
		{
			/*
			 * This is how the convolution scheme works, we access every input
			 * only once as it is potentially expensive. We distribute each
			 * value according to the kernel over the output line. Iterating the
			 * output line is cheap as it is typically a one-dimensional array.
			 * Furthermore, we re-use
			 * 
			 * Kernelsize = 5, e.g. [ 0.05 0.25 0.4 0.25 0.05 ]
			 * 
			 * Input --- --- --- --- --- --- --- --- --- ----- ----- ----- -----
			 * ----- ----- i |0| |1| |2| |3| |4| |5| |6| |7| |8| ... |102| |103|
			 * |104| |105| |106| |107| --- --- --- --- --- --- --- --- --- -----
			 * ----- ----- ----- ----- -----
			 * 
			 * Output --- --- --- --- --- --- --- ----- ----- ----- ----- o |0|
			 * |1| |2| |3| |4| |5| |6| ... |100| |101| |102| |103| --- --- ---
			 * --- --- --- --- ----- ----- ----- -----
			 * 
			 * convolve the first pixels where the input influences less than
			 * kernel.size pixels ( -> referes to add() )
			 * 
			 * names of variables in algorithm: i k o | | | | | | i(0)*kernel[0]
			 * -> o(0) k=i-o
			 * 
			 * i(1)*kernel[1] -> o(0) k=i-o i(1)*kernel[0] -> o(1)
			 * 
			 * i(2)*kernel[2] -> o(0) k=i-o i(2)*kernel[1] -> o(1)
			 * i(2)*kernel[0] -> o(2)
			 * 
			 * i(3)*kernel[3] -> o(0) k=i-o i(3)*kernel[2] -> o(1)
			 * i(3)*kernel[1] -> o(2) i(3)*kernel[0] -> o(3)
			 * 
			 * From now on, it is the same till the end of the line (n=0 at the
			 * beginning)
			 * 
			 * i(n+4)*kernel[0] -> o(n) <- same 0 i(n+4)*kernel[1] -> o(n+1) <-
			 * same 1 i(n+4)*kernel[2] -> o(n+2) i(n+4)*kernel[1] -> o(n+3) <-
			 * same 1 (re-use calculation) i(n+4)*kernel[0] -> o(n+4) <- same 0
			 * (re-use calculation)
			 * 
			 * Compute the last pixels
			 * 
			 * i(104)*kernel[0] -> o(100) i(104)*kernel[1] -> o(101)
			 * i(104)*kernel[2] -> o(102) i(104)*kernel[3] -> o(103)
			 * 
			 * i(105)*kernel[0] -> o(101) i(105)*kernel[1] -> o(102)
			 * i(105)*kernel[2] -> o(103)
			 * 
			 * i(106)*kernel[0] -> o(102) i(106)*kernel[1] -> o(103)
			 * 
			 * i(107)*kernel[0] -> o(103)
			 */

			// convolve the first pixels where the input influences less than
			// kernel.size pixels

			// the FIRST pixel is a special case as we cannot set the cursor to
			// -1 (might not be defined)
			// copy input into a temp variable, it might be expensive to get()
			copy.set( input.get() );

			// set the random access in the processing line to the right
			// position
			randomAccessLeft.setPosition( 0, 0 );

			// now add it to the one output values it contributes to
			copy.mul( kernel[ 0 ] );
			randomAccessLeft.get().add( copy );

			for ( int i = 1; i < kernelSizeMinus1; ++i )
			{
				input.fwd();

				// copy input into a temp variable, it might be expensive to
				// get()
				copy.set( input.get() );

				// set the random access in the processing line to the right
				// position
				randomAccessLeft.setPosition( -1, 0 );

				// now add it to all output values it contributes to
				for ( int o = 0; o <= i; ++o )
				{
					randomAccessLeft.fwd( 0 );

					tmp.set( copy );
					tmp.mul( kernel[ i - o ] );

					randomAccessLeft.get().add( tmp );
				}
			}

			// convolve all values where the input value contributes to the full
			// kernel
			//
			// we set one random access on the left side of the kernel and one
			// on the right
			// side and move them inwards, a bit like the scanner of KITT in
			// Knight Rider(TM),
			// but only one way. In this way we save half the calculations.
			// The pixel in the center is done by the left random access.
			// We perform one movement less than necessary, because in the last
			// pixel before
			// the center we only need to move the left one which is responsible
			// for the center
			final long length = imgSize - kernelSizeMinus1;
			for ( long n = 0; n < length; ++n )
			{
				input.fwd();

				// copy input into a temp variable, it might be expensive to
				// get()
				copy.set( input.get() );

				// set the left and the right random access to the right
				// coordinates
				// the left random access is always responsible for the center
				randomAccessLeft.setPosition( n, 0 );
				randomAccessRight.setPosition( n + kernelSizeMinus1, 0 );

				// move till the last pixel before the center of the kernel
				for ( int k = 0; k < kernelSizeHalfMinus1; ++k )
				{
					tmp.set( copy );
					tmp.mul( kernel[ k ] );

					randomAccessLeft.get().add( tmp );
					randomAccessRight.get().add( tmp );

					randomAccessLeft.fwd( 0 );
					randomAccessRight.bck( 0 );
				}

				// do the last pixel (same as a above, but right cursor doesn't
				// move)
				tmp.set( copy );
				tmp.mul( kernel[ kernelSizeHalfMinus1 ] );

				randomAccessLeft.get().add( tmp );
				randomAccessRight.get().add( tmp );

				randomAccessLeft.fwd( 0 );

				// do the center pixel
				tmp.set( copy );
				tmp.mul( kernel[ kernelSizeHalf ] );

				randomAccessLeft.get().add( tmp );
			}

			/*
			 * Just to visualize it again for the output imgSize Input --- ---
			 * --- --- --- --- --- --- --- ----- ----- ----- ----- ----- ----- i
			 * |0| |1| |2| |3| |4| |5| |6| |7| |8| ... |102| |103| |104| |105|
			 * |106| |107| --- --- --- --- --- --- --- --- --- ----- ----- -----
			 * ----- ----- -----
			 * 
			 * Output --- --- --- --- --- --- --- ----- ----- ----- ----- o |0|
			 * |1| |2| |3| |4| |5| |6| ... |100| |101| |102| |103| --- --- ---
			 * --- --- --- --- ----- ----- ----- -----
			 */

			// convolve the last pixels where the input influences less than
			// kernel.size pixels
			final long endLength = imgSize + kernelSizeMinus1;
			for ( long i = imgSize; i < endLength; ++i )
			{
				// after the fwd() call the random access is at position imgSize
				// as pictured above
				input.fwd();

				// copy input into a temp variable, it might be expensive to
				// get()
				copy.set( input.get() );

				// set the random access in the processing line to the right
				// position
				randomAccessLeft.setPosition( i - kernelSize, 0 );

				// now add it to all output values it contributes to
				int k = 0;
				for ( long o = i - kernelSize + 1; o < imgSize; ++o )
				{
					randomAccessLeft.fwd( 0 );

					tmp.set( copy );
					tmp.mul( kernel[ k++ ] );

					randomAccessLeft.get().add( tmp );
				}
			}
		}
		else
		{
			/*
			 * The area to be convolved is actually smaller than the kernel, so
			 * we face something like that, althought the kernel can potentially
			 * be significanly bigger...
			 * 
			 * Kernelsize = 5, e.g. [ 0.05 0.25 0.4 0.25 0.05 ]
			 * 
			 * Input --- --- --- --- --- --- --- i |0| |1| |2| |3| |4| |5| |6|
			 * --- --- --- --- --- --- ---
			 * 
			 * Output --- --- --- o |0| |1| |2| --- --- ---
			 * 
			 * Input --- --- --- --- --- i |0| |1| |2| |3| |4| --- --- --- ---
			 * ---
			 * 
			 * Output --- o |0| ---
			 */

			// convolve the first pixels where the input influences less than
			// kernel.size pixels

			// the FIRST pixel is a special case as we cannot set the cursor to
			// -1 (might not be defined)
			// copy input into a temp variable, it might be expensive to get()
			copy.set( input.get() );

			// set the random access in the processing line to the right
			// position
			randomAccessLeft.setPosition( 0, 0 );

			// now add it to all output values it contributes to
			copy.mul( kernel[ 0 ] );
			randomAccessLeft.get().add( copy );

			for ( int i = 1; i < imgSize; ++i )
			{
				input.fwd();

				// copy input into a temp variable, it might be expensive to
				// get()
				copy.set( input.get() );

				// set the random access in the processing line to the right
				// position
				randomAccessLeft.setPosition( -1, 0 );

				// now add it to all output values it contributes to
				for ( int o = 0; o <= i; ++o )
				{
					randomAccessLeft.fwd( 0 );

					tmp.set( copy );
					tmp.mul( kernel[ i - o ] );

					randomAccessLeft.get().add( tmp );
				}
			}

			// convolve the last pixels where the input influences less than
			// kernel.size pixels
			for ( long i = imgSize; i < imgSize + kernelSizeMinus1; ++i )
			{
				// after the fwd() call the random access is at position imgSize
				// as pictured above
				input.fwd();

				// copy input into a temp variable, it might be expensive to
				// get()
				copy.set( input.get() );

				// set the random access in the processing line to the right
				// position
				// now add it to all output values it contributes to
				long o = i - kernelSize + 1;
				int k = 0;

				if ( o < 0 )
				{
					k = -( int ) o;
					o = 0;
				}

				randomAccessLeft.setPosition( o - 1, 0 );

				// now add it to all output values it contributes to
				for ( ; o < imgSize; ++o )
				{
					randomAccessLeft.fwd( 0 );

					tmp.set( copy );
					tmp.mul( kernel[ k++ ] );

					randomAccessLeft.get().add( tmp );
				}
			}
		}
	}

	/**
	 * Writes the computed line back into the output/temp image. The idea is to
	 * only iterate over the output once (that's why it is an {@link Iterator})
	 * as it is potentially an expensive operation (e.g. a {@link Converter}
	 * might be involved)
	 * 
	 * @param a
	 *            - the {@link Iterator}/{@link Sampler} over the current output
	 *            line.
	 */
	protected void writeLine( final WritableLineIterator< T > a, final SamplingLineIterator< T > inputLineSampler )
	{
		final Cursor< T > resultCursor = inputLineSampler.resultCursor; // inputLineSampler.getProcessLine().cursor();
		resultCursor.reset();

		// the first pixel is special as we cannot move it to -1 (might not be
		// defined)
		if ( resultCursor.hasNext() )
		{
			resultCursor.fwd();
			a.set( resultCursor.get() );
		}

		while ( resultCursor.hasNext() )
		{
			resultCursor.fwd();
			a.fwd();

			a.set( resultCursor.get() );
		}
	}

	/**
	 * Updates the current {@link SamplingLineIterator} to the location of the
	 * new line that is processed and clears the processing line (set all values
	 * to zero)
	 * 
	 * @param a
	 *            - the {@link SamplingLineIterator}
	 * @param range
	 *            - the size of the output/temp image
	 * @param offset
	 *            - the new position
	 * @param originalLocation
	 *            - the location where is was put initially, all movements are
	 *            relative to that
	 */
	protected void updateInputLineSampler( final SamplingLineIterator< T > a, final Interval range, final long[] offset, final Localizable originalLocation )
	{
		final Positionable positionable = a.getPositionable();

		for ( int d = 0; d < numDimensions; ++d )
			positionable.setPosition( originalLocation.getLongPosition( d ) + offset[ d ], d );

		// clear the processing line
		for ( final T v : a.getProcessLine() )
			v.setZero();
	}

	/**
	 * Updates the current {@link WritableLineIterator} to the location of the
	 * new line that is processed.
	 * 
	 * @param a
	 *            - the {@link WritableLineIterator}
	 * @param range
	 *            - the size of the output/temp image
	 * @param offset
	 *            - the new position
	 * @param originalLocation
	 *            - the location where is was put initially, all movements are
	 *            relative to that
	 */
	protected void updateOutputLineWriter( final WritableLineIterator< T > a, final Interval range, final long[] offset, final Localizable originalLocation )
	{
		final Positionable positionable = a.getPositionable();

		for ( int d = 0; d < numDimensions; ++d )
			positionable.setPosition( originalLocation.getLongPosition( d ) + offset[ d ], d );
	}

	/**
	 * Compute the Gaussian Kernel for all dimensions
	 */
	protected void computeKernel()
	{
		for ( int d = 0; d < numDimensions; ++d )
			this.kernel[ d ] = Util.createGaussianKernel1DDouble( sigma[ d ], true );
	}

	/**
	 * @return - the size the input images need to have
	 */
	protected Interval getTemporaryImgSize()
	{
		return getRange( 0 );
	}

	public int getNumThreads()
	{
		return numThreads;
	}

	public void setNumThreads( final int numThreads )
	{
		this.numThreads = Math.max( 1, numThreads );
	}

	public void call()
	{
		if ( numDimensions > 1 )
		{
			for ( int d = 0; d < numDimensions; ++d )
			{
				final int dim = d;
				final int numThreads = getNumThreads();

				final AtomicInteger ai = new AtomicInteger();
				final Thread[] threads = SimpleMultiThreading.newThreads( numThreads );

				for ( int ithread = 0; ithread < threads.length; ++ithread )
					threads[ ithread ] = new Thread( new Runnable()
					{
						@Override
						public void run()
						{
							final int myNumber = ai.getAndIncrement();

							final Interval range = getRange( dim );

							/**
							 * Here create a virtual
							 * LocalizingZeroMinIntervalIterator to iterate
							 * through all dimensions except the one we are
							 * computing in
							 */
							final long[] fakeSize = new long[ numDimensions - 1 ];
							final long[] tmp = new long[ numDimensions ];

							// get all dimensions except the one we are
							// currently doing the gauss on
							int countDim = 0;
							for ( int d = 0; d < numDimensions; ++d )
								if ( d != dim )
									fakeSize[ countDim++ ] = range.dimension( d );

							// create the iterator in the input image for the
							// current dimension
							final SamplingLineIterator< T > inputLineIterator = createInputLineSampler( dim, range );
							final Localizable offsetInput = inputLineIterator.getOffset();

							// get the iterator in the output image for the
							// current dimension position
							final WritableLineIterator< T > outputLineIterator = createOutputLineWriter( dim, range, inputLineIterator );
							final Localizable offsetOutput = outputLineIterator.getOffset();

							final LocalizingZeroMinIntervalIterator cursorDim = new LocalizingZeroMinIntervalIterator( fakeSize );

							// iterate over all dimensions except the one we are
							// computing in
							while ( cursorDim.hasNext() )
							{
								cursorDim.fwd();

								if ( numThreads == 1 || cursorDim.getIntPosition( 0 ) % numThreads == myNumber )
								{
									// update all positions except for the one
									// we are currrently doing the gauss on
									cursorDim.localize( fakeSize );

									tmp[ dim ] = 0;
									countDim = 0;
									for ( int d = 0; d < numDimensions; ++d )
										if ( d != dim )
											tmp[ d ] = fakeSize[ countDim++ ];

									// update the iterator in the input image
									// for the current dimension position
									updateInputLineSampler( inputLineIterator, range, tmp, offsetInput );

									// compute the current line
									processLine( inputLineIterator, kernel[ dim ] );

									// update the iterator in the input image
									// for the current dimension position
									updateOutputLineWriter( outputLineIterator, range, tmp, offsetOutput );

									// and write it back to the output/temp
									// image
									writeLine( outputLineIterator, inputLineIterator );
								}
							}
						}
					} );

				SimpleMultiThreading.startAndJoin( threads );
			}
		}
		else
		{
			final Interval range = getRange( 0 );

			// create the iterator in the input image for the current dimension
			final SamplingLineIterator< T > inputLineIterator = createInputLineSampler( 0, range );

			// get the iterator in the output image for the current dimension
			// position
			final WritableLineIterator< T > outputLineIterator = createOutputLineWriter( 0, range, inputLineIterator );

			// compute the current line
			processLine( inputLineIterator, kernel[ 0 ] );

			// and write it back to the output/temp image
			writeLine( outputLineIterator, inputLineIterator );
		}
	}
}
