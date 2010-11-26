package mpicbg.imglib.scripting.math;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.math.fn.IFunction;
import mpicbg.imglib.scripting.math.fn.UnaryOperation;
import mpicbg.imglib.type.numeric.RealType;

public class Floor extends UnaryOperation {

	public Floor(final Image<? extends RealType<?>> img) {
		super(img);
	}
	public Floor(final IFunction fn) {
		super(fn);
	}
	public Floor(final Number val) {
		super(val);
	}

	@Override
	public final double eval() {
		return Math.floor(a().eval());
	}
}