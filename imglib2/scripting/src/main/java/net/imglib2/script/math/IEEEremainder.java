package net.imglib2.script.math;

import net.imglib2.IterableRealInterval;
import net.imglib2.script.math.fn.BinaryOperation;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.type.numeric.RealType;

/** Returns sqrt(x2 +y2) without intermediate overflow or underflow. */
public class IEEEremainder extends BinaryOperation {
	public IEEEremainder(final IterableRealInterval<? extends RealType<?>> left, final IterableRealInterval<? extends RealType<?>> right) {
		super(left, right);
	}

	public IEEEremainder(final IFunction fn, final IterableRealInterval<? extends RealType<?>> right) {
		super(fn, right);
	}

	public IEEEremainder(final IterableRealInterval<? extends RealType<?>> left, final IFunction fn) {
		super(left, fn);
	}

	public IEEEremainder(final IFunction fn1, final IFunction fn2) {
		super(fn1, fn2);
	}
	
	public IEEEremainder(final IterableRealInterval<? extends RealType<?>> left, final Number val) {
		super(left, val);
	}

	public IEEEremainder(final Number val,final IterableRealInterval<? extends RealType<?>> right) {
		super(val, right);
	}

	public IEEEremainder(final IFunction left, final Number val) {
		super(left, val);
	}

	public IEEEremainder(final Number val,final IFunction right) {
		super(val, right);
	}
	
	public IEEEremainder(final Number val1, final Number val2) {
		super(val1, val2);
	}

	@Override
	public final double eval() {
		return Math.IEEEremainder(a().eval(), b().eval());
	}
}
