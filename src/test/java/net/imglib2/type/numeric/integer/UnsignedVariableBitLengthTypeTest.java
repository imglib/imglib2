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
