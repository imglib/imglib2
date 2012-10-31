package net.imglib2.ops.pointset;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class TextSpecifiedPointSetIteratorTest {

	@Test
	public void test() {
		PointSet ps = new TextSpecifiedPointSet("x=[1..4],y=[1..4], x > 2, y <= 2");
		PointSetIterator iter = ps.iterator();

		// regular sequence
		assertTrue(iter.hasNext());
		assertArrayEquals(new long[] { 3, 1 }, iter.next());
		assertTrue(iter.hasNext());
		assertArrayEquals(new long[] { 4, 1 }, iter.next());
		assertTrue(iter.hasNext());
		assertArrayEquals(new long[] { 3, 2 }, iter.next());
		assertTrue(iter.hasNext());
		assertArrayEquals(new long[] { 4, 2 }, iter.next());
		assertFalse(iter.hasNext());

		// another regular sequence
		iter.reset();
		assertTrue(iter.hasNext());
		iter.fwd();
		assertArrayEquals(new long[] { 3, 1 }, iter.get());
		assertTrue(iter.hasNext());
		iter.fwd();
		assertArrayEquals(new long[] { 4, 1 }, iter.get());
		assertTrue(iter.hasNext());
		iter.fwd();
		assertArrayEquals(new long[] { 3, 2 }, iter.get());
		assertTrue(iter.hasNext());
		iter.fwd();
		assertArrayEquals(new long[] { 4, 2 }, iter.get());
		assertFalse(iter.hasNext());

		// irregular sequences
		iter.reset();
		iter.next();
		assertArrayEquals(new long[] { 3, 1 }, iter.get());
		iter.fwd();
		assertArrayEquals(new long[] { 4, 1 }, iter.get());
		iter.next();
		assertArrayEquals(new long[] { 3, 2 }, iter.get());
		iter.fwd();
		assertArrayEquals(new long[] { 4, 2 }, iter.get());
		assertFalse(iter.hasNext());
		assertArrayEquals(new long[] { 4, 2 }, iter.get());

	}

}
