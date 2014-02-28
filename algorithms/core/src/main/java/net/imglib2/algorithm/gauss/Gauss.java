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

import net.imglib2.EuclideanSpace;
import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.Point;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.readwrite.RealDoubleSamplerConverter;
import net.imglib2.converter.readwrite.RealFloatSamplerConverter;
import net.imglib2.converter.readwrite.WriteConvertedIterableRandomAccessibleInterval;
import net.imglib2.converter.readwrite.WriteConvertedRandomAccessible;
import net.imglib2.converter.readwrite.WriteConvertedRandomAccessibleInterval;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.outofbounds.OutOfBoundsMirrorFactory;
import net.imglib2.outofbounds.OutOfBoundsMirrorFactory.Boundary;
import net.imglib2.type.NativeType;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

/**
 * TODO
 * 
 */
@Deprecated
public class Gauss
{
	/**
	 * Computes a Gaussian convolution with float precision on an entire
	 * {@link Img} using the {@link OutOfBoundsMirrorFactory} with single
	 * boundary
	 * 
	 * @param sigma
	 *            - the sigma for the convolution
	 * @param img
	 *            - the img {@link Img}
	 * @return the convolved img as {@link FloatType}
	 */
	public static < T extends RealType< T >> Img< FloatType > toFloat( final double sigma, final Img< T > img )
	{
		return toFloat( getSigmaDim( sigma, img ), img );
	}

	/**
	 * Computes a Gaussian convolution with float precision on an entire
	 * {@link Img} using the {@link OutOfBoundsMirrorFactory} with single
	 * boundary
	 * 
	 * @param sigma
	 *            - the sigma for the convolution
	 * @param img
	 *            - the img {@link Img}
	 * @return the convolved img as {@link FloatType}
	 */
	public static < T extends RealType< T >> Img< FloatType > toFloat( final double[] sigma, final Img< T > img )
	{
		return toFloat( sigma, img, new OutOfBoundsMirrorFactory< FloatType, RandomAccessibleInterval< FloatType > >( Boundary.SINGLE ) );
	}

	/**
	 * Computes a Gaussian convolution with float precision on an entire
	 * {@link Img}
	 * 
	 * @param sigma
	 *            - the sigma for the convolution
	 * @param img
	 *            - the img {@link Img}
	 * @param outofbounds
	 *            - the {@link OutOfBoundsFactory}
	 * @return the convolved img as {@link FloatType}
	 */
	public static < T extends RealType< T >> Img< FloatType > toFloat( final double sigma, final Img< T > img,
			final OutOfBoundsFactory< FloatType, RandomAccessibleInterval< FloatType > > outofbounds )
	{
		return toFloat( getSigmaDim( sigma, img ), img, outofbounds );
	}

	/**
	 * Computes a Gaussian convolution with float precision on an entire
	 * {@link Img}
	 * 
	 * @param sigma
	 *            - the sigma for the convolution
	 * @param img
	 *            - the img {@link Img}
	 * @param outofbounds
	 *            - the {@link OutOfBoundsFactory}
	 * @return the convolved img as {@link FloatType}
	 */
	public static < T extends RealType< T >> Img< FloatType > toFloat( final double[] sigma, final Img< T > img,
			final OutOfBoundsFactory< FloatType, RandomAccessibleInterval< FloatType > > outofbounds )
	{
		GaussFloat gauss = null;
		try
		{
			if ( FloatType.class.isInstance( img.firstElement() ) )
			{
				@SuppressWarnings( { "rawtypes", "unchecked" } )
				final Img< FloatType > img2 = ( Img ) img;
				gauss = new GaussFloat( sigma, img2 );
			}
			else
			{
				final RandomAccessibleInterval< FloatType > rIn = new WriteConvertedIterableRandomAccessibleInterval< T, FloatType, Img< T > >( img, new RealFloatSamplerConverter< T >() );
				gauss = new GaussFloat( sigma, Views.extend( rIn, outofbounds ), img, img.factory().imgFactory( new FloatType() ) );
			}
		}
		catch ( final IncompatibleTypeException e )
		{
			return null;
		}

		gauss.call();

		return ( Img< FloatType > ) gauss.getResult();
	}

	/**
	 * Computes a Gaussian convolution with float precision on an entire
	 * {@link Img} using the {@link OutOfBoundsMirrorFactory} with single
	 * boundary
	 * 
	 * @param sigma
	 *            - the sigma for the convolution
	 * @param img
	 *            - the img {@link Img}
	 * @return the convolved img with img precision
	 */
	public static < T extends RealType< T >> Img< T > inFloat( final double sigma, final Img< T > img )
	{
		return inFloat( getSigmaDim( sigma, img ), img );
	}

