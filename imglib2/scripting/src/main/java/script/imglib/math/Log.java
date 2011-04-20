package script.imglib.math;

import script.imglib.math.fn.IFunction;
import script.imglib.math.fn.UnaryOperation;
import mpicbg.imglib.img.Img;
import mpicbg.imglib.type.numeric.RealType;

public class Log extends UnaryOperation {

	public Log(final Img<? extends RealType<?>> img) {
		super(img);
	}
	public Log(final IFunction fn) {
		super(fn);
	}
	public Log(final Number val) {
		super(val);
	}

	@Override
	public final double eval() {
		return Math.log(a().eval());
	}
}
