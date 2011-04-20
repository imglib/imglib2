package script.imglib.math;

import script.imglib.math.fn.IFunction;
import script.imglib.math.fn.UnaryOperation;
import mpicbg.imglib.img.Img;
import mpicbg.imglib.type.numeric.RealType;

public class Expm1 extends UnaryOperation {

	public Expm1(final Img<? extends RealType<?>> img) {
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
