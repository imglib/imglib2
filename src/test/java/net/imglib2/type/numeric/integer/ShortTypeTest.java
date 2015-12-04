package net.imglib2.type.numeric.integer;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import org.junit.Test;


public class ShortTypeTest {

	/**
	 * Test which verifies {@link ShortType#getBigInteger()} returns the
	 * {@code BigInteger} representation of a ShortType.
	 */
	@Test
	public void testGetBigInteger() {

		final ShortType l = new ShortType( (short) 31498 );
		assertEquals( BigInteger.valueOf( 31498l ), l.getBigInteger() );

		final ShortType l2 = new ShortType( (short) -24691 );
		assertEquals( BigInteger.valueOf( -24691l ) , l2.getBigInteger() );
	}

	/**
	 * Test which verifies {@link ShortType#setBigInteger(BigInteger)} can set
	 * ShortTypes with a {@code BigInteger} and still return a {@code short}
	 * value.
	 */
	@Test
	public void testSetBigInteger() {

		final ShortType l = new ShortType( (short) 1082 );

		assertEquals( l.get(), (short) 1082 );

		final BigInteger bi = BigInteger.valueOf( 48906l );
		l.setBigInteger( bi );
		assertEquals( l.get(), (short) -16630 );
	}

}
