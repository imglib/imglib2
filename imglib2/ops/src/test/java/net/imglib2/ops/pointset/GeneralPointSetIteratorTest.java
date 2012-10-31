package net.imglib2.ops.pointset;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import net.imglib2.Cursor;

import org.junit.Test;

public class GeneralPointSetIteratorTest {

	@Test
	public void test() {
		List<long[]> points = new ArrayList<long[]>();

		points.add(new long[] { 1 });
		points.add(new long[] { 2 });
		points.add(new long[] { 3 });
		points.add(new long[] { 4 });

		PointSet ps = new GeneralPointSet(new long[] { 0 }, points);

		Cursor<long[]> iter = ps.iterator();

		// test regular sequence
		assertEquals(true, iter.hasNext());
		assertArrayEquals(new long[] { 1 }, iter.next());
		assertEquals(true, iter.hasNext());
		assertArrayEquals(new long[] { 2 }, iter.next());
		assertEquals(true, iter.hasNext());
		assertArrayEquals(new long[] { 3 }, iter.next());
		assertEquals(true, iter.hasNext());
		assertArrayEquals(new long[] { 4 }, iter.next());
		assertEquals(false, iter.hasNext());

		// test another regular sequence
		iter.reset();
		iter.fwd();
		assertArrayEquals(new long[] { 1 }, iter.get());
		iter.fwd();
		assertArrayEquals(new long[] { 2 }, iter.get());
		iter.fwd();
		assertArrayEquals(new long[] { 3 }, iter.get());
		iter.fwd();
		assertArrayEquals(new long[] { 4 }, iter.get());
		assertEquals(false, iter.hasNext());

		// test some unexpected sequences
		iter.reset();
		assertNull(iter.get());
		iter.fwd();
		assertArrayEquals(new long[] { 1 }, iter.get());
		iter.next();
		assertArrayEquals(new long[] { 2 }, iter.get());
		assertEquals(true, iter.hasNext());
		assertEquals(true, iter.hasNext());
		assertEquals(true, iter.hasNext());
		assertEquals(true, iter.hasNext());
		assertArrayEquals(new long[] { 2 }, iter.get());
		iter.next();
		assertArrayEquals(new long[] { 3 }, iter.get());
		iter.fwd();
		assertEquals(false, iter.hasNext());
		assertEquals(false, iter.hasNext());
		assertArrayEquals(new long[] { 4 }, iter.get());
	}
}
