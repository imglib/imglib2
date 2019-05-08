/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2018 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
/**
 * 
 */
package net.imglib2.type.logic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.Random;

import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.BooleanArray;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests {@link NativeBoolType}.
 * 
 * @author Curtis Rueden
 */
public class NativeBoolTypeTest {

	static ArrayImg< NativeBoolType, BooleanArray > img;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		img = ArrayImgs.booleans( 10, 20, 30 );
	}

	/**
	 * Test method for {@link net.imglib2.type.logic.NativeBoolType#setOne()}.
	 */
	@Test
	public void testSetOne()
	{
		for ( final NativeBoolType t : img )
			t.setOne();
		for ( final NativeBoolType t : img )
			assertTrue( t.get() );
	}

	/**
	 * Test method for {@link net.imglib2.type.logic.NativeBoolType#setZero()}.
	 */
	@Test
	public void testSetZero()
	{
		for ( final NativeBoolType t : img )
			t.setZero();
		for ( final NativeBoolType t : img )
			assertTrue( !t.get() );
	}

	/**
	 * Test method for {@link net.imglib2.type.logic.NativeBoolType#setOne()}.
	 */
	@Test
	public void testSetOneAndZero()
	{
		final Random rnd = new Random( 0 );

		for ( final NativeBoolType t : img )
		{
			final boolean b = rnd.nextBoolean();
			t.set( b );
			assertTrue( t.get() == b );
		}
	}

	/**
	 * Tests that {@link NativeBoolType#getBigInteger()} returns the BigInteger
	 * representation of a NativeBoolType.
	 */
	@Test
	public void testGetBigInteger()
	{
		final NativeBoolType l = new NativeBoolType( false );

		assertEquals( BigInteger.ZERO, l.getBigInteger() );
	}

	/**
	 * Tests {@link NativeBoolType#setBigInteger(BigInteger)} and ensures that
	 * the value returned is within NativeBoolType range.
	 */
	@Test
	public void testSetBigInteger()
	{
		final NativeBoolType ul = new NativeBoolType( false );

		assertEquals( ul.get(), false );

		final BigInteger bi = new BigInteger( "AAAAAA3141343BBBBBBBBBBB4134", 16 );
		ul.setBigInteger( bi );

		assertEquals( ul.get(), true );
	}
}
