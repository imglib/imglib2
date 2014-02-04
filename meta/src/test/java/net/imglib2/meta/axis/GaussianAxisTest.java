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

package net.imglib2.meta.axis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import net.imglib2.meta.AbstractMetaTest;
import net.imglib2.meta.Axes;

import org.junit.Test;

/**
 * Tests {@link GaussianAxis}.
 * 
 * @author Barry DeZonia
 */
public class GaussianAxisTest extends AbstractMetaTest {

	@Test
	public void testOtherCtor() {
		final GaussianAxis axis = new GaussianAxis(Axes.Z, "lp", 4, 3, 2, 1);

		assertEquals(Axes.Z, axis.type());
		assertEquals("lp", axis.unit());
		assertEquals(4, axis.a(), 0);
		assertEquals(3, axis.b(), 0);
		assertEquals(2, axis.c(), 0);
		assertEquals(1, axis.d(), 0);
		assertEquals(calValue(4, axis), axis.calibratedValue(4), 0);
	}

	@Test
	public void testOtherStuff() {
		final GaussianAxis axis = new GaussianAxis(Axes.Z, "lp", 1, 2, 3, 4);

		axis.setA(2);
		axis.setB(3);
		axis.setC(5);
		axis.setD(7);
		assertEquals(2, axis.a(), 0);
		assertEquals(3, axis.b(), 0);
		assertEquals(5, axis.c(), 0);
		assertEquals(7, axis.d(), 0);

		for (int i = 0; i < 100; i++) {
			assertEquals(Double.NaN, axis.rawValue(axis.calibratedValue(i)), 0);
		}
	}

	@Test
	public void testCopy() {
		final GaussianAxis axis = new GaussianAxis(Axes.Z, "lp", 1, 2, 3, 4);
		final GaussianAxis copy = axis.copy();
		assertNotSame(axis, copy);
		assertEquals(axis, copy);
		assertEquals(axis.hashCode(), copy.hashCode());
	}

	private double calValue(final double raw, final GaussianAxis axis) {
		return axis.a() +
			(axis.b() - axis.a()) *
			Math
				.exp(-(raw - axis.c()) * (raw - axis.c()) / (2 * axis.d() * axis.d()));
	}
}