	/**
	 * Computes a Gaussian convolution with float precision on an entire
	 * {@link Img} using the {@link OutOfBoundsMirrorFactory} with single
	 * boundary
	 * 
	 * @param sigma
	 *            - the sigma for the convolution
	 * @param img
	 *            - the img {@link Img}
	 * @return the convolved img with img precision
	 */
	public static < T extends RealType< T >> Img< T > inFloat( final double[] sigma, final Img< T > img )
	{
		return inFloat( sigma, img, new OutOfBoundsMirrorFactory< FloatType, RandomAccessibleInterval< FloatType > >( Boundary.SINGLE ) );
	}

	/**
	 * Computes a Gaussian convolution with float precision on an entire
	 * {@link Img}
	 * 
	 * @param sigma
	 *            - the sigma for the convolution
	 * @param img
	 *            - the img {@link Img}
	 * @param outofbounds
	 *            - the {@link OutOfBoundsFactory}
	 * @return the convolved img with img precision
	 */
	public static < T extends RealType< T >> Img< T > inFloat( final double sigma, final Img< T > img,
			final OutOfBoundsFactory< FloatType, RandomAccessibleInterval< FloatType > > outofbounds )
	{
		return inFloat( getSigmaDim( sigma, img ), img, outofbounds );
	}

	/**
	 * Computes a Gaussian convolution with float precision on an entire
	 * {@link Img}
	 * 
	 * @param sigma
	 *            - the sigma for the convolution
	 * @param img
	 *            - the img {@link Img}
	 * @param outofbounds
	 *            - the {@link OutOfBoundsFactory}
	 * @return the convolved img with img precision
	 */
	@SuppressWarnings( { "unchecked", "rawtypes" } )
	public static < T extends RealType< T >> Img< T > inFloat( final double[] sigma, final Img< T > img,
			final OutOfBoundsFactory< FloatType, RandomAccessibleInterval< FloatType > > outofbounds )
	{
		try
		{
			if ( FloatType.class.isInstance( img.firstElement() ) ) { return ( Img ) toFloat( sigma, img, outofbounds ); }
			final Img< T > output = img.factory().create( img, img.firstElement() );

			final RandomAccessible< FloatType > rIn = Views.extend( new WriteConvertedRandomAccessibleInterval< T, FloatType >( img, new RealFloatSamplerConverter< T >() ), outofbounds );
			final RandomAccessible< FloatType > rOut = new WriteConvertedRandomAccessible< T, FloatType >( output, new RealFloatSamplerConverter< T >() );

			inFloat( sigma, rIn, img, rOut, new Point( sigma.length ), img.factory().imgFactory( new FloatType() ) );

			return output;
		}
		catch ( final IncompatibleTypeException e )
		{
			return null;
		}
	}

	/**
	 * Computes a Gaussian convolution in-place (temporary imgs are necessary)
	 * with float precision on an entire {@link Img} using the
	 * {@link OutOfBoundsMirrorFactory} with single boundary
	 * 
	 * @param sigma
	 *            - the sigma for the convolution
	 * @param img
	 *            - the img {@link Img}
	 */
	public static < T extends RealType< T >> void inFloatInPlace( final double sigma, final Img< T > img )
	{
		inFloatInPlace( getSigmaDim( sigma, img ), img );
	}

	/**
	 * Computes a Gaussian convolution in-place (temporary imgs are necessary)
	 * with float precision on an entire {@link Img} using the
	 * {@link OutOfBoundsMirrorFactory} with single boundary
	 * 
	 * @param sigma
	 *            - the sigma for the convolution
	 * @param img
	 *            - the img {@link Img}
	 */
	public static < T extends RealType< T >> void inFloatInPlace( final double[] sigma, final Img< T > img )
	{
		inFloatInPlace( sigma, img, new OutOfBoundsMirrorFactory< FloatType, RandomAccessibleInterval< FloatType > >( Boundary.SINGLE ) );
	}

	/**
	 * Computes a Gaussian convolution in-place (temporary imgs are necessary)
	 * with float precision on an entire {@link Img}
	 * 
	 * @param sigma
	 *            - the sigma for the convolution
	 * @param img
	 *            - the img {@link Img} that will be convolved in place
	 */
	public static < T extends RealType< T >> void inFloatInPlace( final double sigma, final Img< T > img,
			final OutOfBoundsFactory< FloatType, RandomAccessibleInterval< FloatType > > outofbounds )
	{
		inFloatInPlace( getSigmaDim( sigma, img ), img, outofbounds );
	}

