/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2015 Tobias Pietzsch, Stephan Preibisch, Barry DeZonia,
 * Stephan Saalfeld, Curtis Rueden, Albert Cardona, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Jonathan Hale, Lee Kamentsky, Larry Lindsey, Mark
 * Hiner, Michael Zinsmaier, Martin Horn, Grant Harris, Aivar Grislis, John
 * Bogovic, Steffen Jaensch, Stefan Helfrich, Jan Funke, Nick Perry, Mark Longair,
 * Melissa Linkert and Dimiter Prodanov.
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

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.util.Random;

import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgFactory;

import org.junit.BeforeClass;
import org.junit.Test;

public class Unsigned128BitTypeTest
{
	static ArrayImg< Unsigned128BitType, ? > img;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		
		img = new ArrayImgFactory< Unsigned128BitType >().create( new long[]{ 10, 20, 30 }, new Unsigned128BitType() );
	}

	/**
	 * Tests {@link Unsigned128BitType#set(BigInteger)} with random values.
	 */
	@Test
	public void testSetRandom()
	{
		final Random rnd = new Random( 4096 );

		for ( final Unsigned128BitType t : img )
		{
			final long v1 = Math.abs( rnd.nextLong() );
			final long v2 = Math.abs( rnd.nextLong() );

			final BigInteger b = new BigInteger( v1 + "" + v2 );

			t.set( b );

			assertTrue( t.get().compareTo( b ) == 0 );
		}
	}

	/**
	 * Regression test that verifies small {@link BigInteger} values work as
	 * expected when passed to
	 * {@link Unsigned128BitType#Unsigned128BitType(BigInteger)}.
	 */
	@Test
	public void testSmallBigIntegerValues()
	{
		final BigInteger b = BigInteger.valueOf( 329l );
		final Unsigned128BitType u = new Unsigned128BitType( b );

		assertEquals( b, u.get() );
	}

	/**
	 * Tests {@link Unsigned128BitType#equals(Object)}.
	 */
	@Test
	public void testEquals()
	{
		final Unsigned128BitType b = new Unsigned128BitType( BigInteger.valueOf( 908742l ) );

		// non-matching types and values
		final UnsignedIntType i = new UnsignedIntType( 127l );
		assertFalse( b.equals( i ) );
		assertFalse( i.equals( b ) );

		// non-matching types
		final UnsignedIntType i2 = new UnsignedIntType( 908742l );
		assertFalse( b.equals( i2 ) );
		assertFalse( i2.equals( b ) );

		// non-matching values
		final Unsigned128BitType i3 = new Unsigned128BitType( BigInteger.valueOf( 127l ) );
		assertFalse( b.equals( i3 ) );
		assertFalse( i3.equals( b ) );

		// matching type and value
		final Unsigned128BitType i4 = new Unsigned128BitType( BigInteger.valueOf( 908742l ) );
		assertTrue( b.equals( i4 ) );
		assertTrue( i4.equals( b ) );
	}

	/**
	 * Test which verifies {@link Unsigned128BitType#getBigInteger()} returns the
	 * {@code BigInteger} representation of an Unsigned128BitType.
	 */
	@Test
	public void testGetBigInteger() {

		final BigInteger bi = new BigInteger("CAFE123498230498CAFE", 16);
		final Unsigned128BitType l = new Unsigned128BitType( bi );
		assertEquals( bi, l.getBigInteger() );

		final BigInteger bi2 = BigInteger.valueOf( -279l );
		final Unsigned128BitType l2 = new Unsigned128BitType( bi2 );
		assertEquals( BigInteger.valueOf( 65257l ), l2.getBigInteger() );
	}

	/**
	 * Test which verifies {@link Unsigned128BitType#setBigInteger(BigInteger)}
	 * can set Unsigned128BitTypes with a {@code BigInteger} and still return an
	 * {@code int} value.
	 */
	@Test
	public void testSetBigInteger() {

		final BigInteger b = new BigInteger("BABE09481BEEF", 16);
		final Unsigned128BitType l = new Unsigned128BitType( b );
		assertEquals( l.get(), b );

		final BigInteger bi = BigInteger.valueOf( 7987431l );
		l.setBigInteger( bi );
		assertEquals( l.get(), bi );
	}

}
