package script.imglib.math;

import script.imglib.math.fn.IFunction;
import script.imglib.math.fn.UnaryOperation;
import mpicbg.imglib.container.Img;
import mpicbg.imglib.type.numeric.RealType;

public class Cosh extends UnaryOperation {

	public Cosh(final Img<? extends RealType<?>> img) {
		super(img);
	}
	public Cosh(final IFunction fn) {
		super(fn);
	}
	public Cosh(final Number val) {
		super(val);
	}

	@Override
	public final double eval() {
		return Math.cosh(a().eval());
	}
}