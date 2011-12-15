package net.imglib2.ops.sandbox;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.type.numeric.RealType;

public class RealAverageOperation<U extends RealType<U>,V extends RealType<V>>
{
	public void evaluate(IterableInterval<U> region, V output) {
		double sum = 0;
		long numElements = 0;
		Cursor<U> cursor = region.cursor();
		while (cursor.hasNext()) {
			sum += cursor.next().getRealDouble();
			numElements++;
		}
		if (numElements == 0)
			output.setReal(0);
		else
			output.setReal(sum/numElements);
	}

	public V createOutput(U dataHint) {
	}

	public RealAverageOperation<U,V> copy() {
		return new RealAverageOperation<U,V>();
	}

}
