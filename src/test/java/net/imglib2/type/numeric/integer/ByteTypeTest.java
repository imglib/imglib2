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
package net.imglib2.type.numeric.integer;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import org.junit.Test;


public class ByteTypeTest {

	/**
	 * Test which verifies {@link ByteType#getBigInteger()} returns the
	 * {@code BigInteger} representation of a ByteType.
	 */
	@Test
	public void testGetBigInteger() {

		final ByteType l = new ByteType( (byte) 124 );
		assertEquals( BigInteger.valueOf( 124l ), l.getBigInteger() );

		final ByteType l2 = new ByteType( (byte) -18 );
		assertEquals( BigInteger.valueOf( -18l ) , l2.getBigInteger() );
	}

	/**
	 * Test which verifies {@link ByteType#setBigInteger(BigInteger)} can set
	 * ByteTypes with a {@code BigInteger} and still return a {@code byte} value.
	 */
	@Test
	public void testSetBigInteger() {

		final ByteType l = new ByteType( (byte) 93 );

		assertEquals( l.get(), (byte) 93 );

		final BigInteger bi = BigInteger.valueOf( -71l );
		l.setBigInteger( bi );
		assertEquals( l.get(), (byte) -71 );
	}

}
