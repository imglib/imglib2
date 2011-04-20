package script.imglib.math;

import script.imglib.math.fn.BinaryOperation;
import script.imglib.math.fn.IFunction;
import mpicbg.imglib.img.Img;
import mpicbg.imglib.type.numeric.RealType;

public class Hypot extends BinaryOperation
{
	public Hypot(final Img<? extends RealType<?>> left, final Img<? extends RealType<?>> right) {
		super(left, right);
	}

	public Hypot(final IFunction fn, final Img<? extends RealType<?>> right) {
		super(fn, right);
	}

	public Hypot(final Img<? extends RealType<?>> left, final IFunction fn) {
		super(left, fn);
	}

	public Hypot(final IFunction fn1, final IFunction fn2) {
		super(fn1, fn2);
	}
	
	public Hypot(final Img<? extends RealType<?>> left, final Number val) {
		super(left, val);
	}

	public Hypot(final Number val,final Img<? extends RealType<?>> right) {
		super(val, right);
	}

	public Hypot(final IFunction left, final Number val) {
		super(left, val);
	}

	public Hypot(final Number val,final IFunction right) {
		super(val, right);
	}
	
	public Hypot(final Number val1, final Number val2) {
		super(val1, val2);
	}

	@Override
	public final double eval() {
		return Math.hypot(a().eval(), b().eval());
	}
}
