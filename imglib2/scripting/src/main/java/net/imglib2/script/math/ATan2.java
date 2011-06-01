package net.imglib2.script.math;

import net.imglib2.IterableRealInterval;
import net.imglib2.script.math.fn.BinaryOperation;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.type.numeric.RealType;

public class ATan2 extends BinaryOperation
{
	public ATan2(final IterableRealInterval<? extends RealType<?>> left, final IterableRealInterval<? extends RealType<?>> right) {
		super(left, right);
	}

	public ATan2(final IFunction fn, final IterableRealInterval<? extends RealType<?>> right) {
		super(fn, right);
	}

	public ATan2(final IterableRealInterval<? extends RealType<?>> left, final IFunction fn) {
		super(left, fn);
	}

	public ATan2(final IFunction fn1, final IFunction fn2) {
		super(fn1, fn2);
	}
	
	public ATan2(final IterableRealInterval<? extends RealType<?>> left, final Number val) {
		super(left, val);
	}

	public ATan2(final Number val,final IterableRealInterval<? extends RealType<?>> right) {
		super(val, right);
	}

	public ATan2(final IFunction left, final Number val) {
		super(left, val);
	}

	public ATan2(final Number val,final IFunction right) {
		super(val, right);
	}
	
	public ATan2(final Number val1, final Number val2) {
		super(val1, val2);
	}

	@Override
	public final double eval() {
		return Math.atan2(a().eval(), b().eval());
	}
}
