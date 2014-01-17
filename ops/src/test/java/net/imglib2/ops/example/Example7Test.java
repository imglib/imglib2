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
import net.imglib2.ops.condition.AndCondition;
import net.imglib2.ops.condition.Condition;
import net.imglib2.ops.function.Function;
import net.imglib2.ops.function.general.ConditionalFunction;
import net.imglib2.ops.img.ImageAssignment;
import net.imglib2.ops.input.InputIteratorFactory;
import net.imglib2.ops.input.PointInputIteratorFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

import org.junit.Test;

// a complicated conditional pixel assignment example
//   RealImageAssignment tagged with two conditions
//     (x,y) in a circle and (x+y) > constant
//   assign with ConditionalFunction
//     condition : x < constant
//     if true: return x^2
//     if false: return 3*y + 19

/**
 * 
 * @author Barry DeZonia
 */
public class Example7Test {

	private final long XSIZE = 1201;
	private final long YSIZE = 1201;
	private final long CIRCLE_RADIUS = 200;
	private final long LINE_CONSTANT = 650;
	private final long X_CONSTANT = 400;
	
	private Img<DoubleType> allocateImage() {
		final ArrayImgFactory<DoubleType> imgFactory =
			new ArrayImgFactory<DoubleType>();
		return imgFactory.create(new long[]{XSIZE,YSIZE}, new DoubleType());
	}

	public class XValueCondition implements Condition<long[]> {

		@Override
		public boolean isTrue(long[] point) {
			return point[0] < X_CONSTANT;
		}
		
		@Override
		public XValueCondition copy() {
			return new XValueCondition();
		}
	}
	
	public class XSquaredFunction implements Function<long[],DoubleType> {

		@Override
		public void compute(long[] point, DoubleType output) {
			output.setReal(point[0]*point[0]);
		}
		
		@Override
		public XSquaredFunction copy() {
			return new XSquaredFunction();
		}

		@Override
		public DoubleType createOutput() {
			return new DoubleType();
		}
		
	}

	public class YLineFunction implements Function<long[],DoubleType> {

		@Override
		public void compute(long[] point, DoubleType output) {
			output.setReal(3*point[1]+19);
		}
		
		@Override
		public YLineFunction copy() {
			return new YLineFunction();
		}

		@Override
		public DoubleType createOutput() {
			return new DoubleType();
		}
		
	}
	
	public class CircularCondition implements Condition<long[]> {

		long ctrX = XSIZE / 2;
		long ctrY = YSIZE / 2;
		
		public CircularCondition() {
			// nothing to do
		}
		
		@Override
		public boolean isTrue(long[] point) {
			long dx = point[0] - ctrX;
			long dy = point[1] - ctrY;
			double dist = Math.sqrt(dx*dx + dy*dy);
			return dist <= CIRCLE_RADIUS;
		}
		
		@Override
		public CircularCondition copy() {
			return new CircularCondition();
		}
	}
	
	public class XYSumCondition implements Condition<long[]> {

		public XYSumCondition() {
			// nothing to do
		}
		
		@Override
		public boolean isTrue(long[] point) {
			return (point[0] + point[1]) > LINE_CONSTANT;
		}
		
		@Override
		public XYSumCondition copy() {
			return new XYSumCondition();
		}
	}

	private boolean veryClose(double d1, double d2) {
		return Math.abs(d1-d2) < 0.00001;
	}

	private double expectedValue(int x, int y) {
		double ctrX = XSIZE/2;
		double ctrY = YSIZE/2;
		double dx = x - ctrX;
		double dy = y - ctrY;
		double distFromCtr = Math.sqrt(dx*dx + dy*dy);
		if (distFromCtr > CIRCLE_RADIUS) return 0;
		if ((x+y) <= LINE_CONSTANT) return 0;
		if (x < X_CONSTANT)
			return x*x;
		return 3*y + 19;
	}

	private void testValues(Img<? extends RealType<?>> image) {
		RandomAccess<? extends RealType<?>> accessor = image.randomAccess();
		long[] pos = new long[2];
		for (int x = 0; x < XSIZE; x++) {
			for (int y = 0; y < YSIZE; y++) {
				pos[0] = x;
				pos[1] = y;
				accessor.setPosition(pos);
				double value = accessor.get().getRealDouble();
				assertTrue(veryClose(value, expectedValue(x, y)));
				/*
				{
					System.out.println(" FAILURE at ("+x+","+y+"): expected ("
						+expectedValue(x,y)+") actual ("+value+")");
				}
				*/
			}
		}
	}
	
	@Test
	public void testComplicatedAssignment() {
		Condition<long[]> xValCond = new XValueCondition();
		Function<long[],DoubleType> xSquaredFunc = new XSquaredFunction();
		Function<long[],DoubleType> yLineFunc = new YLineFunction();
		Function<long[],DoubleType> function =
			new ConditionalFunction<long[],DoubleType>(xValCond, xSquaredFunc, yLineFunc);
		Img<DoubleType> image = allocateImage();
		Condition<long[]> circleCond = new CircularCondition();
		Condition<long[]> sumCond = new XYSumCondition();
		Condition<long[]> compositeCondition = new AndCondition<long[]>(circleCond,sumCond);
		InputIteratorFactory<long[]> factory = new PointInputIteratorFactory();
		ImageAssignment<DoubleType,DoubleType,long[]> assigner =
				new ImageAssignment<DoubleType,DoubleType,long[]>(
						image, new long[2], new long[]{XSIZE,YSIZE}, function,
						compositeCondition, factory);
		assigner.assign();
		testValues(image);
	}
}
