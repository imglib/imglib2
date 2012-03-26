package net.imglib2.ops.sandbox;

import net.imglib2.Cursor;
import net.imglib2.type.numeric.RealType;

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
