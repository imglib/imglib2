package net.imglib2.script.math;

import net.imglib2.IterableRealInterval;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.script.math.fn.UnaryOperation;
import net.imglib2.type.numeric.RealType;

public class Abs extends UnaryOperation {

	public <R extends RealType<R>> Abs(final IterableRealInterval<R> img) {
		super(img);
	}
	public Abs(final IFunction fn) {
		super(fn);
	}
	public Abs(final Number val) {
		super(val);
	}

	@Override
	public final double eval() {
		return Math.abs(a().eval());
	}
}
