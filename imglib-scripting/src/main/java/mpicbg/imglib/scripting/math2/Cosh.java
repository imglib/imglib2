package mpicbg.imglib.scripting.math2;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.math2.fn.IFunction;
import mpicbg.imglib.scripting.math2.fn.UnaryOperation;
import mpicbg.imglib.type.numeric.RealType;

public class Cosh extends UnaryOperation {

	public Cosh(final Image<? extends RealType<?>> img) {
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