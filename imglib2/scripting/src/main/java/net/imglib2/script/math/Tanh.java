package net.imglib2.script.math;

import net.imglib2.IterableRealInterval;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.script.math.fn.UnaryOperation;
import net.imglib2.type.numeric.RealType;

public class Tanh extends UnaryOperation {

	public Tanh(final IterableRealInterval<? extends RealType<?>> img) {
		super(img);
	}
	public Tanh(final IFunction fn) {
		super(fn);
	}
	public Tanh(final Number val) {
		super(val);
	}

	@Override
	public final double eval() {
		return Math.tanh(a().eval());
	}
}
