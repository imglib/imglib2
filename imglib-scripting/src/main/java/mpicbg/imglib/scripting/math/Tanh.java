package mpicbg.imglib.scripting.math;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.math.fn.IFunction;
import mpicbg.imglib.scripting.math.fn.UnaryOperation;
import mpicbg.imglib.type.numeric.RealType;

public class Tanh extends UnaryOperation {

	public Tanh(final Image<? extends RealType<?>> img) {
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