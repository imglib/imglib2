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
import net.imglib2.outofbounds.OutOfBoundsPeriodicFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

// an example testing out of bounds access for a real image function

// TODO - test complex image type too

/**
 * 
 * @author Barry DeZonia
 *
 */
public class Example11 {

	private static final int XSIZE = 2;
	private static final int YSIZE = 2;

	private static boolean veryClose(double d1, double d2) {
		return Math.abs(d1-d2) < 0.00001;
	}

	private static Img<DoubleType> allocateRealImage() {
		final ArrayImgFactory<DoubleType> imgFactory = new ArrayImgFactory<DoubleType>();
		return imgFactory.create(new long[]{XSIZE,YSIZE}, new DoubleType());
	}

	private static Img<? extends RealType<?>> makeInputImage() {
		Img<? extends RealType<?>> inputImg = allocateRealImage();
		RandomAccess<? extends RealType<?>> accessor = inputImg.randomAccess();
		long[] pos = new long[3];
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
	
	private static boolean testOutOfBounds() {
		boolean success = true;
		Img<? extends RealType<?>> image = makeInputImage();
		DiscreteNeigh inputNeigh = new DiscreteNeigh(new long[2], new long[]{1,1}, new long[]{1,1});
		Function<long[],Real> imageFunc = new RealImageFunction(image, new OutOfBoundsPeriodicFactory());
		long[] currPt = new long[2];
		Real inbounds = new Real();
		Real left = new Real();
		Real right = new Real();
		Real top = new Real();
		Real bottom = new Real();
		for (int x = 0; x < XSIZE; x++) {
			for (int y = 0; y < YSIZE; y++) {
				currPt[0] = x;
				currPt[1] = y;
				inputNeigh.moveTo(currPt);
				imageFunc.evaluate(inputNeigh, currPt, inbounds);
				currPt[0] = x - XSIZE;
				currPt[1] = y;
				inputNeigh.moveTo(currPt);
				imageFunc.evaluate(inputNeigh, currPt, left);
				if (!veryClose(inbounds.getReal(), left.getReal())) {
					System.out.println(" FAILURE at ("+x+","+y+"): expected ("
						+(inbounds.getReal())+") actual ("+left.getReal()+")");
					success = false;
				}
				currPt[0] = x + XSIZE;
				currPt[1] = y;
				inputNeigh.moveTo(currPt);
				imageFunc.evaluate(inputNeigh, currPt, right);
				if (!veryClose(inbounds.getReal(), right.getReal())) {
					System.out.println(" FAILURE at ("+x+","+y+"): expected ("
						+(inbounds.getReal())+") actual ("+right.getReal()+")");
					success = false;
				}
				currPt[0] = x;
				currPt[1] = y - YSIZE;
				inputNeigh.moveTo(currPt);
				imageFunc.evaluate(inputNeigh, currPt, top);
				if (!veryClose(inbounds.getReal(), top.getReal())) {
					System.out.println(" FAILURE at ("+x+","+y+"): expected ("
						+(inbounds.getReal())+") actual ("+top.getReal()+")");
					success = false;
				}
				currPt[0] = x;
				currPt[1] = y + YSIZE;
				inputNeigh.moveTo(currPt);
				imageFunc.evaluate(inputNeigh, currPt, bottom);
				if (!veryClose(inbounds.getReal(), bottom.getReal())) {
					System.out.println(" FAILURE at ("+x+","+y+"): expected ("
						+(inbounds.getReal())+") actual ("+bottom.getReal()+")");
					success = false;
				}
			}
		}
		return success;
	}
	
	public static void main(String[] args) {
		System.out.println("Example11");
		if (testOutOfBounds())
			System.out.println(" Successful test");
	}
}
