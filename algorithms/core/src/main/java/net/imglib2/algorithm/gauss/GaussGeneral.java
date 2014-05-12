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
import net.imglib2.Localizable;
import net.imglib2.Point;
import net.imglib2.RandomAccessible;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.list.ListImgFactory;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.outofbounds.OutOfBoundsMirrorFactory;
import net.imglib2.outofbounds.OutOfBoundsMirrorFactory.Boundary;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.view.Views;

/**
 * TODO
 * 
 */
@Deprecated
public class GaussGeneral< T extends NumericType< T > > extends AbstractGauss< T >
{
	/**
	 * Computes a Gaussian convolution on a {@link RandomAccessible} in a
	 * certain {@link Interval} and returns an {@link Img} defined by the
	 * {@link ImgFactory} containing the result.
	 * 
	 * WARNING: This is a very slow implementation as it is not written for
	 * {@link NativeType}. If your type is {@link NativeType}, use
	 * {@link GaussNativeType} instead!
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
	public GaussGeneral( final double[] sigma, final RandomAccessible< T > input, final Interval interval, final ImgFactory< T > factory, final T type )
	{
		super( sigma, input, interval, factory.create( interval, type ), new Point( sigma.length ), factory, type );
	}

	/**
	 * Computes a Gaussian convolution with any precision on a
	 * {@link RandomAccessible} in a certain {@link Interval} and writes it into
	 * a given {@link RandomAccessible} at a specific Point
	 * 
	 * WARNING: This is a very slow implementation as it is not written for
	 * {@link NativeType}. If your type is {@link NativeType}, use
	 * {@link GaussNativeType} instead!
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
	public GaussGeneral( final double[] sigma, final RandomAccessible< T > input, final Interval interval, final RandomAccessible< T > output, final Localizable outputOffset, final ImgFactory< T > factory, final T type )
	{
		super( sigma, input, interval, output, outputOffset, factory, type );
	}

	/**
	 * Computes a Gaussian convolution with any precision on an entire
	 * {@link Img} using the {@link OutOfBoundsMirrorFactory} with single
	 * boundary
	 * 
	 * WARNING: This is a very slow implementation as it is not written for
	 * {@link NativeType}. If your type is {@link NativeType}, use
	 * {@link GaussNativeType} instead!
	 * 
	 * @param sigma
	 *            - the sigma for the convolution
	 * @param input
	 *            - the input {@link Img}
	 */
	public GaussGeneral( final double[] sigma, final Img< T > input )
	{
		this( sigma, Views.extend( input, new OutOfBoundsMirrorFactory< T, Img< T > >( Boundary.SINGLE ) ), input, input.factory(), input.firstElement().createVariable() );
	}

	/**
	 * Computes a Gaussian convolution with any precision on an entire
	 * {@link Img} using the {@link OutOfBoundsMirrorFactory} with single
	 * boundary
	 * 
	 * WARNING: This is a very slow implementation as it is not written for
	 * {@link NativeType}. If your type is {@link NativeType}, use
	 * {@link GaussNativeType} instead!
	 * 
	 * @param sigma
	 *            - the sigma for the convolution
	 * @param input
	 *            - the input {@link Img}
	 */
	public GaussGeneral( final double[] sigma, final Img< T > input, final OutOfBoundsFactory< T, Img< T > > outOfBounds )
	{
		this( sigma, Views.extend( input, outOfBounds ), input, input.factory(), input.firstElement().createVariable() );
	}

	@Override
	protected Img< T > getProcessingLine( final long sizeProcessLine )
	{
		final Img< T > processLine;

		// try to use array if each individual line is not too long
		if ( sizeProcessLine <= Integer.MAX_VALUE )
		{
			processLine = new ListImgFactory< T >().create( new long[] { sizeProcessLine }, getProcessingType() );
		}
		else
		{
			System.out.println( "Individual dimension size is too large for ListImg, sorry. We need a CellListImg..." );
			processLine = null;
		}

		return processLine;
	}
}