	/**
	 * Computes a Gaussian convolution in-place (temporary imgs are necessary)
	 * with float precision on an entire {@link Img}
	 * 
	 * @param sigma
	 *            - the sigma for the convolution
	 * @param img
	 *            - the img {@link Img} that will be convolved in place
	 */
	public static < T extends RealType< T >> void inFloatInPlace( final double[] sigma, final Img< T > img,
			final OutOfBoundsFactory< FloatType, RandomAccessibleInterval< FloatType > > outofbounds )
	{
		GaussFloat gauss = null;
		try
		{
			if ( FloatType.class.isInstance( img.firstElement() ) )
			{
				@SuppressWarnings( { "rawtypes", "unchecked" } )
				final Img< FloatType > img2 = ( Img ) img;
				gauss = new GaussFloat( sigma, Views.extend( img2, outofbounds ), img2, img2, new Point( sigma.length ), img2.factory().imgFactory( new FloatType() ) );
			}
			else
			{
				final RandomAccessibleInterval< FloatType > rIn = new WriteConvertedIterableRandomAccessibleInterval< T, FloatType, Img< T > >( img, new RealFloatSamplerConverter< T >() );
				gauss = new GaussFloat( sigma, Views.extend( rIn, outofbounds ), img, rIn, new Point( sigma.length ), img.factory().imgFactory( new FloatType() ) );
			}
		}
		catch ( final IncompatibleTypeException e )
		{
			System.out.println( e );
			return;
		}

		gauss.call();
	}

	/**
	 * Computes a Gaussian convolution with float precision on an infinite
	 * {@link RandomAccessible}
	 * 
	 * @param sigma
	 *            - the sigma for the convolution
	 * @param img
	 *            - the img {@link RandomAccessible} (infinite -> Views.extend(
	 *            ... ) )
	 * @param interval
	 *            - the interval which should be convolved
	 * @param output
	 *            - the output {@link RandomAccessible} (img and output can be
	 *            the same)
	 * @param origin
	 *            - the origin in the output where the result should be placed
	 * @param imgFactory
	 *            - the {@link ImgFactory} for {@link FloatType} which is needed
	 *            for temporary imgs
	 */
	public static < T extends RealType< T >> void inFloat( final double sigma, final RandomAccessible< T > img, final Interval interval,
			final RandomAccessible< T > output, final Localizable origin, final ImgFactory< FloatType > imgFactory )
	{
		inFloat( getSigmaDim( sigma, img ), img, interval, output, origin, imgFactory );
	}

	/**
	 * Computes a Gaussian convolution with float precision on an infinite
	 * {@link RandomAccessible}
	 * 
	 * @param sigma
	 *            - the sigma for the convolution
	 * @param img
	 *            - the img {@link RandomAccessible} (infinite -> Views.extend(
	 *            ... ) )
	 * @param interval
	 *            - the interval which should be convolved
	 * @param output
	 *            - the output {@link RandomAccessible} (img and output can be
	 *            the same)
	 * @param origin
	 *            - the origin in the output where the result should be placed
	 * @param imgFactory
	 *            - the {@link ImgFactory} for {@link FloatType} which is needed
	 *            for temporary imgs
	 */
	public static < T extends RealType< T >> void inFloat( final double[] sigma, final RandomAccessible< T > img, final Interval interval,
			final RandomAccessible< T > output, final Localizable origin, final ImgFactory< FloatType > imgFactory )
	{
		// find out if it is a FloatType, therefore we get the first value
		// defined by the
		// interval in the img
		final long[] tmpCoordinate = new long[ img.numDimensions() ];
		interval.min( tmpCoordinate );
		final RandomAccess< T > tmp = img.randomAccess();
		tmp.setPosition( tmpCoordinate );

		if ( FloatType.class.isInstance( tmp.get() ) )
		{
			@SuppressWarnings( { "rawtypes", "unchecked" } )
			final RandomAccessible< FloatType > rIn = ( RandomAccessible ) img;
			@SuppressWarnings( { "rawtypes", "unchecked" } )
			final RandomAccessible< FloatType > rOut = ( RandomAccessible ) output;

			new GaussFloat( sigma, rIn, interval, rOut, origin, imgFactory ).call();
		}
		else
		{
			final RandomAccessible< FloatType > rIn = new WriteConvertedRandomAccessible< T, FloatType >( img, new RealFloatSamplerConverter< T >() );
			final RandomAccessible< FloatType > rOut = new WriteConvertedRandomAccessible< T, FloatType >( output, new RealFloatSamplerConverter< T >() );

			new GaussFloat( sigma, rIn, interval, rOut, origin, imgFactory ).call();
		}
	}

