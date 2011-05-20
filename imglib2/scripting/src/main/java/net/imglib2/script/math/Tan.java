package net.imglib2.script.math;

import net.imglib2.IterableRealInterval;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.script.math.fn.UnaryOperation;
import net.imglib2.type.numeric.RealType;

public class Tan extends UnaryOperation {

	public Tan(final IterableRealInterval<? extends RealType<?>> img) {
		super(img);
	}
	public Tan(final IFunction fn) {
		super(fn);
	}
	public Tan(final Number val) {
		super(val);
	}

	@Override
	public final double eval() {
		return Math.tan(a().eval());
	}
}
