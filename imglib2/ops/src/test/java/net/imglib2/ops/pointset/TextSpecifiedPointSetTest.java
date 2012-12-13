package net.imglib2.ops.pointset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TextSpecifiedPointSetTest {

	@Test
	public void test() {
		PointSet ps = new TextSpecifiedPointSet("x=[1..4],y=[1..4], x > 2, y <= 2");

		assertEquals(4, ps.size());
		assertEquals(3, ps.min(0));
		assertEquals(1, ps.min(1));
		assertEquals(4, ps.max(0));
		assertEquals(2, ps.max(1));
		assertEquals(3, ps.realMin(0), 0);
		assertEquals(1, ps.realMin(1), 0);
		assertEquals(4, ps.realMax(0), 0);
		assertEquals(2, ps.realMax(1), 0);
		assertEquals(2, ps.dimension(0));
		assertEquals(2, ps.dimension(1));
		assertTrue(ps.includes(new long[]{3,1}));
		assertTrue(ps.includes(new long[]{4,2}));
		assertTrue(ps.includes(new long[]{3,2}));
		assertTrue(ps.includes(new long[]{4,1}));
		assertFalse(ps.includes(new long[]{1,1}));
		assertFalse(ps.includes(new long[]{2,2}));
		assertFalse(ps.includes(new long[]{3,3}));
		assertFalse(ps.includes(new long[]{4,4}));
		
		ps.translate(new long[]{1,1});

		assertEquals(2, ps.size());
		assertEquals(3, ps.min(0));
		assertEquals(2, ps.min(1));
		assertEquals(4, ps.max(0)); // NOTE: this is unintuitive. its because containing bounds go 1..4
		assertEquals(2, ps.max(1));
		assertEquals(3, ps.realMin(0), 0);
		assertEquals(2, ps.realMin(1), 0);
		assertEquals(4, ps.realMax(0), 0);
		assertEquals(2, ps.realMax(1), 0);
		assertEquals(2, ps.dimension(0));
		assertEquals(1, ps.dimension(1));
		assertTrue(ps.includes(new long[]{3,2}));
		assertTrue(ps.includes(new long[]{4,2}));
		assertFalse(ps.includes(new long[]{4,3}));
		assertFalse(ps.includes(new long[]{5,2}));
		assertFalse(ps.includes(new long[]{2,2}));
		assertFalse(ps.includes(new long[]{3,3}));
		assertFalse(ps.includes(new long[]{4,4}));
		assertFalse(ps.includes(new long[]{5,5}));
	}

}
