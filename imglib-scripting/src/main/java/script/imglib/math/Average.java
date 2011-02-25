package script.imglib.math;

import script.imglib.math.fn.BinaryOperation;
import script.imglib.math.fn.IFunction;
import mpicbg.imglib.img.Img;
import mpicbg.imglib.type.numeric.RealType;

public class Average extends BinaryOperation
{
	public Average(final Img<? extends RealType<?>> left, final Img<? extends RealType<?>> right) {
		super(left, right);
	}

	public Average(final IFunction fn, final Img<? extends RealType<?>> right) {
		super(fn, right);
	}

	public Average(final Img<? extends RealType<?>> left, final IFunction fn) {
		super(left, fn);
	}

	public Average(final IFunction fn1, final IFunction fn2) {
		super(fn1, fn2);
	}
	
	public Average(final Img<? extends RealType<?>> left, final Number val) {
		super(left, val);
	}

	public Average(final Number val,final Img<? extends RealType<?>> right) {
		super(val, right);
	}

	public Average(final IFunction left, final Number val) {
		super(left, val);
	}

	public Average(final Number val,final IFunction right) {
		super(val, right);
	}
	
	public Average(final Number val1, final Number val2) {
		super(val1, val2);
	}

	public Average(final Object... elems) throws Exception {
		super(elems);
	}

	@Override
	public final double eval() {
		// Average avoiding overflow
		return a().eval() * 0.5 + b().eval() * 0.5;
	}
}
