package net.imglib2.img.basictypeaccess.constant;

import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.Test;

import net.imglib2.img.basictypeaccess.constant.dirty.DirtyConstantByteAccess;
import net.imglib2.img.basictypeaccess.constant.dirty.DirtyConstantCharAccess;
import net.imglib2.img.basictypeaccess.constant.dirty.DirtyConstantDoubleAccess;
import net.imglib2.img.basictypeaccess.constant.dirty.DirtyConstantFloatAccess;
import net.imglib2.img.basictypeaccess.constant.dirty.DirtyConstantIntAccess;
import net.imglib2.img.basictypeaccess.constant.dirty.DirtyConstantLongAccess;
import net.imglib2.img.basictypeaccess.constant.dirty.DirtyConstantShortAccess;

public class ConstantAccessTest
{

	final byte testByte = ( byte ) 0xff;

	final char testChar = ( char ) 0xf0;

	final short testShort = ( short ) 0xffff;

	final int testInt = 0xffffffff;

	final long testLong = 0xffffffffffffffffL;

	final float testFloat = 1.0F;

	final double testDouble = -1.0D;

	final byte zeroByte = ( byte ) 0;

	final char zeroChar = ( char ) 0;

	final short zeroShort = ( short ) 0;

	final int zeroInt = 0;

	final long zeroLong = 0L;

	final float zeroFloat = 0.0F;

	final double zeroDouble = 0.0D;

	private final int start = -10;

	private final int stop = 10;

	@Test
	public void test()
	{
		testByte();
		testChar();
		testShort();
		testInt();
		testLong();
		testFloat();
		testDouble();
	}

	public void testByte()
	{
		final ConstantByteAccess access1 = new ConstantByteAccess();
		IntStream.range( start, stop ).forEach( index -> Assert.assertEquals( zeroByte, access1.getValue( index ) ) );

		access1.setValue( start, testByte );
		IntStream.range( start, stop ).forEach( index -> Assert.assertEquals( testByte, access1.getValue( index ) ) );

		final ConstantByteAccess access2 = new ConstantByteAccess( testByte );
		IntStream.range( start, stop ).forEach( index -> Assert.assertEquals( testByte, access2.getValue( index ) ) );

		final DirtyConstantByteAccess dirtyAccess = new DirtyConstantByteAccess();
		Assert.assertFalse( dirtyAccess.isDirty() );
		IntStream.range( start, stop ).forEach( index -> Assert.assertEquals( zeroByte, dirtyAccess.getValue( index ) ) );

		dirtyAccess.setValue( start, testByte );
		Assert.assertTrue( dirtyAccess.isDirty() );
		IntStream.range( start, stop ).forEach( index -> Assert.assertEquals( testByte, dirtyAccess.getValue( index ) ) );
	}

	public void testChar()
	{
		final ConstantCharAccess access1 = new ConstantCharAccess();
		IntStream.range( start, stop ).forEach( index -> Assert.assertEquals( zeroChar, access1.getValue( index ) ) );

		access1.setValue( start, testChar );
		IntStream.range( start, stop ).forEach( index -> Assert.assertEquals( testChar, access1.getValue( index ) ) );

		final ConstantCharAccess access2 = new ConstantCharAccess( testChar );
		IntStream.range( start, stop ).forEach( index -> Assert.assertEquals( testChar, access2.getValue( index ) ) );

		final DirtyConstantCharAccess dirtyAccess = new DirtyConstantCharAccess();
		Assert.assertFalse( dirtyAccess.isDirty() );
		IntStream.range( start, stop ).forEach( index -> Assert.assertEquals( zeroChar, dirtyAccess.getValue( index ) ) );

		dirtyAccess.setValue( start, testChar );
		Assert.assertTrue( dirtyAccess.isDirty() );
		IntStream.range( start, stop ).forEach( index -> Assert.assertEquals( testChar, dirtyAccess.getValue( index ) ) );
	}

	public void testShort()
	{
		final ConstantShortAccess access1 = new ConstantShortAccess();
		IntStream.range( start, stop ).forEach( index -> Assert.assertEquals( zeroShort, access1.getValue( index ) ) );

		access1.setValue( start, testShort );
		IntStream.range( start, stop ).forEach( index -> Assert.assertEquals( testShort, access1.getValue( index ) ) );

		final ConstantShortAccess access2 = new ConstantShortAccess( testShort );
		IntStream.range( start, stop ).forEach( index -> Assert.assertEquals( testShort, access2.getValue( index ) ) );

		final DirtyConstantShortAccess dirtyAccess = new DirtyConstantShortAccess();
		Assert.assertFalse( dirtyAccess.isDirty() );
		IntStream.range( start, stop ).forEach( index -> Assert.assertEquals( zeroShort, dirtyAccess.getValue( index ) ) );

		dirtyAccess.setValue( start, testShort );
		Assert.assertTrue( dirtyAccess.isDirty() );
		IntStream.range( start, stop ).forEach( index -> Assert.assertEquals( testShort, dirtyAccess.getValue( index ) ) );
	}

