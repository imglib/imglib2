package net.imglib2.ops.pointset;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class OnePointSetIteratorTest {

	@Test
	public void test() {

		long[] point = new long[] { 1, 2, 3 };
		PointSet ps = new OnePointSet(point);
		PointSetIterator iter = ps.iterator();

		// regular test

		assertTrue(iter.hasNext());
		assertTrue(iter.hasNext());
		assertArrayEquals(point, iter.next());
		assertFalse(iter.hasNext());
		assertFalse(iter.hasNext());

		// another regular test
		iter.reset();
		assertTrue(iter.hasNext());
		assertTrue(iter.hasNext());
		iter.fwd();
		assertArrayEquals(point, iter.get());
		assertFalse(iter.hasNext());
		assertFalse(iter.hasNext());

		// weird orders
		iter.reset();
		assertTrue(iter.hasNext());
		assertTrue(iter.hasNext());
		iter.fwd();
		assertEquals(1, iter.getLongPosition(0));
		assertEquals(2, iter.getLongPosition(1));
		assertEquals(3, iter.getLongPosition(2));
		assertArrayEquals(point, iter.get());
		assertFalse(iter.hasNext());
		assertFalse(iter.hasNext());
	}

}
