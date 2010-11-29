package script.imglib.math;

import script.imglib.math.fn.BinaryOperation;
import script.imglib.math.fn.IFunction;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.RealType;

public class Subtract extends BinaryOperation
{
	public Subtract(final Image<? extends RealType<?>> left, final Image<? extends RealType<?>> right) {
		super(left, right);
	}

	public Subtract(final IFunction fn, final Image<? extends RealType<?>> right) {
		super(fn, right);
	}

	public Subtract(final Image<? extends RealType<?>> left, final IFunction fn) {
		super(left, fn);
	}

	public Subtract(final IFunction fn1, final IFunction fn2) {
		super(fn1, fn2);
	}
	
	public Subtract(final Image<? extends RealType<?>> left, final Number val) {
		super(left, val);
	}

	public Subtract(final Number val,final Image<? extends RealType<?>> right) {
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