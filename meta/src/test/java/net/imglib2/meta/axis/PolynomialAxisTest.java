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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import net.imglib2.meta.AbstractMetaTest;
import net.imglib2.meta.Axes;

import org.junit.Test;

/**
 * Tests {@link PolynomialAxis}.
 * 
 * @author Barry DeZonia
 */
public class PolynomialAxisTest extends AbstractMetaTest {

	@Test
	public void testDefaultConstructor() {
		final PolynomialAxis axis = new PolynomialAxis();
		assertUnknown(axis);
		assertNull(axis.unit());
		assertEquals(0, axis.coeff(0), 0);
		assertEquals(0, axis.coeff(1), 0);
		assertEquals(1, axis.coeff(2), 0);
		for (int i = 3; i < 25; i++)
			assertEquals(0, axis.coeff(i), 0);
		assertEquals(2, axis.degree());
	}

	@Test
	public void testSpecificConstructor() {
		final PolynomialAxis axis =
			new PolynomialAxis(Axes.X, "florps", 1, 2, 3, 4, 5);
		assertEquals(Axes.X, axis.type());
		assertEquals("florps", axis.unit());
		assertEquals(1, axis.coeff(0), 0);
		assertEquals(2, axis.coeff(1), 0);
		assertEquals(3, axis.coeff(2), 0);
		assertEquals(4, axis.coeff(3), 0);
		assertEquals(5, axis.coeff(4), 0);
		for (int i = 5; i < 25; i++)
			assertEquals(0, axis.coeff(i), 0);
		assertEquals(4, axis.degree());
	}

	@Test
	public void testGeneralEquation() {
		PolynomialAxis axis = new PolynomialAxis(Axes.Y, "mm", 7, 2, 1);
		assertEquals("y = a + b*x + c*x^2", axis.generalEquation());
		axis = new PolynomialAxis(Axes.Y, "mm", 5, 4, 3, 2, 1);
		assertEquals("y = a + b*x + c*x^2 + d*x^3 + e*x^4", axis.generalEquation());
	}

	@Test
	public void testParticularEquation() {
		PolynomialAxis axis = new PolynomialAxis(Axes.Y, "mm", 7, 2, 1);
		assertEquals("y = (7.0) + (2.0)*x + (1.0)*x^2", axis.particularEquation());
		axis = new PolynomialAxis(Axes.Y, "mm", 5, 4, 3, 2, 1);
		assertEquals("y = (5.0) + (4.0)*x + (3.0)*x^2 + (2.0)*x^3 + (1.0)*x^4",
			axis.particularEquation());
	}

	@Test
	public void testCalibratedValue() {
		final PolynomialAxis axis = new PolynomialAxis();
		axis.setCoeff(0, 1);
		axis.setCoeff(1, 2);
		axis.setCoeff(2, 3);
		assertEquals(1, axis.calibratedValue(0), 0);
		assertEquals(6, axis.calibratedValue(1), 0);
		assertEquals(17, axis.calibratedValue(2), 0);

		axis.setCoeff(3, 1);
		assertEquals(1, axis.calibratedValue(0), 0);
		assertEquals(7, axis.calibratedValue(1), 0);
		assertEquals(25, axis.calibratedValue(2), 0);
	}

	@Test
	public void testRawValue() {
		final PolynomialAxis axis = new PolynomialAxis();
		assertTrue(Double.isNaN(axis.rawValue(93.7)));
	}

	@Test
	public void testSetCoeff() {
		final PolynomialAxis axis = new PolynomialAxis();
		axis.setCoeff(23, 1000);
		assertEquals(1000, axis.coeff(23), 0);
		assertEquals(23, axis.degree());
		try {
			axis.setCoeff(24, 50);
			fail();
		}
		catch (final IllegalArgumentException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testType() {
		final PolynomialAxis axis = new PolynomialAxis();
		axis.setType(Axes.CHANNEL);
		assertEquals(Axes.CHANNEL, axis.type());
	}

	@Test
	public void testUnit() {
		final PolynomialAxis axis = new PolynomialAxis();
		axis.setUnit("hjh");
		assertEquals("hjh", axis.unit());
	}

	@Test
	public void testCopy() {
		final PolynomialAxis axis = new PolynomialAxis(Axes.Y, "mm", 7, 2, 1);
		final PolynomialAxis copy = axis.copy();
		assertNotSame(axis, copy);
		assertEquals(axis, copy);
		assertEquals(axis.hashCode(), copy.hashCode());
	}

}
