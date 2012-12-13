package net.imglib2.ops.pointset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import net.imglib2.IterableInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.real.DoubleType;

import org.junit.Test;

public class IterableIntervalPointSetTest {

	@Test
	public void test() {
		ArrayImgFactory<DoubleType> factory = new ArrayImgFactory<DoubleType>();
		Img<DoubleType> img = factory.create(new long[]{3,3}, new DoubleType());
		IterableInterval<DoubleType> interval = img;
		PointSet ps = new IterableIntervalPointSet(interval);
		assertEquals(9, ps.size());
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
		assertTrue(ps.includes(new long[]{1,1}));
		assertTrue(ps.includes(new long[]{2,2}));
		assertTrue(ps.includes(new long[]{0,2}));
		assertTrue(ps.includes(new long[]{2,0}));
		assertFalse(ps.includes(new long[]{3,0}));
		assertFalse(ps.includes(new long[]{2,3}));
		assertFalse(ps.includes(new long[]{1,-1}));
		
		try {
			ps.translate(new long[]{1,2});
			fail();
		} catch (Exception e) {
			assertTrue(true);
		}
	}

}
