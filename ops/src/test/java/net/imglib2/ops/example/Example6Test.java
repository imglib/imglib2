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
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

import org.junit.Test;

// an interpolation example:
//   uses continuous neighborhoods
//   does simple 2d bilinear interpolation (class is not generalized)

// TODO
//   generalize
//   - number of dimensions
//   - interpolation functions

/**
 * 
 * @author Barry DeZonia
 */
public class Example6Test {

	private final int XSIZE = 250;
	private final int YSIZE = 400;
	
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

	private boolean veryClose(double d1, double d2) {
		return Math.abs(d1-d2) < 0.00001;
	}

	public double interpolate(double ix, double iy, double ul, double ur,
		double ll, double lr)
	{
		double value = 0;
		value += (1 - ix) * (1 - iy) * ul;
		value += (1 - ix) * (iy) * ll;
		value += (ix) * (1 - iy) * ur;
		value += (ix) * (iy) * lr;
		return value;
	}

	private double expectedValue(int x, int y, double ix, double iy) {
		double ul = (x+0) + 2*(y+0);
		double ur = (x+1) + 2*(y+0);
		double ll = (x+0) + 2*(y+1);
		double lr = (x+1) + 2*(y+1);
		return interpolate(ix,iy,ul,ur,ll,lr);
	}
	
	private class RealBilinearInterpolatorFunction<T extends RealType<T>> 
		implements Function<double[], T>
	{
		private Function<long[],T> discreteFunc;
		private long[] index;
		private T ul, ur, ll, lr;
		
		public RealBilinearInterpolatorFunction(Function<long[],T> discreteFunc) {
			this.discreteFunc = discreteFunc;
			this.index = new long[2];
			this.ul = createOutput();
			this.ur = createOutput();
			this.ll = createOutput();
			this.lr = createOutput();
		}

		@Override
		public void compute(double[] point, T output) {
			long x = (long) Math.floor(point[0]);
			long y = (long) Math.floor(point[1]);
			double ix = point[0] - x;
			double iy = point[1] - y;
			getValue((x + 0), (y + 0), ul);
			getValue((x + 1), (y + 0), ur);
			getValue((x + 0), (y + 1), ll);
			getValue((x + 1), (y + 1), lr);
			double value = interpolate(ix, iy, ul.getRealDouble(), 
					ur.getRealDouble(), ll.getRealDouble(), lr.getRealDouble());
			output.setReal(value);
		}
		
		@Override
		public RealBilinearInterpolatorFunction<T> copy() {
			throw new UnsupportedOperationException("not needed yet");
		}
		
		private void getValue(long x, long y, T output) {
			index[0] = x;
			index[1] = y;
			discreteFunc.compute(index, output);
		}
		
		@Override
		public T createOutput() {
			return discreteFunc.createOutput();
		}
	}

	private void doTestCase(double ix, double iy) {
		Img<DoubleType> inputImg = makeInputImage();
		Function<long[], DoubleType> input =
			new RealImageFunction<DoubleType, DoubleType>(inputImg, new DoubleType());
		Function<double[], DoubleType> interpolator =
			new RealBilinearInterpolatorFunction<DoubleType>(input);
		DoubleType variable = new DoubleType();
		double[] point = new double[2];
		for (int x = 0; x < XSIZE-2; x++) {
			for (int y = 0; y < YSIZE-2; y++) {
				point[0] = x + ix;
				point[1] = y + iy;
				interpolator.compute(point, variable);
				assertTrue(veryClose(variable.getRealDouble(), expectedValue(x, y, ix, iy)));
				/*
				{
					System.out.println(" FAILURE at ("+(x+ix)+","+(y+iy)+"): expected ("
						+expectedValue(x,y,ix,iy)+") actual ("+variable.getRealDouble()+")");
					success = false;
				}
				*/
			}
		}
	}
	
	@Test
	public void testInterpolation() {
		for (double ix = 0; ix <= 1; ix += 0.1) {
			for (double iy = 0; iy <= 1; iy += 0.1) {
				doTestCase(ix,iy);
			}
		}
	}
}
