package net.imglib2.ops.pointset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PointSetDifferenceTest {

	@Test
	public void test() {
		PointSet ps1 = new HyperVolumePointSet(new long[] { 8 });
		PointSet ps2 = new HyperVolumePointSet(new long[] { 4 }, new long[] { 10 });
		PointSet ps = new PointSetDifference(ps1, ps2);
		
		assertEquals(4, ps.size());
		assertEquals(0, ps.min(0));
		assertEquals(3, ps.max(0));
		assertEquals(0, ps.realMin(0), 0);
		assertEquals(3, ps.realMax(0), 0);
		assertEquals(4, ps.dimension(0));
		assertTrue(ps.includes(new long[]{0}));
		assertTrue(ps.includes(new long[]{1}));
		assertTrue(ps.includes(new long[]{2}));
		assertTrue(ps.includes(new long[]{3}));
		assertFalse(ps.includes(new long[]{-1}));
		assertFalse(ps.includes(new long[]{4}));
		
		ps.translate(new long[]{2});

		assertEquals(4, ps.size());
		assertEquals(2, ps.min(0));
		assertEquals(5, ps.max(0));
		assertEquals(2, ps.realMin(0), 0);
		assertEquals(5, ps.realMax(0), 0);
		assertEquals(4, ps.dimension(0));
		assertTrue(ps.includes(new long[]{2}));
		assertTrue(ps.includes(new long[]{3}));
		assertTrue(ps.includes(new long[]{4}));
		assertTrue(ps.includes(new long[]{5}));
		assertFalse(ps.includes(new long[]{1}));
		assertFalse(ps.includes(new long[]{6}));
	}

}
