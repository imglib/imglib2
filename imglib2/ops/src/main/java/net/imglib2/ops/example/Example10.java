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
import net.imglib2.ops.Complex;
import net.imglib2.ops.DiscreteNeigh;
import net.imglib2.ops.Function;
import net.imglib2.ops.Real;
import net.imglib2.ops.function.complex.ComplexAdapterFunction;
import net.imglib2.ops.function.complex.DFTFunction;
import net.imglib2.ops.function.complex.IDFTFunction;
import net.imglib2.ops.function.real.RealImageFunction;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;


/**
 * 
 * @author Barry DeZonia
 *
 */
public class Example10 {
	
	private static final long XSIZE = 50;
	private static final long YSIZE = 50;
	
	private static Img<? extends RealType<?>> testImg;
	private static Function<long[],Real> image;
	private static Function<long[],Complex> dft;
	
	private static boolean veryClose(double d1, double d2) {
		return Math.abs(d1-d2) < 0.00001;
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
				accessor.get().setReal(x+y);
			}			
		}
		return inputImg;
	}

	private static boolean testDFT() {
		image = new RealImageFunction(testImg);
		Function<long[],Complex> spatialFunction = new ComplexAdapterFunction<long[]>(image);
		dft = new DFTFunction(spatialFunction, new long[]{XSIZE,YSIZE}, new long[2], new long[2]);
		// TODO - test something
		return true;
	}
	
	private static boolean testIDFT() {
		boolean success = true;
		DiscreteNeigh neigh = new DiscreteNeigh(new long[2], new long[2], new long[2]);
		Function<long[],Complex> idft = new IDFTFunction(dft, new long[]{XSIZE,YSIZE}, new long[2], new long[2]);
		long[] pos = new long[2];
		Real original = new Real();
		Complex computed = new Complex();
		for (int x = 0; x < XSIZE; x++) {
			for (int y = 0; y < YSIZE; y++) {
				pos[0] = x;
				pos[1] = y;
				neigh.moveTo(pos);
				image.evaluate(neigh, pos, original);
				idft.evaluate(neigh, pos, computed);
				if (
						(!veryClose(computed.getX(), original.getReal())) ||
						(!veryClose(computed.getY(), 0)))
				{
					System.out.println(" FAILURE at ("+x+","+y+"): expected ("
							+original.getReal()+",0) actual ("+computed.getX()+","+computed.getY()+")");
					success = false;
				}
			}
		}
		return success;
	}

	public static void main(String[] args) {
		System.out.println("Example10");
		testImg = makeInputImage();
		if (testDFT() && testIDFT())
			System.out.println(" Successful test");
	}
}
