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

import java.math.BigInteger;

import org.junit.Test;

public class UnsignedShortTypeTest
{
	/**
	 * Regression test that verifies in range int values work as expected when
	 * passed to {@link UnsignedShortType#UnsignedShortType(int)}.
	 */
	@Test
	public void testInRangeValues()
	{
		final int i = 6004;
		final UnsignedShortType u = new UnsignedShortType( i );

		assertEquals( i, u.get() );

		u.set( 0 );
		assertEquals( 0, u.get() );

		u.set( 34 );
		assertEquals( 34, u.get() );
	}

	/**
	 * Regression test that verifies that an int which is greater than 65535 works
	 * as expected when passed to {@link UnsignedShortType#UnsignedShortType(int)}.
	 */
	@Test
	public void testPositiveOutOfRangeValue()
	{
		final int i = 78098;
		final UnsignedShortType u = new UnsignedShortType( i );

		assertEquals( 12562, u.get() );
	}

	/**
	 * Regression test that verifies that a negative value works as
	 * expected when passed to {@link UnsignedShortType#UnsignedShortType(int)}.
	 */
	@Test
	public void testNegativeOutOfRangeValue()
	{
		final int i = -597;
		final UnsignedShortType u = new UnsignedShortType( i );

		assertEquals( 64939, u.get() );
	}

	/**
	 * Test which verifies {@link UnsignedShortType#getBigInteger()} returns the
	 * {@code BigInteger} representation of an UnsignedShortType.
	 */
	@Test
	public void testGetBigInteger() {

		final UnsignedShortType l = new UnsignedShortType( 1000 );
		assertEquals( BigInteger.valueOf( 1000l ), l.getBigInteger() );

		final UnsignedShortType l2 = new UnsignedShortType( 32001 );
		assertEquals( BigInteger.valueOf( 32001l ) , l2.getBigInteger() );
	}

	/**
	 * Test which verifies {@link UnsignedShortType#setBigInteger(BigInteger)}
	 * can set UnsignedShortTypes with a {@code BigInteger} and still return an
	 * {@code int} value within range.
	 */
	@Test
	public void testSetBigInteger() {

		final UnsignedShortType l = new UnsignedShortType( 93 );

		assertEquals( l.get(), 93 );

		final BigInteger bi = BigInteger.valueOf( -33125l );
		l.setBigInteger( bi );
		assertEquals( l.get(), 32411 );
	}
}
