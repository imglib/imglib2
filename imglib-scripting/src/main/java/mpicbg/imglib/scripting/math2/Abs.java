package mpicbg.imglib.scripting.math2;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.math2.fn.IFunction;
import mpicbg.imglib.scripting.math2.fn.UnaryOperation;
import mpicbg.imglib.type.numeric.RealType;

public class Abs extends UnaryOperation {

	public Abs(final Image<? extends RealType<?>> img) {
		super(img);
	}
	public Abs(final IFunction fn) {
		super(fn);
	}
	public Abs(final Number val) {
		super(val);
	}

	@Override
	public final double eval() {
		return Math.abs(a().eval());
	}
}