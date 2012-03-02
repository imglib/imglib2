package net.imglib2.script.math;

import net.imglib2.IterableRealInterval;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.script.math.fn.UnaryOperation;
import net.imglib2.type.numeric.RealType;

public class Floor extends UnaryOperation {

	public <R extends RealType<R>> Floor(final IterableRealInterval<R> img) {
		super(img);
	}
	public Floor(final IFunction fn) {
		super(fn);
	}
	public Floor(final Number val) {
		super(val);
	}

	@Override
	public final double eval() {
		return Math.floor(a().eval());
	}
}
