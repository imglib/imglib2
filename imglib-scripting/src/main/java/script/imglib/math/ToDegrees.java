package script.imglib.math;

import script.imglib.math.fn.IFunction;
import script.imglib.math.fn.UnaryOperation;
import mpicbg.imglib.img.Img;
import mpicbg.imglib.type.numeric.RealType;

public class ToDegrees extends UnaryOperation {

	public ToDegrees(final Img<? extends RealType<?>> img) {
		super(img);
	}
	public ToDegrees(final IFunction fn) {
		super(fn);
	}
	public ToDegrees(final Number val) {
		super(val);
	}

	@Override
	public final double eval() {
		return Math.toDegrees(a().eval());
	}
}
