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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.Random;

import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgFactory;

import org.junit.BeforeClass;
import org.junit.Test;

public class Unsigned4BitTypeTest
{
	static ArrayImg< Unsigned4BitType, ? > img;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		
		img = new ArrayImgFactory< Unsigned4BitType >().create( new long[]{ 10, 20, 30 }, new Unsigned4BitType() );
	}

	/**
	 * Test method for {@link net.imglib2.type.numeric.integer.Unsigned4BitType}.
	 */
	@Test
	public void testSetRandom()
	{
		final Random rnd = new Random( 0 );

		for ( final Unsigned4BitType t : img )
		{
			final int v = rnd.nextInt( 16 );
			t.set( v );
			assertTrue( t.get() == v );
		}
	}

	/**
	 * Test which verifies {@link Unsigned4BitType#getBigInteger()} returns the
	 * {@code BigInteger} representation of an Unsigned4BitType.
	 */
	@Test
	public void testGetBigInteger() {

		final Unsigned4BitType l = new Unsigned4BitType( 14l );
		assertEquals( BigInteger.valueOf( 14l ), l.getBigInteger() );

		final Unsigned4BitType l2 = new Unsigned4BitType( -7l );
		assertEquals( BigInteger.valueOf( 9l ) , l2.getBigInteger() );
	}

	/**
	 * Test which verifies {@link Unsigned4BitType#setBigInteger(BigInteger)}
	 * can set Unsigned4BitType with a {@code BigInteger} and still return an
	 * {@code int} value that is in the proper range.
	 */
	@Test
	public void testSetBigInteger() {

		final Unsigned4BitType l = new Unsigned4BitType( 4l );

		assertEquals( l.get(), 4l );

		final BigInteger bi = BigInteger.valueOf( 163l );
		l.setBigInteger( bi );
		assertEquals( l.get(), 3l );
	}
}
