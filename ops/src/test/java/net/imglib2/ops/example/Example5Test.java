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
import net.imglib2.ops.function.real.RealCorrelationFunction;
import net.imglib2.ops.function.real.RealImageFunction;
import net.imglib2.ops.input.PointSetInputIterator;
import net.imglib2.ops.pointset.HyperVolumePointSet;
import net.imglib2.ops.pointset.PointSet;
import net.imglib2.type.numeric.real.DoubleType;

import org.junit.Test;

// a 3x3 correlation example

/**
 * 
 * @author Barry DeZonia
 */
public class Example5Test {

	private final int XSIZE = 50;
	private final int YSIZE = 75;

	private double[] KERNEL = new double[]{1,2,3,4,5,6,7,8,9};
	
	private boolean veryClose(double d1, double d2) {
		return Math.abs(d1-d2) < 0.00001;
	}

	private double expectedValue(int x, int y) {
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
	
	private Img<DoubleType> allocateImage() {
		final ArrayImgFactory<DoubleType> imgFactory =
			new ArrayImgFactory<DoubleType>();
		return imgFactory.create(new long[]{XSIZE,YSIZE}, new DoubleType());
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
				accessor.get().setReal(x + 2*y);
			}			
		}
		return inputImg;
	}

	@Test
	public void test3x3Correletion() {
		
		// calculate output values as a correlation of 3x3 cells of image with
		// KERNEL
		
		Img<DoubleType> image = makeInputImage();
		HyperVolumePointSet inputNeigh =
			new HyperVolumePointSet(new long[2], new long[] { 1, 1 }, new long[] { 1,
				1 });
		Function<long[], DoubleType> imageFunc =
			new RealImageFunction<DoubleType, DoubleType>(image, new DoubleType());
		Function<PointSet, DoubleType> corrFunc =
			new RealCorrelationFunction<DoubleType>(imageFunc, KERNEL);
		HyperVolumePointSet space =
			new HyperVolumePointSet(new long[] { 1, 1 }, new long[] { XSIZE - 2,
				YSIZE - 2 });
		PointSetInputIterator iter = new PointSetInputIterator(space, inputNeigh);
		DoubleType variable = new DoubleType();
		PointSet points = null;
		while (iter.hasNext()) {
			points = iter.next(points);
			corrFunc.compute(points, variable);
			int x = (int) points.getOrigin()[0];
			int y = (int) points.getOrigin()[1];
			//{
			//	System.out.println(" Point ("+x+","+y+"): expected ("
			//		+expectedValue(x, y)+") actual ("+variable.getRealDouble()+")");
			//	success = false;
			//}
			assertTrue(veryClose(variable.getRealDouble(), expectedValue(x, y)));
		}
	}
}
