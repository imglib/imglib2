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

package net.imglib2.meta;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

/**
 * @author Barry DeZonia
 */
public class DefaultCalibratedSpaceTest {

	private DefaultCalibratedSpace space;

	@Test
	public void test1() {
		LinearAxis axis0 = new LinearAxis(Axes.X, "nm", 2);
		LinearAxis axis1 = new LinearAxis(Axes.Y, "nm", 3);
		LinearAxis axis2 = new LinearAxis(Axes.Z, "cm", 4);
		space = new DefaultCalibratedSpace(axis0, axis1, axis2);
		assertEquals(Axes.X, space.axis(0).type());
		assertEquals(Axes.Y, space.axis(1).type());
		assertEquals(Axes.Z, space.axis(2).type());
		assertEquals("nm", space.unit(0));
		assertEquals("nm", space.unit(1));
		assertEquals("cm", space.unit(2));
		assertEquals(2, space.axis(0).calibratedValue(1), 0);
		assertEquals(3, space.axis(1).calibratedValue(1), 0);
		assertEquals(4, space.axis(2).calibratedValue(1), 0);
	}

	@Test
	public void test2() {
		LinearAxis axis0 = new LinearAxis(Axes.X, "nm", 2);
		LinearAxis axis1 = new LinearAxis(Axes.Y, "nm", 3);
		LinearAxis axis2 = new LinearAxis(Axes.Z, "cm", 4);
		space =
			new DefaultCalibratedSpace(Arrays.asList(new CalibratedAxis[] { axis0,
				axis1, axis2 }));
		assertEquals(Axes.X, space.axis(0).type());
		assertEquals(Axes.Y, space.axis(1).type());
		assertEquals(Axes.Z, space.axis(2).type());
		assertEquals("nm", space.unit(0));
		assertEquals("nm", space.unit(1));
		assertEquals("cm", space.unit(2));
		assertEquals(2, space.axis(0).calibratedValue(1), 0);
		assertEquals(3, space.axis(1).calibratedValue(1), 0);
		assertEquals(4, space.axis(2).calibratedValue(1), 0);
	}

	@Test
	public void test3() {
		space = new DefaultCalibratedSpace(3);
		assertTrue(space.axis(0).type() instanceof Axes.CustomType);
		assertTrue(space.axis(1).type() instanceof Axes.CustomType);
		assertTrue(space.axis(2).type() instanceof Axes.CustomType);
		assertNull(space.unit(0));
		assertNull(space.unit(1));
		assertNull(space.unit(2));
		assertEquals(0, space.axis(0).calibratedValue(0), 0);
		assertEquals(0, space.axis(1).calibratedValue(0), 0);
		assertEquals(0, space.axis(2).calibratedValue(0), 0);
		assertEquals(1, space.axis(0).calibratedValue(1), 0);
		assertEquals(1, space.axis(1).calibratedValue(1), 0);
		assertEquals(1, space.axis(2).calibratedValue(1), 0);
		LinearAxis axis0 = new LinearAxis(Axes.X, "nm", 2);
		LinearAxis axis1 = new LinearAxis(Axes.Y, "nm", 3);
		LinearAxis axis2 = new LinearAxis(Axes.Z, "cm", 4);
		space.setAxis(axis0, 0);
		space.setAxis(axis1, 1);
		space.setAxis(axis2, 2);
		assertEquals(Axes.X, space.axis(0).type());
		assertEquals(Axes.Y, space.axis(1).type());
		assertEquals(Axes.Z, space.axis(2).type());
		assertEquals("nm", space.unit(0));
		assertEquals("nm", space.unit(1));
		assertEquals("cm", space.unit(2));
		assertEquals(2, space.axis(0).calibratedValue(1), 0);
		assertEquals(3, space.axis(1).calibratedValue(1), 0);
		assertEquals(4, space.axis(2).calibratedValue(1), 0);
	}
}
