package net.imglib2.ops.pointset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class OnePointSetTest {

	@Test
	public void test() {
		PointSet ps = new OnePointSet(new long[]{73,45});
		
		assertEquals(1, ps.size());
		assertEquals(73, ps.min(0));
		assertEquals(45, ps.min(1));
		assertEquals(73, ps.max(0));
		assertEquals(45, ps.max(1));
		assertEquals(73, ps.realMin(0), 0);
		assertEquals(45, ps.realMin(1), 0);
		assertEquals(73, ps.realMax(0), 0);
		assertEquals(45, ps.realMax(1), 0);
		assertEquals(1, ps.dimension(0));
		assertEquals(1, ps.dimension(1));
		assertTrue(ps.includes(new long[]{73,45}));
		assertFalse(ps.includes(new long[]{72,45}));
		assertFalse(ps.includes(new long[]{73,44}));
		assertFalse(ps.includes(new long[]{5,4}));
		
		ps.translate(new long[]{1,2});

		assertEquals(1, ps.size());
		assertEquals(74, ps.min(0));
		assertEquals(47, ps.min(1));
		assertEquals(74, ps.max(0));
		assertEquals(47, ps.max(1));
		assertEquals(74, ps.realMin(0), 0);
		assertEquals(47, ps.realMin(1), 0);
		assertEquals(74, ps.realMax(0), 0);
		assertEquals(47, ps.realMax(1), 0);
		assertEquals(1, ps.dimension(0));
		assertEquals(1, ps.dimension(1));
		assertTrue(ps.includes(new long[]{74,47}));
		assertFalse(ps.includes(new long[]{73,47}));
		assertFalse(ps.includes(new long[]{74,46}));
		assertFalse(ps.includes(new long[]{6,6}));
	}

}
