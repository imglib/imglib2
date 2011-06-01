package net.imglib2.script.math;

import net.imglib2.IterableRealInterval;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.script.math.fn.UnaryOperation;
import net.imglib2.type.numeric.RealType;

public class Ceil extends UnaryOperation {

	public Ceil(final IterableRealInterval<? extends RealType<?>> img) {
		super(img);
	}
	public Ceil(final IFunction fn) {
		super(fn);
	}
	public Ceil(final Number val) {
		super(val);
	}

	@Override
	public final double eval() {
		return Math.ceil(a().eval());
	}
}
