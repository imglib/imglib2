/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2022 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.test.ImgLib2Assert;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.logic.BoolType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.ByteType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.LongType;
import net.imglib2.type.numeric.integer.ShortType;
import net.imglib2.type.numeric.integer.Unsigned128BitType;
import net.imglib2.type.numeric.integer.Unsigned12BitType;
import net.imglib2.type.numeric.integer.Unsigned2BitType;
import net.imglib2.type.numeric.integer.Unsigned4BitType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedIntType;
import net.imglib2.type.numeric.integer.UnsignedLongType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test {@link RealTypeConverterInternals}.
 *
 * @author Matthias Arzt
 */
public class RealTypeConverterInternalsTest
{
	public static final List< RealType< ? > > TYPES = Arrays.asList(
			new BitType(),
			new BoolType(),
			new ByteType(),
			new ShortType(),
			new IntType(),
			new LongType(),
			new Unsigned2BitType(),
			new Unsigned4BitType(),
			new Unsigned12BitType(),
			new UnsignedByteType(),
			new UnsignedShortType(),
			new UnsignedIntType(),
			new UnsignedLongType(),
			new Unsigned128BitType(),
			new FloatType(),
			new DoubleType()
	);

	private static final float HIGHEST_INTEGER_CORRECTLY_REPRESENTED_BY_FLOAT = ( float ) Math.pow( 2, 24 );

	private static final float FLOAT_PRECISION = ( float ) Math.pow( 2.0, -24 );

	@Test
	public void testConvertUnsignedByteToFloatType()
	{
		UnsignedByteType in = new UnsignedByteType( 42 );
		FloatType out = new FloatType();
		Converter< UnsignedByteType, FloatType > converter = RealTypeConverters.getConverter( in, out );
		converter.convert( in, out );
		assertEquals( 42, out.getRealFloat(), 0 );
	}

	@Test
	public void testConvertImportantValuesBetweenAllTypes()
	{
		for ( RealType< ? > in : TYPES )
			for ( RealType< ? > out : TYPES )
			{
				Converter< RealType< ? >, RealType< ? > > converter = RealTypeConverters.getConverter( in, out );
				testMinValueConversion( converter, in.createVariable(), out.createVariable() );
				testMaxValueConversion( converter, in.createVariable(), out.createVariable() );
				testValueConversion( 0.0, converter, in, out );
				testValueConversion( 1.0, converter, in, out );
			}
	}

	private void testMinValueConversion( Converter< RealType< ? >, RealType< ? > > converter, RealType< ? > in, RealType< ? > out )
	{
		double value = Math.max( in.getMinValue(), out.getMinValue() );
		testValueConversion( value, converter, in, out );
	}

	private void testMaxValueConversion( Converter< RealType< ? >, RealType< ? > > converter, RealType< ? > in, RealType< ? > out )
	{
		final double inMaxValue = in.getMaxValue();
		final double outMaxValue = out.getMaxValue();
		double value = decreaseMaxValue( Math.min( inMaxValue, outMaxValue ) );
		testValueConversion( value, converter, in, out );
	}

	private double decreaseMaxValue( double value )
	{
		// NB: If an integer value is two high it cannot be represented
		// correctly by float. This tends to round up Integer.MAX_VALUE
		// Long.MAX_VALUE ... etc. The consequence is an integer overflow.
		// The value is decreased by the float precision, to compensate
		// for the rounding up.
		if ( value <= HIGHEST_INTEGER_CORRECTLY_REPRESENTED_BY_FLOAT )
			return value;
		return value * ( 1.0 - FLOAT_PRECISION );
	}

	private void testValueConversion( double value, Converter< RealType< ? >, RealType< ? > > converter, RealType< ? > in,
			RealType< ? > out )
	{
		in.setReal( value );
		converter.convert( in, out );
		double delta = value * FLOAT_PRECISION;
		assertEquals( "Conversion of value: " + value +
				" from: " + in.getClass().getSimpleName() + " (" + in + ")" +
				" to: " + out.getClass().getSimpleName() + " (" + out + ")",
				in.getRealDouble(), out.getRealDouble(), delta );
	}

	@Test
	public void testSmallerThanInt()
	{
		assertTrue( RealTypeConverterInternals.smallerThanInt( new IntType() ) );
		assertFalse( RealTypeConverterInternals.smallerThanInt( new UnsignedIntType() ) );
		assertFalse( RealTypeConverterInternals.smallerThanInt( new UnsignedLongType() ) );
		assertFalse( RealTypeConverterInternals.smallerThanInt( new LongType() ) );
		assertFalse( RealTypeConverterInternals.smallerThanInt( new Unsigned128BitType() ) );
	}

	@Test
	public void testTypeIdentityConverters()
	{
		for ( RealType< ? > type : TYPES )
			testConverterType( type.createVariable(), type.createVariable(), TypeIdentity.class );
	}

	@Test
	public void testOptimalConverterTypes()
	{
		testConverterType( new BoolType(), new BitType(), RealTypeConverterInternals.BooleanConverter.class );
		testConverterType( new UnsignedByteType(), new ByteType(), RealTypeConverterInternals.ByteConverter.class );
		testConverterType( new UnsignedShortType(), new ShortType(), RealTypeConverterInternals.ShortConverter.class );
		testConverterType( new UnsignedIntType(), new IntType(), RealTypeConverterInternals.IntegerConverter.class );
		testConverterType( new UnsignedLongType(), new LongType(), RealTypeConverterInternals.LongConverter.class );
		testConverterType( new Unsigned12BitType(), new LongType(), RealTypeConverterInternals.LongConverter.class );
		testConverterType( new Unsigned12BitType(), new UnsignedLongType(), RealTypeConverterInternals.LongConverter.class );
		testConverterType( new Unsigned12BitType(), new IntType(), RealTypeConverterInternals.IntegerConverter.class );
		testConverterType( new Unsigned12BitType(), new UnsignedIntType(), RealTypeConverterInternals.IntegerConverter.class );
		testConverterType( new Unsigned12BitType(), new ShortType(), RealTypeConverterInternals.LongConverter.class );
		testConverterType( new BoolType(), new FloatType(), RealTypeConverterInternals.FloatConverter.class );
		testConverterType( new BoolType(), new IntType(), RealTypeConverterInternals.IntegerConverter.class );
	}

	@Test
	public void testConvertRandomAccessibleInterval()
	{
		RandomAccessibleInterval< IntType > ints = ArrayImgs.ints( new int[] { 42 }, 1 );
		RandomAccessibleInterval< UnsignedByteType > bytes = RealTypeConverters.convert( ints, new UnsignedByteType() );
		RandomAccessibleInterval< UnsignedByteType > expected = ArrayImgs.unsignedBytes( new byte[] { 42 }, 1 );
		ImgLib2Assert.assertImageEquals( expected, bytes );
	}

	private void testConverterType( RealType< ? > inputType, RealType< ? > ouputType, Class< ? extends Converter > expected )
	{
		Converter< ?, ? > converter = RealTypeConverters.getConverter( inputType, ouputType );
		assertEquals( expected.getName(), converter.getClass().getName() );
	}
}
