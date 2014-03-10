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

import net.imglib2.Interval;
import net.imglib2.Iterator;
import net.imglib2.Localizable;
import net.imglib2.Point;
import net.imglib2.RandomAccessible;
import net.imglib2.Sampler;
import net.imglib2.converter.Converter;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.NativeImg;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.basictypeaccess.array.DoubleArray;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.outofbounds.OutOfBoundsMirrorFactory;
import net.imglib2.outofbounds.OutOfBoundsMirrorFactory.Boundary;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.view.Views;

/**
 * TODO
 * 
 * @author Stephan Preibisch
 */
@Deprecated
final public class GaussDouble extends AbstractGauss< DoubleType >
{
	protected boolean isArray;

	/**
	 * Computes a Gaussian convolution with double precision on a
	 * {@link RandomAccessible} of {@link DoubleType} in a certain
	 * {@link Interval} and returns an {@link Img} defined by the
	 * {@link ImgFactory} containing the result
	 * 
	 * @param sigma
	 *            - the sigma for the convolution
	 * @param input
	 *            - the {@link RandomAccessible} to work on
	 * @param interval
	 *            - the area that is convolved
	 * @param factory
	 *            - the {@link ImgFactory} that defines the temporary and output
	 *            images to be used
	 */
	public GaussDouble( final double[] sigma, final RandomAccessible< DoubleType > input, final Interval interval, final ImgFactory< DoubleType > factory )
	{
		super( sigma, input, interval, factory.create( interval, new DoubleType() ), new Point( sigma.length ), factory, new DoubleType() );
	}

	/**
	 * Computes a Gaussian convolution with double precision on a
	 * {@link RandomAccessible} of {@link DoubleType} in a certain
	 * {@link Interval} and writes it into a given {@link RandomAccessible} at a
	 * specific Point
	 * 
	 * @param sigma
	 *            - the sigma for the convolution
	 * @param input
	 *            - the {@link RandomAccessible} to work on
	 * @param interval
	 *            - the area that is convolved
	 * @param output
	 *            - the {@link RandomAccessible} where the output will be
	 *            written to
	 * @param outputOffset
	 *            - the offset that corresponds to the first pixel in output
	 *            {@link RandomAccessible}
	 * @param factory
	 *            - the {@link ImgFactory} for creating temporary images
	 */
	public GaussDouble( final double[] sigma, final RandomAccessible< DoubleType > input, final Interval interval, final RandomAccessible< DoubleType > output, final Localizable outputOffset, final ImgFactory< DoubleType > factory )
	{
		super( sigma, input, interval, output, outputOffset, factory, new DoubleType() );
	}

	/**
	 * Computes a Gaussian convolution with double precision on an entire
	 * {@link Img} using the {@link OutOfBoundsMirrorFactory} with single
	 * boundary
	 * 
	 * @param sigma
	 *            - the sigma for the convolution
	 * @param input
	 *            - the input {@link Img}
	 */
	public GaussDouble( final double[] sigma, final Img< DoubleType > input )
	{
		this( sigma, Views.extend( input, new OutOfBoundsMirrorFactory< DoubleType, Img< DoubleType > >( Boundary.SINGLE ) ), input, input.factory() );
	}

	/**
	 * Computes a Gaussian convolution with double precision on an entire
	 * {@link Img}
	 * 
	 * @param sigma
	 *            - the sigma for the convolution
	 * @param input
	 *            - the input {@link Img}
	 * @param outOfBounds
	 *            - the {@link OutOfBoundsFactory} to use
	 */
	public GaussDouble( final double[] sigma, final Img< DoubleType > input, final OutOfBoundsFactory< DoubleType, Img< DoubleType > > outOfBounds )
	{
		this( sigma, Views.extend( input, outOfBounds ), input, input.factory() );
	}

	public static Img< DoubleType > gauss( final double[] sigma, final Img< DoubleType > input )
	{
		final GaussDouble gauss = new GaussDouble( sigma, input );
		gauss.call();
		return ( Img< DoubleType > ) gauss.getResult();
	}

	public static Img< DoubleType > gauss( final double[] sigma, final Img< DoubleType > input, final OutOfBoundsFactory< DoubleType, Img< DoubleType > > outOfBounds )
	{
		final GaussDouble gauss = new GaussDouble( sigma, input, outOfBounds );
		gauss.call();
		return ( Img< DoubleType > ) gauss.getResult();
	}

	public static Img< DoubleType > gauss( final double[] sigma, final RandomAccessible< DoubleType > input, final Interval interval, final ImgFactory< DoubleType > factory )
	{
		final GaussDouble gauss = new GaussDouble( sigma, input, interval, factory );
		gauss.call();
		return ( Img< DoubleType > ) gauss.getResult();
	}

	public static void gauss( final double[] sigma, final RandomAccessible< DoubleType > input, final Interval interval, final RandomAccessible< DoubleType > output, final Localizable outputOffset, final ImgFactory< DoubleType > factory )
	{
		final GaussDouble gauss = new GaussDouble( sigma, input, interval, output, outputOffset, factory );
		gauss.call();
	}

