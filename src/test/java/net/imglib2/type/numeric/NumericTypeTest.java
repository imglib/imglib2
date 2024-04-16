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

package net.imglib2.type.numeric;

import net.imglib2.type.BooleanType;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.logic.BoolType;
import net.imglib2.type.numeric.complex.ComplexDoubleType;
import net.imglib2.type.numeric.complex.ComplexFloatType;
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
import net.imglib2.type.numeric.integer.UnsignedVariableBitLengthType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.type.volatiles.VolatileARGBType;
import net.imglib2.type.volatiles.VolatileByteType;
import net.imglib2.type.volatiles.VolatileDoubleType;
import net.imglib2.type.volatiles.VolatileFloatType;
import net.imglib2.type.volatiles.VolatileIntType;
import net.imglib2.type.volatiles.VolatileLongType;
import net.imglib2.type.volatiles.VolatileNumericType;
import net.imglib2.type.volatiles.VolatileRealType;
import net.imglib2.type.volatiles.VolatileShortType;
import net.imglib2.type.volatiles.VolatileUnsignedByteType;
import net.imglib2.type.volatiles.VolatileUnsignedIntType;
import net.imglib2.type.volatiles.VolatileUnsignedLongType;
import net.imglib2.type.volatiles.VolatileUnsignedShortType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assume.assumeFalse;
import static org.junit.Assume.assumeTrue;

/**
 * Test basic functionality of every NumericType implementation.
 * <p>
 * It's a parameterized test, see <a href=
 * "https://github.com/junit-team/junit4/wiki/parameterized-tests">Junit4
 * Parameterized Test</a>. The test is executed for every type in the list
 * {@link NumericTypeTest#numericTypes}.
 *
 * @param <T>
 */
@RunWith( Parameterized.class )
public class NumericTypeTest< T extends NumericType< T > >
{
	private static final List< NumericType< ? > > numericTypes = Arrays.asList(
			new ARGBDoubleType(),
			new ARGBType(),
			new BitType(),
			new BoolType(),
			new ByteType(),
			new ComplexDoubleType(),
			new ComplexFloatType(),
			new DoubleType(),
			new FloatType(),
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
			new UnsignedVariableBitLengthType( 7 ),
			new VolatileARGBType(),
			new VolatileByteType(),
			new VolatileDoubleType(),
			new VolatileFloatType(),
			new VolatileIntType(),
			new VolatileLongType(),
			new VolatileShortType(),
			new VolatileUnsignedByteType(),
			new VolatileUnsignedIntType(),
			new VolatileUnsignedLongType(),
			new VolatileUnsignedShortType(),
			new VolatileNumericType<>( new DoubleType() ),
			new VolatileRealType<>( new DoubleType() )
	);

	private final T type;

	// NB: The class is parameterized with pairs of (className, numeric type)
	// className is there for nicer error messages when a test fails.
	@Parameterized.Parameters( name = "{0}" )
	public static Collection< Object > data()
	{
		return numericTypes.stream().map(
				type -> new Object[] { type.getClass().getSimpleName(), type }
		).collect( Collectors.toList() );
	}

	public NumericTypeTest( final String className, final T type )
	{
		this.type = type;
	}

	@Test
	public void testValueEquals()
	{
		final boolean result = newOne().valueEquals( newOne() );
		assumeTrue( result );
	}

	@Test
	public void testNotValueEquals()
	{
		final boolean result = newZero().valueEquals( newOne() );
		assumeFalse( result );
	}

	@Test
	public void testEquals()
	{
		final boolean result = newOne().equals( newOne() );
		assertTrue( result );
	}

	@Test
	public void testNotEqual()
	{
		final boolean result = newOne().equals( newZero() );
		assertFalse( result );
	}

	@Test
	public void testSet()
	{
		final T a = newZero();
		final T b = newOne();
		a.set( b );
		assertEquals( b, a );
	}

	@Test
	public void testOneMinusOne()
	{
		final T value = newOne();
		value.sub( newOne() );
		assertEquals( newZero(), value );
	}

	@Test
	public void testAdd()
	{
		if ( isMaxValueLessThanSix() )
			return;

		final T value = newNumber( 3 );
		value.add( newNumber( 2 ) );
		assertEquals( newNumber( 5 ), value );
	}

