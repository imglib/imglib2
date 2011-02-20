package script.imglib.math;

import script.imglib.math.fn.IFunction;
import script.imglib.math.fn.UnaryOperation;
import mpicbg.imglib.container.Img;
import mpicbg.imglib.type.numeric.RealType;

public class ToRadians extends UnaryOperation {

	public ToRadians(final Img<? extends RealType<?>> img) {
		super(img);
	}
	public ToRadians(final IFunction fn) {
		super(fn);
	}
	public ToRadians(final Number val) {
		super(val);
	}

	@Override
	public final double eval() {
		return Math.toRadians(a().eval());
	}
}