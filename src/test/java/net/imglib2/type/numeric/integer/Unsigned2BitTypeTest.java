/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2021 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.Random;

import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgFactory;

import org.junit.BeforeClass;
import org.junit.Test;

public class Unsigned2BitTypeTest
{
	static ArrayImg< Unsigned2BitType, ? > img;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{

		img = new ArrayImgFactory<>( new Unsigned2BitType() ).create( 10, 20, 30 );
	}

	/**
	 * Test method for {@link net.imglib2.type.numeric.integer.Unsigned2BitType}.
	 */
	@Test
	public void testSetRandom()
	{
		final Random rnd = new Random( 0 );

		for ( final Unsigned2BitType t : img )
		{
			final int v = rnd.nextInt( 4 );
			t.set( v );
			assertTrue( t.get() == v );
		}
	}

	/**
	 * Test which verifies {@link Unsigned2BitType#getBigInteger()} returns the
	 * {@code BigInteger} representation of an Unsigned2BitType.
	 */
	@Test
	public void testGetBigInteger() {

		final Unsigned2BitType l = new Unsigned2BitType( 2 );
		assertEquals( BigInteger.valueOf( 2l ), l.getBigInteger() );

		final Unsigned2BitType l2 = new Unsigned2BitType( 0l );
		assertEquals( BigInteger.ZERO , l2.getBigInteger() );
	}

	/**
	 * Test which verifies {@link Unsigned2BitType#setBigInteger(BigInteger)}
	 * can set Unsigned2BitTypes with a {@code BigInteger} and still return an
	 * {@code int} value that is in the proper range.
	 */
	@Test
	public void testSetBigInteger() {

		final Unsigned2BitType l = new Unsigned2BitType( 10l );

		assertEquals( l.get(), 2l );

		final BigInteger bi = BigInteger.valueOf( -122l );
		l.setBigInteger( bi );
		assertEquals( l.get(), 2l );
	}

	/**
	 * Tests {@link Unsigned2BitType#equals(Object)}.
	 */
	@Test
	public void testEquals()
	{
		final Unsigned2BitType b = new Unsigned2BitType( 3l );

		// non-matching types and values
		final UnsignedIntType i = new UnsignedIntType( 1l );
		assertFalse( b.equals( i ) );
		assertFalse( i.equals( b ) );

		// non-matching types
		final UnsignedIntType i2 = new UnsignedIntType( 3l );
		assertFalse( b.equals( i2 ) );
		assertFalse( i2.equals( b ) );

		// non-matching values
		final Unsigned2BitType i3 = new Unsigned2BitType( 1l );
		assertFalse( b.equals( i3 ) );
		assertFalse( i3.equals( b ) );

		// matching type and value
		final Unsigned2BitType i4 = new Unsigned2BitType( 3l );
		assertTrue( b.equals( i4 ) );
		assertTrue( i4.equals( b ) );
	}


	/** Tests {@link Unsigned2BitType#hashCode()}. */
	@Test
	public void testHashCode()
	{
		for (int i = 0; i < 4; i++) {
			final Unsigned2BitType b = new Unsigned2BitType( i );
			assertEquals( i, b.hashCode() );
		}
	}

}
