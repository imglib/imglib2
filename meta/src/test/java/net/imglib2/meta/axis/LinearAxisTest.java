/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2013 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
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
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package net.imglib2.meta.axis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import net.imglib2.meta.Axes;

import org.junit.Test;

/**
 * Tests {@link LinearAxis}.
 * 
 * @author Barry DeZonia
 */
public class LinearAxisTest {

	private LinearAxis axis;

	@Test
	public void testConstructor1() {
		axis = new LinearAxis();
		assertTrue(axis.type() instanceof Axes.CustomType);
		assertEquals(null, axis.unit());
		assertEquals(1, axis.scale(), 0);
		assertEquals(0, axis.origin(), 0);
	}

	@Test
	public void testConstructor2() {
		axis = new LinearAxis(59);
		assertTrue(axis.type() instanceof Axes.CustomType);
		assertEquals(null, axis.unit());
		assertEquals(59, axis.scale(), 0);
		assertEquals(0, axis.origin(), 0);
	}

	@Test
	public void testConstructor3() {
		axis = new LinearAxis(22, 7);
		assertTrue(axis.type() instanceof Axes.CustomType);
		assertEquals(null, axis.unit());
		assertEquals(22, axis.scale(), 0);
		assertEquals(7, axis.origin(), 0);
	}

	@Test
	public void testConstructor4() {
		axis = new LinearAxis(Axes.Z);
		assertEquals(Axes.Z, axis.type());
		assertEquals(null, axis.unit());
		assertEquals(1, axis.scale(), 0);
		assertEquals(0, axis.origin(), 0);
	}

	@Test
	public void testConstructor5() {
		axis = new LinearAxis(Axes.X, -3);
		assertEquals(Axes.X, axis.type());
		assertEquals(null, axis.unit());
		assertEquals(-3, axis.scale(), 0);
		assertEquals(0, axis.origin(), 0);
	}

	@Test
	public void testConstructor6() {
		axis = new LinearAxis(Axes.X, -3, 8);
		assertEquals(Axes.X, axis.type());
		assertEquals(null, axis.unit());
		assertEquals(-3, axis.scale(), 0);
		assertEquals(8, axis.origin(), 0);
	}

	@Test
	public void testConstructor7() {
		axis = new LinearAxis(Axes.X, "mm");
		assertEquals(Axes.X, axis.type());
		assertEquals("mm", axis.unit());
		assertEquals(1, axis.scale(), 0);
		assertEquals(0, axis.origin(), 0);
	}

	@Test
	public void testConstructor8() {
		axis = new LinearAxis(Axes.X, "mm", 9);
		assertEquals(Axes.X, axis.type());
		assertEquals("mm", axis.unit());
		assertEquals(9, axis.scale(), 0);
		assertEquals(0, axis.origin(), 0);
	}

	@Test
	public void testConstructor9() {
		axis = new LinearAxis(Axes.X, "mm", 5, 3);
		assertEquals(Axes.X, axis.type());
		assertEquals("mm", axis.unit());
		assertEquals(5, axis.scale(), 0);
		assertEquals(3, axis.origin(), 0);
	}

	@Test
	public void testOtherMethods() {
		axis = new LinearAxis(Axes.Y, "heptoflops", 5, 3);
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
		axis = new LinearAxis();
		axis.setOrigin(2);
		axis.setScale(3);

		for (int i = 0; i < 100; i++) {
			assertEquals(axis.rawValue(axis.calibratedValue(i)), i, 0.000001);
		}
	}
}