	/**
	 * Computes a Gaussian convolution with double precision on an entire
	 * {@link Img} using the {@link OutOfBoundsMirrorFactory} with single
	 * boundary
	 * 
	 * @param sigma
	 *            - the sigma for the convolution
	 * @param img
	 *            - the img {@link Img}
	 * @return the convolved img in {@link DoubleType}
	 */
	public static < T extends RealType< T >> Img< DoubleType > toDouble( final double sigma, final Img< T > img )
	{
		return toDouble( getSigmaDim( sigma, img ), img );
	}

	/**
	 * Computes a Gaussian convolution with double precision on an entire
	 * {@link Img} using the {@link OutOfBoundsMirrorFactory} with single
	 * boundary
	 * 
	 * @param sigma
	 *            - the sigma for the convolution
	 * @param img
	 *            - the img {@link Img}
	 * @return the convolved img in {@link DoubleType}
	 */
	public static < T extends RealType< T >> Img< DoubleType > toDouble( final double[] sigma, final Img< T > img )
	{
		return toDouble( sigma, img, new OutOfBoundsMirrorFactory< DoubleType, RandomAccessibleInterval< DoubleType > >( Boundary.SINGLE ) );
	}

	/**
	 * Computes a Gaussian convolution with double precision on an entire
	 * {@link Img}
	 * 
	 * @param sigma
	 *            - the sigma for the convolution
	 * @param img
	 *            - the img {@link Img}
	 * @param outofbounds
	 *            - the {@link OutOfBoundsFactory}
	 * @return the convolved img in {@link DoubleType}
	 */
	public static < T extends RealType< T >> Img< DoubleType > toDouble( final double sigma, final Img< T > img,
			final OutOfBoundsFactory< DoubleType, RandomAccessibleInterval< DoubleType > > outofbounds )
	{
		return toDouble( getSigmaDim( sigma, img ), img, outofbounds );
	}

	/**
	 * Computes a Gaussian convolution with double precision on an entire
	 * {@link Img}
	 * 
	 * @param sigma
	 *            - the sigma for the convolution
	 * @param img
	 *            - the img {@link Img}
	 * @param outofbounds
	 *            - the {@link OutOfBoundsFactory}
	 * @return the convolved img in {@link DoubleType}
	 */
	public static < T extends RealType< T >> Img< DoubleType > toDouble( final double[] sigma, final Img< T > img,
			final OutOfBoundsFactory< DoubleType, RandomAccessibleInterval< DoubleType > > outofbounds )
	{
		GaussDouble gauss = null;
		try
		{
			if ( DoubleType.class.isInstance( img.firstElement() ) )
			{
				@SuppressWarnings( { "rawtypes", "unchecked" } )
				final Img< DoubleType > img2 = ( Img ) img;
				gauss = new GaussDouble( sigma, img2 );
			}
			else
			{
				final RandomAccessibleInterval< DoubleType > rIn = new WriteConvertedIterableRandomAccessibleInterval< T, DoubleType, Img< T > >( img, new RealDoubleSamplerConverter< T >() );
				gauss = new GaussDouble( sigma, Views.extend( rIn, outofbounds ), img, img.factory().imgFactory( new DoubleType() ) );
			}
		}
		catch ( final IncompatibleTypeException e )
		{
			return null;
		}

		gauss.call();

		return ( Img< DoubleType > ) gauss.getResult();
	}

	/**
	 * Computes a Gaussian convolution with double precision on an entire
	 * {@link Img} using the {@link OutOfBoundsMirrorFactory} with single
	 * boundary
	 * 
	 * @param sigma
	 *            - the sigma for the convolution
	 * @param img
	 *            - the img {@link Img}
	 * @return the convolved img having the input type
	 */
	public static < T extends RealType< T >> Img< T > inDouble( final double sigma, final Img< T > img )
	{
		return inDouble( getSigmaDim( sigma, img ), img );
	}

