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
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.meta.axis.InverseRodbardAxis;
import net.imglib2.meta.axis.LinearAxis;
import net.imglib2.meta.axis.LogLinearAxis;
import net.imglib2.type.numeric.integer.ByteType;

import org.junit.Test;

/**
 * Tests {@link ImgPlus}.
 * 
 * @author Barry DeZonia
 */
public class ImgPlusTest extends AbstractMetaTest {

	@Test
	public void testAxes() {
		final Img<ByteType> img = ArrayImgs.bytes(9, 8);
		final ImgPlus<?> imgPlus =
			new ImgPlus<ByteType>(img, "HUBBY", new AxisType[] { Axes.X, Axes.Z },
				new double[] { 5, 13 });
		assertEquals("HUBBY", imgPlus.getName());
		assertEquals(Axes.X, imgPlus.axis(0).type());
		assertEquals(Axes.Z, imgPlus.axis(1).type());
		assertEquals(5, imgPlus.averageScale(0), 0);
		assertEquals(13, imgPlus.averageScale(1), 0);
		assertEquals(9, imgPlus.dimension(0));
		assertEquals(8, imgPlus.dimension(1));
		assertTrue(imgPlus.axis(1) instanceof LinearAxis);
		((LinearAxis) imgPlus.axis(1)).setScale(48);
		assertEquals(48, imgPlus.averageScale(1), 0);
		assertEquals(0, imgPlus.min(0));
		assertEquals(0, imgPlus.min(1));
		assertEquals(8, imgPlus.max(0));
		assertEquals(7, imgPlus.max(1));
		assertEquals(0, imgPlus.getValidBits());
		assertEquals(72, imgPlus.size());
		assertNull(imgPlus.axis(0).unit());
		imgPlus.axis(0).setUnit("WUNGA");
		assertEquals("WUNGA", imgPlus.axis(0).unit());
	}

	@Test
	public void testCopy() {
		final CalibratedAxis xAxis =
			new InverseRodbardAxis(Axes.X, "foo", 2.1, 4.3, 6.5, 8.7);
		final CalibratedAxis yAxis =
			new LogLinearAxis(Axes.Y, "bar", -6.7, -8.9, -3.4, -1.2);
		final ImgPlus<ByteType> original =
			new ImgPlus<ByteType>(ArrayImgs.bytes(9, 8), "original", xAxis, yAxis);
		final ImgPlus<ByteType> copy = original.copy();
		assertNotSame(original, copy);
		assertNotSame(original.getImg(), copy.getImg());
		assertEquals(original.numDimensions(), copy.numDimensions());
		for (int d = 0; d < original.numDimensions(); d++) {
			assertNotSame(original.axis(d), copy.axis(d));
			assertSame(original.axis(d).getClass(), copy.axis(d).getClass());
			assertEquals(original.axis(d).unit(), copy.axis(d).unit());
			assertEquals(original.axis(d).particularEquation(), copy.axis(d)
				.particularEquation());
		}
	}

}
