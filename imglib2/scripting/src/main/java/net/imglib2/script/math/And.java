package net.imglib2.script.math;

import net.imglib2.IterableRealInterval;
import net.imglib2.script.math.fn.BinaryOperation;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.type.numeric.RealType;

/** AND two float values by first casting them to int.
 *  While the casting may look outrageous, that's what ImgJ does
 *  and is thus the expected behavior. In the future, we may be able
 *  to provide a type converter that reads actual integers from integer
 *  Imgs when possible. */
public class And extends BinaryOperation
{
	public And(final IterableRealInterval<? extends RealType<?>> left, final IterableRealInterval<? extends RealType<?>> right) {
		super(left, right);
	}

	public And(final IFunction fn, final IterableRealInterval<? extends RealType<?>> right) {
		super(fn, right);
	}

	public And(final IterableRealInterval<? extends RealType<?>> left, final IFunction fn) {
		super(left, fn);
	}

	public And(final IFunction fn1, final IFunction fn2) {
		super(fn1, fn2);
	}
	
	public And(final IterableRealInterval<? extends RealType<?>> left, final Number val) {
		super(left, val);
	}

	public And(final Number val,final IterableRealInterval<? extends RealType<?>> right) {
		super(val, right);
	}

	public And(final IFunction left, final Number val) {
		super(left, val);
	}

	public And(final Number val,final IFunction right) {
		super(val, right);
	}
	
	public And(final Number val1, final Number val2) {
		super(val1, val2);
	}

	public And(final Object... elems) throws Exception {
		super(elems);
	}

	@Override
	public final double eval() {
		return ((int)a().eval()) & ((int)b().eval());
	}
}
