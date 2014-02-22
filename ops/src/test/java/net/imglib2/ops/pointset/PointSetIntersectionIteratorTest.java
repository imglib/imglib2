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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests {@link PointSetIntersectionIterator}.
 * 
 * @author Barry DeZonia
 */
public class PointSetIntersectionIteratorTest {

	@Test
	public void test() {
		PointSet ps1 = new HyperVolumePointSet(new long[] { 0 }, new long[] { 10 });
		PointSet ps2 = new HyperVolumePointSet(new long[] { 7 }, new long[] { 15 });
		PointSet ps = new PointSetIntersection(ps1, ps2);
		PointSetIterator iter = ps.iterator();

		// regular sequence
		assertTrue(iter.hasNext());
		assertArrayEquals(new long[] { 7 }, iter.next());
		assertTrue(iter.hasNext());
		assertArrayEquals(new long[] { 8 }, iter.next());
		assertTrue(iter.hasNext());
		assertArrayEquals(new long[] { 9 }, iter.next());
		assertTrue(iter.hasNext());
		assertArrayEquals(new long[] { 10 }, iter.next());
		assertFalse(iter.hasNext());

		// another regular sequence
		iter.reset();
		assertTrue(iter.hasNext());
		iter.fwd();
		assertArrayEquals(new long[] { 7 }, iter.get());
		assertTrue(iter.hasNext());
		iter.fwd();
		assertArrayEquals(new long[] { 8 }, iter.get());
		assertTrue(iter.hasNext());
		iter.fwd();
		assertArrayEquals(new long[] { 9 }, iter.get());
		assertTrue(iter.hasNext());
		iter.fwd();
		assertArrayEquals(new long[] { 10 }, iter.get());
		assertFalse(iter.hasNext());

		// irregular sequences
		iter.reset();
		iter.next();
		assertArrayEquals(new long[] { 7 }, iter.get());
		iter.fwd();
		assertArrayEquals(new long[] { 8 }, iter.get());
		iter.next();
		assertArrayEquals(new long[] { 9 }, iter.get());
		iter.fwd();
		assertArrayEquals(new long[] { 10 }, iter.get());
		assertFalse(iter.hasNext());
		assertArrayEquals(new long[] { 10 }, iter.get());
	}

}
