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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.ops.function.Function;
import net.imglib2.ops.function.real.RealArithmeticMeanFunction;
import net.imglib2.ops.function.real.RealImageFunction;
import net.imglib2.ops.function.real.RealMedianFunction;
import net.imglib2.ops.img.ImageAssignment;
import net.imglib2.ops.input.InputIteratorFactory;
import net.imglib2.ops.input.PointSetInputIterator;
import net.imglib2.ops.input.PointSetInputIteratorFactory;
import net.imglib2.ops.pointset.HyperVolumePointSet;
import net.imglib2.ops.pointset.PointSet;
import net.imglib2.type.numeric.real.DoubleType;

import org.junit.Test;

// a dual neighborhood example that also uses an out of bounds strategy.
// each point of output equals the median of the 3x3 XY neighborhood of the 1xZ averages of a 3d image

// This example shows a problem in redesign. Lets say we want a function that calcs a 3x3 XY median of
// averages of 1xZ samples. We want to Function::compute() to look something like this:
//   compute(PointSet points, Tuple<Axis,Long> indices ...)
// Inside any function iterators fix some axes from the tuple and range freely over the others. Only
// points in the PointSet that are compatible with the specified indices are included.
// Note the indices are a way of efficiently making a new PointSet from an existing PointSet. In
// perfect world we'd calc a new PointSet and pass it in but this is probably way too inefficient.
// Would need to have a way to minimize object creation.

// TODO - add out of bounds code and fix nested for loops to go [0,SIZE-1] rather than [1,SIZE-2] 

/**
 * 
 * @author Barry DeZonia
 */
public class Example8Test {
	private final long XSIZE = 20;
	private final long YSIZE = 15;
	private final long ZSIZE = 5;

	private Img<DoubleType> img;
	
	private Img<DoubleType> allocateImage(long[] dims) {
		final ArrayImgFactory<DoubleType> imgFactory =
				new ArrayImgFactory<DoubleType>();
		return imgFactory.create(dims, new DoubleType());
	}

	private Img<DoubleType> makeInputImage() {
		Img<DoubleType> inputImg = allocateImage(new long[]{XSIZE,YSIZE,ZSIZE});
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

	private Img<DoubleType> makeTmpImage() {
		return allocateImage(new long[]{XSIZE,YSIZE,1});
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

	private void testAssignmentResults(Img<DoubleType> image) {
		assertEquals(image.numDimensions(), 3);
		assertEquals(image.dimension(0), XSIZE);
		assertEquals(image.dimension(1), YSIZE);
		assertEquals(image.dimension(2), 1);
		long[] pos = new long[3];
		Cursor<DoubleType> cursor = image.cursor();
		while (cursor.hasNext()) {
			DoubleType ave = cursor.next();
			cursor.localize(pos);
			long x = pos[0];
			long y = pos[1];
			double expectedAve = average((int)x,(int)y);
			assertEquals(expectedAve, ave.getRealDouble(), 0.0000001);
		}
	}
	
	@Test
	public void testTwoNeighborhoodFunction() {
		img = makeInputImage();
		Img<DoubleType> tmpImg = makeTmpImage();
		Function<long[],DoubleType> imgFunc =
				new RealImageFunction<DoubleType,DoubleType>(img, new DoubleType());
		Function<PointSet,DoubleType> avgFunc =
				new RealArithmeticMeanFunction<DoubleType>(imgFunc);
		HyperVolumePointSet avgNeigh =
			new HyperVolumePointSet(new long[] { 1, 1, ZSIZE });
		InputIteratorFactory<PointSet> factory =
			new PointSetInputIteratorFactory(avgNeigh);
		ImageAssignment<DoubleType,DoubleType,PointSet> assigner =
			new ImageAssignment<DoubleType, DoubleType, PointSet>(tmpImg,
				new long[3], new long[] { XSIZE, YSIZE, 1 }, avgFunc, null, factory);
		assigner.assign();
		testAssignmentResults(tmpImg);
		Function<long[], DoubleType> tmpImgFunc =
			new RealImageFunction<DoubleType, DoubleType>(tmpImg, new DoubleType());
		HyperVolumePointSet medianNeigh =
			new HyperVolumePointSet(new long[3], new long[] { 1, 1, 0 }, new long[] {
				1, 1, 0 });
		Function<PointSet, DoubleType> medianFunc =
			new RealMedianFunction<DoubleType>(tmpImgFunc);
		DoubleType output = new DoubleType();
		HyperVolumePointSet space =
			new HyperVolumePointSet(new long[] { 1, 1, 0 }, new long[] { XSIZE - 2,
				YSIZE - 2, 0 });
		PointSetInputIterator iter = new PointSetInputIterator(space, medianNeigh);
		PointSet points = null;
		while (iter.hasNext()) {
			points = iter.next(points);
			medianFunc.compute(points, output);
			int x = (int) points.getOrigin()[0];
			int y = (int) points.getOrigin()[1];
			/*
			{
				System.out.println(" FAILURE at ("+x+","+y+"): expected ("
					+expectedValue(x,y)+") actual ("+output.getRealDouble()+")");
				success = false;
			}
			 */
			assertTrue(veryClose(output.getRealDouble(), expectedValue(x, y)));
		}
	}
}
