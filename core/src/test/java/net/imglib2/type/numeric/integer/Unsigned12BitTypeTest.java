package net.imglib2.type.numeric.integer;

import static org.junit.Assert.*;

import java.util.Random;

import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgFactory;

import org.junit.BeforeClass;
import org.junit.Test;

public class Unsigned12BitTypeTest
{
	static ArrayImg< Unsigned12BitType, ? > img;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		
		img = new ArrayImgFactory< Unsigned12BitType >().create( new long[]{ 10, 20, 30 }, new Unsigned12BitType() );
	}

	/**
	 * Test method for {@link net.imglib2.type.logic.BitType#setOne()}.
	 */
	@Test
	public void testSetRandom()
	{
		final Random rnd = new Random( 4096 );

		for ( final Unsigned12BitType t : img )
		{
			final int v = rnd.nextInt( 16 );
			t.set( v );
			assertTrue( t.get() == v );
		}
	}
}
