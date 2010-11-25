package mpicbg.imglib.scripting.math2;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.math2.fn.BinaryOperation;
import mpicbg.imglib.scripting.math2.fn.IFunction;
import mpicbg.imglib.type.numeric.RealType;

public class Min extends BinaryOperation
{
	public Min(final Image<? extends RealType<?>> left, final Image<? extends RealType<?>> right) {
		super(left, right);
	}

	public Min(final IFunction fn, final Image<? extends RealType<?>> right) {
		super(fn, right);
	}

	public Min(final Image<? extends RealType<?>> left, final IFunction fn) {
		super(left, fn);
	}

	public Min(final IFunction fn1, final IFunction fn2) {
		super(fn1, fn2);
	}
	
	public Min(final Image<? extends RealType<?>> left, final Number val) {
		super(left, val);
	}

	public Min(final Number val,final Image<? extends RealType<?>> right) {
		super(val, right);
	}

	public Min(final IFunction left, final Number val) {
		super(left, val);
	}

	public Min(final Number val,final IFunction right) {
		super(val, right);
	}
	
	public Min(final Number val1, final Number val2) {
		super(val1, val2);
	}

	public Min(final Object... elems) throws Exception {
		super(elems);
	}

	@Override
	public final double eval() {
		return Math.min(a().eval(), b().eval());
	}
}