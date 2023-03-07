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

/**
 * Tests for {@link ShortType} functionality.
 *
 * @author Gabe Selzer
 *
 */

public class ShortTypeTest
{

	/**
	 * Test which verifies {@link ShortType#getBigInteger()} returns the
	 * {@code BigInteger} representation of a ShortType.
	 */
	@Test
	public void testGetBigInteger()
	{

		final ShortType l = new ShortType( ( short ) 31498 );
		assertEquals( BigInteger.valueOf( 31498l ), l.getBigInteger() );

		final ShortType l2 = new ShortType( ( short ) -24691 );
		assertEquals( BigInteger.valueOf( -24691l ), l2.getBigInteger() );
	}

	/**
	 * Test which verifies {@link ShortType#setBigInteger(BigInteger)} can set
	 * ShortTypes with a {@code BigInteger} and still return a {@code short}
	 * value.
	 */
	@Test
	public void testSetBigInteger()
	{

		final ShortType l = new ShortType( ( short ) 1082 );

		assertEquals( l.get(), ( short ) 1082 );

		final BigInteger bi = BigInteger.valueOf( 48906l );
		l.setBigInteger( bi );
		assertEquals( l.get(), ( short ) -16630 );
	}

	/**
	 * Test which verifies {@link ShortType#add(ShortType)}
	 */
	@Test
	public void testAdd()
	{
		final ShortType l = new ShortType( ( short ) 20000 );

		l.add( new ShortType( ( short ) 5 ) );

		assertEquals( l.get(), ( short ) 20005 );

		final ShortType m = new ShortType( Short.MAX_VALUE );

		m.add( new ShortType( ( short ) 1 ) );

		assertEquals( m.get(), Short.MIN_VALUE );
	}

	/**
	 * Test which verifies {@link ShortType#sub(ShortType)}
	 */
	@Test
	public void testSub()
	{
		final ShortType l = new ShortType( ( short ) 20000 );

		l.sub( new ShortType( ( short ) 5 ) );

		assertEquals( l.get(), ( short ) 19995 );

		final ShortType m = new ShortType( Short.MIN_VALUE );

		m.sub( new ShortType( ( short ) 1 ) );

		assertEquals( m.get(), Short.MAX_VALUE );
	}

	/**
	 * Test which verifies {@link ShortType#mul(ShortType)}
	 */
	@Test
	public void testMul()
	{
		final ShortType l = new ShortType( ( short ) 125 );

		l.mul( new ShortType( ( short ) 5 ) );

		assertEquals( l.get(), ( short ) 625 );

		final ShortType m = new ShortType( ( short ) 16384 );

		m.mul( new ShortType( ( short ) 2 ) );

		assertEquals( m.get(), Short.MIN_VALUE );
	}

	/**
	 * Test which verifies {@link ShortType#div(ShortType)}
	 */
	@Test
	public void testDiv()
	{
		final ShortType l = new ShortType( ( short ) 125 );

		l.div( new ShortType( ( short ) 5 ) );

		assertEquals( l.get(), ( short ) 25 );

		final ShortType m = new ShortType( ( short ) 17 );

		m.div( new ShortType( ( short ) 2 ) );

		assertEquals( m.get(), ( short ) 8 );
	}

	@Test( expected = ArithmeticException.class )
	public void testDivByZero()
	{
		final ShortType n = new ShortType( ( short ) 17 );

		n.div( new ShortType( ( short ) 0 ) );

		assertEquals( n.get(), ( short ) 8 );
	}

}
