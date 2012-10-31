package net.imglib2.ops.pointset;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;


public class PointSetComplementIteratorTest {

	@Test
	public void test() {
		ArrayList<long[]> pts = new ArrayList<long[]>();
		pts.add(new long[] { 0 });
		pts.add(new long[] { 2 });
		pts.add(new long[] { 4 });
		pts.add(new long[] { 6 });
		pts.add(new long[] { 8 });
		PointSet otherPs = new GeneralPointSet(new long[] { 0 }, pts);
		PointSet ps = new PointSetComplement(otherPs);
		PointSetIterator iter = ps.iterator();

		// regular sequence
		assertTrue(iter.hasNext());
		assertArrayEquals(new long[] { 1 }, iter.next());
		assertTrue(iter.hasNext());
		assertArrayEquals(new long[] { 3 }, iter.next());
		assertTrue(iter.hasNext());
		assertArrayEquals(new long[] { 5 }, iter.next());
		assertTrue(iter.hasNext());
		assertArrayEquals(new long[] { 7 }, iter.next());
		assertFalse(iter.hasNext());

		// another regular sequence
		iter.reset();
		assertTrue(iter.hasNext());
		iter.fwd();
		assertArrayEquals(new long[] { 1 }, iter.get());
		assertTrue(iter.hasNext());
		iter.fwd();
		assertArrayEquals(new long[] { 3 }, iter.get());
		assertTrue(iter.hasNext());
		iter.fwd();
		assertArrayEquals(new long[] { 5 }, iter.get());
		assertTrue(iter.hasNext());
		iter.fwd();
		assertArrayEquals(new long[] { 7 }, iter.get());
		assertFalse(iter.hasNext());

		// irregular sequences
		iter.reset();
		iter.next();
		assertArrayEquals(new long[] { 1 }, iter.get());
		iter.fwd();
		assertArrayEquals(new long[] { 3 }, iter.get());
		iter.next();
		assertArrayEquals(new long[] { 5 }, iter.get());
		iter.fwd();
		assertArrayEquals(new long[] { 7 }, iter.get());
		assertFalse(iter.hasNext());
		assertArrayEquals(new long[] { 7 }, iter.get());
	}

}
