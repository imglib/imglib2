package mpicbg.imglib.scripting.math2;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.math2.fn.IFunction;
import mpicbg.imglib.scripting.math2.fn.UnaryOperation;
import mpicbg.imglib.type.numeric.RealType;

public class Cos extends UnaryOperation {

	public Cos(final Image<? extends RealType<?>> img) {
		super(img);
	}
	public Cos(final IFunction fn) {
		super(fn);
	}
	public Cos(final Number val) {
		super(val);
	}

	@Override
	public final double eval() {
		return Math.cos(a().eval());
	}
}