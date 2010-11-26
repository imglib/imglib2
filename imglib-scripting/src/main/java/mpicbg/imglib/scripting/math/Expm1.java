package mpicbg.imglib.scripting.math;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.math.fn.IFunction;
import mpicbg.imglib.scripting.math.fn.UnaryOperation;
import mpicbg.imglib.type.numeric.RealType;

public class Expm1 extends UnaryOperation {

	public Expm1(final Image<? extends RealType<?>> img) {
		super(img);
	}
	public Expm1(final IFunction fn) {
		super(fn);
	}
	public Expm1(final Number val) {
		super(val);
	}

	@Override
	public final double eval() {
		return Math.expm1(a().eval());
	}
}