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
import net.imglib2.ops.function.real.RealImageFunction;
import net.imglib2.ops.function.real.RealMedianFunction;
import net.imglib2.ops.input.PointSetInputIterator;
import net.imglib2.ops.pointset.HyperVolumePointSet;
import net.imglib2.ops.pointset.PointSet;
import net.imglib2.type.numeric.integer.LongType;

import org.junit.Test;

// a 3x3x3 median example

/**
 * 
 * @author Barry DeZonia
 */
public class Example3Test {

	private final int XSIZE = 50;
	private final int YSIZE = 75;
	private final int ZSIZE = 100;

	private boolean veryClose(double d1, double d2) {
		return Math.abs(d1-d2) < 0.00001;
	}

	private Img<LongType> allocateImage() {
		final ArrayImgFactory<LongType> imgFactory = new ArrayImgFactory<LongType>();
		return imgFactory.create(new long[]{XSIZE,YSIZE,ZSIZE}, new LongType());
	}

	private Img<LongType> makeInputImage() {
		Img<LongType> inputImg = allocateImage();
		RandomAccess<LongType> accessor = inputImg.randomAccess();
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
		return inputImg;
	}
	
	@Test
	public void test3x3x3Median() {

		// calculate output values as a median of 3x3x3 cells of image
		
		Img<LongType> image = makeInputImage();
		HyperVolumePointSet inputNeigh =
			new HyperVolumePointSet(new long[3], new long[] { 1, 1, 1 }, new long[] {
				1, 1, 1 });
		Function<long[], LongType> imageFunc =
			new RealImageFunction<LongType, LongType>(image, new LongType());
		Function<PointSet, LongType> medFunc =
			new RealMedianFunction<LongType>(imageFunc);
		HyperVolumePointSet space =
			new HyperVolumePointSet(new long[] { 1, 1, 1 }, new long[] { XSIZE - 2,
				YSIZE - 2, ZSIZE - 2 });
		PointSetInputIterator iter = new PointSetInputIterator(space, inputNeigh);
		LongType variable = new LongType();
		PointSet points = null;
		while (iter.hasNext()) {
			points = iter.next(points);
			medFunc.compute(points, variable);
			long[] currPos = points.getOrigin();
			long x = currPos[0];
			long y = currPos[1];
			long z = currPos[2];
			/*
			System.out.println(" Point ("+x+","+y+","+z+"): expected ("
				+(x + 2*y + 3*z)+") actual ("+variable.getRealDouble()+")");
			 */
			assertTrue(veryClose(variable.getRealDouble(), x + 2*y + 3*z));
		}
	}
}
