/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2023 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

public class UnsignedByteTypeTest
{
	/**
	 * Regression test that verifies in range int values work as expected when
	 * passed to {@link UnsignedByteType#UnsignedByteType(int)}.
	 */
	@Test
	public void testInRangeValues()
	{
		final int i = 126;
		final UnsignedByteType u = new UnsignedByteType( i );

		assertEquals( i, u.get() );

		u.set( 0 );
		assertEquals( 0, u.get() );

		u.set( 5 );
		assertEquals( 5, u.get() );
	}

	/**
	 * Regression test that verifies that an int which is greater than 256 works
	 * as expected when passed to {@link UnsignedByteType#UnsignedByteType(int)}.
	 */
	@Test
	public void testPositiveOutOfRangeValue()
	{
		final int i = 1027;
		final UnsignedByteType u = new UnsignedByteType( i );

		assertEquals( 3, u.get() );
	}

	/**
	 * Regression test that verifies that a negative value works as
	 * expected when passed to {@link UnsignedByteType#UnsignedByteType(int)}.
	 */
	@Test
	public void testNegativeOutOfRangeValue()
	{
		final int i = -212;
		final UnsignedByteType u = new UnsignedByteType( i );

		assertEquals( 44, u.get() );
	}

	/**
	 * Test which verifies {@link UnsignedByteType#getBigInteger()} returns the
	 * {@code BigInteger} representation of an UnsignedByteType.
	 */
	@Test
	public void testGetBigInteger() {

		final UnsignedByteType l = new UnsignedByteType( 255 );
		assertEquals( BigInteger.valueOf( 255l ), l.getBigInteger() );

		final UnsignedByteType l2 = new UnsignedByteType( -127 );
		assertEquals( BigInteger.valueOf( 129l ) , l2.getBigInteger() );
	}

	/**
	 * Test which verifies {@link UnsignedByteType#setBigInteger(BigInteger)} can
	 * set UnsignedByteTypes with a {@code BigInteger} and still return an the
	 * proper {@code int} value.
	 */
	@Test
	public void testSetBigInteger() {

		final UnsignedByteType l = new UnsignedByteType( 255 );

		assertEquals( l.get(), 255 );

		final BigInteger bi = BigInteger.valueOf( 400l );
		l.setBigInteger( bi );
		assertEquals( l.get(), 144 );
	}
}
