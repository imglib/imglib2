package script.imglib.math;

import script.imglib.math.fn.BinaryOperation;
import script.imglib.math.fn.IFunction;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.RealType;

/** Xor two float values by first casting them to int.
 *  While the casting may look outrageous, that's what ImageJ does
 *  Xor is thus the expected behavior. In the future, we may be able
 *  to provide a type converter that reads actual integers from integer
 *  images when possible. */
public class Xor extends BinaryOperation
{
	public Xor(final Image<? extends RealType<?>> left, final Image<? extends RealType<?>> right) {
		super(left, right);
	}

	public Xor(final IFunction fn, final Image<? extends RealType<?>> right) {
		super(fn, right);
	}

	public Xor(final Image<? extends RealType<?>> left, final IFunction fn) {
		super(left, fn);
	}

	public Xor(final IFunction fn1, final IFunction fn2) {
		super(fn1, fn2);
	}
	
	public Xor(final Image<? extends RealType<?>> left, final Number val) {
		super(left, val);
	}

	public Xor(final Number val,final Image<? extends RealType<?>> right) {
		super(val, right);
	}

	public Xor(final IFunction left, final Number val) {
		super(left, val);
	}

	public Xor(final Number val,final IFunction right) {
		super(val, right);
	}
	
	public Xor(final Number val1, final Number val2) {
		super(val1, val2);
	}

	public Xor(final Object... elems) throws Exception {
		super(elems);
	}

	@Override
	public final double eval() {
		return ((int)a().eval()) ^ ((int)b().eval());
	}
}