package net.imglib2.type.numeric.integer;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.util.Random;

import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgFactory;

import org.junit.BeforeClass;
import org.junit.Test;

public class Unsigned128BitTypeTest
{
	static ArrayImg< Unsigned128BitType, ? > img;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		
		img = new ArrayImgFactory< Unsigned128BitType >().create( new long[]{ 10, 20, 30 }, new Unsigned128BitType() );
	}

	/**
	 * Test method for {@link net.imglib2.type.numeric.integer.Unsigned128BitType}.
	 */
	@Test
	public void testSetRandom()
	{
		final Random rnd = new Random( 4096 );

		for ( final Unsigned128BitType t : img )
		{
			final long v1 = Math.abs( rnd.nextLong() );
			final long v2 = Math.abs( rnd.nextLong() );

			final BigInteger b = new BigInteger( v1 + "" + v2 );

			t.set( b );

			assertTrue( t.get().compareTo( b ) == 0 );
		}
	}
}
