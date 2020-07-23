/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2020 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package net.imglib2.type.numeric.integer;

import net.imglib2.type.logic.BitType;
import net.imglib2.type.logic.BoolType;
import net.imglib2.type.numeric.IntegerType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

/**
 * Test basic functionality of every NumericType implementation.
 * <p>
 * It's a parameterized test, see <a href=
 * "https://github.com/junit-team/junit4/wiki/parameterized-tests">Junit4
 * Parameterized Test</a>. The test is executed for every type in the list
 * {@link IntegerTypeTest#types}.
 *
 * @param <T>
 */
@RunWith( Parameterized.class )
public class IntegerTypeTest< T extends IntegerType< T > >
{
	private static final List< IntegerType< ? > > types = Arrays.asList(
			new BitType(),
			new BoolType(),
			new ByteType(),
			new IntType(),
			new LongType(),
			new ShortType(),
			new UnsignedByteType(),
			new UnsignedIntType(),
			new UnsignedLongType(),
			new UnsignedShortType(),
			new Unsigned128BitType(),
			new Unsigned2BitType(),
			new Unsigned4BitType(),
			new Unsigned12BitType(),
			new UnsignedVariableBitLengthType( 7 )
	);

	private final T type;

	private final List< BigInteger > values;

	// NB: The class is parameterized with pairs of (className, numeric type)
	// className is there for nicer error messages when a test fails.
	@Parameterized.Parameters( name = "{0}" )
	public static Collection< Object > data()
	{
		return types.stream().map(
				type -> new Object[] { type.getClass().getSimpleName(), type }
		).collect( Collectors.toList() );
	}

	public IntegerTypeTest( final String className, final T type )
	{
		this.type = type;
		this.values = initValues();
	}

	/**
	 * Return a list of positive powers of two, and negative powers of two.
	 * For example: 1, 2, 4, 8, -1, -2, -4, -8.
	 * But only contains numbers that are in the allow range between min
	 * value and max value.
	 */
	private List< BigInteger > initValues()
	{
		final List< BigInteger > values = new ArrayList<>();
		final boolean isSigned = type.getMinValue() < 0;
		final int bits = type.getBitsPerPixel() - (isSigned ? 1 : 0);

		values.addAll( powersOfTwo( bits ) );
		if(isSigned)
			values.addAll( negate( powersOfTwo( bits ) ) );

		return values;
	}

	/** Returns a list: 1, 2, 4, 8, ... , 2^(count - 1). */
	private List< BigInteger > powersOfTwo( int count ) {
		BigInteger value  = BigInteger.ONE;
		List<BigInteger> results = new ArrayList<>( count );
		for ( int i = 0; i < count; i++, value.multiply( BigInteger.valueOf( 2 ) ) )
			results.add( value );
		return results;
	}

	/** Return a list that contains the negated values of the given list. */
	private List< BigInteger > negate( List<BigInteger> input )
	{
		return input.stream().map( BigInteger::negate ).collect(Collectors.toList());
	}


	@Test
	public void testGetAndSetBigInteger() {
		T type = zero();
		BigInteger value = toBigInteger( type.getMaxValue() * 0.9 );
		type.setBigInteger( value );
		assertEquals( value, type.getBigInteger() );
	}

	@Test
	public void testToString() {
		for ( BigInteger value : values )
		{
			final T variable = zero();
			variable.setBigInteger( value );
			assertEquals( value.toString(), value.toString() );
		}
	}


	@Test
	public void testGetReal() {
		for ( BigInteger value : values )
		{
			final T variable = zero();
			variable.setBigInteger( value );
			assertEquals( value.doubleValue(), variable.getRealDouble(), 0);
			assertEquals( value.floatValue(), variable.getRealFloat(), 0);
		}
	}

	@Test
	public void testSetRealDouble() {
		for ( BigInteger value : values )
		{
			final T variable = zero();
			variable.setReal( value.doubleValue() );
			assertEquals( value.doubleValue(), variable.getRealDouble(), 0 );
		}
	}

	@Test
	public void testSetRealFloat() {
		for ( BigInteger value : values )
		{
			final T variable = zero();
			variable.setReal( value.floatValue() );
			assertEquals( value.floatValue(), variable.getRealFloat(), 0 );
		}
	}

	private BigInteger toBigInteger( double v )
	{
		return BigDecimal.valueOf( v ).toBigInteger();
	}

	private T zero()
	{
		return this.type.createVariable();
	}
}
