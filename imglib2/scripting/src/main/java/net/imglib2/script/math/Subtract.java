package net.imglib2.script.math;

import net.imglib2.IterableRealInterval;
import net.imglib2.script.math.fn.BinaryOperation;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.type.numeric.RealType;

public class Subtract extends BinaryOperation
{
	public <S extends RealType<S>, R extends RealType<R>> Subtract(final IterableRealInterval<S> left, final IterableRealInterval<R> right) {
		super(left, right);
	}

	public <R extends RealType<R>> Subtract(final IFunction fn, final IterableRealInterval<R> right) {
		super(fn, right);
	}

	public <R extends RealType<R>> Subtract(final IterableRealInterval<R> left, final IFunction fn) {
		super(left, fn);
	}

	public Subtract(final IFunction fn1, final IFunction fn2) {
		super(fn1, fn2);
	}
	
	public <R extends RealType<R>> Subtract(final IterableRealInterval<R> left, final Number val) {
		super(left, val);
	}

	public <R extends RealType<R>> Subtract(final Number val,final IterableRealInterval<R> right) {
		super(val, right);
	}

	public Subtract(final IFunction left, final Number val) {
		super(left, val);
	}

	public Subtract(final Number val,final IFunction right) {
		super(val, right);
	}
	
	public Subtract(final Number val1, final Number val2) {
		super(val1, val2);
	}

	public Subtract(final Object... elems) throws Exception {
		super(elems);
	}

	@Override
	public final double eval() {
		return a().eval() - b().eval();
	}
}
