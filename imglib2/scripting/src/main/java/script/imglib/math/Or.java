package script.imglib.math;

import script.imglib.math.fn.BinaryOperation;
import script.imglib.math.fn.IFunction;
import mpicbg.imglib.img.Img;
import mpicbg.imglib.type.numeric.RealType;

/** Or two float values by first casting them to int.
 *  While the casting may look outrageous, that's what ImgJ does
 *  and is thus the expected behavior. In the future, we may be able
 *  to provide a type converter that reads actual integers from integer
 *  Imgs when possible. */
public class Or extends BinaryOperation
{
	public Or(final Img<? extends RealType<?>> left, final Img<? extends RealType<?>> right) {
		super(left, right);
	}

	public Or(final IFunction fn, final Img<? extends RealType<?>> right) {
		super(fn, right);
	}

	public Or(final Img<? extends RealType<?>> left, final IFunction fn) {
		super(left, fn);
	}

	public Or(final IFunction fn1, final IFunction fn2) {
		super(fn1, fn2);
	}
	
	public Or(final Img<? extends RealType<?>> left, final Number val) {
		super(left, val);
	}

	public Or(final Number val,final Img<? extends RealType<?>> right) {
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
