package script.imglib.math;

import script.imglib.math.fn.IFunction;
import script.imglib.math.fn.UnaryOperation;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.RealType;

public class Sinh extends UnaryOperation {

	public Sinh(final Image<? extends RealType<?>> img) {
		super(img);
	}
	public Sinh(final IFunction fn) {
		super(fn);
	}
	public Sinh(final Number val) {
		super(val);
	}

	@Override
	public final double eval() {
		return Math.sinh(a().eval());
	}
}