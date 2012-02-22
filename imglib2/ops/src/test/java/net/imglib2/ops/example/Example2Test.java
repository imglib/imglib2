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
import net.imglib2.ops.function.real.RealAverageFunction;
import net.imglib2.ops.function.real.RealImageFunction;
import net.imglib2.type.numeric.real.DoubleType;


// take an average of the z values of a 3d image

/**
 * 
 * @author Barry DeZonia
 *
 */
public class Example2Test {

	private final int XSIZE = 50;
	private final int YSIZE = 75;
	private final int ZSIZE = 5;

	private boolean veryClose(double d1, double d2) {
		return Math.abs(d1-d2) < 0.00001;
	}

	private Img<DoubleType> allocateImage() {
		final ArrayImgFactory<DoubleType> imgFactory = new ArrayImgFactory<DoubleType>();
		return imgFactory.create(new long[]{XSIZE,YSIZE,ZSIZE}, new DoubleType());
	}

	private Img<DoubleType> makeInputImage() {
		Img<DoubleType> inputImg = allocateImage();
		RandomAccess<DoubleType> accessor = inputImg.randomAccess();
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
	
	@Test
	public void testZAveraging() {

		// calculate output values as an average of a number of Z planes
		
		Img<DoubleType> image = makeInputImage();
		DiscreteNeigh inputNeigh = new DiscreteNeigh(new long[]{0,0,0}, new long[]{0,0,0}, new long[]{0,0,ZSIZE-1});
		Function<long[],DoubleType> imageFunc = new RealImageFunction<DoubleType,DoubleType>(image, new DoubleType());
		Function<long[],DoubleType> aveFunc = new RealAverageFunction<DoubleType>(imageFunc);
		long[] currPt = inputNeigh.getKeyPoint();
		DoubleType variable = new DoubleType();
		for (int x = 0; x < XSIZE; x++) {
			for (int y = 0; y < YSIZE; y++) {
				currPt[0] = x;
				currPt[1] = y;
				currPt[2] = 0;
				aveFunc.evaluate(inputNeigh, currPt, variable);
				assertTrue(veryClose(variable.getRealDouble(), x+y+((0.0+1+2+3+4) / 5.0)));
				/*
				{
					System.out.println(" FAILURE at ("+x+","+y+"): expected ("
						+(x+y+((0.0+1+2+3+4) / 5.0))+") actual ("+variable.getRealDouble()+")");
					success = false;
				}
				*/
			}
		}
	}
}
