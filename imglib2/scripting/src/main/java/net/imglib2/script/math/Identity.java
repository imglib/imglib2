package net.imglib2.script.math;

import net.imglib2.IterableRealInterval;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.script.math.fn.UnaryOperation;
import net.imglib2.type.numeric.RealType;

/** Does nothing other than returning {@link #a()} when evaluated. */
public final class Identity extends UnaryOperation
{
	public Identity(final IterableRealInterval<? extends RealType<?>> img) {
		super(img);
	}
	public Identity(final IFunction fn) {
		super(fn);
	}
	public Identity(final Number val) {
		super(val);
	}

	@Override
	public final double eval() {
		return a().eval();
	}

	@Override
	public final IFunction duplicate() throws Exception {
		return new Identity(a());
	}
}