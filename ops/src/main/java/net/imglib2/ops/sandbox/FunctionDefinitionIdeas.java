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

package net.imglib2.ops.sandbox;

import net.imglib2.AbstractInterval;
import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.ComplexType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.complex.ComplexDoubleType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.DoubleType;

/**
 * This is a mock up class for testing the idea of functions working on
 * intervals rather than working with other functions.
 * 
 *
 * @author Barry DeZonia
 */
public class FunctionDefinitionIdeas {

	/*
	 * Need to define a relocating interval. Then want to pass it to Average
	 * function at constructor time or before 1st call to evaluate(). This allows
	 * us to reuse cursors etc.
	 */

	/*
	private interface RelocatableCursor<K> extends Cursor<K> {
		void setOrigin(long[] newOrigin);
	}
	
	private interface RelocatingIterableInterval<K> {
		Cursor<K> cursor();
		void relocate(long[] newOrigin);
	}
	*/

	// NOTE - see how output can be any type passed in
	private interface ComplexFunction<T> {
		void evaluate(long[] coordinate, SubInterval<T> interval, ComplexType<?> output);
		T createVariable();
	}
	

	private class SubInterval<K> extends AbstractInterval implements RandomAccessibleInterval<K> {

		// TODO - if input interval is a SubInterval have them register with each
		// other such that the parent orient() call can change children.
		
		// include the whole parent interval
		SubInterval(RandomAccessibleInterval<K> interval) {
			super(interval);
			long[] dims = new long[interval.numDimensions()];
			long[] negOffs = new long[interval.numDimensions()];
			long[] posOffs = new long[interval.numDimensions()];
			for (int i = 0; i < dims.length; i++)
				posOffs[i] = dims[i] - 1;
			init(interval,negOffs, posOffs);
		}
		
		// include a subset of the parent interval
		SubInterval(RandomAccessibleInterval<K> interval, long[] negOffs, long[] posOffs) {
			super(interval);
			init(interval,negOffs, posOffs);
		}
		
		void orient(long[] newKeyPt) {
			// updates all exist cursors on this Subinterval to reference new
			// values.
		}
		
		private void init(RandomAccessibleInterval<K> interval, long[] negOffs, long[] posOffs) {
		}

		@Override
		public RandomAccess<K> randomAccess() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public RandomAccess<K> randomAccess(Interval interval) {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	// fake func that takes a 3 plane 3d dataset and sums the averages of each plane
	//   NOTE how having funcs using funcs cannot be very hierarchical with real data
	//   intervals. the nicety of passing functions of functions is lost I think.
	
	private class SumOf3Averages<T extends Type<T>> implements ComplexFunction<T> {

		private T type;
		private ComplexDoubleType temp;
		
		private SubInterval<T> interval1;
		private SubInterval<T> interval2;
		private SubInterval<T> interval3;
		
		private AverageFunction<T> aveFunc;
		
		public SumOf3Averages(T type) {
			this.type = type.createVariable();
			this.temp = new ComplexDoubleType();
		}

		@Override
		public T createVariable() {
			return type.createVariable();
		}

		@Override
		public void evaluate(long[] coordinate,
			SubInterval<T> interval, ComplexType<?> output)
		{
			if (aveFunc == null) {
				aveFunc = new AverageFunction<T>(type);
				long[] planeSize = new long[3];
				planeSize[0] = interval.dimension(0);
				planeSize[1] = interval.dimension(1);
				planeSize[2] = 1;
				interval1 = new SubInterval<T>(interval, new long[3], planeSize);
				interval2 = new SubInterval<T>(interval, new long[3], planeSize);
				interval3 = new SubInterval<T>(interval, new long[3], planeSize);
				long[] newKeyPt = new long[3];
				newKeyPt[2] = 0;
				interval1.orient(newKeyPt);
				newKeyPt[2] = 1;
				interval2.orient(newKeyPt);
				newKeyPt[2] = 2;
				interval3.orient(newKeyPt);
			}
			
			double sumR = 0;
			double sumI = 0;
			
			// NOTE - does coordinate need to move relative to interval i ???
			
			aveFunc.evaluate(coordinate, interval1, temp);
			sumR += temp.getRealDouble();
			sumI += temp.getRealDouble();
			
			aveFunc.evaluate(coordinate, interval2, temp);
			sumR += temp.getRealDouble();
			sumI += temp.getRealDouble();
			
			aveFunc.evaluate(coordinate, interval3, temp);
			sumR += temp.getRealDouble();
			sumI += temp.getRealDouble();

			output.setReal(sumR);
			output.setImaginary(sumI);
		}
		
	}

	// Note how Avg func is dummified in that it no longer averages another function
	// but rather has an interval of actual data values. Less powerful. Cannot
	// create mathematical data.
	
	private class AverageFunction<T extends Type<T>> implements ComplexFunction<T> {

		private T type;
		
		public AverageFunction(T type) {
			this.type = type.createVariable();
		}
		
		@Override
		public T createVariable() {
			return type.createVariable();
		}

		// Note how internal data type can be one thing while output type can be
		// another. This flexibility is nice.
		
		@Override
		public void evaluate(long[] coordinate,
			SubInterval<T> interval, ComplexType<?> output)
		{
			double sum = 0;
			long numElem = 0;
			Cursor<RealType<?>> cursor = null;
			// DOES NOT COMPILE HERE
			//cursor = interval.cursor();  // probably need RegionIndexIterator idea instead
			while (cursor.hasNext()) {
				sum += cursor.get().getRealDouble();
				numElem++;
			}
			if (numElem == 0)
				output.setReal(0);
			else
				output.setReal(sum/numElem);
		}
		
	}
	
	public void skeleton() {
		
		// NOTE: this code would be in something like ImageAssignment
		
		ComplexFunction<UnsignedByteType> func = null;
		
		DoubleType output = new DoubleType(); // NOTE - output different than internal type 

		Img<UnsignedByteType> image = null;
		
		RandomAccessibleInterval<UnsignedByteType> interval = image;
		
		long MAX_X = 200;
		long MAX_Y = 200;
		long[] keyPt = new long[2];

		// get a summary stat on a whole image
		SubInterval<UnsignedByteType> subInterval = new SubInterval<UnsignedByteType>(image);
		keyPt[0] = 0;
		keyPt[1] = 0;
		subInterval.orient(keyPt);
		func.evaluate(keyPt, subInterval, output);

		// get a summary stat on a region centered on an image
		subInterval = new SubInterval<UnsignedByteType>(image,new long[]{1,1},new long[]{1,1}); // 3x3 neigh
		keyPt[0] = MAX_X / 2; // midPt
		keyPt[1] = MAX_Y / 2; // midPt
		subInterval.orient(keyPt);
		func.evaluate(keyPt, subInterval, output);

		// now slide along an image and repeatedly eval func at new center
		//   NOTE: should already work with out of bounds enabled intervals
		subInterval = new SubInterval<UnsignedByteType>(interval,new long[]{2,2}, new long[]{2,2}); // 5x5 neigh

		for (long x = 0; x < MAX_X; x++) {
			for (long y = 0; y < MAX_Y; y++) {
				keyPt[0] = x;
				keyPt[1] = y;
				subInterval.orient(keyPt);
				func.evaluate(keyPt, subInterval, output);
			}
		}
		
	}
}
