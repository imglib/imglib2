package net.imglib2.type.numeric.integer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class UnsignedLongTypeTest {

	private UnsignedLongType u = new UnsignedLongType();
	private UnsignedLongType t = new UnsignedLongType();

	@Test
	public void testComparePosNeg(){

		u.set( -1L );
		t.set( 1L );
		assertTrue( u.compareTo( t ) >= 1 );

		u.set( 9223372036854775807L );
		t.set( -9223372036854775808L );
		assertTrue( u.compareTo( t ) <= -1 );

		u.set( -109817491384701984L );
		t.set( 12L );
		assertTrue( u.compareTo( t ) >= 1 );

	}

	@Test
	public void testCompareNegatives(){

		u.set( -9000L );
		t.set( -9000L );
		assertEquals( u.compareTo( t ), 0 );

		u.set( -16L );
		t.set( -10984012840123984L );
		assertTrue( u.compareTo( t ) >= 1 );

		u.set( -500L );
		t.set( -219L );
		assertTrue( u.compareTo( t ) <= -1 );

	}

	@Test
	public void testComparePositives(){

		u.set( 100L );
		t.set( 100L );
		assertEquals( u.compareTo( t ), 0);

		u.set( 3098080948019L );
		t.set( 1L );
		assertTrue( u.compareTo( t ) >= 1 );

		u.set( 199L );
		t.set( 299L );
		assertTrue( u.compareTo( t ) <= -1 );

	}

	@Test
	public void testCompareZero() {

		u.set( 0L );
		t.set( 0L );
		assertEquals( u.compareTo( t ), 0 );

		u.set( -17112921L );
		t.set( 0L );
		assertTrue( u.compareTo( t ) >= 1 );

		u.set( 0L );
		t.set( 698L );
		assertTrue( u.compareTo( t ) <= -1 );

	}

}
