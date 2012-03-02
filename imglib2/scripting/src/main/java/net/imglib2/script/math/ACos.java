package net.imglib2.script.math;

import net.imglib2.IterableRealInterval;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.script.math.fn.UnaryOperation;
import net.imglib2.type.numeric.RealType;

public class ACos extends UnaryOperation {

	public <R extends RealType<R>> ACos(final IterableRealInterval<R> img) {
		super(img);
	}
	public ACos(final IFunction fn) {
		super(fn);
	}
	public ACos(final Number val) {
		super(val);
	}

	@Override
	public final double eval() {
		return Math.acos(a().eval());
	}
}
