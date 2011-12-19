package net.imglib2.algorithm.gauss;

import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.Location;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.ConvertedIterableRandomAccessibleInterval;
import net.imglib2.converter.ConvertedRandomAccessible;
import net.imglib2.converter.sampler.RealDoubleSamplerConverter;
import net.imglib2.converter.sampler.RealFloatSamplerConverter;
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
	
	/**
	 * Computes a Gaussian convolution with float precision on an infinite {@link RandomAccessible}
	 * 
	 * @param sigma - the sigma for the convolution
	 * @param input - the input {@link RandomAccessible} (infinite -> Views.extend( ... ) )
	 * @param inputInterval - the interval which should be convolved
	 * @param output - the output {@link RandomAccessible} (input and output can be the same)
	 * @param origin - the origin in the output where the result should be placed
	 * @param imgFactory - the {@link ImgFactory} for {@link FloatType} which is needed for temporary images
	 */
	public static <T extends RealType<T>> void inFloat( final double[] sigma, final RandomAccessible< T > input, final Interval inputInterval, 
			final RandomAccessible< T > output, final Localizable origin, final ImgFactory< FloatType > imgFactory )
	{
		// find out if it is a FloatType, therefore we get the first value defined by the
		// interval in the input
		final long[] tmpCoordinate = new long[ input.numDimensions() ];
		inputInterval.min( tmpCoordinate );
		final RandomAccess<T> tmp = input.randomAccess();
		tmp.setPosition( tmpCoordinate );
		
		if ( FloatType.class.isInstance( tmp.get() ) )
		{
			@SuppressWarnings( { "rawtypes", "unchecked" } )
			final RandomAccessible< FloatType > rIn = (RandomAccessible) input;
			@SuppressWarnings( { "rawtypes", "unchecked" } )
			final RandomAccessible< FloatType > rOut = (RandomAccessible) output;
			
			new GaussFloat( sigma, rIn, inputInterval, rOut, origin, imgFactory );			
		}
		else
		{
			final RandomAccessible<FloatType> rIn = new ConvertedRandomAccessible< T, FloatType >( input, new RealFloatSamplerConverter<T>());
			final RandomAccessible<FloatType> rOut = new ConvertedRandomAccessible< T, FloatType >( output, new RealFloatSamplerConverter<T>());
			
			new GaussFloat( sigma, rIn, inputInterval, rOut, origin, imgFactory );
		}
	}

	/**
	 * Computes a Gaussian convolution with double precision on an entire {@link Img} using the {@link OutOfBoundsMirrorFactory} with single boundary
	 *
	 * @param sigma - the sigma for the convolution
	 * @param input - the input {@link Img}
	 * @return the convolved image
	 */
	public static <T extends RealType<T>> Img<DoubleType> inDouble( final double[] sigma, final Img< T > input )
	{
		return inDouble( sigma, input, new OutOfBoundsMirrorFactory< DoubleType, RandomAccessibleInterval< DoubleType > >( Boundary.SINGLE ) );
	}

	/**
	 * Computes a Gaussian convolution with double precision on an entire {@link Img}
	 *
	 * @param sigma - the sigma for the convolution
	 * @param input - the input {@link Img}
	 * @param outofbounds - the {@link OutOfBoundsFactory}
	 * @return the convolved image
	 */
	public static <T extends RealType<T>> Img<DoubleType> inDouble( final double[] sigma, final Img< T > input,
			final OutOfBoundsFactory< DoubleType, RandomAccessibleInterval< DoubleType > > outofbounds )
	{
		GaussDouble gauss = null;
		try
		{
			if ( DoubleType.class.isInstance( input.firstElement() ) )
			{
				@SuppressWarnings( { "rawtypes", "unchecked" } )
				final Img< DoubleType > img = (Img) input;
				gauss = new GaussDouble( sigma, img );
			}
			else
			{
				final RandomAccessibleInterval<DoubleType> rIn = new ConvertedIterableRandomAccessibleInterval< T, DoubleType, Img<T> >( input, new RealDoubleSamplerConverter<T>());
				gauss = new GaussDouble( sigma, Views.extend( rIn, outofbounds ), input, input.factory().imgFactory( new DoubleType() ) );
			}
		}
		catch (final IncompatibleTypeException e)
		{
			return null;
		}

		gauss.call();

		return (Img<DoubleType>)gauss.getResult();
	}

	/**
	 * Computes a Gaussian convolution in-place (temporary images are necessary) with double precision on an entire {@link Img}
	 * using the {@link OutOfBoundsMirrorFactory} with single boundary
	 *
	 * @param sigma - the sigma for the convolution
	 * @param input - the input {@link Img}
	 * @return the convolved image
	 */
	public static <T extends RealType<T>> void inDoubleInPlace( final double[] sigma, final Img< T > image )
	{
		inDoubleInPlace( sigma, image, new OutOfBoundsMirrorFactory< DoubleType, RandomAccessibleInterval< DoubleType > >( Boundary.SINGLE ) );
	}

	/**
	 * Computes a Gaussian convolution in-place (temporary images are necessary) with double precision on an entire {@link Img}
	 *
	 * @param sigma - the sigma for the convolution
	 * @param image - the image {@link Img} that will be convolved in place
	 */
	public static <T extends RealType<T>> void inDoubleInPlace( final double[] sigma, final Img< T > image,
			final OutOfBoundsFactory< DoubleType, RandomAccessibleInterval< DoubleType > > outofbounds )
	{
		GaussDouble gauss = null;
		try
		{
			if ( DoubleType.class.isInstance( image.firstElement() ) )
			{
				@SuppressWarnings( { "rawtypes", "unchecked" } )
				final Img< DoubleType > img = (Img) image;
				gauss = new GaussDouble( sigma, Views.extend( img, outofbounds ), image, img, new Location( sigma.length ), image.factory().imgFactory( new DoubleType() ) );
			}
			else
			{
				final RandomAccessibleInterval<DoubleType> rIn = new ConvertedIterableRandomAccessibleInterval< T, DoubleType, Img<T> >( image, new RealDoubleSamplerConverter<T>());
				gauss = new GaussDouble( sigma, Views.extend( rIn, outofbounds ), image, rIn, new Location( sigma.length ), image.factory().imgFactory( new DoubleType() ) );
			}
		}
		catch (final IncompatibleTypeException e)
		{
			return;
		}

		gauss.call();
	}
	
	/**
	 * Computes a Gaussian convolution with double precision on an infinite {@link RandomAccessible}
	 * 
	 * @param sigma - the sigma for the convolution
	 * @param input - the input {@link RandomAccessible} (infinite -> Views.extend( ... ) )
	 * @param inputInterval - the interval which should be convolved
	 * @param output - the output {@link RandomAccessible} (input and output can be the same)
	 * @param origin - the origin in the output where the result should be placed
	 * @param imgFactory - the {@link ImgFactory} for {@link DoubleType} which is needed for temporary images
	 */
	public static <T extends RealType<T>> void inDouble( final double[] sigma, final RandomAccessible< T > input, final Interval inputInterval, 
			final RandomAccessible< T > output, final Localizable origin, final ImgFactory< DoubleType > imgFactory )
	{
		// find out if it is a DoubleType, therefore we get the first value defined by the
		// interval in the input
		final long[] tmpCoordinate = new long[ input.numDimensions() ];
		inputInterval.min( tmpCoordinate );
		final RandomAccess<T> tmp = input.randomAccess();
		tmp.setPosition( tmpCoordinate );
		
		if ( DoubleType.class.isInstance( tmp.get() ) )
		{
			@SuppressWarnings( { "rawtypes", "unchecked" } )
			final RandomAccessible< DoubleType > rIn = (RandomAccessible) input;
			@SuppressWarnings( { "rawtypes", "unchecked" } )
			final RandomAccessible< DoubleType > rOut = (RandomAccessible) output;
			
			new GaussDouble( sigma, rIn, inputInterval, rOut, origin, imgFactory );			
		}
		else
		{
			final RandomAccessible<DoubleType> rIn = new ConvertedRandomAccessible< T, DoubleType >( input, new RealDoubleSamplerConverter<T>());
			final RandomAccessible<DoubleType> rOut = new ConvertedRandomAccessible< T, DoubleType >( output, new RealDoubleSamplerConverter<T>());
			
			new GaussDouble( sigma, rIn, inputInterval, rOut, origin, imgFactory );
		}
	}

	// here
	
	/**
	 * Computes a Gaussian convolution with the precision of the type provided 
	 * on an entire {@link Img} using the {@link OutOfBoundsMirrorFactory} with single boundary
	 *
	 * @param sigma - the sigma for the convolution
	 * @param input - the input {@link Img}
	 * @return the convolved image
	 */
	public static <T extends NumericType<T>> Img<T> inNumericType( final double[] sigma, final Img< T > input )
	{
		return inNumericType( sigma, input, new OutOfBoundsMirrorFactory< T, RandomAccessibleInterval< T > >( Boundary.SINGLE ) );
	}

	/**
	 * Computes a Gaussian convolution with the precision of the type provided on an entire {@link Img}
	 *
	 * @param sigma - the sigma for the convolution
	 * @param input - the input {@link Img}
	 * @param outofbounds - the {@link OutOfBoundsFactory}
	 * @return the convolved image
	 */
	public static <T extends NumericType<T>> Img<T> inNumericType( final double[] sigma, final Img< T > input,
			final OutOfBoundsFactory< T, RandomAccessibleInterval< T > > outofbounds )
	{
		final Img< T > output = input.factory().create( input, input.firstElement() );
		inNumericType( sigma, Views.extend( input, outofbounds ), input, output, new Location( sigma.length ), input.factory() );
		return output;
	}

	/**
	 * Computes a Gaussian convolution in-place (temporary images are necessary) with the precision of the type provided on an entire {@link Img}
	 * using the {@link OutOfBoundsMirrorFactory} with single boundary
	 *
	 * @param sigma - the sigma for the convolution
	 * @param input - the input {@link Img}
	 * @return the convolved image
	 */
	public static <T extends NumericType<T>> void inNumericTypeInPlace( final double[] sigma, final Img< T > image )
	{
		inNumericTypeInPlace( sigma, image, new OutOfBoundsMirrorFactory< T, RandomAccessibleInterval< T > >( Boundary.SINGLE ) );
	}

	/**
	 * Computes a Gaussian convolution in-place (temporary images are necessary) with the precision of the type provided on an entire {@link Img}
	 *
	 * @param sigma - the sigma for the convolution
	 * @param image - the image {@link Img} that will be convolved in place
	 */
	public static <T extends NumericType<T>> void inNumericTypeInPlace( final double[] sigma, final Img< T > image,
			final OutOfBoundsFactory< T, RandomAccessibleInterval< T > > outofbounds )
	{
		inNumericType( sigma, Views.extend( image, outofbounds ), image, image, new Location( sigma.length ), image.factory() );
	}
	
	/**
	 * Computes a Gaussian convolution with the precision of the type provided on an infinite {@link RandomAccessible}
	 * 
	 * @param sigma - the sigma for the convolution
	 * @param input - the input {@link RandomAccessible} (infinite -> Views.extend( ... ) )
	 * @param inputInterval - the interval which should be convolved
	 * @param output - the output {@link RandomAccessible} (input and output can be the same)
	 * @param origin - the origin in the output where the result should be placed
	 * @param imgFactory - the {@link ImgFactory} for T which is needed for temporary images
	 */
	@SuppressWarnings("rawtypes")
	public static <T extends NumericType<T>> void inNumericType( final double[] sigma, final RandomAccessible< T > input, final Interval inputInterval, 
			final RandomAccessible< T > output, final Localizable origin, final ImgFactory< T > imgFactory )
	{
		// find out if it is a NativeType, therefore we get the first value defined by the
		// interval in the input - we also need it in this case for the convolution
		final long[] tmpCoordinate = new long[ input.numDimensions() ];
		inputInterval.min( tmpCoordinate );
		final RandomAccess<T> tmp = input.randomAccess();
		tmp.setPosition( tmpCoordinate );
		
		if ( NativeType.class.isInstance( tmp.get() ) )
		{
			// we have to call it in an extra method because we cannot cast to (NumericType<T> & NativeType<T>)
			computeInNativeType( sigma, (RandomAccessible)input, inputInterval, (RandomAccessible)output, origin, (ImgFactory)imgFactory, tmp.get() );
		}
		else
		{
			final GaussGeneral< T > gauss = new GaussGeneral< T >( sigma, input, inputInterval, output, origin, imgFactory, tmp.get() );
			gauss.call();
		}
	}
	
	private static final < T extends NumericType< T > & NativeType< T > > void computeInNativeType( final double[] sigma, final RandomAccessible< T > input, final Interval inputInterval, 
			final RandomAccessible< T > output, final Localizable origin, final ImgFactory< T > imgFactory, @SuppressWarnings("rawtypes") final Type type )
	{
		@SuppressWarnings("unchecked")
		final GaussNativeType< T > gauss = new GaussNativeType< T >( sigma, input, inputInterval, output, origin, imgFactory, (T)type );
		gauss.call();
	}
}
