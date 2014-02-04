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
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.ops.function.Function;
import net.imglib2.ops.function.general.GeneralBinaryFunction;
import net.imglib2.ops.function.real.RealConstantFunction;
import net.imglib2.ops.function.real.RealImageFunction;
import net.imglib2.ops.input.PointInputIterator;
import net.imglib2.ops.operation.real.binary.RealAdd;
import net.imglib2.ops.pointset.HyperVolumePointSet;
import net.imglib2.type.numeric.real.DoubleType;

import org.junit.Test;

/**
 * 
 * @author Barry DeZonia
 */
public class Example1Test {

	private final long XSIZE = 100;
	private final long YSIZE = 200;

	private boolean veryClose(double d1, double d2) {
		return Math.abs(d1 - d2) < 0.00001;
	}

	private Img<DoubleType> allocateImage() {
		final ArrayImgFactory<DoubleType> imgFactory =
			new ArrayImgFactory<DoubleType>();
		return imgFactory.create(new long[] { XSIZE, YSIZE }, new DoubleType());
	}

	private Img<DoubleType> makeInputImage() {
		Img<DoubleType> inputImg = allocateImage();
		RandomAccess<DoubleType> accessor = inputImg.randomAccess();
		long[] pos = new long[2];
		for (int x = 0; x < XSIZE; x++) {
			for (int y = 0; y < YSIZE; y++) {
				pos[0] = x;
				pos[1] = y;
				accessor.setPosition(pos);
				accessor.get().setReal(x + y);
			}
		}
		return inputImg;
	}

	@Test
	public void testAssignment() {

		// calculate output values by adding 15 to the values of an input image

		Img<DoubleType> inputImage = makeInputImage();

		Function<long[], DoubleType> constant =
			new RealConstantFunction<long[], DoubleType>(new DoubleType(15));

		Function<long[], DoubleType> image =
			new RealImageFunction<DoubleType, DoubleType>(inputImage,
				new DoubleType());

		RealAdd<DoubleType, DoubleType, DoubleType> op =
			new RealAdd<DoubleType, DoubleType, DoubleType>();

		Function<long[], DoubleType> additionFunc =
			new GeneralBinaryFunction<long[], DoubleType, DoubleType, DoubleType>(
				constant, image, op, new DoubleType());

		HyperVolumePointSet pointSet =
			new HyperVolumePointSet(new long[] { XSIZE, YSIZE });
		PointInputIterator iter = new PointInputIterator(pointSet);

		DoubleType value = new DoubleType();
		long[] index = null;
		while (iter.hasNext()) {
			index = iter.next(index);
			additionFunc.compute(index, value);
			assertTrue(veryClose(value.getRealDouble(), (index[0] + index[1] + 15)));
			/*
			{
				System.out.println(" FAILURE at (" + x + "," + y
						+ "): expected (" + (x + y + 15) + ") actual ("
						+ pointValue.getRealDouble() + ")");
				success = false;
			}
			*/
		}
	}

}
