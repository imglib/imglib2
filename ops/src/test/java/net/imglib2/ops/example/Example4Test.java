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

import java.util.ArrayList;

import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.ops.function.Function;
import net.imglib2.ops.function.real.RealImageFunction;
import net.imglib2.ops.function.real.RealProductFunction;
import net.imglib2.ops.input.PointSetInputIterator;
import net.imglib2.ops.pointset.GeneralPointSet;
import net.imglib2.ops.pointset.HyperVolumePointSet;
import net.imglib2.ops.pointset.PointSet;
import net.imglib2.type.numeric.real.DoubleType;

import org.junit.Test;

// get values that are an average of the 5 values in a 3x3 cross

/**
 * TODO
 *
 */
public class Example4Test {

	private final int XSIZE = 200;
	private final int YSIZE = 300;
	
	private boolean veryClose(double d1, double d2) {
		return Math.abs(d1-d2) < 0.00001;
	}

	private double expectedValue(int x, int y) {
		double ctr = x+y;
		double ne = (x+1) + (y-1);
		double nw = (x-1) + (y-1);
		double se = (x+1) + (y+1);
		double sw = (x-1) + (y+1);
		return ctr * ne * nw * se * sw;
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
				accessor.get().setReal(x+y);
			}			
		}
		return inputImg;
	}
	
	@Test
	public void testCrossNeighborhoodProduct() {

		Img<DoubleType> inputImg = makeInputImage();

		/* old way
		DiscreteNeigh neigh = new DiscreteNeigh(new long[2], new long[]{1,1}, new long[]{1,1});
		Condition<long[]> condition = new OnTheXYCrossCondition();
		Function<long[],DoubleType> input = new RealImageFunction<DoubleType,DoubleType>(inputImg, new DoubleType());
		Function<long[],DoubleType> one = new ConstantRealFunction<long[],DoubleType>(inputImg.firstElement(),1);
		Function<long[],DoubleType> conditionalFunc = new ConditionalFunction<long[],DoubleType>(condition, input, one);
		Function<long[],DoubleType> prodFunc = new RealProductFunction<DoubleType>(conditionalFunc); 
		long[] index = new long[2];
		DoubleType output = new DoubleType();
		for (int x = 1; x < XSIZE-1; x++) {
			for (int y = 1; y < YSIZE-1; y++) {
				index[0] = x;
				index[1] = y;
				neigh.moveTo(index);
				prodFunc.evaluate(neigh, neigh.getKeyPoint(), output);
				//{
				//	System.out.println(" FAILURE at ("+x+","+y+"): expected ("
				//		+expectedValue(x,y)+") actual ("+output.getRealDouble()+")");
				//	success = false;
				//}
				assertTrue(veryClose(output.getRealDouble(), expectedValue(x,y)));
			}
		}
		*/
		
		ArrayList<long[]> pts = new ArrayList<long[]>();
		pts.add(new long[]{-1,-1});
		pts.add(new long[]{-1, 1});
		pts.add(new long[]{ 0, 0});
		pts.add(new long[]{ 1,-1});
		pts.add(new long[]{ 1, 1});
		GeneralPointSet neigh = new GeneralPointSet(new long[]{0,0}, pts);
		Function<long[], DoubleType> input =
			new RealImageFunction<DoubleType, DoubleType>(inputImg, new DoubleType());
		Function<PointSet, DoubleType> prodFunc =
			new RealProductFunction<DoubleType>(input);
		HyperVolumePointSet space =
			new HyperVolumePointSet(new long[] { 1, 1 }, new long[] { XSIZE - 2,
				YSIZE - 2 });
		PointSetInputIterator iter = new PointSetInputIterator(space, neigh);
		DoubleType output = new DoubleType();
		PointSet points = null;
		while (iter.hasNext()) {
			points = iter.next(points);
			prodFunc.compute(points, output);
			int x = (int) points.getOrigin()[0];
			int y = (int) points.getOrigin()[1];
			//{
			//	System.out.println(" Point ("+x+","+y+"): expected ("
			//		+expectedValue(x,y)+") actual ("+output.getRealDouble()+")");
			//	success = false;
			//}
			assertTrue(veryClose(output.getRealDouble(), expectedValue(x,y)));
		}
	}
}
