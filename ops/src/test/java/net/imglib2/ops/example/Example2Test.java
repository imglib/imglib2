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
import net.imglib2.ops.function.real.RealArithmeticMeanFunction;
import net.imglib2.ops.function.real.RealImageFunction;
import net.imglib2.ops.input.PointSetInputIterator;
import net.imglib2.ops.pointset.HyperVolumePointSet;
import net.imglib2.ops.pointset.PointSet;
import net.imglib2.type.numeric.real.DoubleType;

import org.junit.Test;

// take an average of the z values of a 3d image

/**
 * 
 * @author Barry DeZonia
 */
public class Example2Test {

	private final int XSIZE = 50;
	private final int YSIZE = 75;
	private final int ZSIZE = 5;

	private boolean veryClose(double d1, double d2) {
		return Math.abs(d1-d2) < 0.00001;
	}

	private Img<DoubleType> allocateImage() {
		final ArrayImgFactory<DoubleType> imgFactory =
				new ArrayImgFactory<DoubleType>();
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
		HyperVolumePointSet xySpace =
			new HyperVolumePointSet(new long[] { XSIZE, YSIZE, 1 });
		HyperVolumePointSet zNeigh =
			new HyperVolumePointSet(new long[] { 1, 1, ZSIZE });
		Function<long[], DoubleType> imageFunc =
			new RealImageFunction<DoubleType, DoubleType>(image, new DoubleType());
		Function<PointSet, DoubleType> aveFunc =
			new RealArithmeticMeanFunction<DoubleType>(imageFunc);
		DoubleType variable = new DoubleType();
		PointSetInputIterator iter = new PointSetInputIterator(xySpace, zNeigh);
		PointSet points = null;
		while (iter.hasNext()) {
			points = iter.next(points);
			aveFunc.compute(zNeigh, variable);
			long[] currOrigin = points.getOrigin();
			long x = currOrigin[0];
			long y = currOrigin[1];
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
