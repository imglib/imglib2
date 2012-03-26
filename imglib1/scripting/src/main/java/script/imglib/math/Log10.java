package script.imglib.math;

import script.imglib.math.fn.IFunction;
import script.imglib.math.fn.UnaryOperation;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.RealType;

public class Log10 extends UnaryOperation {

	public Log10(final Image<? extends RealType<?>> img) {
		super(img);
	}
	public Log10(final IFunction fn) {
		super(fn);
	}
	public Log10(final Number val) {
		super(val);
	}

	@Override
	public final double eval() {
		return Math.log10(a().eval());
	}
}
