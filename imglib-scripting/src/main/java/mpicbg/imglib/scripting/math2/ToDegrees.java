package mpicbg.imglib.scripting.math2;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.math2.fn.IFunction;
import mpicbg.imglib.scripting.math2.fn.UnaryOperation;
import mpicbg.imglib.type.numeric.RealType;

public class ToDegrees extends UnaryOperation {

	public ToDegrees(final Image<? extends RealType<?>> img) {
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