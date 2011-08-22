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

import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.ops.DiscreteNeigh;
import net.imglib2.ops.Function;
import net.imglib2.ops.Real;
import net.imglib2.ops.function.real.RealImageFunction;
import net.imglib2.ops.function.real.RealConvolutionFunction;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

// a 3x3x3 median example

/**
 * 
 * @author Barry DeZonia
 *
 */
public class Example5 {

	private static final int XSIZE = 50;
	private static final int YSIZE = 75;

	private static double[] KERNEL = new double[]{1,2,3,4,5,6,7,8,9};
	
	private static boolean veryClose(double d1, double d2) {
		return Math.abs(d1-d2) < 0.00001;
	}

	private static double expectedValue(int x, int y) {
		double nw = (x-1) + 2*(y-1);
		double n = (x) + 2*(y-1);
		double ne = (x+1) + 2*(y-1);
		double w = (x-1) + 2*(y);
		double c = (x) + 2*(y);
		double e = (x+1) + 2*(y);
		double sw = (x-1) + 2*(y+1);
		double s = (x) + 2*(y+1);
		double se = (x+1) + 2*(y+1);
		double value = 0;
		value += KERNEL[0] * nw;
		value += KERNEL[1] * n;
		value += KERNEL[2] * ne;
		value += KERNEL[3] * w;
		value += KERNEL[4] * c;
		value += KERNEL[5] * e;
		value += KERNEL[6] * sw;
		value += KERNEL[7] * s;
		value += KERNEL[8] * se;
		return value;
	}
	
	private static Img<DoubleType> allocateImage() {
		final ArrayImgFactory<DoubleType> imgFactory = new ArrayImgFactory<DoubleType>();
		return imgFactory.create(new long[]{XSIZE,YSIZE}, new DoubleType());
	}

	private static Img<? extends RealType<?>> makeInputImage() {
		Img<? extends RealType<?>> inputImg = allocateImage();
		RandomAccess<? extends RealType<?>> accessor = inputImg.randomAccess();
		long[] pos = new long[2];
		for (int x = 0; x < XSIZE; x++) {
			for (int y = 0; y < YSIZE; y++) {
				pos[0] = x;
				pos[1] = y;
				accessor.setPosition(pos);
				accessor.get().setReal(x + 2*y);
			}			
		}
		return inputImg;
	}
	
	// calculate output values as a convolution of 3x3 cells of image with KERNEL
	
	private static boolean test3x3Convolution() {
		boolean success = true;
		Img<? extends RealType<?>> image = makeInputImage();
		DiscreteNeigh inputNeigh = new DiscreteNeigh(new long[2], new long[]{1,1}, new long[]{1,1});
		Function<long[],Real> imageFunc = new RealImageFunction(image);
		Function<long[],Real> convFunc = new RealConvolutionFunction(imageFunc,KERNEL);
		long[] currPt = new long[2];
		Real variable = new Real();
		for (int x = 1; x < XSIZE-1; x++) {
			for (int y = 1; y < YSIZE-1; y++) {
				currPt[0] = x;
				currPt[1] = y;
				inputNeigh.moveTo(currPt);
				convFunc.evaluate(inputNeigh, currPt, variable);
				if (!veryClose(variable.getReal(), expectedValue(x, y))) {
					System.out.println(" FAILURE at ("+x+","+y+"): expected ("
						+expectedValue(x, y)+") actual ("+variable.getReal()+")");
					success = false;
				}
			}
		}
		return success;
	}
	
	public static void main(String[] args) {
		System.out.println("Example5");
		if (test3x3Convolution())
			System.out.println(" Successful test");
	}
}
