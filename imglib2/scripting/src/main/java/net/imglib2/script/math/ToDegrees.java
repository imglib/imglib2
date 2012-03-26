package net.imglib2.script.math;

import net.imglib2.IterableRealInterval;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.script.math.fn.UnaryOperation;
import net.imglib2.type.numeric.RealType;

public class ToDegrees extends UnaryOperation {

	public <R extends RealType<R>> ToDegrees(final IterableRealInterval<R> img) {
		super(img);
	}
	public ToDegrees(final IFunction fn) {
		super(fn);
	}
	public ToDegrees(final Number val) {
		super(val);
	}

	@Override
	public final double eval() {
		return Math.toDegrees(a().eval());
	}
}
