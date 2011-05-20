package net.imglib2.script.math;

import net.imglib2.IterableRealInterval;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.script.math.fn.UnaryOperation;
import net.imglib2.type.numeric.RealType;

public class Cosh extends UnaryOperation {

	public Cosh(final IterableRealInterval<? extends RealType<?>> img) {
		super(img);
	}
	public Cosh(final IFunction fn) {
		super(fn);
	}
	public Cosh(final Number val) {
		super(val);
	}

	@Override
	public final double eval() {
		return Math.cosh(a().eval());
	}
}
