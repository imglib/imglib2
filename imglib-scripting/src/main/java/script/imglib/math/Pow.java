package script.imglib.math;

import script.imglib.math.fn.BinaryOperation;
import script.imglib.math.fn.IFunction;
import mpicbg.imglib.container.Img;
import mpicbg.imglib.type.numeric.RealType;

public class Pow extends BinaryOperation
{
	public Pow(final Img<? extends RealType<?>> left, final Img<? extends RealType<?>> right) {
		super(left, right);
	}

	public Pow(final IFunction fn, final Img<? extends RealType<?>> right) {
		super(fn, right);
	}

	public Pow(final Img<? extends RealType<?>> left, final IFunction fn) {
		super(left, fn);
	}

	public Pow(final IFunction fn1, final IFunction fn2) {
		super(fn1, fn2);
	}
	
	public Pow(final Img<? extends RealType<?>> left, final Number val) {
		super(left, val);
	}

	public Pow(final Number val,final Img<? extends RealType<?>> right) {
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