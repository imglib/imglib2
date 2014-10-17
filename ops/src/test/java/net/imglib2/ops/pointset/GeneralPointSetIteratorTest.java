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

import java.util.ArrayList;
import java.util.List;

import net.imglib2.Cursor;

import org.junit.Test;

/**
 * Tests {@link GeneralPointSetIterator}.
 * 
 * @author Barry DeZonia
 */
public class GeneralPointSetIteratorTest {

	@Test
	public void test() {
		List<long[]> points = new ArrayList<long[]>();

		points.add(new long[] { 1 });
		points.add(new long[] { 2 });
		points.add(new long[] { 3 });
		points.add(new long[] { 4 });

		PointSet ps = new GeneralPointSet(new long[] { 0 }, points);

		Cursor<long[]> iter = ps.iterator();

		// test regular sequence
		assertEquals(true, iter.hasNext());
		assertArrayEquals(new long[] { 1 }, iter.next());
		assertEquals(true, iter.hasNext());
		assertArrayEquals(new long[] { 2 }, iter.next());
		assertEquals(true, iter.hasNext());
		assertArrayEquals(new long[] { 3 }, iter.next());
		assertEquals(true, iter.hasNext());
		assertArrayEquals(new long[] { 4 }, iter.next());
		assertEquals(false, iter.hasNext());

		// test another regular sequence
		iter.reset();
		iter.fwd();
		assertArrayEquals(new long[] { 1 }, iter.get());
		iter.fwd();
		assertArrayEquals(new long[] { 2 }, iter.get());
		iter.fwd();
		assertArrayEquals(new long[] { 3 }, iter.get());
		iter.fwd();
		assertArrayEquals(new long[] { 4 }, iter.get());
		assertEquals(false, iter.hasNext());

		// test some unexpected sequences
		iter.reset();
		assertNull(iter.get());
		iter.fwd();
		assertArrayEquals(new long[] { 1 }, iter.get());
		iter.next();
		assertArrayEquals(new long[] { 2 }, iter.get());
		assertEquals(true, iter.hasNext());
		assertEquals(true, iter.hasNext());
		assertEquals(true, iter.hasNext());
		assertEquals(true, iter.hasNext());
		assertArrayEquals(new long[] { 2 }, iter.get());
		iter.next();
		assertArrayEquals(new long[] { 3 }, iter.get());
		iter.fwd();
		assertEquals(false, iter.hasNext());
		assertEquals(false, iter.hasNext());
		assertArrayEquals(new long[] { 4 }, iter.get());
	}
}
