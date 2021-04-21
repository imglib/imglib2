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
import net.imglib2.img.basictypeaccess.array.LongArray;
import net.imglib2.type.logic.BitType;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Stephan Saalfeld
 * @author Stephan Preibisch
 * @author Tobias Pietzsch
 */
public class BitTypeTest {

	static ArrayImg< BitType, LongArray > img;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		img = ArrayImgs.bits( 10, 20, 30 );
	}

	/**
	 * Test method for {@link net.imglib2.type.logic.BitType#setOne()}.
	 */
	@Test
	public void testSetOne()
	{
		for ( final BitType t : img )
			t.setOne();
		for ( final BitType t : img )
			assertTrue( t.get() );
	}

	/**
	 * Test method for {@link net.imglib2.type.logic.BitType#setZero()}.
	 */
	@Test
	public void testSetZero()
	{
		for ( final BitType t : img )
			t.setZero();
		for ( final BitType t : img )
			assertTrue( !t.get() );
	}
	
	/**
	 * Test method for {@link net.imglib2.type.logic.BitType#setOne()}.
	 */
	@Test
	public void testSetOneAndZero()
	{
		final Random rnd = new Random( 0 );

		for ( final BitType t : img )
		{
			final boolean b = rnd.nextBoolean();
			t.set( b );
			assertTrue( t.get() == b );
		}
	}

	/**
	 * Tests that {@link BitType#getBigInteger()} returns the BigInteger 
	 * representation of a BitType.
	 */
	@Test
	public void testGetBigInteger() {

		final BitType l = new BitType(false);

		assertEquals( BigInteger.ZERO, l.getBigInteger() );
	}

	/**
	 * Tests {@link BitType#setBigInteger(BigInteger)} and ensures that the value
	 * returned is within BitType range.
	 */
	@Test
	public void testSetBigInteger() {

		final BitType ul = new BitType( false );

		assertEquals( ul.get(), false );

		final BigInteger bi = new BigInteger( "AAAAAA3141343BBBBBBBBBBB4134", 16 );
		ul.setBigInteger( bi );

		assertEquals( ul.get(), true );
	}

//	/**
//	 * Test method for {@link net.imglib2.type.logic.BitType#toString()}.
//	 */
//	@Test
//	public void testToString() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link net.imglib2.type.logic.BitType#inc()}.
//	 */
//	@Test
//	public void testInc() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link net.imglib2.type.logic.BitType#dec()}.
//	 */
//	@Test
//	public void testDec() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link net.imglib2.type.logic.BitType#BitType(net.imglib2.img.NativeImg)}.
//	 */
//	@Test
//	public void testBitTypeNativeImgOfBitTypeQextendsLongAccess() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link net.imglib2.type.logic.BitType#BitType(boolean)}.
//	 */
//	@Test
//	public void testBitTypeBoolean() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link net.imglib2.type.logic.BitType#BitType(net.imglib2.img.basictypeaccess.LongAccess)}.
//	 */
//	@Test
//	public void testBitTypeLongAccess() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link net.imglib2.type.logic.BitType#BitType()}.
//	 */
//	@Test
//	public void testBitType() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link net.imglib2.type.logic.BitType#createSuitableNativeImg(net.imglib2.img.NativeImgFactory, long[])}.
//	 */
//	@Test
//	public void testCreateSuitableNativeImg() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link net.imglib2.type.logic.BitType#updateContainer(java.lang.Object)}.
//	 */
//	@Test
//	public void testUpdateContainer() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link net.imglib2.type.logic.BitType#duplicateTypeOnSameNativeImg()}.
//	 */
//	@Test
//	public void testDuplicateTypeOnSameNativeImg() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link net.imglib2.type.logic.BitType#get()}.
//	 */
//	@Test
//	public void testGet() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link net.imglib2.type.logic.BitType#set(boolean)}.
//	 */
//	@Test
//	public void testSetBoolean() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link net.imglib2.type.logic.BitType#getInteger()}.
//	 */
//	@Test
//	public void testGetInteger() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link net.imglib2.type.logic.BitType#getIntegerLong()}.
//	 */
//	@Test
//	public void testGetIntegerLong() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link net.imglib2.type.logic.BitType#setInteger(int)}.
//	 */
//	@Test
//	public void testSetIntegerInt() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link net.imglib2.type.logic.BitType#setInteger(long)}.
//	 */
//	@Test
//	public void testSetIntegerLong() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link net.imglib2.type.logic.BitType#getMaxValue()}.
//	 */
//	@Test
//	public void testGetMaxValue() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link net.imglib2.type.logic.BitType#getMinValue()}.
//	 */
//	@Test
//	public void testGetMinValue() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link net.imglib2.type.logic.BitType#set(net.imglib2.type.logic.BitType)}.
//	 */
//	@Test
//	public void testSetBitType() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link net.imglib2.type.logic.BitType#and(net.imglib2.type.logic.BitType)}.
//	 */
//	@Test
//	public void testAnd() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link net.imglib2.type.logic.BitType#or(net.imglib2.type.logic.BitType)}.
//	 */
//	@Test
//	public void testOr() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link net.imglib2.type.logic.BitType#xor(net.imglib2.type.logic.BitType)}.
//	 */
//	@Test
//	public void testXor() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link net.imglib2.type.logic.BitType#not()}.
//	 */
//	@Test
//	public void testNot() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link net.imglib2.type.logic.BitType#add(net.imglib2.type.logic.BitType)}.
//	 */
//	@Test
//	public void testAddBitType() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link net.imglib2.type.logic.BitType#div(net.imglib2.type.logic.BitType)}.
//	 */
//	@Test
//	public void testDivBitType() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link net.imglib2.type.logic.BitType#mul(net.imglib2.type.logic.BitType)}.
//	 */
//	@Test
//	public void testMulBitType() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link net.imglib2.type.logic.BitType#sub(net.imglib2.type.logic.BitType)}.
//	 */
//	@Test
//	public void testSubBitType() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link net.imglib2.type.logic.BitType#compareTo(net.imglib2.type.logic.BitType)}.
//	 */
//	@Test
//	public void testCompareToBitType() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link net.imglib2.type.logic.BitType#createVariable()}.
//	 */
//	@Test
//	public void testCreateVariable() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link net.imglib2.type.logic.BitType#copy()}.
//	 */
//	@Test
//	public void testCopy() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link net.imglib2.type.logic.BitType#getEntitiesPerPixel()}.
//	 */
//	@Test
//	public void testGetEntitiesPerPixel() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link net.imglib2.type.logic.BitType#updateIndex(int)}.
//	 */
//	@Test
//	public void testUpdateIndex() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link net.imglib2.type.logic.BitType#getIndex()}.
//	 */
//	@Test
//	public void testGetIndex() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link net.imglib2.type.logic.BitType#incIndex()}.
//	 */
//	@Test
//	public void testIncIndex() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link net.imglib2.type.logic.BitType#incIndex(int)}.
//	 */
//	@Test
//	public void testIncIndexInt() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link net.imglib2.type.logic.BitType#decIndex()}.
//	 */
//	@Test
//	public void testDecIndex() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link net.imglib2.type.logic.BitType#decIndex(int)}.
//	 */
//	@Test
//	public void testDecIndexInt() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link net.imglib2.type.logic.BitType#getBitsPerPixel()}.
//	 */
//	@Test
//	public void testGetBitsPerPixel() {
//		fail("Not yet implemented");
//	}

}
