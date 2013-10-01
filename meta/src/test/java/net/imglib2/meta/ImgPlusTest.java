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
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.meta.axis.LinearAxis;
import net.imglib2.type.numeric.integer.ByteType;

import org.junit.Test;

/**
 * @author Barry DeZonia
 */
public class ImgPlusTest {

	private ImgPlus<?> imgPlus;

	@Test
	public void test() {
		Img<ByteType> img = ArrayImgs.bytes(9, 8);
		imgPlus =
			new ImgPlus<ByteType>(img, "HUBBY", new AxisType[] { Axes.X, Axes.Z },
				new double[] { 5, 13 });
		assertEquals("HUBBY", imgPlus.getName());
		assertEquals(Axes.X, imgPlus.axis(0).type());
		assertEquals(Axes.Z, imgPlus.axis(1).type());
		assertEquals(5, imgPlus.axis(0).averageScale(0, 1), 0);
		assertEquals(13, imgPlus.axis(1).averageScale(0, 1), 0);
		assertEquals(9, imgPlus.dimension(0));
		assertEquals(8, imgPlus.dimension(1));
		assertTrue(imgPlus.axis(1) instanceof LinearAxis);
		((LinearAxis) imgPlus.axis(1)).setScale(48);
		assertEquals(48, imgPlus.axis(1).averageScale(0, 1), 0);
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

}
