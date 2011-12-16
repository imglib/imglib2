package net.imglib2.algorithm.gauss;

import net.imglib2.Location;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.ConvertedIterableRandomAccessibleInterval;
import net.imglib2.converter.sampler.RealFloatSamplerConverter;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.outofbounds.OutOfBoundsMirrorFactory;
import net.imglib2.outofbounds.OutOfBoundsMirrorFactory.Boundary;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

public class Gauss
{
	/**
	 * Computes a Gaussian convolution with float precision on an entire {@link Img} using the {@link OutOfBoundsMirrorFactory} with single boundary
	 *
	 * @param sigma - the sigma for the convolution
	 * @param input - the input {@link Img}
	 * @return the convolved image
	 */
	public static <T extends RealType<T>> Img<FloatType> inFloat( final double[] sigma, final Img< T > input )
	{
		return inFloat( sigma, input, new OutOfBoundsMirrorFactory< FloatType, RandomAccessibleInterval< FloatType > >( Boundary.SINGLE ) );
	}

	/**
	 * Computes a Gaussian convolution with float precision on an entire {@link Img}
	 *
	 * @param sigma - the sigma for the convolution
	 * @param input - the input {@link Img}
	 * @param outofbounds - the {@link OutOfBoundsFactory}
	 * @return the convolved image
	 */
	public static <T extends RealType<T>> Img<FloatType> inFloat( final double[] sigma, final Img< T > input,
			final OutOfBoundsFactory< FloatType, RandomAccessibleInterval< FloatType > > outofbounds )
	{
		GaussFloat gauss = null;
		try
		{
			if ( FloatType.class.isInstance( input.firstElement() ) )
			{
				@SuppressWarnings( { "rawtypes", "unchecked" } )
				final Img< FloatType > img = (Img) input;
				gauss = new GaussFloat( sigma, img );
			}
			else
			{
				final RandomAccessibleInterval<FloatType> rIn = new ConvertedIterableRandomAccessibleInterval< T, FloatType, Img<T> >( input, new RealFloatSamplerConverter<T>());
				gauss = new GaussFloat( sigma, Views.extend( rIn, outofbounds ), input, input.factory().imgFactory( new FloatType() ) );
			}
		}
		catch (final IncompatibleTypeException e)
		{
			return null;
		}

		gauss.call();

		return (Img<FloatType>)gauss.getResult();
	}

	/**
	 * Computes a Gaussian convolution in-place (temporary images are necessary) with float precision on an entire {@link Img}
	 * using the {@link OutOfBoundsMirrorFactory} with single boundary
	 *
	 * @param sigma - the sigma for the convolution
	 * @param input - the input {@link Img}
	 * @return the convolved image
	 */
	public static <T extends RealType<T>> void inFloatInPlace( final double[] sigma, final Img< T > image )
	{
		inFloatInPlace( sigma, image, new OutOfBoundsMirrorFactory< FloatType, RandomAccessibleInterval< FloatType > >( Boundary.SINGLE ) );
	}

	/**
	 * Computes a Gaussian convolution in-place (temporary images are necessary) with float precision on an entire {@link Img}
	 *
	 * @param sigma - the sigma for the convolution
	 * @param image - the image {@link Img} that will be convolved in place
	 */
	public static <T extends RealType<T>> void inFloatInPlace( final double[] sigma, final Img< T > image,
			final OutOfBoundsFactory< FloatType, RandomAccessibleInterval< FloatType > > outofbounds )
	{
		GaussFloat gauss = null;
		try
		{
			if ( FloatType.class.isInstance( image.firstElement() ) )
			{
				@SuppressWarnings( { "rawtypes", "unchecked" } )
				final Img< FloatType > img = (Img) image;
				gauss = new GaussFloat( sigma, Views.extend( img, outofbounds ), image, img, new Location( sigma.length ), image.factory().imgFactory( new FloatType() ) );
			}
			else
			{
				final RandomAccessibleInterval<FloatType> rIn = new ConvertedIterableRandomAccessibleInterval< T, FloatType, Img<T> >( image, new RealFloatSamplerConverter<T>());
				gauss = new GaussFloat( sigma, Views.extend( rIn, outofbounds ), image, rIn, new Location( sigma.length ), image.factory().imgFactory( new FloatType() ) );
			}
		}
		catch (final IncompatibleTypeException e)
		{
			return;
		}

		gauss.call();
	}
}
