package net.imglib2.ops.pointset;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import net.imglib2.ops.condition.Condition;
import net.imglib2.ops.condition.RangeCondition;

import org.junit.Test;


public class ConditionalPointSetIteratorTest {

	@Test
	public void test() {
		PointSet ps = new HyperVolumePointSet(new long[] { 10 });
		Condition<long[]> cond = new RangeCondition(0, 1, 8, 2);
		ConditionalPointSet cps = new ConditionalPointSet(ps, cond);

		PointSetIterator iter = cps.iterator();

		// example of expected iteration

		assertEquals(true, iter.hasNext());
		assertArrayEquals(new long[] { 1 }, iter.next());
		assertEquals(true, iter.hasNext());
		assertArrayEquals(new long[] { 3 }, iter.next());
		assertEquals(true, iter.hasNext());
		assertArrayEquals(new long[] { 5 }, iter.next());
		assertEquals(true, iter.hasNext());
		assertArrayEquals(new long[] { 7 }, iter.next());
		assertEquals(false, iter.hasNext());

		iter.reset();
		assertNull(iter.get());
		assertEquals(true, iter.hasNext());

		// example2 of expected iteration

		iter.reset();
		iter.fwd();
		assertArrayEquals(new long[] { 1 }, iter.get());
		iter.fwd();
		assertArrayEquals(new long[] { 3 }, iter.get());
		iter.fwd();
		assertArrayEquals(new long[] { 5 }, iter.get());
		iter.fwd();
		assertArrayEquals(new long[] { 7 }, iter.get());
		assertEquals(false, iter.hasNext());

		// example 1 of an unexpected iteration

		iter.reset();
		assertEquals(true, iter.hasNext());
		assertArrayEquals(new long[] { 1 }, iter.next());
		assertArrayEquals(new long[] { 3 }, iter.next());
		assertArrayEquals(new long[] { 5 }, iter.next());
		assertArrayEquals(new long[] { 7 }, iter.next());
		assertEquals(false, iter.hasNext());

		// example 2 of an unexpected iteration

		iter.reset();
		iter.fwd();
		assertArrayEquals(new long[] { 1 }, iter.get());
		iter.fwd();
		assertArrayEquals(new long[] { 3 }, iter.get());
		iter.fwd();
		assertArrayEquals(new long[] { 5 }, iter.get());
		iter.fwd();
		assertArrayEquals(new long[] { 7 }, iter.get());
		assertEquals(false, iter.hasNext());

		// example 3 of an unexpected iteration

		iter.reset();
		iter.fwd();
		assertArrayEquals(new long[] { 1 }, iter.get());
		iter.fwd();
		assertArrayEquals(new long[] { 3 }, iter.get());
		assertEquals(true, iter.hasNext());
		iter.fwd();
		assertArrayEquals(new long[] { 5 }, iter.get());
		iter.fwd();
		assertArrayEquals(new long[] { 7 }, iter.get());
		assertEquals(false, iter.hasNext());

		// this code should pass!!!!
		iter.reset();
		for (int i = 0; i < 20; i++)
			assertEquals(true, iter.hasNext());

		// do a combo of weird stuff

		iter.reset();
		assertArrayEquals(new long[] { 1 }, iter.next());
		assertEquals(true, iter.hasNext());
		iter.fwd();
		assertArrayEquals(new long[] { 3 }, iter.get());
		assertArrayEquals(new long[] { 5 }, iter.next());
		iter.fwd();
		assertEquals(false, iter.hasNext());
		assertArrayEquals(new long[] { 7 }, iter.get());

		// purposely go too far
		iter.reset();
		iter.fwd();
		iter.fwd();
		iter.fwd();
		iter.fwd();
		try {
			iter.fwd();
			fail("did not catch fwd() beyond end");
		}
		catch (Exception e) {
			assertEquals(true, true);
		}
	}

}
