package script.imglib.math;

import script.imglib.math.fn.IFunction;
import script.imglib.math.fn.UnaryOperation;
import mpicbg.imglib.container.Img;
import mpicbg.imglib.type.numeric.RealType;

public class Log10 extends UnaryOperation {

	public Log10(final Img<? extends RealType<?>> img) {
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