	public void testInt()
	{
		final ConstantIntAccess access1 = new ConstantIntAccess();
		IntStream.range( start, stop ).forEach( index -> Assert.assertEquals( zeroInt, access1.getValue( index ) ) );

		access1.setValue( start, testInt );
		IntStream.range( start, stop ).forEach( index -> Assert.assertEquals( testInt, access1.getValue( index ) ) );

		final ConstantIntAccess access2 = new ConstantIntAccess( testInt );
		IntStream.range( start, stop ).forEach( index -> Assert.assertEquals( testInt, access2.getValue( index ) ) );

		final DirtyConstantIntAccess dirtyAccess = new DirtyConstantIntAccess();
		Assert.assertFalse( dirtyAccess.isDirty() );
		IntStream.range( start, stop ).forEach( index -> Assert.assertEquals( zeroInt, dirtyAccess.getValue( index ) ) );

		dirtyAccess.setValue( start, testInt );
		Assert.assertTrue( dirtyAccess.isDirty() );
		IntStream.range( start, stop ).forEach( index -> Assert.assertEquals( testInt, dirtyAccess.getValue( index ) ) );
	}

	public void testLong()
	{
		final ConstantLongAccess access1 = new ConstantLongAccess();
		IntStream.range( start, stop ).forEach( index -> Assert.assertEquals( zeroLong, access1.getValue( index ) ) );

		access1.setValue( start, testLong );
		IntStream.range( start, stop ).forEach( index -> Assert.assertEquals( testLong, access1.getValue( index ) ) );

		final ConstantLongAccess access2 = new ConstantLongAccess( testLong );
		IntStream.range( start, stop ).forEach( index -> Assert.assertEquals( testLong, access2.getValue( index ) ) );

		final DirtyConstantLongAccess dirtyAccess = new DirtyConstantLongAccess();
		Assert.assertFalse( dirtyAccess.isDirty() );
		IntStream.range( start, stop ).forEach( index -> Assert.assertEquals( zeroLong, dirtyAccess.getValue( index ) ) );

		dirtyAccess.setValue( start, testLong );
		Assert.assertTrue( dirtyAccess.isDirty() );
		IntStream.range( start, stop ).forEach( index -> Assert.assertEquals( testLong, dirtyAccess.getValue( index ) ) );
	}

	public void testFloat()
	{
		final ConstantFloatAccess access1 = new ConstantFloatAccess();
		IntStream.range( start, stop ).forEach( index -> Assert.assertEquals( zeroFloat, access1.getValue( index ), 0.0F ) );

		access1.setValue( start, testFloat );
		IntStream.range( start, stop ).forEach( index -> Assert.assertEquals( testFloat, access1.getValue( index ), 0.0F ) );

		final ConstantFloatAccess access2 = new ConstantFloatAccess( testFloat );
		IntStream.range( start, stop ).forEach( index -> Assert.assertEquals( testFloat, access2.getValue( index ), 0.0F ) );

		final DirtyConstantFloatAccess dirtyAccess = new DirtyConstantFloatAccess();
		Assert.assertFalse( dirtyAccess.isDirty() );
		IntStream.range( start, stop ).forEach( index -> Assert.assertEquals( zeroFloat, dirtyAccess.getValue( index ), 0.0F ) );

		dirtyAccess.setValue( start, testFloat );
		Assert.assertTrue( dirtyAccess.isDirty() );
		IntStream.range( start, stop ).forEach( index -> Assert.assertEquals( testFloat, dirtyAccess.getValue( index ), 0.0F ) );
	}

	public void testDouble()
	{
		final ConstantDoubleAccess access1 = new ConstantDoubleAccess();
		IntStream.range( start, stop ).forEach( index -> Assert.assertEquals( zeroDouble, access1.getValue( index ), 0.0D ) );

		access1.setValue( start, testDouble );
		IntStream.range( start, stop ).forEach( index -> Assert.assertEquals( testDouble, access1.getValue( index ), 0.0D ) );

		final ConstantDoubleAccess access2 = new ConstantDoubleAccess( testDouble );
		IntStream.range( start, stop ).forEach( index -> Assert.assertEquals( testDouble, access2.getValue( index ), 0.0D ) );

		final DirtyConstantDoubleAccess dirtyAccess = new DirtyConstantDoubleAccess();
		Assert.assertFalse( dirtyAccess.isDirty() );
		IntStream.range( start, stop ).forEach( index -> Assert.assertEquals( zeroDouble, dirtyAccess.getValue( index ), 0.0D ) );

		dirtyAccess.setValue( start, testDouble );
		Assert.assertTrue( dirtyAccess.isDirty() );
		IntStream.range( start, stop ).forEach( index -> Assert.assertEquals( testDouble, dirtyAccess.getValue( index ), 0.0D ) );
	}

}
