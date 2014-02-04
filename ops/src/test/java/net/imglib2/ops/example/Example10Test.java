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
import net.imglib2.ops.function.complex.CartesianComplexFunction;
import net.imglib2.ops.function.complex.DFTFunction;
import net.imglib2.ops.function.complex.IDFTFunction;
import net.imglib2.ops.function.real.RealConstantFunction;
import net.imglib2.ops.function.real.RealImageFunction;
import net.imglib2.ops.input.PointInputIterator;
import net.imglib2.ops.pointset.HyperVolumePointSet;
import net.imglib2.type.numeric.complex.ComplexDoubleType;
import net.imglib2.type.numeric.real.DoubleType;

import org.junit.Test;

/**
 * 
 * @author Barry DeZonia
 */
public class Example10Test {
	
	private final long XSIZE = 50;
	private final long YSIZE = 50;
	
	private Img<DoubleType> testImg;
	private Function<long[],DoubleType> image;
	private Function<long[],ComplexDoubleType> dft;
	
	private boolean veryClose(double d1, double d2) {
		return Math.abs(d1-d2) < 0.00001;
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

	private void testDFT() {
		image =
			new RealImageFunction<DoubleType, DoubleType>(testImg, new DoubleType());
		Function<long[], DoubleType> zero =
			new RealConstantFunction<long[], DoubleType>(new DoubleType(0));
		Function<long[],ComplexDoubleType> spatialFunction =
			new CartesianComplexFunction<long[], DoubleType, DoubleType, ComplexDoubleType>(
				image, zero, new ComplexDoubleType());
		ArrayImgFactory<ComplexDoubleType> factory =
			new ArrayImgFactory<ComplexDoubleType>();
		dft =
			new DFTFunction<ComplexDoubleType>(factory, spatialFunction, new long[] {
				XSIZE, YSIZE });
		// TODO - test something
		assertTrue(true);
	}
	
	private void testIDFT() {
		ArrayImgFactory<ComplexDoubleType> factory =
			new ArrayImgFactory<ComplexDoubleType>();
		Function<long[],ComplexDoubleType> idft =
			new IDFTFunction<ComplexDoubleType>(factory, dft, new long[] { XSIZE,
				YSIZE });
		long[] pos = new long[2];
		DoubleType original = new DoubleType();
		ComplexDoubleType computed = new ComplexDoubleType();
		HyperVolumePointSet pointSet =
			new HyperVolumePointSet(new long[] { XSIZE, YSIZE });
		PointInputIterator iter = new PointInputIterator(pointSet);
		long[] point = null;
		while (iter.hasNext()) {
			point = iter.next(point);
			image.compute(pos, original);
			idft.compute(pos, computed);
			/*
			{
				System.out.println(" FAILURE at ("+x+","+y+"): expected ("
						+original.getRealDouble()+",0) actual ("+computed.getRealDouble()+","+computed.getImaginaryDouble()+")");
				success = false;
			}
			*/
			assertTrue(veryClose(computed.getRealDouble(), original.getRealDouble()));
			assertTrue(veryClose(computed.getImaginaryDouble(), 0));
		}
	}

	@Test
	public void test() {
		testImg = makeInputImage();
		testDFT();
		testIDFT();
	}
}
