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

package net.imglib2.ops.sandbox;

import net.imglib2.Cursor;
import net.imglib2.type.numeric.RealType;

/**
 * TODO
 *
 */
public class TempFunctionDefinition {
	
	private interface Func<U,V> {
		void evaluate(Cursor<U> cursor, V output);
		V createOutput();
	}

	// key ideas
	//   avoid hatching cursors over and over inside evaluate
	//   relocate intervals or cursors from outside to minimize overhead
	//   realImgFunc goes away - its an average of a single pixel
	//   a 3x3 median of a cursor would work the same
	private class AvgFunc<L extends RealType<L>, M extends RealType<M>> implements Func<L,M> {

		@Override
		public void evaluate(Cursor<L> cursor, M output) {
			L sum = cursor.get().createVariable();
			sum.setZero();
			long numElements = 0;
			while (cursor.hasNext()) {
				sum.add(cursor.next());
				numElements++;
			}
			if (numElements == 0)
				output.setReal(0);
			else
				output.setReal(sum.getRealDouble() / numElements);
		}

		@Override
		public M createOutput() {
			return null;
		}
		
	}
	
	public static void main(String[] args) {

		/*
        RectangleRegionOfInterest roi = new RectangleRegionOfInterest(
                      new double[] { 0, 1 }, new double[] { 5, 5 });
        double[] pos = new double[2];
        IterableInterval<BitType> ii = roi
                      .getIterableIntervalOverROI(new ConstantRandomAccessible<BitType>(
                                   new BitType(), 2));
        Cursor<BitType> iiC = ii.cursor();
        while (iiC.hasNext()) {
               iiC.fwd();
        }
        iiC.reset();
        roi.setOrigin(new double[] { 7, 123242 });
        while (iiC.hasNext()) {
               iiC.fwd();
               iiC.localize(pos);
               System.out.println(Arrays.toString(pos));
        }
        roi.setOrigin(new double[] { 0, 0 });
        iiC.reset();
        while (iiC.hasNext()) {
               iiC.fwd();
               iiC.localize(pos);
               System.out.println(Arrays.toString(pos));
        }
        */
	}
}