	/**
	 * Computes a Gaussian convolution with double precision on an entire
	 * {@link Img} using the {@link OutOfBoundsMirrorFactory} with single
	 * boundary
	 * 
	 * @param sigma
	 *            - the sigma for the convolution
	 * @param img
	 *            - the img {@link Img}
	 * @return the convolved img having the input type
	 */
	public static < T extends RealType< T >> Img< T > inDouble( final double[] sigma, final Img< T > img )
	{
		return inDouble( sigma, img, new OutOfBoundsMirrorFactory< DoubleType, RandomAccessibleInterval< DoubleType > >( Boundary.SINGLE ) );
	}

	/**
	 * Computes a Gaussian convolution with double precision on an entire
	 * {@link Img}
	 * 
	 * @param sigma
	 *            - the sigma for the convolution
	 * @param img
	 *            - the img {@link Img}
	 * @param outofbounds
	 *            - the {@link OutOfBoundsFactory}
	 * @return the convolved img having the input type
	 */
	public static < T extends RealType< T >> Img< T > inDouble( final double sigma, final Img< T > img,
			final OutOfBoundsFactory< DoubleType, RandomAccessibleInterval< DoubleType > > outofbounds )
	{
		return inDouble( getSigmaDim( sigma, img ), img, outofbounds );
	}

	/**
	 * Computes a Gaussian convolution with double precision on an entire
	 * {@link Img}
	 * 
	 * @param sigma
	 *            - the sigma for the convolution
	 * @param img
	 *            - the img {@link Img}
	 * @param outofbounds
	 *            - the {@link OutOfBoundsFactory}
	 * @return the convolved img having the input type
	 */
	@SuppressWarnings( { "unchecked", "rawtypes" } )
	public static < T extends RealType< T >> Img< T > inDouble( final double[] sigma, final Img< T > img,
			final OutOfBoundsFactory< DoubleType, RandomAccessibleInterval< DoubleType > > outofbounds )
	{
		try
		{
			if ( DoubleType.class.isInstance( img.firstElement() ) ) { return ( Img ) toDouble( sigma, img, outofbounds ); }
			final Img< T > output = img.factory().create( img, img.firstElement() );

			final RandomAccessible< DoubleType > rIn = Views.extend( new WriteConvertedRandomAccessibleInterval< T, DoubleType >( img, new RealDoubleSamplerConverter< T >() ), outofbounds );
			final RandomAccessible< DoubleType > rOut = new WriteConvertedRandomAccessible< T, DoubleType >( output, new RealDoubleSamplerConverter< T >() );

			inDouble( sigma, rIn, img, rOut, new Point( sigma.length ), img.factory().imgFactory( new DoubleType() ) );

			return output;
		}
		catch ( final IncompatibleTypeException e )
		{
			return null;
		}
	}

	/**
	 * Computes a Gaussian convolution in-place (temporary imgs are necessary)
	 * with double precision on an entire {@link Img} using the
	 * {@link OutOfBoundsMirrorFactory} with single boundary
	 * 
	 * @param sigma
	 *            - the sigma for the convolution
	 * @param img
	 *            - the img {@link Img}
	 */
	public static < T extends RealType< T >> void inDoubleInPlace( final double sigma, final Img< T > img )
	{
		inDoubleInPlace( getSigmaDim( sigma, img ), img );
	}

	/**
	 * Computes a Gaussian convolution in-place (temporary imgs are necessary)
	 * with double precision on an entire {@link Img} using the
	 * {@link OutOfBoundsMirrorFactory} with single boundary
	 * 
	 * @param sigma
	 *            - the sigma for the convolution
	 * @param img
	 *            - the img {@link Img}
	 */
	public static < T extends RealType< T >> void inDoubleInPlace( final double[] sigma, final Img< T > img )
	{
		inDoubleInPlace( sigma, img, new OutOfBoundsMirrorFactory< DoubleType, RandomAccessibleInterval< DoubleType > >( Boundary.SINGLE ) );
	}

	/**
	 * Computes a Gaussian convolution in-place (temporary imgs are necessary)
	 * with double precision on an entire {@link Img}
	 * 
	 * @param sigma
	 *            - the sigma for the convolution
	 * @param img
	 *            - the img {@link Img} that will be convolved in place
	 */
	public static < T extends RealType< T >> void inDoubleInPlace( final double sigma, final Img< T > img,
			final OutOfBoundsFactory< DoubleType, RandomAccessibleInterval< DoubleType > > outofbounds )
	{
		inDoubleInPlace( getSigmaDim( sigma, img ), img, outofbounds );
	}

