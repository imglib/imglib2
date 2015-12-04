package net.imglib2.type.numeric.integer;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import org.junit.Test;


public class IntTypeTest {

	/**
	 * Test which verifies {@link IntType#getBigInteger()} returns the
	 * {@code BigInteger} representation of an IntType.
	 */
	@Test
	public void testGetBigInteger() {

		final IntType l = new IntType( 10948 );
		assertEquals( BigInteger.valueOf( 10948l ), l.getBigInteger() );

		final IntType l2 = new IntType( -40913824 );
		assertEquals( BigInteger.valueOf( -40913824l ) , l2.getBigInteger() );
	}

	/**
	 * Test which verifies {@link IntType#setBigInteger(BigInteger)}
	 * can set IntTypes with a {@code BigInteger} and still return an
	 * {@code int} value.
	 */
	@Test
	public void testSetBigInteger() {

		final IntType l = new IntType( 0 );

		assertEquals( l.get(), 0 );

		final BigInteger bi = BigInteger.valueOf( 7987431l );
		l.setBigInteger( bi );
		assertEquals( l.get(), 7987431 );
	}

}
