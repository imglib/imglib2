package net.imglib2.ops.pointset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PointSetIntersectionTest {

	@Test
	public void test() {
		PointSet ps1 = new HyperVolumePointSet(new long[] { 0 }, new long[] { 10 });
		PointSet ps2 = new HyperVolumePointSet(new long[] { 7 }, new long[] { 15 });
		PointSet ps = new PointSetIntersection(ps1, ps2);
		
		assertEquals(4, ps.size());
		assertEquals(7, ps.min(0));
		assertEquals(10, ps.max(0));
		assertEquals(7, ps.realMin(0), 0);
		assertEquals(10, ps.realMax(0), 0);
		assertEquals(4, ps.dimension(0));
		assertTrue(ps.includes(new long[]{7}));
		assertTrue(ps.includes(new long[]{8}));
		assertTrue(ps.includes(new long[]{9}));
		assertTrue(ps.includes(new long[]{10}));
		assertFalse(ps.includes(new long[]{6}));
		assertFalse(ps.includes(new long[]{11}));
		
		ps.translate(new long[]{2});

		assertEquals(4, ps.size());
		assertEquals(9, ps.min(0));
		assertEquals(12, ps.max(0));
		assertEquals(9, ps.realMin(0), 0);
		assertEquals(12, ps.realMax(0), 0);
		assertEquals(4, ps.dimension(0));
		assertTrue(ps.includes(new long[]{9}));
		assertTrue(ps.includes(new long[]{10}));
		assertTrue(ps.includes(new long[]{11}));
		assertTrue(ps.includes(new long[]{12}));
		assertFalse(ps.includes(new long[]{8}));
		assertFalse(ps.includes(new long[]{13}));
	}

}
