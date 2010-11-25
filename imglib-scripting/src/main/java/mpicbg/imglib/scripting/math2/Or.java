package mpicbg.imglib.scripting.math2;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.math2.fn.BinaryOperation;
import mpicbg.imglib.scripting.math2.fn.IFunction;
import mpicbg.imglib.type.numeric.RealType;

/** Or two float values by first casting them to int.
 *  While the casting may look outrageous, that's what ImageJ does
 *  and is thus the expected behavior. In the future, we may be able
 *  to provide a type converter that reads actual integers from integer
 *  images when possible. */
public class Or extends BinaryOperation
{
	public Or(final Image<? extends RealType<?>> left, final Image<? extends RealType<?>> right) {
		super(left, right);
	}

	public Or(final IFunction fn, final Image<? extends RealType<?>> right) {
		super(fn, right);
	}

	public Or(final Image<? extends RealType<?>> left, final IFunction fn) {
		super(left, fn);
	}

	public Or(final IFunction fn1, final IFunction fn2) {
		super(fn1, fn2);
	}
	
	public Or(final Image<? extends RealType<?>> left, final Number val) {
		super(left, val);
	}

	public Or(final Number val,final Image<? extends RealType<?>> right) {
		super(val, right);
	}

	public Or(final IFunction left, final Number val) {
		super(left, val);
	}

	public Or(final Number val,final IFunction right) {
		super(val, right);
	}
	
	public Or(final Number val1, final Number val2) {
		super(val1, val2);
	}

	public Or(final Object... elems) throws Exception {
		super(elems);
	}

	@Override
	public final double eval() {
		return ((int)a().eval()) | ((int)b().eval());
	}
}