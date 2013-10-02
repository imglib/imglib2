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
import net.imglib2.FinalInterval;
import net.imglib2.FinalRealInterval;
import net.imglib2.Interval;
import net.imglib2.RealInterval;
import net.imglib2.meta.axis.DefaultLinearAxis;
import net.imglib2.meta.axis.LinearAxis;

import org.junit.Test;

/**
 * Tests {@link IntervalUtils}.
 * 
 * @author Barry DeZonia
 */
public class IntervalUtilsTest {

	@Test
	public void test1() {
		final Interval interval = new FinalInterval(new long[] { 10, 20 });
		final long[] dims = IntervalUtils.getDims(interval);
		assertEquals(10, dims[0]);
		assertEquals(20, dims[1]);
	}

	@Test
	public void test2() {
		final RealInterval interval =
			new FinalRealInterval(new double[] { 0, 0 }, new double[] { 7, 13 });
		final double[] extents = IntervalUtils.getExtents(interval);
		assertEquals(7, extents[0], 0);
		assertEquals(13, extents[1], 0);
	}

	@Test
	public void test3() {
		final DefaultCalibratedRealInterval interval =
			new DefaultCalibratedRealInterval(new double[] { 10, 20 });
		final LinearAxis axis0 = new DefaultLinearAxis(Axes.X, null, 7);
		final LinearAxis axis1 = new DefaultLinearAxis(Axes.Y, null, 9);
		interval.setAxis(axis0, 0);
		interval.setAxis(axis1, 1);
		final double[] extents = IntervalUtils.getCalibratedExtents(interval);
		assertEquals(10 * axis0.scale(), extents[0], 0);
		assertEquals(20 * axis1.scale(), extents[1], 0);
	}

}
