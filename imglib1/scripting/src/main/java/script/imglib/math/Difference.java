package script.imglib.math;

import script.imglib.math.fn.BinaryOperation;
import script.imglib.math.fn.IFunction;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.RealType;

public class Difference extends BinaryOperation
{
	public Difference(final Image<? extends RealType<?>> left, final Image<? extends RealType<?>> right) {
		super(left, right);
	}

	public Difference(final IFunction fn, final Image<? extends RealType<?>> right) {
		super(fn, right);
	}

	public Difference(final Image<? extends RealType<?>> left, final IFunction fn) {
		super(left, fn);
	}

	public Difference(final IFunction fn1, final IFunction fn2) {
		super(fn1, fn2);
	}
	
	public Difference(final Image<? extends RealType<?>> left, final Number val) {
		super(left, val);
	}

	public Difference(final Number val,final Image<? extends RealType<?>> right) {
		super(val, right);
	}

	public Difference(final IFunction left, final Number val) {
		super(left, val);
	}

	public Difference(final Number val,final IFunction right) {
		super(val, right);
	}
	
	public Difference(final Number val1, final Number val2) {
		super(val1, val2);
	}

	public Difference(final Object... elems) throws Exception {
		super(elems);
	}

	@Override
	public final double eval() {
		// Difference avoiding overflow
		final double a = a().eval(),
					 b = b().eval();
		return Math.max(a, b) - Math.min(a, b);
	}
}
