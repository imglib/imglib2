package mpicbg.imglib.scripting.math2;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.math2.fn.BinaryOperation;
import mpicbg.imglib.scripting.math2.fn.IFunction;
import mpicbg.imglib.type.numeric.RealType;

public class Divide extends BinaryOperation
{
	public Divide(final Image<? extends RealType<?>> left, final Image<? extends RealType<?>> right) {
		super(left, right);
	}

	public Divide(final IFunction fn, final Image<? extends RealType<?>> right) {
		super(fn, right);
	}

	public Divide(final Image<? extends RealType<?>> left, final IFunction fn) {
		super(left, fn);
	}

	public Divide(final IFunction fn1, final IFunction fn2) {
		super(fn1, fn2);
	}
	
	public Divide(final Image<? extends RealType<?>> left, final Number val) {
		super(left, val);
	}

	public Divide(final Number val,final Image<? extends RealType<?>> right) {
		super(val, right);
	}

	public Divide(final IFunction left, final Number val) {
		super(left, val);
	}

	public Divide(final Number val,final IFunction right) {
		super(val, right);
	}
	
	public Divide(final Number val1, final Number val2) {
		super(val1, val2);
	}

	public Divide(final Object... elems) throws Exception {
		super(elems);
	}

	/** 1 / img */
	public Divide(final Image<? extends RealType<?>> right) {
		super(1, right);
	}

	/** 1 / val */
	public Divide(final Number val) {
		super(1, val);
	}

	/** 1 / fn.eval() */
	public Divide(final IFunction fn) {
		super(1, fn);
	}

	@Override
	public final double eval() {
		return a().eval() / b().eval();
	}
}