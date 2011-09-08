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
import net.imglib2.ops.function.real.RealAverageFunction;
import net.imglib2.ops.function.real.RealImageFunction;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;


// take an average of the z values of a 3d image

/**
 * 
 * @author Barry DeZonia
 *
 */
public class Example2 {

	private static final int XSIZE = 50;
	private static final int YSIZE = 75;
	private static final int ZSIZE = 5;

	private static boolean veryClose(double d1, double d2) {
		return Math.abs(d1-d2) < 0.00001;
	}

	private static Img<DoubleType> allocateImage() {
		final ArrayImgFactory<DoubleType> imgFactory = new ArrayImgFactory<DoubleType>();
		return imgFactory.create(new long[]{XSIZE,YSIZE,ZSIZE}, new DoubleType());
	}

	private static Img<? extends RealType<?>> makeInputImage() {
		Img<? extends RealType<?>> inputImg = allocateImage();
		RandomAccess<? extends RealType<?>> accessor = inputImg.randomAccess();
		long[] pos = new long[3];
		for (int x = 0; x < XSIZE; x++) {
			for (int y = 0; y < YSIZE; y++) {
				for (int z = 0; z < ZSIZE; z++) {
					pos[0] = x;
					pos[1] = y;
					pos[2] = z;
					accessor.setPosition(pos);
					accessor.get().setReal(x+y+z);
				}
			}			
		}
		return inputImg;
	}
	
	// calculate output values as an average of a number of Z planes
	
	private static boolean testZAveraging() {
		boolean success = true;
		Img<? extends RealType<?>> image = makeInputImage();
		DiscreteNeigh inputNeigh = new DiscreteNeigh(new long[]{0,0,0}, new long[]{0,0,0}, new long[]{0,0,ZSIZE-1});
		Function<long[],Real> imageFunc = new RealImageFunction(image);
		Function<long[],Real> aveFunc = new RealAverageFunction(imageFunc);
		long[] currPt = inputNeigh.getKeyPoint();
		Real variable = new Real();
		for (int x = 0; x < XSIZE; x++) {
			for (int y = 0; y < YSIZE; y++) {
				currPt[0] = x;
				currPt[1] = y;
				currPt[2] = 0;
				aveFunc.evaluate(inputNeigh, currPt, variable);
				if (!veryClose(variable.getReal(), x+y+((0.0+1+2+3+4) / 5.0))) {
					System.out.println(" FAILURE at ("+x+","+y+"): expected ("
						+(x+y+((0.0+1+2+3+4) / 5.0))+") actual ("+variable.getReal()+")");
					success = false;
				}
			}
		}
		return success;
	}
	
	public static void main(String[] args) {
		System.out.println("Example2");
		if (testZAveraging())
			System.out.println(" Successful test");
	}
}
