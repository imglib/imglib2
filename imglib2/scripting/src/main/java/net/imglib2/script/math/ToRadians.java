package net.imglib2.script.math;

import net.imglib2.IterableRealInterval;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.script.math.fn.UnaryOperation;
import net.imglib2.type.numeric.RealType;

public class ToRadians extends UnaryOperation {

	public <R extends RealType<R>> ToRadians(final IterableRealInterval<R> img) {
		super(img);
	}
	public ToRadians(final IFunction fn) {
		super(fn);
	}
	public ToRadians(final Number val) {
		super(val);
	}

	@Override
	public final double eval() {
		return Math.toRadians(a().eval());
	}
}
