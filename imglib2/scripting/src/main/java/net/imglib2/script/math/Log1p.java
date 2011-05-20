package net.imglib2.script.math;

import net.imglib2.IterableRealInterval;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.script.math.fn.UnaryOperation;
import net.imglib2.type.numeric.RealType;

/** Returns the natural logarithm of the sum of the argument and 1. */
public class Log1p extends UnaryOperation {

	public Log1p(final IterableRealInterval<? extends RealType<?>> img) {
		super(img);
	}
	public Log1p(final IFunction fn) {
		super(fn);
	}
	public Log1p(final Number val) {
		super(val);
	}

	@Override
	public final double eval() {
		return Math.log1p(a().eval());
	}
}
