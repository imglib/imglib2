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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import net.imglib2.meta.axis.DefaultLinearAxis;
import net.imglib2.meta.axis.LinearAxis;

import org.junit.Test;

/**
 * Tests {@link DefaultCalibratedRealInterval}.
 * 
 * @author Barry DeZonia
 */
public class DefaultCalibratedRealIntervalTest extends AbstractMetaTest {

	private DefaultCalibratedRealInterval interval;

	@Test
	public void testExtentsConstructor() {
		// verify that interval extents are assigned correctly
		final double[] extents = new double[] { 5, 10, 20 };
		final double[] temp = new double[extents.length];
		final CalibratedAxis[] axes = new CalibratedAxis[extents.length];
		interval = new DefaultCalibratedRealInterval(extents);
		assertEquals(extents.length, interval.numDimensions());
		interval.realMin(temp);
		assertArrayEquals(new double[3], temp, 0);
		interval.realMax(temp);
		assertArrayEquals(new double[] { 5, 10, 20 }, temp, 0);
		// verify that axes have default calibrations
		interval.axes(axes);
		for (int i = 0; i < extents.length; i++) {
			assertSame(interval.axis(i), axes[i]);
			assertUnknown(interval.axis(i));
			assertNull(interval.axis(i).unit());
		}
		// verify that axes are assigned correctly
		final LinearAxis axis = new DefaultLinearAxis(Axes.X, "plorps", 4);
		interval.setAxis(axis, 0);
		assertSame(axis, interval.axis(0));
	}

	@Test
	public void testExtentsAxisArrayConstructor() {
		final double[] extents = new double[] { 5, 10, 20 };
		final double[] temp = new double[extents.length];
		final CalibratedAxis axis0 =
			new DefaultLinearAxis(Axes.TIME, "froop", 1);
		final CalibratedAxis axis1 = new DefaultLinearAxis(Axes.X, "orp", 3);
		final CalibratedAxis axis2 =
			new DefaultLinearAxis(Axes.CHANNEL, "smump", 5);
		interval = new DefaultCalibratedRealInterval(extents, axis0, axis1, axis2);
		assertEquals(extents.length, interval.numDimensions());
		interval.realMin(temp);
		assertArrayEquals(new double[3], temp, 0);
		interval.realMax(temp);
		assertArrayEquals(new double[] { 5, 10, 20 }, temp, 0);
		final CalibratedAxis[] axes = new CalibratedAxis[extents.length];
		interval.axes(axes);
		assertSame(axis0, axes[0]);
		assertSame(axis1, axes[1]);
		assertSame(axis2, axes[2]);
		assertEquals(Axes.TIME, interval.axis(0).type());
		assertEquals(Axes.X, interval.axis(1).type());
		assertEquals(Axes.CHANNEL, interval.axis(2).type());
		assertEquals("froop", interval.axis(0).unit());
		assertEquals("orp", interval.axis(1).unit());
		assertEquals("smump", interval.axis(2).unit());
		assertEquals(1, interval.averageScale(0), 0);
		assertEquals(3, interval.averageScale(1), 0);
		assertEquals(5, interval.averageScale(2), 0);
	}

}
