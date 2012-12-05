package net.imglib2.ops.pointset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import net.imglib2.roi.RectangleRegionOfInterest;
import net.imglib2.roi.RegionOfInterest;

import org.junit.Test;

public class RoiPointSetTest {

	@Test
	public void test() {
		double[] origin = new double[] { 0, 0 };
		double[] extent = new double[] { 2, 2 };
		RegionOfInterest roi = new RectangleRegionOfInterest(origin, extent);
		PointSet ps = new RoiPointSet(roi);

		assertEquals(4, ps.size());
		assertEquals(0, ps.min(0));
		assertEquals(0, ps.min(1));
		assertEquals(2, ps.max(0));
		assertEquals(2, ps.max(1));
		assertEquals(0, ps.realMin(0), 0);
		assertEquals(0, ps.realMin(1), 0);
		assertEquals(2, ps.realMax(0), 0);
		assertEquals(2, ps.realMax(1), 0);
		assertEquals(3, ps.dimension(0));
		assertEquals(3, ps.dimension(1));
		assertTrue(ps.includes(new long[]{0,0}));
		assertTrue(ps.includes(new long[]{0,1}));
		assertTrue(ps.includes(new long[]{1,0}));
		assertTrue(ps.includes(new long[]{1,1}));
		assertFalse(ps.includes(new long[]{-1,-1}));
		assertFalse(ps.includes(new long[]{2,3}));
		assertFalse(ps.includes(new long[]{0,4}));
		
		ps.translate(new long[]{1,2});
		
		assertEquals(4, ps.size());
		assertEquals(1, ps.min(0));
		assertEquals(2, ps.min(1));
		assertEquals(3, ps.max(0));
		assertEquals(4, ps.max(1));
		assertEquals(1, ps.realMin(0), 0);
		assertEquals(2, ps.realMin(1), 0);
		assertEquals(3, ps.realMax(0), 0);
		assertEquals(4, ps.realMax(1), 0);
		assertEquals(3, ps.dimension(0));
		assertEquals(3, ps.dimension(1));
		assertTrue(ps.includes(new long[]{1,2}));
		assertTrue(ps.includes(new long[]{1,3}));
		assertTrue(ps.includes(new long[]{2,2}));
		assertTrue(ps.includes(new long[]{2,3}));
		assertFalse(ps.includes(new long[]{0,1}));
		assertFalse(ps.includes(new long[]{3,5}));
		assertFalse(ps.includes(new long[]{1,6}));
	}

}
