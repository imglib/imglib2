package net.imglib2.ops.pointset;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class PointSetDifferenceIteratorTest {

	@Test
	public void test() {
		PointSet ps1 = new HyperVolumePointSet(new long[] { 8 });
		PointSet ps2 = new HyperVolumePointSet(new long[] { 4 }, new long[] { 10 });
		PointSet ps = new PointSetDifference(ps1, ps2);
		PointSetIterator iter = ps.iterator();

		// regular sequence
		assertTrue(iter.hasNext());
		assertArrayEquals(new long[] { 0 }, iter.next());
		assertTrue(iter.hasNext());
		assertArrayEquals(new long[] { 1 }, iter.next());
		assertTrue(iter.hasNext());
		assertArrayEquals(new long[] { 2 }, iter.next());
		assertTrue(iter.hasNext());
		assertArrayEquals(new long[] { 3 }, iter.next());
		assertFalse(iter.hasNext());

		// another regular sequence
		iter.reset();
		assertTrue(iter.hasNext());
		iter.fwd();
		assertArrayEquals(new long[] { 0 }, iter.get());
		assertTrue(iter.hasNext());
		iter.fwd();
		assertArrayEquals(new long[] { 1 }, iter.get());
		assertTrue(iter.hasNext());
		iter.fwd();
		assertArrayEquals(new long[] { 2 }, iter.get());
		assertTrue(iter.hasNext());
		iter.fwd();
		assertArrayEquals(new long[] { 3 }, iter.get());
		assertFalse(iter.hasNext());

		// irregular sequences
		iter.reset();
		iter.next();
		assertArrayEquals(new long[] { 0 }, iter.get());
		iter.fwd();
		assertArrayEquals(new long[] { 1 }, iter.get());
		iter.next();
		assertArrayEquals(new long[] { 2 }, iter.get());
		iter.fwd();
		assertArrayEquals(new long[] { 3 }, iter.get());
		assertFalse(iter.hasNext());
		assertArrayEquals(new long[] { 3 }, iter.get());
	}

}
