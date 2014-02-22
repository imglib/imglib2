/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package net.imglib2.ops.pointset;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import net.imglib2.ops.condition.Condition;
import net.imglib2.ops.condition.RangeCondition;

import org.junit.Test;

/**
 * Tests {@link ConditionalPointSetIterator}.
 * 
 * @author Barry DeZonia
 */
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