	/**
	 * Computes a Gaussian convolution in-place (temporary imgs are necessary)
	 * with double precision on an entire {@link Img}
	 * 
	 * @param sigma
	 *            - the sigma for the convolution
	 * @param img
	 *            - the img {@link Img} that will be convolved in place
	 */
	public static < T extends RealType< T >> void inDoubleInPlace( final double[] sigma, final Img< T > img,
			final OutOfBoundsFactory< DoubleType, RandomAccessibleInterval< DoubleType > > outofbounds )
	{
		GaussDouble gauss = null;
		try
		{
			if ( DoubleType.class.isInstance( img.firstElement() ) )
			{
				@SuppressWarnings( { "rawtypes", "unchecked" } )
				final Img< DoubleType > img2 = ( Img ) img;
				gauss = new GaussDouble( sigma, Views.extend( img2, outofbounds ), img2, img2, new Point( sigma.length ), img2.factory().imgFactory( new DoubleType() ) );
			}
			else
			{
				final RandomAccessibleInterval< DoubleType > rIn = new WriteConvertedIterableRandomAccessibleInterval< T, DoubleType, Img< T > >( img, new RealDoubleSamplerConverter< T >() );
				gauss = new GaussDouble( sigma, Views.extend( rIn, outofbounds ), img, rIn, new Point( sigma.length ), img.factory().imgFactory( new DoubleType() ) );
			}
		}
		catch ( final IncompatibleTypeException e )
		{
			return;
		}

		gauss.call();
	}

	/**
	 * Computes a Gaussian convolution with double precision on an infinite
	 * {@link RandomAccessible}
	 * 
	 * @param sigma
	 *            - the sigma for the convolution
	 * @param img
	 *            - the img {@link RandomAccessible} (infinite -> Views.extend(
	 *            ... ) )
	 * @param interval
	 *            - the interval which should be convolved
	 * @param output
	 *            - the output {@link RandomAccessible} (img and output can be
	 *            the same)
	 * @param origin
	 *            - the origin in the output where the result should be placed
	 * @param imgFactory
	 *            - the {@link ImgFactory} for {@link DoubleType} which is
	 *            needed for temporary imgs
	 */
	public static < T extends RealType< T >> void inDouble( final double sigma, final RandomAccessible< T > img, final Interval interval,
			final RandomAccessible< T > output, final Localizable origin, final ImgFactory< DoubleType > imgFactory )
	{
		inDouble( getSigmaDim( sigma, img ), img, interval, output, origin, imgFactory );
	}

	/**
	 * Computes a Gaussian convolution with double precision on an infinite
	 * {@link RandomAccessible}
	 * 
	 * @param sigma
	 *            - the sigma for the convolution
	 * @param img
	 *            - the img {@link RandomAccessible} (infinite -> Views.extend(
	 *            ... ) )
	 * @param interval
	 *            - the interval which should be convolved
	 * @param output
	 *            - the output {@link RandomAccessible} (img and output can be
	 *            the same)
	 * @param origin
	 *            - the origin in the output where the result should be placed
	 * @param imgFactory
	 *            - the {@link ImgFactory} for {@link DoubleType} which is
	 *            needed for temporary imgs
	 */
	public static < T extends RealType< T >> void inDouble( final double[] sigma, final RandomAccessible< T > img, final Interval interval,
			final RandomAccessible< T > output, final Localizable origin, final ImgFactory< DoubleType > imgFactory )
	{
		// find out if it is a DoubleType, therefore we get the first value
		// defined by the
		// interval in the img
		final long[] tmpCoordinate = new long[ img.numDimensions() ];
		interval.min( tmpCoordinate );
		final RandomAccess< T > tmp = img.randomAccess();
		tmp.setPosition( tmpCoordinate );

		if ( DoubleType.class.isInstance( tmp.get() ) )
		{
			@SuppressWarnings( { "rawtypes", "unchecked" } )
			final RandomAccessible< DoubleType > rIn = ( RandomAccessible ) img;
			@SuppressWarnings( { "rawtypes", "unchecked" } )
			final RandomAccessible< DoubleType > rOut = ( RandomAccessible ) output;

			new GaussDouble( sigma, rIn, interval, rOut, origin, imgFactory ).call();
		}
		else
		{
			final RandomAccessible< DoubleType > rIn = new WriteConvertedRandomAccessible< T, DoubleType >( img, new RealDoubleSamplerConverter< T >() );
			final RandomAccessible< DoubleType > rOut = new WriteConvertedRandomAccessible< T, DoubleType >( output, new RealDoubleSamplerConverter< T >() );

			new GaussDouble( sigma, rIn, interval, rOut, origin, imgFactory ).call();
		}
	}

