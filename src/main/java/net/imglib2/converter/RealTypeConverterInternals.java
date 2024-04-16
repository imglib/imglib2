/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2024 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

import net.imglib2.loops.ClassCopyProvider;
import net.imglib2.type.BooleanType;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.GenericByteType;
import net.imglib2.type.numeric.integer.GenericIntType;
import net.imglib2.type.numeric.integer.GenericShortType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.LongType;
import net.imglib2.type.numeric.real.FloatType;

import java.util.Arrays;

/**
 * Implementation of {@link RealTypeConverters#getConverter(RealType, RealType)}.
 */
class RealTypeConverterInternals
{
	static < S extends RealType< ? >, T extends RealType< ? > > Converter< S, T > getConverter( S inputType, T outputType )
	{
		final RealTypeConverterInternals.ConverterFactory factory = RealTypeConverterInternals.getConverterFactory( inputType, outputType );
		return factory.create( inputType, outputType );
	}

	private static ConverterFactory TYPE_IDENTITY = new ConverterFactory( TypeIdentity.class );

	private static ConverterFactory DOUBLE = new ConverterFactory( DoubleConverter.class );

	private static ConverterFactory FLOAT = new ConverterFactory( FloatConverter.class );

	private static ConverterFactory INTEGER = new ConverterFactory( IntegerConverter.class );

	private static ConverterFactory LONG = new ConverterFactory( LongConverter.class );

	private static ConverterFactory BYTE = new ConverterFactory( ByteConverter.class );

	private static ConverterFactory SHORT = new ConverterFactory( ShortConverter.class );

	private static ConverterFactory BOOLEAN = new ConverterFactory( BooleanConverter.class );

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
		// This method is package-private to allow testing.
		final boolean lowerBound = variable.getMinValue() >= Integer.MIN_VALUE;
		final boolean upperBound = variable.getMaxValue() <= Integer.MAX_VALUE;
		return lowerBound && upperBound;
	}

	/**
	 * The class {@link ConverterFactory} encapsulates the {@link ClassCopyProvider}
	 * that is used to create a {@link Converter}.
	 */
	private static class ConverterFactory
	{
		private final ClassCopyProvider< Converter > provider;

		private ConverterFactory( Class< ? extends Converter > converterClass )
		{
			this.provider = new ClassCopyProvider<>( converterClass, Converter.class );
		}

		public Converter create( RealType< ? > in, RealType< ? > out )
		{
			return provider.newInstanceForKey( Arrays.asList( in.getClass(), out.getClass() ) );
		}
	}

	/** Converts via {@link RealType#getRealDouble()}. */
	public static class DoubleConverter implements Converter< RealType< ? >, RealType< ? > >
	{
		// This class is public, in order that it can be used with ClassCopyProvider.
		@Override
		public void convert( RealType< ? > input, RealType< ? > output )
		{
			output.setReal( input.getRealDouble() );
		}
	}

	/** Converts via {@link RealType#getRealFloat()}. */
	public static class FloatConverter implements Converter< RealType< ? >, RealType< ? > >
	{
		// This class is public, in order that it can be used with ClassCopyProvider.
		@Override
		public void convert( RealType< ? > input, RealType< ? > output )
		{
			output.setReal( input.getRealFloat() );
		}
	}

	/** Converts via {@link IntegerType#getInteger()}. */
	public static class IntegerConverter implements Converter< IntegerType< ? >, IntegerType< ? > >
	{
		// This class is public, in order that it can be used with ClassCopyProvider.
		@Override
		public void convert( IntegerType< ? > input, IntegerType< ? > output )
		{
			output.setInteger( input.getInteger() );
		}
	}

	/** Converts via {@link IntegerType#getInteger()}. */
	public static class LongConverter implements Converter< IntegerType< ? >, IntegerType< ? > >
	{
		// This class is public, in order that it can be used with ClassCopyProvider.
		@Override
		public void convert( IntegerType< ? > input, IntegerType< ? > output )
		{
			output.setInteger( input.getIntegerLong() );
		}
	}

	/** Converts via {@link GenericByteType#getByte()}. */
	public static class ByteConverter implements Converter< GenericByteType< ? >, GenericByteType< ? > >
	{
		// This class is public, in order that it can be used with ClassCopyProvider.
		@Override
		public void convert( GenericByteType< ? > input, GenericByteType< ? > output )
		{
			output.setByte( input.getByte() );
		}
	}

	/** Converts void {@link GenericShortType#getShort()}. */
	public static class ShortConverter implements Converter< GenericShortType< ? >, GenericShortType< ? > >
	{
		// This class is public, in order that it can be used with ClassCopyProvider.
		@Override
		public void convert( GenericShortType< ? > input, GenericShortType< ? > output )
		{
			output.setShort( input.getShort() );
		}
	}

	/** Converts via {@link BooleanType#get()}. */
	public static class BooleanConverter implements Converter< BooleanType< ? >, BooleanType< ? > >
	{
		// This class is public, in order that it can be used with ClassCopyProvider.
		@Override
		public void convert( BooleanType< ? > input, BooleanType< ? > output )
		{
			output.set( input.get() );
		}
	}
}
