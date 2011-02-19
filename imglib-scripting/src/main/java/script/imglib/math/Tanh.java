package script.imglib.math;

import script.imglib.math.fn.IFunction;
import script.imglib.math.fn.UnaryOperation;
import mpicbg.imglib.container.Img;
import mpicbg.imglib.type.numeric.RealType;

public class Tanh extends UnaryOperation {

	public Tanh(final Img<? extends RealType<?>> img) {
		super(img);
	}
	public Tanh(final IFunction fn) {
		super(fn);
	}
	public Tanh(final Number val) {
		super(val);
	}

	@Override
	public final double eval() {
		return Math.tanh(a().eval());
	}
}