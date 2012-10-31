package net.imglib2.ops.pointset;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import net.imglib2.roi.RectangleRegionOfInterest;
import net.imglib2.roi.RegionOfInterest;

import org.junit.Test;


public class RoiPointSetIteratorTest {

	@Test
	public void test() {
		double[] origin = new double[] { 0, 0 };
		double[] extent = new double[] { 2, 2 };
		RegionOfInterest roi = new RectangleRegionOfInterest(origin, extent);
		PointSet ps = new RoiPointSet(roi);
		PointSetIterator iter = ps.iterator();

		// regular sequence
		assertTrue(iter.hasNext());
		assertArrayEquals(new long[] { 0, 0 }, iter.next());
		assertTrue(iter.hasNext());
		assertArrayEquals(new long[] { 1, 0 }, iter.next());
		assertTrue(iter.hasNext());
		assertArrayEquals(new long[] { 0, 1 }, iter.next());
		assertTrue(iter.hasNext());
		assertArrayEquals(new long[] { 1, 1 }, iter.next());
		assertFalse(iter.hasNext());

		// another regular sequence
		iter.reset();
		assertTrue(iter.hasNext());
		iter.fwd();
		assertArrayEquals(new long[] { 0, 0 }, iter.get());
		assertTrue(iter.hasNext());
		iter.fwd();
		assertArrayEquals(new long[] { 1, 0 }, iter.get());
		assertTrue(iter.hasNext());
		iter.fwd();
		assertArrayEquals(new long[] { 0, 1 }, iter.get());
		assertTrue(iter.hasNext());
		iter.fwd();
		assertArrayEquals(new long[] { 1, 1 }, iter.get());
		assertFalse(iter.hasNext());

		// irregular sequences
		iter.reset();
		iter.next();
		assertArrayEquals(new long[] { 0, 0 }, iter.get());
		iter.fwd();
		assertArrayEquals(new long[] { 1, 0 }, iter.get());
		iter.next();
		assertArrayEquals(new long[] { 0, 1 }, iter.get());
		iter.fwd();
		assertArrayEquals(new long[] { 1, 1 }, iter.get());
		assertFalse(iter.hasNext());
		assertArrayEquals(new long[] { 1, 1 }, iter.get());

	}

}
