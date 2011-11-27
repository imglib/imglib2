package net.imglib2.script.math;

import net.imglib2.IterableRealInterval;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.script.math.fn.UnaryOperation;
import net.imglib2.type.numeric.RealType;

public class ASin extends UnaryOperation {

	public <R extends RealType<R>> ASin(final IterableRealInterval<R> img) {
		super(img);
	}
	public ASin(final IFunction fn) {
		super(fn);
	}
	public ASin(final Number val) {
		super(val);
	}

	@Override
	public final double eval() {
		return Math.asin(a().eval());
	}
}
