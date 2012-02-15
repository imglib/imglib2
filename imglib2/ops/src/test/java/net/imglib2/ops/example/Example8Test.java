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

import java.util.ArrayList;
import java.util.Collections;

import org.junit.Test;

import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.ops.DiscreteNeigh;
import net.imglib2.ops.Function;
import net.imglib2.ops.function.general.NeighborhoodAdapterFunction;
import net.imglib2.ops.function.real.RealAverageFunction;
import net.imglib2.ops.function.real.RealImageFunction;
import net.imglib2.ops.function.real.RealMedianFunction;
import net.imglib2.type.numeric.real.DoubleType;

// a dual neighborhood example that also uses an out of bounds strategy.
// each point of output equals the median of the 3x3 XY neighborhood of the 1xZ averages of a 3d image

// TODO - add out of bounds code and fix nested for loops to go [0,SIZE-1] rather than (0,SIZE-1) 

/**
 * 
 * @author Barry DeZonia
 *
 */
public class Example8Test {
	private final long XSIZE = 20;
	private final long YSIZE = 15;
	private final long ZSIZE = 5;

	private Img<DoubleType> img;
	
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
					accessor.get().setReal(x + 2*y + 3*z);
				}
			}			
		}
		// TODO - add out of bounds stuff
		//inputImg.randomAccess(new OutOfBoundsConstantValue(0));
		return inputImg;
	}

	private double average(int x, int y) {
		RandomAccess<DoubleType> accessor = img.randomAccess();
		long[] pos = new long[3];
		pos[0] = x;
		pos[1] = y;
		double sum = 0;
		double numElements = 0;
		for (int z = 0; z < ZSIZE; z++) {
			pos[2] = z;
			accessor.setPosition(pos);
			sum += accessor.get().getRealDouble();
			numElements++;
		}
		return sum / numElements;
	}
	
	private boolean veryClose(double d1, double d2) {
		return Math.abs(d1-d2) < 0.00001;
	}

	private double expectedValue(int x, int y) {
		ArrayList<Double> values = new ArrayList<Double>();
		for (int xi = x-1; xi <= x+1; xi++) {
			for (int yi = y-1; yi <= y+1; yi++) {
				values.add(average(xi,yi));
			}
		}
		Collections.sort(values);
		return values.get(4);
	}

	@Test
	public void testTwoNeighborhoodFunction() {
		img = makeInputImage();
		DiscreteNeigh avgNeigh = new DiscreteNeigh(new long[3], new long[]{0,0,0}, new long[]{0,0,ZSIZE-1});
		DiscreteNeigh medianNeigh = new DiscreteNeigh(new long[3], new long[]{1,1,0}, new long[]{1,1,0});
		Function<long[],DoubleType> imgFunc = new RealImageFunction<DoubleType,DoubleType>(img, new DoubleType());
		Function<long[],DoubleType> avgFunc = new RealAverageFunction<DoubleType>(imgFunc);
		Function<long[],DoubleType> adapFunc = new NeighborhoodAdapterFunction<long[],DoubleType>(avgFunc, avgNeigh);
		Function<long[],DoubleType> medianFunc = new RealMedianFunction<DoubleType>(adapFunc);
		DoubleType output = new DoubleType();
		for (int x = 1; x < XSIZE-1; x++) {
			for (int y = 1; y < YSIZE-1; y++) {
				medianNeigh.getKeyPoint()[0] = x;
				medianNeigh.getKeyPoint()[1] = y;
				medianNeigh.getKeyPoint()[2] = 0;
				medianFunc.evaluate(medianNeigh, medianNeigh.getKeyPoint(), output);
				assertTrue(veryClose(output.getRealDouble(), expectedValue(x, y)));
				/*
				{
					System.out.println(" FAILURE at ("+x+","+y+"): expected ("
						+expectedValue(x,y)+") actual ("+output.getRealDouble()+")");
					success = false;
				}
				 */
			}			
		}
	}
}
