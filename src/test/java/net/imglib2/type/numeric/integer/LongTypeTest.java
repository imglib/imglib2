package net.imglib2.type.numeric.integer;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import org.junit.Test;


public class LongTypeTest {

	/**
	 * Test which verifies {@link LongType#getBigInteger()} returns the
	 * {@code BigInteger} representation of an LongType.
	 */
	@Test
	public void testGetBigInteger() {

		final LongType l = new LongType( 901374907l );
		assertEquals( BigInteger.valueOf( 901374907l ), l.getBigInteger() );

		final LongType l2 = new LongType( -98174938174l );
		assertEquals( BigInteger.valueOf( -98174938174l ) , l2.getBigInteger() );
	}

	/**
	 * Test which verifies {@link LongType#setBigInteger(BigInteger)} can set
	 * LongTypes with a {@code BigInteger} and still return a {@code long} value.
	 */
	@Test
	public void testSetBigInteger() {

		final LongType l = new LongType( 72l );

		assertEquals( l.get(), 72l );

		final BigInteger bi = BigInteger.valueOf( 1093840120l );
		l.setBigInteger( bi );
		assertEquals( l.get(), 1093840120l );
	}

}