	@Override
	protected Img< DoubleType > getProcessingLine( final long sizeProcessLine )
	{
		final Img< DoubleType > processLine;

		// try to use array if each individual line is not too long
		if ( sizeProcessLine <= Integer.MAX_VALUE )
		{
			isArray = true;
			processLine = new ArrayImgFactory< DoubleType >().create( new long[] { sizeProcessLine }, new DoubleType() );
		}
		else
		{
			isArray = false;
			processLine = new CellImgFactory< DoubleType >( Integer.MAX_VALUE / 16 ).create( new long[] { sizeProcessLine }, new DoubleType() );
		}

		return processLine;
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
	@Override
	protected void processLine( final SamplingLineIterator< DoubleType > input, final double[] kernel )
	{
		if ( !isArray() )
		{
			super.processLine( input, kernel );
			return;
		}

		final int kernelSize = kernel.length;
		final int kernelSizeMinus1 = kernelSize - 1;
		final int kernelSizeHalf = kernelSize / 2;
		final int kernelSizeHalfMinus1 = kernelSizeHalf - 1;

		final double[] v = ( ( DoubleArray ) ( ( NativeImg< ?, ? > ) input.getProcessLine() ).update( null ) ).getCurrentStorageArray();

		final int imgSize = v.length;

		int indexLeft = 0;
		int indexRight = 0;

		if ( imgSize >= kernelSize )
		{
			// convolve the first pixels where the input influences less than
			// kernel.size pixels

			// the FIRST pixel is a special case as we cannot set the cursor to
			// -1 (might not be defined)
			// copy input into a temp variable, it might be expensive to get()
			v[ 0 ] += input.get().get() * kernel[ 0 ];

			for ( int i = 1; i < kernelSizeMinus1; ++i )
			{
				input.fwd();

				// copy input into a temp variable, it might be expensive to
				// get()
				final double copy = input.get().get();

				// set the random access in the processing line to the right
				// position
				indexLeft = -1;

				// now add it to all output values it contributes to
				for ( int o = 0; o <= i; ++o )
					v[ ++indexLeft ] += ( copy * kernel[ i - o ] );
			}

			// convolve all values where the input value contributes to the full
			// kernel
			final int length = imgSize - kernelSizeMinus1;
			for ( int n = 0; n < length; ++n )
			{
				input.fwd();

				// copy input into a temp variable, it might be expensive to
				// get()
				final double copy = input.get().get();

				// set the left and the right random access to the right
				// coordinates
				// the left random access is always responsible for the center
				indexLeft = n;
				indexRight = n + kernelSizeMinus1;

				// move till the last pixel before the center of the kernel
				for ( int k = 0; k < kernelSizeHalfMinus1; ++k )
				{
					final double tmp = ( copy * kernel[ k ] );

					v[ indexLeft++ ] += tmp;
					v[ indexRight-- ] += tmp;
				}

				// do the last pixel (same as a above, but right cursor doesn't
				// move)
				final double tmp = ( copy * kernel[ kernelSizeHalfMinus1 ] );

				v[ indexLeft++ ] += tmp;
				v[ indexRight ] += tmp;

				// do the center pixel
				v[ indexLeft ] += ( copy * kernel[ kernelSizeHalf ] );
			}

			// convolve the last pixels where the input influences less than
			// kernel.size pixels
			final int endLength = imgSize + kernelSizeMinus1;
			for ( int i = imgSize; i < endLength; ++i )
			{
				// after the fwd() call the random access is at position imgSize
				// as pictured above
				input.fwd();

				// copy input into a temp variable, it might be expensive to
				// get()
				final double copy = input.get().get();

				// set the random access in the processing line to the right
				// position
				indexLeft = i - kernelSize;

				// now add it to all output values it contributes to
				int k = 0;
				for ( long o = i - kernelSize + 1; o < imgSize; ++o )
					v[ ++indexLeft ] += ( copy * kernel[ k++ ] );
			}
		}
		else
		{
			// convolve the first pixels where the input influences less than
			// kernel.size pixels

			// the FIRST pixel is a special case as we cannot set the cursor to
			// -1 (might not be defined)
			// copy input into a temp variable, it might be expensive to get()
			v[ 0 ] += input.get().get() * kernel[ 0 ];

			for ( int i = 1; i < imgSize; ++i )
			{
				input.fwd();

				// copy input into a temp variable, it might be expensive to
				// get()
				final double copy = input.get().get();

				// set the random access in the processing line to the right
				// position
				indexLeft = -1;

				// now add it to all output values it contributes to
				for ( int o = 0; o <= i; ++o )
					v[ ++indexLeft ] += ( copy * kernel[ i - o ] );
			}

			// convolve the last pixels where the input influences less than
			// kernel.size pixels
			for ( int i = imgSize; i < imgSize + kernelSizeMinus1; ++i )
			{
				// after the fwd() call the random access is at position imgSize
				// as pictured above
				input.fwd();

				// copy input into a temp variable, it might be expensive to
				// get()
				final double copy = input.get().get();

				// set the random access in the processing line to the right
				// position
				// now add it to all output values it contributes to
				int o = i - kernelSize + 1;
				int k = 0;

				if ( o < 0 )
				{
					k = -o;
					o = 0;
				}

				for ( ; o < imgSize; ++o )
					v[ o ] += ( copy * kernel[ k++ ] );
			}
		}
	}

	protected boolean isArray()
	{
		return isArray;
	}
}
