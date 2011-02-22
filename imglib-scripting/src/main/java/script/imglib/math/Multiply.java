package script.imglib.math;

import script.imglib.math.fn.BinaryOperation;
import script.imglib.math.fn.IFunction;
import mpicbg.imglib.container.Img;
import mpicbg.imglib.type.numeric.RealType;

public class Multiply extends BinaryOperation
{
	public Multiply(final Img<? extends RealType<?>> left, final Img<? extends RealType<?>> right) {
		super(left, right);
	}

	public Multiply(final IFunction fn, final Img<? extends RealType<?>> right) {
		super(fn, right);
	}

	public Multiply(final Img<? extends RealType<?>> left, final IFunction fn) {
		super(left, fn);
	}

	public Multiply(final IFunction fn1, final IFunction fn2) {
		super(fn1, fn2);
	}
	
	public Multiply(final Img<? extends RealType<?>> left, final Number val) {
		super(left, val);
	}

	public Multiply(final Number val,final Img<? extends RealType<?>> right) {
		super(val, right);
	}

	public Multiply(final IFunction left, final Number val) {
		super(left, val);
	}

	public Multiply(final Number val,final IFunction right) {
		super(val, right);
	}
	
	public Multiply(final Number val1, final Number val2) {
		super(val1, val2);
	}

	public Multiply(final Object... elems) throws Exception {
		super(elems);
	}

	@Override
	public final double eval() {
		return a().eval() * b().eval();
	}
}