	/**
	 * Computes a Gaussian convolution with the precision of the type provided
	 * on an entire {@link Img} using the {@link OutOfBoundsMirrorFactory} with
	 * single boundary
	 * 
	 * @param sigma
	 *            - the sigma for the convolution
	 * @param img
	 *            - the img {@link Img}
	 * @return the convolved img
	 */
	public static < T extends NumericType< T >> Img< T > inNumericType( final double sigma, final Img< T > img )
	{
		return inNumericType( getSigmaDim( sigma, img ), img );
	}

	/**
	 * Computes a Gaussian convolution with the precision of the type provided
	 * on an entire {@link Img} using the {@link OutOfBoundsMirrorFactory} with
	 * single boundary
	 * 
	 * @param sigma
	 *            - the sigma for the convolution
	 * @param img
	 *            - the img {@link Img}
	 * @return the convolved img
	 */
	public static < T extends NumericType< T >> Img< T > inNumericType( final double[] sigma, final Img< T > img )
	{
		return inNumericType( sigma, img, new OutOfBoundsMirrorFactory< T, RandomAccessibleInterval< T > >( Boundary.SINGLE ) );
	}

	/**
	 * Computes a Gaussian convolution with the precision of the type provided
	 * on an entire {@link Img}
	 * 
	 * @param sigma
	 *            - the sigma for the convolution
	 * @param img
	 *            - the img {@link Img}
	 * @param outofbounds
	 *            - the {@link OutOfBoundsFactory}
	 * @return the convolved img
	 */
	public static < T extends NumericType< T >> Img< T > inNumericType( final double sigma, final Img< T > img,
			final OutOfBoundsFactory< T, RandomAccessibleInterval< T > > outofbounds )
	{
		return inNumericType( getSigmaDim( sigma, img ), img, outofbounds );
	}

	/**
	 * Computes a Gaussian convolution with the precision of the type provided
	 * on an entire {@link Img}
	 * 
	 * @param sigma
	 *            - the sigma for the convolution
	 * @param img
	 *            - the img {@link Img}
	 * @param outofbounds
	 *            - the {@link OutOfBoundsFactory}
	 * @return the convolved img
	 */
	public static < T extends NumericType< T >> Img< T > inNumericType( final double[] sigma, final Img< T > img,
			final OutOfBoundsFactory< T, RandomAccessibleInterval< T > > outofbounds )
	{
		final Img< T > output = img.factory().create( img, img.firstElement() );
		inNumericType( sigma, Views.extend( img, outofbounds ), img, output, new Point( sigma.length ), img.factory() );
		return output;
	}

	/**
	 * Computes a Gaussian convolution in-place (temporary imgs are necessary)
	 * with the precision of the type provided on an entire {@link Img} using
	 * the {@link OutOfBoundsMirrorFactory} with single boundary
	 * 
	 * @param sigma
	 *            - the sigma for the convolution
	 * @param img
	 *            - the img {@link Img}
	 */
	public static < T extends NumericType< T >> void inNumericTypeInPlace( final double sigma, final Img< T > img )
	{
		inNumericTypeInPlace( getSigmaDim( sigma, img ), img );
	}

	/**
	 * Computes a Gaussian convolution in-place (temporary imgs are necessary)
	 * with the precision of the type provided on an entire {@link Img} using
	 * the {@link OutOfBoundsMirrorFactory} with single boundary
	 * 
	 * @param sigma
	 *            - the sigma for the convolution
	 * @param img
	 *            - the img {@link Img}
	 */
	public static < T extends NumericType< T >> void inNumericTypeInPlace( final double[] sigma, final Img< T > img )
	{
		inNumericTypeInPlace( sigma, img, new OutOfBoundsMirrorFactory< T, RandomAccessibleInterval< T > >( Boundary.SINGLE ) );
	}

	/**
	 * Computes a Gaussian convolution in-place (temporary imgs are necessary)
	 * with the precision of the type provided on an entire {@link Img}
	 * 
	 * @param sigma
	 *            - the sigma for the convolution
	 * @param img
	 *            - the img {@link Img} that will be convolved in place
	 */
	public static < T extends NumericType< T >> void inNumericTypeInPlace( final double sigma, final Img< T > img,
			final OutOfBoundsFactory< T, RandomAccessibleInterval< T > > outofbounds )
	{
		inNumericTypeInPlace( getSigmaDim( sigma, img ), img, outofbounds );
	}

