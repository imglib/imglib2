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

package net.imglib2.ops.example;

import static org.junit.Assert.assertTrue;
import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.ops.operation.real.binary.RealAdd;
import net.imglib2.type.numeric.real.DoubleType;

import org.junit.Test;

/**
 * TODO
 *
 */
public class Example12Test {

	private final int XSIZE = 10;
	private final int YSIZE = 20;

	private boolean veryClose(double d1, double d2) {
		return Math.abs(d1 - d2) < 0.00001;
	}

	private Img<DoubleType> allocateImage() {
		final ArrayImgFactory<DoubleType> imgFactory =
			new ArrayImgFactory<DoubleType>();
		return imgFactory.create(new long[] { XSIZE, YSIZE }, new DoubleType());
	}

	private void fillImage(Img<DoubleType> img, double value) {
		Cursor<DoubleType> cursor = img.cursor();
		while (cursor.hasNext()) {
			cursor.fwd();
			cursor.get().set(value);
		}
	}

	@Test
	public void test() {
		Img<DoubleType> in1 = allocateImage();
		fillImage(in1, 1);
		Img<DoubleType> in2 = allocateImage();
		fillImage(in2, 2);
		Img<DoubleType> out = allocateImage();
		fillImage(out, 0);
		RealAdd<DoubleType,DoubleType,DoubleType> op =
				new RealAdd<DoubleType, DoubleType, DoubleType>();
		Cursor<DoubleType> ic1 = in1.cursor();
		Cursor<DoubleType> ic2 = in2.cursor();
		Cursor<DoubleType> oc = out.cursor();
		while ((ic1.hasNext()) && (ic2.hasNext()) && (oc.hasNext())) {
			ic1.fwd();
			ic2.fwd();
			oc.fwd();
			op.compute(ic1.get(), ic2.get(), oc.get());
		}

		oc.reset();
		while (oc.hasNext()) {
			// System.out.println(i++);
			oc.fwd();
			double value = oc.get().getRealDouble();
			assertTrue(veryClose(value, 1 + 2));
		}
	}
}
