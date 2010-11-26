package mpicbg.imglib.scripting.math;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.math.fn.IFunction;
import mpicbg.imglib.scripting.math.fn.UnaryOperation;
import mpicbg.imglib.type.numeric.RealType;

public class Sin extends UnaryOperation {

	public Sin(final Image<? extends RealType<?>> img) {
		super(img);
	}
	public Sin(final IFunction fn) {
		super(fn);
	}
	public Sin(final Number val) {
		super(val);
	}

	@Override
	public double eval() {
		return Math.sin(a().eval());
	}
}