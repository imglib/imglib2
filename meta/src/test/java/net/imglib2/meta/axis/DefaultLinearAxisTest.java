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
import static org.junit.Assert.assertNull;
import net.imglib2.meta.AbstractMetaTest;
import net.imglib2.meta.Axes;

import org.junit.Test;

/**
 * Tests {@link DefaultLinearAxis}.
 * 
 * @author Barry DeZonia
 */
public class DefaultLinearAxisTest extends AbstractMetaTest {

	@Test
	public void testDefaultConstructor() {
		final LinearAxis axis = new DefaultLinearAxis();
		assertUnknown(axis);
		assertNull(axis.unit());
		assertEquals(1, axis.scale(), 0);
		assertEquals(0, axis.origin(), 0);
	}

	@Test
	public void testScaleConstructor() {
		final LinearAxis axis = new DefaultLinearAxis(59);
		assertUnknown(axis);
		assertNull(axis.unit());
		assertEquals(59, axis.scale(), 0);
		assertEquals(0, axis.origin(), 0);
	}

	@Test
	public void testScaleOriginConstructor() {
		final LinearAxis axis = new DefaultLinearAxis(22, 7);
		assertUnknown(axis);
		assertNull(axis.unit());
		assertEquals(22, axis.scale(), 0);
		assertEquals(7, axis.origin(), 0);
	}

	@Test
	public void testAxisTypeConstructor() {
		final LinearAxis axis = new DefaultLinearAxis(Axes.Z);
		assertEquals(Axes.Z, axis.type());
		assertNull(axis.unit());
		assertEquals(1, axis.scale(), 0);
		assertEquals(0, axis.origin(), 0);
	}

	@Test
	public void testAxisTypeScaleConstructor() {
		final LinearAxis axis = new DefaultLinearAxis(Axes.X, -3);
		assertEquals(Axes.X, axis.type());
		assertNull(axis.unit());
		assertEquals(-3, axis.scale(), 0);
		assertEquals(0, axis.origin(), 0);
	}

	@Test
	public void testAxisTypeScaleOriginConstructor() {
		final LinearAxis axis = new DefaultLinearAxis(Axes.X, -3, 8);
		assertEquals(Axes.X, axis.type());
		assertNull(axis.unit());
		assertEquals(-3, axis.scale(), 0);
		assertEquals(8, axis.origin(), 0);
	}

	@Test
	public void testAxisTypeUnitConstructor() {
		final LinearAxis axis = new DefaultLinearAxis(Axes.X, "mm");
		assertEquals(Axes.X, axis.type());
		assertEquals("mm", axis.unit());
		assertEquals(1, axis.scale(), 0);
		assertEquals(0, axis.origin(), 0);
	}

	@Test
	public void testAxisTypeUnitScaleConstructor() {
		final LinearAxis axis = new DefaultLinearAxis(Axes.X, "mm", 9);
		assertEquals(Axes.X, axis.type());
		assertEquals("mm", axis.unit());
		assertEquals(9, axis.scale(), 0);
		assertEquals(0, axis.origin(), 0);
	}

	@Test
	public void testAxisTypeUnitScaleOriginConstructor() {
		final LinearAxis axis = new DefaultLinearAxis(Axes.X, "mm", 5, 3);
		assertEquals(Axes.X, axis.type());
		assertEquals("mm", axis.unit());
		assertEquals(5, axis.scale(), 0);
		assertEquals(3, axis.origin(), 0);
	}

	@Test
	public void testOtherMethods() {
		final LinearAxis axis = new DefaultLinearAxis(Axes.Y, "heptoflops", 5, 3);
		assertEquals(5, axis.averageScale(10, 20), 0);
		assertEquals("y = (3.0) + (5.0)*x", axis.particularEquation());
		assertEquals(23, axis.calibratedValue(4), 0);
		assertEquals("y = a + b*x", axis.generalEquation());
		assertEquals(3, axis.origin(), 0);
		assertEquals(7, axis.rawValue(38), 0);
		assertEquals(5, axis.scale(), 0);
		assertEquals(Axes.Y, axis.type());
		assertEquals("heptoflops", axis.unit());

		axis.setOrigin(1005);
		assertEquals(1005, axis.origin(), 0);
		axis.setScale(0.5);
		assertEquals(0.5, axis.scale(), 0);
		axis.setType(Axes.Y);
		assertEquals(Axes.Y, axis.type());
		axis.setUnit("ThisHadBetterWork");
		assertEquals("ThisHadBetterWork", axis.unit());
	}

	@Test
	public void testInverseMapping() {
		final LinearAxis axis = new DefaultLinearAxis();
		axis.setOrigin(2);
		axis.setScale(3);

		for (int i = 0; i < 100; i++) {
			assertEquals(axis.rawValue(axis.calibratedValue(i)), i, 0.000001);
		}
	}

	@Test
	public void testCopy() {
		final LinearAxis axis = new DefaultLinearAxis(Axes.Y, "heptoflops", 5, 3);
		final LinearAxis copy = axis.copy();
		assertNotSame(axis, copy);
		assertEquals(axis, copy);
		assertEquals(axis.hashCode(), copy.hashCode());
	}

}
