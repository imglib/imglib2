package net.imglib2.script.math;

import net.imglib2.IterableRealInterval;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.script.math.fn.UnaryOperation;
import net.imglib2.type.numeric.RealType;

public class Rint extends UnaryOperation {

	public <R extends RealType<R>> Rint(final IterableRealInterval<R> img) {
		super(img);
	}
	public Rint(final IFunction fn) {
		super(fn);
	}
	public Rint(final Number val) {
		super(val);
	}

	@Override
	public final double eval() {
		return Math.rint(a().eval());
	}
}
