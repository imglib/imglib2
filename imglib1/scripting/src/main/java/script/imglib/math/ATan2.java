package script.imglib.math;

import script.imglib.math.fn.BinaryOperation;
import script.imglib.math.fn.IFunction;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.RealType;

public class ATan2 extends BinaryOperation
{
	public ATan2(final Image<? extends RealType<?>> left, final Image<? extends RealType<?>> right) {
		super(left, right);
	}

	public ATan2(final IFunction fn, final Image<? extends RealType<?>> right) {
		super(fn, right);
	}

	public ATan2(final Image<? extends RealType<?>> left, final IFunction fn) {
		super(left, fn);
	}

	public ATan2(final IFunction fn1, final IFunction fn2) {
		super(fn1, fn2);
	}
	
	public ATan2(final Image<? extends RealType<?>> left, final Number val) {
		super(left, val);
	}

	public ATan2(final Number val,final Image<? extends RealType<?>> right) {
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
