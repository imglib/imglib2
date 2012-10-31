package net.imglib2.ops.pointset;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class PointSetIntersectionIteratorTest {

	@Test
	public void test() {
		PointSet ps1 = new HyperVolumePointSet(new long[] { 0 }, new long[] { 10 });
		PointSet ps2 = new HyperVolumePointSet(new long[] { 7 }, new long[] { 15 });
		PointSet ps = new PointSetIntersection(ps1, ps2);
		PointSetIterator iter = ps.iterator();

		// regular sequence
		assertTrue(iter.hasNext());
		assertArrayEquals(new long[] { 7 }, iter.next());
		assertTrue(iter.hasNext());
		assertArrayEquals(new long[] { 8 }, iter.next());
		assertTrue(iter.hasNext());
		assertArrayEquals(new long[] { 9 }, iter.next());
		assertTrue(iter.hasNext());
		assertArrayEquals(new long[] { 10 }, iter.next());
		assertFalse(iter.hasNext());

		// another regular sequence
		iter.reset();
		assertTrue(iter.hasNext());
		iter.fwd();
		assertArrayEquals(new long[] { 7 }, iter.get());
		assertTrue(iter.hasNext());
		iter.fwd();
		assertArrayEquals(new long[] { 8 }, iter.get());
		assertTrue(iter.hasNext());
		iter.fwd();
		assertArrayEquals(new long[] { 9 }, iter.get());
		assertTrue(iter.hasNext());
		iter.fwd();
		assertArrayEquals(new long[] { 10 }, iter.get());
		assertFalse(iter.hasNext());

		// irregular sequences
		iter.reset();
		iter.next();
		assertArrayEquals(new long[] { 7 }, iter.get());
		iter.fwd();
		assertArrayEquals(new long[] { 8 }, iter.get());
		iter.next();
		assertArrayEquals(new long[] { 9 }, iter.get());
		iter.fwd();
		assertArrayEquals(new long[] { 10 }, iter.get());
		assertFalse(iter.hasNext());
		assertArrayEquals(new long[] { 10 }, iter.get());
	}

}
