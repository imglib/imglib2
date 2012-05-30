package net.imglib2.type.numeric.integer;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestUnsignedLongType {

	@Test
	public void test() {
		UnsignedLongType u1 = new UnsignedLongType( 1 ),
		                 u2 = new UnsignedLongType( 2 ),
		                 uPreMax = new UnsignedLongType( 0xfffffffffffffffeL ),
		                 uMax = new UnsignedLongType( 0xffffffffffffffffL ),
		                 u = new UnsignedLongType();
		
		// Check if adding 1 to the almost maximum value results in the maximum value:
		u.set( uPreMax );
		u.add( u1 );
		assertTrue(u.get() == uMax.get() );
		assertTrue(0 == u.compareTo(uMax));
		
		// Check if subracting one returns that almost maximum value:
		u.sub( u1 );
		assertTrue(u.get() == uPreMax.get());
		assertTrue(0 == u.compareTo(uPreMax));
		
		// Check if taking the maximum signed long value, multiplying it by 2 and adding 1
		// results in the maximum unsigned value:
		u.set( Long.MAX_VALUE );
		u.mul( u2 );
		u.add( u1 );
		assertTrue("\n" + Long.toBinaryString(u.get()) + "\n != \n" + Long.toBinaryString(uMax.get()),
				Long.toBinaryString(u.get()).equals(Long.toBinaryString(uMax.get())));
		assertTrue(0 == u.compareTo(uMax));
		
		// Check if adding 2 to the maximum value wraps around and becomes 1
		u.set( uMax );
		u.add( u2 );
		assertTrue(0 == u.compareTo( u1 ));
		
		// Check if a number with its bit sign not set is smaller than one with it:
		u.set( Long.MIN_VALUE );
		assertTrue( -1 == u.compareTo( new UnsignedLongType( - Long.MAX_VALUE )));
		
		// Check if a number with its bit sign set is larger than one without it:
		u.set( - Long.MIN_VALUE );
		assertTrue( 1 == u.compareTo( new UnsignedLongType( Long.MAX_VALUE )));
	}
}
