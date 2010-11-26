package mpicbg.imglib.scripting.math;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.math.fn.IFunction;
import mpicbg.imglib.scripting.math.fn.UnaryOperation;
import mpicbg.imglib.type.numeric.RealType;

public class ATan extends UnaryOperation {

	public ATan(final Image<? extends RealType<?>> img) {
		super(img);
	}
	public ATan(final IFunction fn) {
		super(fn);
	}
	public ATan(final Number val) {
		super(val);
	}

	@Override
	public final double eval() {
		return Math.atan(a().eval());
	}
}