	/**
	 * Computes a Gaussian convolution in-place (temporary imgs are necessary)
	 * with the precision of the type provided on an entire {@link Img}
	 * 
	 * @param sigma
	 *            - the sigma for the convolution
	 * @param img
	 *            - the img {@link Img} that will be convolved in place
	 */
	public static < T extends NumericType< T >> void inNumericTypeInPlace( final double[] sigma, final Img< T > img,
			final OutOfBoundsFactory< T, RandomAccessibleInterval< T > > outofbounds )
	{
		inNumericType( sigma, Views.extend( img, outofbounds ), img, img, new Point( sigma.length ), img.factory() );
	}

	/**
	 * Computes a Gaussian convolution with the precision of the type provided
	 * on an infinite {@link RandomAccessible}
	 * 
	 * @param sigma
	 *            - the sigma for the convolution
	 * @param img
	 *            - the img {@link RandomAccessible} (infinite -> Views.extend(
	 *            ... ) )
	 * @param interval
	 *            - the interval which should be convolved
	 * @param output
	 *            - the output {@link RandomAccessible} (img and output can be
	 *            the same)
	 * @param origin
	 *            - the origin in the output where the result should be placed
	 * @param imgFactory
	 *            - the {@link ImgFactory} for T which is needed for temporary
	 *            imgs
	 */
	public static < T extends NumericType< T >> void inNumericType( final double sigma, final RandomAccessible< T > img, final Interval interval,
			final RandomAccessible< T > output, final Localizable origin, final ImgFactory< T > imgFactory )
	{
		inNumericType( getSigmaDim( sigma, img ), img, interval, output, origin, imgFactory );
	}

	/**
	 * Computes a Gaussian convolution with the precision of the type provided
	 * on an infinite {@link RandomAccessible}
	 * 
	 * @param sigma
	 *            - the sigma for the convolution
	 * @param img
	 *            - the img {@link RandomAccessible} (infinite -> Views.extend(
	 *            ... ) )
	 * @param interval
	 *            - the interval which should be convolved
	 * @param output
	 *            - the output {@link RandomAccessible} (img and output can be
	 *            the same)
	 * @param origin
	 *            - the origin in the output where the result should be placed
	 * @param imgFactory
	 *            - the {@link ImgFactory} for T which is needed for temporary
	 *            imgs
	 */
	@SuppressWarnings( "rawtypes" )
	public static < T extends NumericType< T >> void inNumericType( final double[] sigma, final RandomAccessible< T > img, final Interval interval,
			final RandomAccessible< T > output, final Localizable origin, final ImgFactory< T > imgFactory )
	{
		// find out if it is a NativeType, therefore we get the first value
		// defined by the
		// interval in the img - we also need it in this case for the
		// convolution
		final long[] tmpCoordinate = new long[ img.numDimensions() ];
		interval.min( tmpCoordinate );
		final RandomAccess< T > tmp = img.randomAccess();
		tmp.setPosition( tmpCoordinate );

		if ( NativeType.class.isInstance( tmp.get() ) )
		{
			// we have to call it in an extra method because we cannot cast to
			// (NumericType<T> & NativeType<T>)
			computeInNativeType( sigma, ( RandomAccessible ) img, interval, ( RandomAccessible ) output, origin, ( ImgFactory ) imgFactory, tmp.get() );
		}
		else
		{
			final GaussGeneral< T > gauss = new GaussGeneral< T >( sigma, img, interval, output, origin, imgFactory, tmp.get() );
			gauss.call();
		}
	}

	private static final < T extends NumericType< T > & NativeType< T > > void computeInNativeType( final double[] sigma, final RandomAccessible< T > img, final Interval interval,
			final RandomAccessible< T > output, final Localizable origin, final ImgFactory< T > imgFactory, @SuppressWarnings( "rawtypes" ) final Type type )
	{
		@SuppressWarnings( "unchecked" )
		final GaussNativeType< T > gauss = new GaussNativeType< T >( sigma, img, interval, output, origin, imgFactory, ( T ) type );
		gauss.call();
	}

	private static final double[] getSigmaDim( final double sigma, final EuclideanSpace img )
	{
		final double s[] = new double[ img.numDimensions() ];

		for ( int d = 0; d < img.numDimensions(); ++d )
			s[ d ] = sigma;

		return s;
	}
}
