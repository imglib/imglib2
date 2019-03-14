/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2018 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
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

package net.imglib2.converter;

import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.loops.ClassCopyProvider;
import net.imglib2.loops.LoopBuilder;
import net.imglib2.type.BooleanType;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.ByteType;
import net.imglib2.type.numeric.integer.GenericByteType;
import net.imglib2.type.numeric.integer.GenericIntType;
import net.imglib2.type.numeric.integer.GenericShortType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.LongType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Util;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;

import java.util.Arrays;

public final class RealTypeConverters
{

	private RealTypeConverters()
	{
		// prevent from instantiation
	}

	/**
	 * Copy the image content from a source image to a destination image.
	 * The area to be copied is defined by the destination.
	 * <p>
	 * Both images need to be of type {@link RealType}. So for example
	 * {@link UnsignedByteType}, {@link IntType}, {@link FloatType}
	 * and many more types are supported. A type conversion is done
	 * if needed.
	 *
	 * @param source Image that is source of the copy operation.
	 * @param destination Image that is destination of the copy operation.
	 */
	public static void copyFromTo(
			RandomAccessible< ? extends RealType< ? > > source,
			RandomAccessibleInterval< ? extends RealType< ? > > destination
	)
	{
		IntervalView< ? extends RealType< ? > > sourceInterval = Views.interval( source, destination );
		RealType< ? > s = Util.getTypeFromInterval( sourceInterval );
		RealType< ? > d = Util.getTypeFromInterval( destination );
		Converter< RealType< ? >, RealType< ? > > copy = getConverter( s, d );
		LoopBuilder.setImages( sourceInterval, destination ).forEachPixel( copy::convert );
	}

	/**
	 * Convert the pixel type of the given image to a given output pixel type.
	 * <p>
	 * Example, convert and image from {@link UnsignedByteType} to {@link IntType}:
	 * <pre>
	 * {@code
	 *
	 * RandomAccessibleInterval<UnsignedByteType> image = ...;
	 * RandomAccessibleInterval<IntType> intImage =
	 *     RealTypeConverters.convert( image, new IntType() );
	 * }
	 * </pre>
	 *
	 * The conversion is done on-the-fly when a pixel value is red.
	 * @param image image to convert
	 * @param pixelType pixel type of the result image
	 */
	public static <T extends RealType<T>> RandomAccessibleInterval<T> convert(
			RandomAccessibleInterval<? extends RealType<?>> image,
			T pixelType )
	{
		RealType<?> in = Util.getTypeFromInterval( image );
		Converter< RealType<?>, T > converter = getConverter( in, pixelType );
		return Converters.convert( image, converter, pixelType );
	}

	/**
	 * Returns a converter that converts from input to output type.
	 * <p>
	 * To get a converter from {@link UnsignedByteType} to {@link LongType} use:
	 * <p>
	 * {@code
	 * Converter< UnsignedByteType, LongType > converter =
	 * RealConverters.getConverter(new UnsignedByteType(), new LongType());
	 * }
	 * <p>
	 * The functionality is equivalent to {@link RealDoubleConverter}.
	 * But the returned converter is faster and has higher numerical precision
	 * than {@link RealDoubleConverter}.
	 * This is because it's optimal for the given pair of types.
	 * A converter from {@link UnsignedByteType} to {@link ByteType}
	 * will for example copy the byte directly and never convert to double.
	 */
	public static < S extends RealType< ? >, T extends RealType< ? > > Converter< S, T > getConverter( S inputType, T outputType )
	{
		final ConverterFactory factory = getConverterFactory( inputType, outputType );
		return factory.create( inputType, outputType );
	}

	private static ConverterFactory getConverterFactory( RealType< ? > inputType, RealType< ? > outputType )
	{
		if ( inputType.getClass().equals( outputType.getClass() ) )
			return TYPE_IDENTITY;
		if ( ( inputType instanceof IntegerType ) && ( outputType instanceof IntegerType ) )
			return integerConverterFactory( inputType, outputType );
		return floatingPointConverterFactory( inputType, outputType );
	}

	private static ConverterFactory floatingPointConverterFactory( RealType< ? > inputType, RealType< ? > outputType )
	{
		if ( ( inputType instanceof FloatType ) || ( outputType instanceof FloatType ) )
			return FLOAT;
		return DOUBLE;
	}

