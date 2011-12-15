package net.imglib2.ops.sandbox;

import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealRandomAccessibleRealInterval;
import net.imglib2.type.numeric.RealType;

public class NewAvgFunc<U extends RealType<U>,V extends RealType<V>>
	implements NewFunc<U, V>
{
	private Cursor<U> crs = null;
	private V outType = null;
	
	public NewAvgFunc(V outType) {
		this.outType = outType;
	}

	@Override
	public void evaluate(NewIterableInterval<U> i, V output) {
		if (crs == null) crs = i.cursor();
		crs.reset();
		double sum = 0;
		long numElements = 0;
		while (crs.hasNext()) {
			sum += crs.next().getRealDouble();
			numElements++;
		}
		if (numElements == 0)
			output.setZero();
		else
			output.setReal(sum / numElements);
	}
	
	// MIGHT BE INCONVENIENT - works in RealIntervals (might be useful too)
	public void evaluate(RealRandomAccessibleRealInterval<U> interval, V output) {
		// NB - can't use any RegionOfInterest because it is always of type
		// BitType and no ops of ours will get the right numeric data. Use next
		// lower one in chain
		
		// CAN'T RELOCATE THE INTERVAL
		// CAN'T CREATE A CURSOR

		// CAN DO THIS BUT BACK AT SQUARE ONE
		interval.realRandomAccess();
	}

	// NB - attempt1 - FAILURE
	public void evaluate(Cursor<U> cursor, V output) {
		// need to make a copy of the cursor as we will change its position and
		// parent might hate that. (MAYBE I'M WRONG HERE). But any function that
		// evals another function needs its own cursor
		if (crs == null) {
			crs = cursor.copyCursor();
			//position = new long[cursor.numDimensions()];
		}
		//cursor.localize(position);  // EXPENSIVE as setPosition() call from pure method
		//cursor.relocate(position);  // NOPE - can't do this type of needed op
		crs.reset();
		double sum = 0;
		long numElements = 0;
		while (crs.hasNext()) {
			sum += crs.next().getRealDouble();
			numElements++;
		}
		if (numElements == 0)
			output.setZero();
		else
			output.setReal(sum / numElements);
	}

	@Override
	public V createOutput() {
		return outType.createVariable();
	}
	
	@Override
	public NewAvgFunc<U,V> copy() {
		return new NewAvgFunc<U,V>(outType);
	}
}
