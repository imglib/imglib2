/**
 * 
 */
package net.imglib2.type.logic;

import static org.junit.Assert.*;

import java.util.Random;

import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.LongArray;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author saalfeld
 *
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
	public void testSetOne() {
		for ( final BitType t : img )
			t.setOne();
		for ( final BitType t : img )
			assertTrue( t.get() );
	}

	/**
	 * Test method for {@link net.imglib2.type.logic.BitType#setZero()}.
	 */
	@Test
	public void testSetZero() {
		for ( final BitType t : img )
			t.setZero();
		for ( final BitType t : img )
			assertTrue( !t.get() );
	}
	
	/**
	 * Test method for {@link net.imglib2.type.logic.BitType#setOne()}.
	 */
	@Test
	public void testSetOneAndZero() {
		final Random rnd = new Random( 0 );
		
		for ( final BitType t : img )
			if ( rnd.nextBoolean() )
				t.setOne();
			else
				t.setZero();
		
		final Random rnd1 = new Random( 0 );
		for ( final BitType t : img )
			assertTrue( t.get() == rnd1.nextBoolean() );
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
