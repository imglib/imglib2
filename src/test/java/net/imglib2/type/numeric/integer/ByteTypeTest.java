package net.imglib2.type.numeric.integer;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import org.junit.Test;


public class ByteTypeTest {

	/**
	 * Test which verifies {@link ByteType#getBigInteger()} returns the
	 * {@code BigInteger} representation of a ByteType.
	 */
	@Test
	public void testGetBigInteger() {

		final ByteType l = new ByteType( (byte) 124 );
		assertEquals( BigInteger.valueOf( 124l ), l.getBigInteger() );

		final ByteType l2 = new ByteType( (byte) -18 );
		assertEquals( BigInteger.valueOf( -18l ) , l2.getBigInteger() );
	}

	/**
	 * Test which verifies {@link ByteType#setBigInteger(BigInteger)} can set
	 * ByteTypes with a {@code BigInteger} and still return a {@code byte} value.
	 */
	@Test
	public void testSetBigInteger() {

		final ByteType l = new ByteType( (byte) 93 );

		assertEquals( l.get(), (byte) 93 );

		final BigInteger bi = BigInteger.valueOf( -71l );
		l.setBigInteger( bi );
		assertEquals( l.get(), (byte) -71 );
	}

}