	private static ConverterFactory integerConverterFactory( RealType< ? > inputType, RealType< ? > outputType )
	{
		if ( inputType instanceof LongType || outputType instanceof LongType )
			return LONG;
		if ( inputType instanceof IntType || outputType instanceof IntType )
			return INTEGER;
		if ( inputType instanceof GenericShortType && outputType instanceof GenericShortType )
			return SHORT;
		if ( inputType instanceof GenericByteType && outputType instanceof GenericByteType )
			return BYTE;
		if ( inputType instanceof BooleanType && outputType instanceof BooleanType )
			return BOOLEAN;
		if ( inputType instanceof GenericIntType && smallerThanInt( outputType ) ||
				smallerThanInt( inputType ) && outputType instanceof GenericIntType )
			return INTEGER;
		return LONG;
	}

	static boolean smallerThanInt( RealType< ? > variable )
	{
		final boolean lowerBound = variable.getMinValue() >= Integer.MIN_VALUE;
		final boolean upperBound = variable.getMaxValue() <= Integer.MAX_VALUE;
		return lowerBound && upperBound;
	}

	private static ConverterFactory TYPE_IDENTITY = new ConverterFactory( TypeIdentity.class );

	private static ConverterFactory DOUBLE = new ConverterFactory( DoubleConverter.class );

	private static ConverterFactory FLOAT = new ConverterFactory( FloatConverter.class );

	private static ConverterFactory INTEGER = new ConverterFactory( IntegerConverter.class );

	private static ConverterFactory LONG = new ConverterFactory( LongConverter.class );

	private static ConverterFactory BYTE = new ConverterFactory( ByteConverter.class );

	private static ConverterFactory SHORT = new ConverterFactory( ShortConverter.class );

	private static ConverterFactory BOOLEAN = new ConverterFactory( BooleanConverter.class );

	/**
	 * The class {@link ConverterFactory} encapsulates the {@link ClassCopyProvider}
	 * that is used to create a {@link Converter}.
	 */
	private static class ConverterFactory
	{
		private final ClassCopyProvider< Converter > provider;

		ConverterFactory( Class< ? extends Converter > converterClass )
		{
			this.provider = new ClassCopyProvider<>( converterClass, Converter.class );
		}

		public Converter create( RealType< ? > in, RealType< ? > out )
		{
			return provider.newInstanceForKey( Arrays.asList( in.getClass(), out.getClass() ) );
		}
	}

	public static class DoubleConverter implements Converter< RealType< ? >, RealType< ? > >
	{
		@Override
		public void convert( RealType< ? > input, RealType< ? > output )
		{
			output.setReal( input.getRealDouble() );
		}
	}

	public static class FloatConverter implements Converter< RealType< ? >, RealType< ? > >
	{
		@Override
		public void convert( RealType< ? > input, RealType< ? > output )
		{
			output.setReal( input.getRealFloat() );
		}
	}

	public static class IntegerConverter implements Converter< IntegerType< ? >, IntegerType< ? > >
	{
		@Override
		public void convert( IntegerType< ? > input, IntegerType< ? > output )
		{
			output.setInteger( input.getInteger() );
		}
	}

	public static class LongConverter implements Converter< IntegerType< ? >, IntegerType< ? > >
	{
		@Override
		public void convert( IntegerType< ? > input, IntegerType< ? > output )
		{
			output.setInteger( input.getIntegerLong() );
		}
	}

	public static class ByteConverter implements Converter< GenericByteType< ? >, GenericByteType< ? > >
	{
		@Override
		public void convert( GenericByteType< ? > input, GenericByteType< ? > output )
		{
			output.setByte( input.getByte() );
		}
	}

	public static class ShortConverter implements Converter< GenericShortType< ? >, GenericShortType< ? > >
	{
		@Override
		public void convert( GenericShortType< ? > input, GenericShortType< ? > output )
		{
			output.setShort( input.getShort() );
		}
	}

	public static class BooleanConverter implements Converter< BooleanType< ? >, BooleanType< ? > >
	{
		@Override
		public void convert( BooleanType< ? > input, BooleanType< ? > output )
		{
			output.set( input.get() );
		}
	}
}
