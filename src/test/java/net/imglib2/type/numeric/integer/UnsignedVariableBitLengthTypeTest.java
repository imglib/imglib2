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

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import org.junit.Test;


public class UnsignedVariableBitLengthTypeTest {

	/**
	 * Test which verifies {@link UnsignedVariableBitLengthType#getBigInteger()}
	 * returns the {@code BigInteger} representation of an
	 * UnsignedVariableBitLengthType.
	 */
	@Test
	public void testGetBigInteger() {

		final UnsignedVariableBitLengthType l = new
				UnsignedVariableBitLengthType( 1234l, 16 );
		assertEquals( BigInteger.valueOf( 1234l ), l.getBigInteger() );

		final UnsignedVariableBitLengthType l2 = new
				UnsignedVariableBitLengthType( -196, 8);
		assertEquals( BigInteger.valueOf( 60l ), l2.getBigInteger() );

		final UnsignedVariableBitLengthType l3 = new
				UnsignedVariableBitLengthType( -9223372036854775807l, 64 );
		assertEquals( BigInteger.valueOf( -9223372036854775807l ).and( 
			new BigInteger("FFFFFFFFFFFFFFFF", 16) ), l3.getBigInteger() );
	}

	/**
	 * Test which verifies
	 * {@link UnsignedVariableBitLengthType#setBigInteger(BigInteger)} can set
	 * UnsignedVariableBitLengthTypes with a {@code BigInteger} and still return
	 * a {@code long} value that is in the correct range.
	 */
	@Test
	public void testSetBigInteger() {

		final UnsignedVariableBitLengthType ul = new
				UnsignedVariableBitLengthType( 6347, 14 );
		assertEquals( ul.get(), 6347 );

		ul.setBigInteger( BigInteger.valueOf( 15004l ) );
		assertEquals( ul.get(), 15004l );

		ul.setBigInteger( BigInteger.valueOf( 25625l ) );
		assertEquals( ul.get(), 9241l );
	}
}