	@Test
	public void testSub()
	{
		if ( isMaxValueLessThanSix() )
			return;

		final T value = newNumber( 5 );
		value.sub( newNumber( 3 ) );
		assertEquals( newNumber( 2 ), value );
	}

	@Test
	public void testMulNumerivType()
	{
		if ( isMaxValueLessThanSix() )
			return;
		final T value = newNumber( 3 );
		value.mul( newNumber( 2 ) );
		assertEquals( newNumber( 6 ), value );
	}

	@Test
	public void testMulDouble()
	{
		if ( isMaxValueLessThanSix() )
			return;
		final T value = newNumber( 3 );
		value.mul( 2.0 );
		assertEquals( newNumber( 6 ), value );
	}

	@Test
	public void testMulFloat()
	{
		if ( isMaxValueLessThanSix() )
			return;

		final T value = newNumber( 3 );
		value.mul( 2.0f );
		assertEquals( newNumber( 6 ), value );
	}

	@Test
	public void testDiv()
	{
		if ( isMaxValueLessThanSix() )
			return;
		final T value = newNumber( 6 );
		value.div( newNumber( 2 ) );
		assertEquals( newNumber( 3 ), value );
	}

	@Test
	public void testHashCodeEquals()
	{
		final int hashA = newOne().hashCode();
		final int hashB = newOne().hashCode();
		assertEquals( hashA, hashB );
	}

	/*
	 * NB: This test is here as a sanity check. In principle, there may be hash
	 * collision between 0 and 1 values. In practice, for the current
	 * implementations there never is. (If there is ever a good reason to change
	 * that, this test can be removed.)
	 */
	@Test
	public void testHashCodeChanges()
	{
		final T variable = newZero();
		final int hashZero = variable.hashCode();
		variable.set( newOne() );
		final int hashOne = variable.hashCode();
		assertNotEquals( hashZero, hashOne );
	}

	@Test
	public void testCompareTo()
	{
		final T t = newZero();
		if ( ( t instanceof Comparable ) && !( t instanceof BooleanType ) )
			testCompareTo( ( Comparable ) newZero(), ( Comparable ) newOne(), ( Comparable ) newOne() );
	}

	private < T extends Comparable< T > > void testCompareTo( final T one, final T two, final T anotherOne )
	{
		assertTrue( one.compareTo( two ) < 0 );
		assertTrue( two.compareTo( one ) > 0 );
		assertEquals( 0, two.compareTo( anotherOne ) );
	}

	@Test
	public void testCompareMinMax()
	{
		if ( newZero() instanceof RealType )
			testCompareMinMax( ( RealType ) newOne() );
	}

	private < T extends RealType< T > > void testCompareMinMax( final T one )
	{
		// NB: This ensures that comparison for unsigned types is correctly implemented.
		final T minValue = minValue( one );
		final T maxValue = maxValue( one );
		assertTrue( minValue.compareTo( maxValue ) < 0 );
	}

	private < T extends RealType< T > > T maxValue( final T one )
	{
		if ( one instanceof Unsigned128BitType )
			// NB: Max value for Unsigned128BitType, is not returned with required precision by one.getMaxValue().
			return ( T ) new Unsigned128BitType( 0xffffffffffffffffL, 0xffffffffffffffffL );
		final T maxValue = one.copy();
		maxValue.mul( one.getMaxValue() );
		return maxValue;
	}

	private < T extends RealType< T > > T minValue( final T one )
	{
		final T minValue = one.copy();
		minValue.mul( one.getMinValue() );
		return minValue;
	}

	// -- Helper methods --

	private T newZero()
	{
		final T zero = type.createVariable();
		zero.setZero();
		return zero;
	}

	private T newOne()
	{
		final T one = type.createVariable();
		one.setOne();
		return one;
	}

	private boolean isMaxValueLessThanSix()
	{
		return type instanceof BooleanType
				|| type instanceof Unsigned2BitType;
	}

	private T newNumber( final int value )
	{
		if ( value < 0 )
			throw new AssertionError();
		final T result = newZero();
		final T one = newOne();
		for ( int i = 0; i < value; i++ )
			result.add( one );
		return result;
	}
}
