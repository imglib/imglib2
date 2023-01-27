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

public class LongTypeTest
{

	/**
	 * Test which verifies {@link LongType#getBigInteger()} returns the
	 * {@code BigInteger} representation of an LongType.
	 */
	@Test
	public void testGetBigInteger()
	{

		final LongType l = new LongType( 901374907l );
		assertEquals( BigInteger.valueOf( 901374907l ), l.getBigInteger() );

		final LongType l2 = new LongType( -98174938174l );
		assertEquals( BigInteger.valueOf( -98174938174l ), l2.getBigInteger() );
	}

	/**
	 * Test which verifies {@link LongType#setBigInteger(BigInteger)} can set
	 * LongTypes with a {@code BigInteger} and still return a {@code long} value.
	 */
	@Test
	public void testSetBigInteger()
	{

		final LongType l = new LongType( 72l );

		assertEquals( l.get(), 72l );

		final BigInteger bi = BigInteger.valueOf( 1093840120l );
		l.setBigInteger( bi );
		assertEquals( l.get(), 1093840120l );
	}

	@Test
	public void testSetRealFloat()
	{
		testSetRealFloat( 0.4f, 0 );
		testSetRealFloat( 0.6f, 1 );
		testSetRealFloat( -0.6f, -1 );
		testSetRealFloat( ( float ) Long.MAX_VALUE, Long.MAX_VALUE );
		testSetRealFloat( ( float ) Long.MIN_VALUE, Long.MIN_VALUE );
	}

	private void testSetRealFloat( float floatValue, long longValue )
	{
		LongType type = new LongType();
		type.setReal( floatValue );
		assertEquals( longValue, type.getLong() );
	}

	@Test
	public void testSetMinMaxValue()
	{
		testSetRealDouble( new LongType().getMaxValue(), Long.MAX_VALUE );
		testSetRealDouble( new LongType().getMinValue(), Long.MIN_VALUE );
	}

	private void testSetRealDouble( double doubleValue, long longValue )
	{
		LongType type = new LongType();
		type.setReal( doubleValue );
		assertEquals( longValue, type.getLong() );
	}
}
