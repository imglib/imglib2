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
