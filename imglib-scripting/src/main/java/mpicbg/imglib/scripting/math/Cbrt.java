package mpicbg.imglib.scripting.math;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.math.fn.IFunction;
import mpicbg.imglib.scripting.math.fn.UnaryOperation;
import mpicbg.imglib.type.numeric.RealType;

public class Cbrt extends UnaryOperation {

	public Cbrt(final Image<? extends RealType<?>> img) {
		super(img);
	}
	public Cbrt(final IFunction fn) {
		super(fn);
	}
	public Cbrt(final Number val) {
		super(val);
	}

	@Override
	public final double eval() {
		return Math.cbrt(a().eval());
	}
}