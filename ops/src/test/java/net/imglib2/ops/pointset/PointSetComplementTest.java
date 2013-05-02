package net.imglib2.ops.pointset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

public class PointSetComplementTest {

	@Test
	public void test() {
		ArrayList<long[]> points = new ArrayList<long[]>();
		points.add(new long[]{0,0});
		points.add(new long[]{2,0});
		points.add(new long[]{0,2});
		points.add(new long[]{2,2});
		PointSet ps1 = new GeneralPointSet(points.get(0), points);
		PointSet ps = new PointSetComplement(ps1);
		
		assertEquals(5, ps.size());
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
		assertTrue(ps.includes(new long[]{0,1}));
		assertTrue(ps.includes(new long[]{1,1}));
		assertTrue(ps.includes(new long[]{1,0}));
		assertFalse(ps.includes(new long[]{0,0}));
		assertFalse(ps.includes(new long[]{0,2}));
		assertFalse(ps.includes(new long[]{2,0}));
		assertFalse(ps.includes(new long[]{2,2}));
		
		ps.translate(new long[]{1,2});

		assertEquals(5, ps.size());
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
		assertTrue(ps.includes(new long[]{1,3}));
		assertTrue(ps.includes(new long[]{2,3}));
		assertTrue(ps.includes(new long[]{2,2}));
		assertFalse(ps.includes(new long[]{1,2}));
		assertFalse(ps.includes(new long[]{1,4}));
		assertFalse(ps.includes(new long[]{3,2}));
		assertFalse(ps.includes(new long[]{3,4}));
	}

}
