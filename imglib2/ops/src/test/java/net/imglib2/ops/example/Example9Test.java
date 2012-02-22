/*

Copyright (c) 2011, Barry DeZonia.
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 * Neither the name of the Fiji project developers nor the
    names of its contributors may be used to endorse or promote products
    derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.
 */

package net.imglib2.ops.example;

import static org.junit.Assert.*;

import org.junit.Test;

import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.ops.DiscreteNeigh;
import net.imglib2.ops.Function;
import net.imglib2.ops.function.general.GeneralBinaryFunction;
import net.imglib2.ops.function.general.GeneralUnaryFunction;
import net.imglib2.ops.function.real.RealConvolutionFunction;
import net.imglib2.ops.function.real.RealImageFunction;
import net.imglib2.ops.operation.binary.real.RealAdd;
import net.imglib2.ops.operation.unary.real.RealAbs;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

// A Sobel filter / gradient example

/**
 * 
 * @author Barry DeZonia
 * 
 */
public class Example9Test {

	private static final int XSIZE = 45;
	private static final int YSIZE = 104;

	private static Img<DoubleType> img;

	private static long[] globalPos = new long[2];

	private static RandomAccess<? extends RealType<?>> queryAccessor;

	private static Img<DoubleType> allocateImage() {
		final ArrayImgFactory<DoubleType> imgFactory = new ArrayImgFactory<DoubleType>();
		return imgFactory.create(new long[] { XSIZE, YSIZE }, new DoubleType());
	}

	private static Img<DoubleType> makeInputImage() {
		Img<DoubleType> inputImg = allocateImage();
		RandomAccess<DoubleType> accessor = inputImg.randomAccess();
		long[] pos = new long[3];
		for (int x = 0; x < XSIZE; x++) {
			pos[0] = x;
			for (int y = 0; y < YSIZE / 3; y++) {
				pos[1] = y;
				accessor.setPosition(pos);
				accessor.get().setReal(x);
			}
			for (int y = YSIZE / 3; y < 2 * YSIZE / 3; y++) {
				pos[1] = y;
				accessor.setPosition(pos);
				accessor.get().setReal(x + 2 * y);
			}
			for (int y = 2 * YSIZE / 3; y < YSIZE; y++) {
				pos[1] = y;
				accessor.setPosition(pos);
				accessor.get().setReal(3 * x);
			}
		}
		queryAccessor = accessor;
		return inputImg;
	}

	private static boolean veryClose(double d1, double d2) {
		return Math.abs(d1 - d2) < 0.00001;
	}

	@SuppressWarnings("unused")
	private static double expectedValue(int x, int y) {
		globalPos[0] = x - 1;
		globalPos[1] = y - 1;
		queryAccessor.setPosition(globalPos);
		double z1 = queryAccessor.get().getRealDouble();
		globalPos[0] = x;
		globalPos[1] = y - 1;
		queryAccessor.setPosition(globalPos);
		double z2 = queryAccessor.get().getRealDouble();
		globalPos[0] = x + 1;
		globalPos[1] = y - 1;
		queryAccessor.setPosition(globalPos);
		double z3 = queryAccessor.get().getRealDouble();
		globalPos[0] = x - 1;
		globalPos[1] = y;
		queryAccessor.setPosition(globalPos);
		double z4 = queryAccessor.get().getRealDouble();
		globalPos[0] = x;
		globalPos[1] = y;
		queryAccessor.setPosition(globalPos);
		double z5 = queryAccessor.get().getRealDouble();
		globalPos[0] = x + 1;
		globalPos[1] = y;
		queryAccessor.setPosition(globalPos);
		double z6 = queryAccessor.get().getRealDouble();
		globalPos[0] = x - 1;
		globalPos[1] = y + 1;
		queryAccessor.setPosition(globalPos);
		double z7 = queryAccessor.get().getRealDouble();
		globalPos[0] = x;
		globalPos[1] = y + 1;
		queryAccessor.setPosition(globalPos);
		double z8 = queryAccessor.get().getRealDouble();
		globalPos[0] = x + 1;
		globalPos[1] = y + 1;
		queryAccessor.setPosition(globalPos);
		double z9 = queryAccessor.get().getRealDouble();

		// approximate (using abs()) sobel formula taken from Gonzalez & Woods

		double gx = (z7 + 2 * z8 + z9) - (z1 + 2 * z2 + z3);
		double gy = (z3 + 2 * z6 + z9) - (z1 + 2 * z4 + z7);

		return Math.abs(gx) + Math.abs(gy);
	}

	@Test
	public void testSobel() {
		img = makeInputImage();
		DiscreteNeigh neigh = new DiscreteNeigh(new long[2],
				new long[] { 1, 1 }, new long[] { 1, 1 });
		DoubleType outType = new DoubleType();
		Function<long[], DoubleType> imgFunc = new RealImageFunction<DoubleType,DoubleType>(
				img, new DoubleType());
		double[] kernel1 = new double[] { -1, -2, -1, 0, 0, 0, 1, 2, 1 };
		double[] kernel2 = new double[] { -1, 0, 1, -2, 0, 2, -1, 0, 1 };
		Function<long[], DoubleType> convFunc1 = new RealConvolutionFunction<DoubleType>(
				imgFunc, kernel1);
		Function<long[], DoubleType> convFunc2 = new RealConvolutionFunction<DoubleType>(
				imgFunc, kernel2);
		Function<long[], DoubleType> absFunc1 = new GeneralUnaryFunction<long[], DoubleType, DoubleType>(
				convFunc1, new RealAbs<DoubleType,DoubleType>(), new DoubleType());
		Function<long[], DoubleType> absFunc2 = new GeneralUnaryFunction<long[], DoubleType, DoubleType>(
				convFunc2, new RealAbs<DoubleType,DoubleType>(), new DoubleType());
		Function<long[], DoubleType> addFunc = new GeneralBinaryFunction<long[], DoubleType, DoubleType, DoubleType>(
				absFunc1, absFunc2,	new RealAdd<DoubleType,DoubleType,DoubleType>(), outType);
		DoubleType output = new DoubleType();
		for (int x = 1; x < XSIZE - 1; x++) {
			for (int y = 1; y < YSIZE - 1; y++) {
				neigh.getKeyPoint()[0] = x;
				neigh.getKeyPoint()[1] = y;
				addFunc.evaluate(neigh, neigh.getKeyPoint(), output);
				assertTrue(veryClose(output.getRealDouble(), expectedValue(x, y)));
				/*
				{
					System.out.println(" FAILURE at (" + x + "," + y
							+ "): expected (" + expectedValue(x, y)
							+ ") actual (" + output.getRealDouble() + ")");
					success = false;
				}
				*/
			}
		}
	}
}
