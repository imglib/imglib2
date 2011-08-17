/*

Copyright (c) 2011, Stephan Preibisch & Stephan Saalfeld.
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

import net.imglib2.ops.DiscreteNeigh;
import net.imglib2.ops.Function;
import net.imglib2.ops.Real;
import net.imglib2.ops.function.real.RealImageFunction;
import net.imglib2.ops.function.real.RealMedianFunction;
import net.imglib2.ops.image.RealImage;

// a 3x3x3 median example

/**
 * 
 * @author Barry DeZonia
 *
 */
public class Example3 {

	private static final int XRANGE = 50;
	private static final int YRANGE = 75;
	private static final int ZRANGE = 100;

	private static boolean veryClose(double d1, double d2) {
		return Math.abs(d1-d2) < 0.00001;
	}

	private static RealImage makeTestImage() {
		RealImage image = new RealImage(new long[]{XRANGE, YRANGE, ZRANGE}, new String[]{"X","Y","Z"});
		long[] index = new long[3];
		Real value = new Real();
		for (int x = 0; x < image.dimension(0); x++) {
			for (int y = 0; y < image.dimension(1); y++) {
				for (int z = 0; z < image.dimension(2); z++) {
					index[0] = x;
					index[1] = y;
					index[2] = z;
					value.setReal(x + 2*y + 3*z);
					image.setReal(index, value);
				}
			}
		}
		return image;
	}
	
	// calculate output values as a median of 3x3x3 cells of image
	
	private static boolean test3x3x3Median() {
		boolean success = true;
		/*
		RealImage image = makeTestImage();
		DiscreteNeigh inputNeigh = new DiscreteNeigh(new long[3], new long[]{1,1,1}, new long[]{1,1,1});
		Function<DiscreteNeigh,Real> imageFunc = new RealImageFunction(image);
		Function<DiscreteNeigh,Real> medFunc = new RealMedianFunction(inputNeigh,imageFunc);
		long[] currPt = new long[3];
		Real variable = new Real();
		for (int x = 1; x < XRANGE-1; x++) {
			for (int y = 1; y < YRANGE-1; y++) {
				for (int z = 1; z < ZRANGE-1; z++) {
					currPt[0] = x;
					currPt[1] = y;
					currPt[2] = z;
					inputNeigh.moveTo(currPt);
					medFunc.evaluate(inputNeigh, variable);
					if (!veryClose(variable.getReal(), x + 2*y + 3*z)) {
						System.out.println(" FAILURE at ("+x+","+y+"): expected ("
							+(x + 2*y + 3*z)+") actual ("+variable.getReal()+")");
						success = false;
					}
				}
			}
		}
		*/
		return success;
	}
	
	public static void main(String[] args) {
		System.out.println("Example3");
		if (test3x3x3Median())
			System.out.println(" Successful test");
	}
}
