package net.imglib2.ops.sandbox;

import net.imglib2.IterableInterval;
import net.imglib2.ops.Function;
import net.imglib2.ops.Neighborhood;
import net.imglib2.ops.UnaryOperation;
import net.imglib2.type.numeric.RealType;

public class RealBogusFunction<IN,OUT extends RealType<OUT>> implements Function<IN,OUT> {

	UnaryOperation<IN,OUT> op;
	
	public RealBogusFunction(UnaryOperation<IN,OUT> op) {
		this.op = op;
	}

	public void evaluate(IterableInterval<IN> interval, OUT output) {
		// note 1: outside how do I move interval and reuse over and over?
		//interval.translate(); // how???;
		// now let's pretend we are an image function that has no input point
		//   how to get value of first element?
		//interval.firstElement();  // this hatches a cursor maybe?? ouch
		//   or do we need middle element? what about even neighborhoods?
		//   and avoiding recalculation?
		//interval.???;
		op.compute(interval.firstElement(), output);
		// interval.
		// impulse function??
	}

	@Override
	public OUT createOutput() {
		return null;
	}

	@Override
	public Function<IN, OUT> copy() {
		return new RealBogusFunction<IN,OUT>(op);
	}

	@Override
	public void evaluate(Neighborhood<IN> neigh, IN point, OUT output) {
		// TODO Auto-generated method stub
		
	}

}
