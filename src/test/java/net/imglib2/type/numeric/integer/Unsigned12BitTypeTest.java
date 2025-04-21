/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2025 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

public class Unsigned12BitTypeTest
{
	static ArrayImg< Unsigned12BitType, ? > img;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{

		img = new ArrayImgFactory<>( new Unsigned12BitType() ).create( 10, 20, 30 );
	}

	/**
	 * Test method for {@link net.imglib2.type.numeric.integer.Unsigned12BitType}.
	 */
	@Test
	public void testSetRandom()
	{
		final Random rnd = new Random( 4096 );

		for ( final Unsigned12BitType t : img )
		{
			final int v = rnd.nextInt( 16 );
			t.set( v );
			assertTrue( t.get() == v );
		}
	}

	/**
	 * Test which verifies {@link Unsigned12BitType#getBigInteger()} returns the
	 * {@code BigInteger} representation of an Unsigned12BitType.
	 */
	@Test
	public void testGetBigInteger() {

		final Unsigned12BitType l = new Unsigned12BitType( 300l );
		assertEquals( BigInteger.valueOf( 300l ), l.getBigInteger() );

		final Unsigned12BitType l2 = new Unsigned12BitType( 5700l );
		assertEquals( BigInteger.valueOf( 1604l ) , l2.getBigInteger() );
	}

	/**
	 * Test which verifies {@link Unsigned12BitType#setBigInteger(BigInteger)}
	 * can set Unsigned12BitTypes with a {@code BigInteger} and still return an
	 * {@code int} value that is in the proper range.
	 */
	@Test
	public void testSetBigInteger() {

		final Unsigned12BitType l = new Unsigned12BitType( 1029l );

		assertEquals( l.get(), 1029l );

		final BigInteger bi = BigInteger.valueOf( -122l );
		l.setBigInteger( bi );
		assertEquals( l.get(), 3974l );
	}

	/**
	 * Tests {@link Unsigned12BitType#equals(Object)}.
	 */
	@Test
	public void testEquals()
	{
		final Unsigned12BitType b = new Unsigned12BitType( 3526l );

		// non-matching types and values
		final UnsignedIntType i = new UnsignedIntType( 127l );
		assertFalse( b.equals( i ) );
		assertFalse( i.equals( b ) );

		// non-matching types
		final UnsignedIntType i2 = new UnsignedIntType( 3526l );
		assertFalse( b.equals( i2 ) );
		assertFalse( i2.equals( b ) );

		// non-matching values
		final Unsigned12BitType i3 = new Unsigned12BitType( 127l );
		assertFalse( b.equals( i3 ) );
		assertFalse( i3.equals( b ) );

		// matching type and value
		final Unsigned12BitType i4 = new Unsigned12BitType( 3526l );
		assertTrue( b.equals( i4 ) );
		assertTrue( i4.equals( b ) );
	}


	/** Tests {@link Unsigned12BitType#hashCode()}. */
	@Test
	public void testHashCode()
	{
		final Unsigned12BitType b = new Unsigned12BitType( 3526l );
		assertEquals( 3526, b.hashCode() );
	}

}
