package net.imglib2.script.math;

import net.imglib2.IterableRealInterval;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.script.math.fn.UnaryOperation;
import net.imglib2.type.numeric.RealType;

public class Sinh extends UnaryOperation {

	public <R extends RealType<R>> Sinh(final IterableRealInterval<R> img) {
		super(img);
	}
	public Sinh(final IFunction fn) {
		super(fn);
	}
	public Sinh(final Number val) {
		super(val);
	}

	@Override
	public final double eval() {
		return Math.sinh(a().eval());
	}
}
