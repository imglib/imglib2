/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
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
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */


package net.imglib2.ops.example;

import static org.junit.Assert.*;

import org.junit.Test;

import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.ops.Function;
import net.imglib2.ops.function.real.RealImageFunction;
import net.imglib2.ops.input.PointInputIterator;
import net.imglib2.ops.pointset.HyperVolumePointSet;
import net.imglib2.outofbounds.OutOfBoundsPeriodicFactory;
import net.imglib2.type.numeric.real.DoubleType;

// an example testing out of bounds access for a real image function

// TODO - test complex image type too

/**
 * 
 * @author Barry DeZonia
 */
public class Example11Test {

	private final int XSIZE = 2;
	private final int YSIZE = 2;

	private boolean veryClose(double d1, double d2) {
		return Math.abs(d1-d2) < 0.00001;
	}

	private Img<DoubleType> allocateRealImage() {
		final ArrayImgFactory<DoubleType> imgFactory = new ArrayImgFactory<DoubleType>();
		return imgFactory.create(new long[]{XSIZE,YSIZE}, new DoubleType());
	}

	private Img<DoubleType> makeInputImage() {
		Img<DoubleType> inputImg = allocateRealImage();
		RandomAccess<DoubleType> accessor = inputImg.randomAccess();
		long[] pos = new long[3];
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
	public void testOutOfBounds() {
		Img<DoubleType> image = makeInputImage();
		Function<long[],DoubleType> imageFunc =
			new RealImageFunction<DoubleType,DoubleType>(
				image,
				new OutOfBoundsPeriodicFactory<DoubleType, Img<DoubleType>>(),
				new DoubleType());
		long[] currPt = new long[2];
		DoubleType inbounds = new DoubleType();
		DoubleType left = new DoubleType();
		DoubleType right = new DoubleType();
		DoubleType top = new DoubleType();
		DoubleType bottom = new DoubleType();
		HyperVolumePointSet pointSet = new HyperVolumePointSet(new long[]{0,0},new long[]{0,0}, new long[]{XSIZE,YSIZE});
		PointInputIterator iter = new PointInputIterator(pointSet);
		long[] iterPt = null;
		while (iter.hasNext()) {
			iterPt = iter.next(iterPt);
			long x = iterPt[0];
			long y = iterPt[1];
			currPt[0] = x;
			currPt[1] = y;
			imageFunc.compute(currPt, inbounds);
			currPt[0] = x - XSIZE;
			currPt[1] = y;
			imageFunc.compute(currPt, left);
			assertTrue(veryClose(inbounds.getRealDouble(), left.getRealDouble()));
			/*
			{
				System.out.println(" FAILURE at ("+x+","+y+"): expected ("
					+(inbounds.getRealDouble())+") actual ("+left.getRealDouble()+")");
				success = false;
			}
			*/
			currPt[0] = x + XSIZE;
			currPt[1] = y;
			imageFunc.compute(currPt, right);
			assertTrue(veryClose(inbounds.getRealDouble(), right.getRealDouble()));
			/*
			{
				System.out.println(" FAILURE at ("+x+","+y+"): expected ("
					+(inbounds.getRealDouble())+") actual ("+right.getRealDouble()+")");
				success = false;
			}
			*/
			currPt[0] = x;
			currPt[1] = y - YSIZE;
			imageFunc.compute(currPt, top);
			assertTrue(veryClose(inbounds.getRealDouble(), top.getRealDouble()));
			/*
			{
				System.out.println(" FAILURE at ("+x+","+y+"): expected ("
					+(inbounds.getRealDouble())+") actual ("+top.getRealDouble()+")");
				success = false;
			}
			*/
			currPt[0] = x;
			currPt[1] = y + YSIZE;
			imageFunc.compute(currPt, bottom);
			assertTrue(veryClose(inbounds.getRealDouble(), bottom.getRealDouble()));
			/*
			{
				System.out.println(" FAILURE at ("+x+","+y+"): expected ("
					+(inbounds.getRealDouble())+") actual ("+bottom.getRealDouble()+")");
				success = false;
			}
			*/
		}
	}
}
