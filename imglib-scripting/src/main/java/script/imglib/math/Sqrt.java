package script.imglib.math;

import script.imglib.math.fn.IFunction;
import script.imglib.math.fn.UnaryOperation;
import mpicbg.imglib.container.Img;
import mpicbg.imglib.type.numeric.RealType;

public class Sqrt extends UnaryOperation {

	public Sqrt(final Img<? extends RealType<?>> img) {
		super(img);
	}
	public Sqrt(final IFunction fn) {
		super(fn);
	}
	public Sqrt(final Number val) {
		super(val);
	}

	@Override
	public double eval() {
		return Math.sqrt(a().eval());
	}
}