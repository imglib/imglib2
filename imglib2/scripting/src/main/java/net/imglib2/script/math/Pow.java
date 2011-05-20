package net.imglib2.script.math;

import net.imglib2.IterableRealInterval;
import net.imglib2.script.math.fn.BinaryOperation;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.type.numeric.RealType;

public class Pow extends BinaryOperation
{
	public Pow(final IterableRealInterval<? extends RealType<?>> left, final IterableRealInterval<? extends RealType<?>> right) {
		super(left, right);
	}

	public Pow(final IFunction fn, final IterableRealInterval<? extends RealType<?>> right) {
		super(fn, right);
	}

	public Pow(final IterableRealInterval<? extends RealType<?>> left, final IFunction fn) {
		super(left, fn);
	}

	public Pow(final IFunction fn1, final IFunction fn2) {
		super(fn1, fn2);
	}
	
	public Pow(final IterableRealInterval<? extends RealType<?>> left, final Number val) {
		super(left, val);
	}

	public Pow(final Number val,final IterableRealInterval<? extends RealType<?>> right) {
		super(val, right);
	}

	public Pow(final IFunction left, final Number val) {
		super(left, val);
	}

	public Pow(final Number val,final IFunction right) {
		super(val, right);
	}
	
	public Pow(final Number val1, final Number val2) {
		super(val1, val2);
	}

	@Override
	public final double eval() {
		return Math.pow(a().eval(), b().eval());
	}
}
