package net.imglib2.script.math;

import net.imglib2.IterableRealInterval;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.script.math.fn.UnaryOperation;
import net.imglib2.type.numeric.RealType;

public class Signum extends UnaryOperation {

	public <R extends RealType<R>> Signum(final IterableRealInterval<R> img) {
		super(img);
	}
	public Signum(final IFunction fn) {
		super(fn);
	}
	public Signum(final Number val) {
		super(val);
	}

	@Override
	public final double eval() {
		return Math.signum(a().eval());
	}
}
