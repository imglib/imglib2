package script.imglib.math;

import script.imglib.math.fn.IFunction;
import script.imglib.math.fn.UnaryOperation;
import mpicbg.imglib.img.Img;
import mpicbg.imglib.type.numeric.RealType;

public class Rint extends UnaryOperation {

	public Rint(final Img<? extends RealType<?>> img) {
		super(img);
	}
	public Rint(final IFunction fn) {
		super(fn);
	}
	public Rint(final Number val) {
		super(val);
	}

	@Override
	public final double eval() {
		return Math.rint(a().eval());
	}
}
