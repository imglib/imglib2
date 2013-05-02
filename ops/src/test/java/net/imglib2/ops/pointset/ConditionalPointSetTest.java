package net.imglib2.ops.pointset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import net.imglib2.ops.condition.Condition;
import net.imglib2.ops.condition.RangeCondition;

import org.junit.Test;

public class ConditionalPointSetTest {

	@Test
	public void test() {
		PointSet hv = new HyperVolumePointSet(new long[]{15,15});
		Condition<long[]> cond = new RangeCondition(0, 1, 10, 1);
		PointSet ps = new ConditionalPointSet(hv, cond);
		
		assertEquals(150, ps.size());
		
		assertEquals(1, ps.min(0));
		assertEquals(0, ps.min(1));
		assertEquals(10, ps.max(0));
		assertEquals(14, ps.max(1));
		
		assertEquals(1, ps.realMin(0), 0);
		assertEquals(0, ps.realMin(1), 0);
		assertEquals(10, ps.realMax(0), 0);
		assertEquals(14, ps.realMax(1), 0);
		
		assertEquals(10, ps.dimension(0));
		assertEquals(15, ps.dimension(1));
	
		assertFalse(ps.includes(new long[]{0,5}));
		assertTrue(ps.includes(new long[]{1,5}));
		assertTrue(ps.includes(new long[]{2,5}));
		assertTrue(ps.includes(new long[]{9,10}));
		assertTrue(ps.includes(new long[]{10,10}));
		assertFalse(ps.includes(new long[]{11,10}));
		assertTrue(ps.includes(new long[]{8,14}));
		assertFalse(ps.includes(new long[]{8,15}));
		
		ps.translate(new long[]{1,2});

		assertEquals(1, ps.min(0));
		assertEquals(2, ps.min(1));
		assertEquals(10, ps.max(0));
		assertEquals(16, ps.max(1));
		
		assertEquals(1, ps.realMin(0), 0);
		assertEquals(2, ps.realMin(1), 0);
		assertEquals(10, ps.realMax(0), 0);
		assertEquals(16, ps.realMax(1), 0);
	}

}
