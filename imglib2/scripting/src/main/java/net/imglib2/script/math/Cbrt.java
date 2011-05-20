package net.imglib2.script.math;

import net.imglib2.IterableRealInterval;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.script.math.fn.UnaryOperation;
import net.imglib2.type.numeric.RealType;

public class Cbrt extends UnaryOperation {

	public Cbrt(final IterableRealInterval<? extends RealType<?>> img) {
		super(img);
	}
	public Cbrt(final IFunction fn) {
		super(fn);
	}
	public Cbrt(final Number val) {
		super(val);
	}

	@Override
	public final double eval() {
		return Math.cbrt(a().eval());
	}
}
