package net.imglib2.script.math;

import net.imglib2.IterableRealInterval;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.script.math.fn.UnaryOperation;
import net.imglib2.type.numeric.RealType;

public class Exp extends UnaryOperation {

	public <R extends RealType<R>> Exp(final IterableRealInterval<R> img) {
		super(img);
	}
	public Exp(final IFunction fn) {
		super(fn);
	}
	public Exp(final Number val) {
		super(val);
	}

	@Override
	public final double eval() {
		return Math.exp(a().eval());
	}
}
