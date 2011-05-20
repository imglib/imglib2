package net.imglib2.script.math;

import net.imglib2.IterableRealInterval;
import net.imglib2.script.math.fn.BinaryOperation;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.type.numeric.RealType;

public class Divide extends BinaryOperation
{
	public Divide(final IterableRealInterval<? extends RealType<?>> left, final IterableRealInterval<? extends RealType<?>> right) {
		super(left, right);
	}

	public Divide(final IFunction fn, final IterableRealInterval<? extends RealType<?>> right) {
		super(fn, right);
	}

	public Divide(final IterableRealInterval<? extends RealType<?>> left, final IFunction fn) {
		super(left, fn);
	}

	public Divide(final IFunction fn1, final IFunction fn2) {
		super(fn1, fn2);
	}
	
	public Divide(final IterableRealInterval<? extends RealType<?>> left, final Number val) {
		super(left, val);
	}

	public Divide(final Number val,final IterableRealInterval<? extends RealType<?>> right) {
		super(val, right);
	}

	public Divide(final IFunction left, final Number val) {
		super(left, val);
	}

	public Divide(final Number val,final IFunction right) {
		super(val, right);
	}
	
	public Divide(final Number val1, final Number val2) {
		super(val1, val2);
	}

	public Divide(final Object... elems) throws Exception {
		super(elems);
	}

	/** 1 / img */
	public Divide(final IterableRealInterval<? extends RealType<?>> right) {
		super(1, right);
	}

	/** 1 / val */
	public Divide(final Number val) {
		super(1, val);
	}

	/** 1 / fn.eval() */
	public Divide(final IFunction fn) {
		super(1, fn);
	}

	@Override
	public final double eval() {
		return a().eval() / b().eval();
	}
}
