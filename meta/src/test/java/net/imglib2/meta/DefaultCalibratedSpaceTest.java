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

package net.imglib2.meta;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;

import net.imglib2.meta.axis.DefaultLinearAxis;
import net.imglib2.meta.axis.LinearAxis;

import org.junit.Test;

/**
 * Tests {@link DefaultCalibratedSpace}.
 * 
 * @author Barry DeZonia
 */
public class DefaultCalibratedSpaceTest extends AbstractMetaTest {

	private DefaultCalibratedSpace space;

	@Test
	public void testArrayConstructor() {
		final LinearAxis axis0 = new DefaultLinearAxis(Axes.X, "nm", 2);
		final LinearAxis axis1 = new DefaultLinearAxis(Axes.Y, "nm", 3);
		final LinearAxis axis2 = new DefaultLinearAxis(Axes.Z, "cm", 4);
		space = new DefaultCalibratedSpace(axis0, axis1, axis2);
		assertEquals(Axes.X, space.axis(0).type());
		assertEquals(Axes.Y, space.axis(1).type());
		assertEquals(Axes.Z, space.axis(2).type());
		assertEquals("nm", space.axis(0).unit());
		assertEquals("nm", space.axis(1).unit());
		assertEquals("cm", space.axis(2).unit());
		assertEquals(2, space.axis(0).calibratedValue(1), 0);
		assertEquals(3, space.axis(1).calibratedValue(1), 0);
		assertEquals(4, space.axis(2).calibratedValue(1), 0);
	}

	@Test
	public void testListConstructor() {
		// verify that axes are assigned correctly in the constructor
		final LinearAxis axis0 = new DefaultLinearAxis(Axes.X, "nm", 2);
		final LinearAxis axis1 = new DefaultLinearAxis(Axes.Y, "nm", 3);
		final LinearAxis axis2 = new DefaultLinearAxis(Axes.Z, "cm", 4);
		space =
			new DefaultCalibratedSpace(Arrays.asList(new CalibratedAxis[] { axis0,
				axis1, axis2 }));
		assertEquals(Axes.X, space.axis(0).type());
		assertEquals(Axes.Y, space.axis(1).type());
		assertEquals(Axes.Z, space.axis(2).type());
		assertEquals("nm", space.axis(0).unit());
		assertEquals("nm", space.axis(1).unit());
		assertEquals("cm", space.axis(2).unit());
		assertEquals(2, space.axis(0).calibratedValue(1), 0);
		assertEquals(3, space.axis(1).calibratedValue(1), 0);
		assertEquals(4, space.axis(2).calibratedValue(1), 0);
	}

	@Test
	public void testDefaultConstructor() {
		space = new DefaultCalibratedSpace(3);
		// verify that axes have default (identity) calibrations
		assertUnknown(space.axis(0));
		assertUnknown(space.axis(1));
		assertUnknown(space.axis(2));
		assertNull(space.axis(0).unit());
		assertNull(space.axis(1).unit());
		assertNull(space.axis(2).unit());
		assertEquals(0, space.axis(0).calibratedValue(0), 0);
		assertEquals(0, space.axis(1).calibratedValue(0), 0);
		assertEquals(0, space.axis(2).calibratedValue(0), 0);
		assertEquals(1, space.axis(0).calibratedValue(1), 0);
		assertEquals(1, space.axis(1).calibratedValue(1), 0);
		assertEquals(1, space.axis(2).calibratedValue(1), 0);
		// verify that axes are assigned correctly
		final LinearAxis axis0 = new DefaultLinearAxis(Axes.X, "nm", 2);
		final LinearAxis axis1 = new DefaultLinearAxis(Axes.Y, "nm", 3);
		final LinearAxis axis2 = new DefaultLinearAxis(Axes.Z, "cm", 4);
		space.setAxis(axis0, 0);
		space.setAxis(axis1, 1);
		space.setAxis(axis2, 2);
		assertEquals(Axes.X, space.axis(0).type());
		assertEquals(Axes.Y, space.axis(1).type());
		assertEquals(Axes.Z, space.axis(2).type());
		assertEquals("nm", space.axis(0).unit());
		assertEquals("nm", space.axis(1).unit());
		assertEquals("cm", space.axis(2).unit());
		assertEquals(2, space.axis(0).calibratedValue(1), 0);
		assertEquals(3, space.axis(1).calibratedValue(1), 0);
		assertEquals(4, space.axis(2).calibratedValue(1), 0);
	}

	@Test
	public void testAverageScale() {
		space = new DefaultCalibratedSpace(3);
		for (int d = 0; d < space.numDimensions(); d++) {
			assertEquals(1.0, space.averageScale(d), 0.0);
		}
	}

}
