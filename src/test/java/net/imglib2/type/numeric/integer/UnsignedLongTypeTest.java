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

package net.imglib2.type.numeric.integer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;

import org.junit.Test;

public class UnsignedLongTypeTest
{

	private UnsignedLongType u = new UnsignedLongType();

	private UnsignedLongType t = new UnsignedLongType();

	/** Tests that {@link UnsignedLongType#compareTo(UnsignedLongType)} works for
	 * comparing a positive number to a negative number and vice versa.
	 */
	@Test
	public void testComparePosNeg()
	{

		u.set( -1L );
		t.set( 1L );
		assertTrue( u.compareTo( t ) >= 1 );

		u.set( 9223372036854775807L );
		t.set( -9223372036854775808L );
		assertTrue( u.compareTo( t ) <= -1 );

		u.set( -109817491384701984L );
		t.set( 12L );
		assertTrue( u.compareTo( t ) >= 1 );

	}

	/** Tests that {@link UnsignedLongType#compareTo(UnsignedLongType)} works for
	 * comparing two negative numbers.
	 */
	@Test
	public void testCompareNegatives()
	{

		u.set( -9000L );
		t.set( -9000L );
		assertEquals( u.compareTo( t ), 0 );

		u.set( -16L );
		t.set( -10984012840123984L );
		assertTrue( u.compareTo( t ) >= 1 );

		u.set( -500L );
		t.set( -219L );
		assertTrue( u.compareTo( t ) <= -1 );

	}

	/** Tests that {@link UnsignedLongType#compareTo(UnsignedLongType)} works for
	 * comparing two positive numbers.
	 */
	@Test
	public void testComparePositives()
	{

		u.set( 100L );
		t.set( 100L );
		assertEquals( u.compareTo( t ), 0 );

		u.set( 3098080948019L );
		t.set( 1L );
		assertTrue( u.compareTo( t ) >= 1 );

		u.set( 199L );
		t.set( 299L );
		assertTrue( u.compareTo( t ) <= -1 );

	}

	/** Tests that {@link UnsignedLongType#compareTo(UnsignedLongType)} works
	 * when comparing values to zero.
	 */
	@Test
	public void testCompareZero()
	{

		u.set( 0L );
		t.set( 0L );
		assertEquals( u.compareTo( t ), 0 );

		u.set( -17112921L );
		t.set( 0L );
		assertTrue( u.compareTo( t ) >= 1 );

		u.set( 0L );
		t.set( 698L );
		assertTrue( u.compareTo( t ) <= -1 );

	}

	/**
	 * Tests {@link UnsignedLongType#UnsignedLongType(BigInteger)} works for out
	 * of range values.
	 */
	@Test
	public void testBIConstructor()
	{

		final BigInteger bi = new BigInteger( "ABCD14984904EFEFEFE4324904294D17A", 16 );
		final UnsignedLongType l = new UnsignedLongType( bi );

		assertEquals( bi.longValue(), l.get() );
	}

	/**
	 * Tests that {@link UnsignedLongType#getBigInteger()} returns the unsigned
	 * representation of an {@link UnsignedLongType} regardless of if it was
	 * constructed with a {@code long} or a {@code BigInteger}.
	 */
	@Test
	public void testGetBigInteger()
	{

		final BigInteger mask = new BigInteger( "FFFFFFFFFFFFFFFF", 16 );
		final BigInteger bi = new BigInteger( "DEAD12345678BEEF", 16 );
		final UnsignedLongType l = new UnsignedLongType( bi );

		assertEquals( bi.and( mask ), l.getBigInteger() );

		final UnsignedLongType l2 = new UnsignedLongType( -473194873871904l );

		assertEquals( BigInteger.valueOf( -473194873871904l ).and( mask ),
				l2.getBigInteger() );
	}

	/**
	 * Tests that {@link UnsignedLongType#setBigInteger(BigInteger)} works and
	 * can still return the proper long value.
	 */
	@Test
	public void testSetBigInteger()
	{

		final long l = -184713894790123847l;
		final UnsignedLongType ul = new UnsignedLongType( l );

		assertEquals( ul.get(), l );

		final BigInteger bi = new BigInteger( "AAAAAA3141343BBBBBBBBBBB4134", 16 );
		ul.setBigInteger( bi );

		assertEquals( ul.get(), bi.longValue() );
	}

	@Test
	public void testGetMaxValue()
	{
		assertEquals( ( double ) Long.MAX_VALUE - ( double ) Long.MIN_VALUE, new UnsignedLongType().getMaxValue(), 0.0 );
	}

	@Test
	public void testGetRealDouble()
	{

		final UnsignedLongType ul = new UnsignedLongType( -1 );
		assertEquals( ul.getMaxValue(), ul.getRealDouble(), 0.0 );
	}

	@Test
	public void testGetRealFloat()
	{

		final UnsignedLongType ul = new UnsignedLongType( -1 );
		assertEquals( ( float ) ul.getMaxValue(), ul.getRealFloat(), 0.0f );
	}

	@Test
	public void testSetRealDouble()
	{
		// simple values
		testSetRealDouble( 0, 0 );
		testSetRealDouble( 42, 42 );
		// negative
		testSetRealDouble( -1, 0 );
		// max unsigned long
		testSetRealDouble( Math.pow( 2, 64 ) - 1, ( long ) -1 );
		// max long + 1
		testSetRealDouble( Math.pow( 2, 63 ), 1l << 63 );
		// value smaller than "max long + 1", that can be represented by double
		testSetRealDouble( Math.pow( 2, 63 ) - Math.pow( 2, 10 ), ( 1l << 63 ) - ( 1l << 10 ) );
		// max unsigned long + 1
		testSetRealDouble( Math.pow( 2, 64 ) + 1, ( long ) -1 );
	}

	private void testSetRealDouble( double doubleValue, long longValue )
	{
		final UnsignedLongType type = new UnsignedLongType();
		type.setReal( doubleValue );
		assertEquals( longValue, type.getLong() );
	}

	@Test
	public void testSetMinMax()
	{
		testSetRealDouble( new UnsignedLongType().getMaxValue(), -1l );
		testSetRealDouble( new UnsignedLongType().getMinValue(), 0 );
	}

	@Test
	public void testSetRealFloat()
	{
		// simple values
		testSetRealFloat( 0, 0 );
		testSetRealFloat( 42, 42 );
		// negative
		testSetRealFloat( -1, 0 );
		// max unsigned long
		testSetRealFloat( Math.pow( 2, 64 ) - 1, ( long ) -1 );
		// max long + 1
		testSetRealFloat( Math.pow( 2, 63 ), 1l << 63 );
		// value smaller than "max long + 1", that can be represented by float
		testSetRealFloat( Math.pow( 2, 63 ) - Math.pow( 2, 39 ), ( 1l << 63 ) - ( 1l << 39 ) );
		// max unsigned long + 1
		testSetRealFloat( Math.pow( 2, 64 ) + 1, ( long ) -1 );
	}

	private void testSetRealFloat( double realValue, long longValue )
	{
		UnsignedLongType type = new UnsignedLongType();
		type.setReal( ( float ) realValue );
		assertEquals( longValue, type.getLong() );
	}
}
