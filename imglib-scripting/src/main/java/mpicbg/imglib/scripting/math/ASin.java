package mpicbg.imglib.scripting.math;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.math.fn.IFunction;
import mpicbg.imglib.scripting.math.fn.UnaryOperation;
import mpicbg.imglib.type.numeric.RealType;

public class ASin extends UnaryOperation {

	public ASin(final Image<? extends RealType<?>> img) {
		super(img);
	}
	public ASin(final IFunction fn) {
		super(fn);
	}
	public ASin(final Number val) {
		super(val);
	}

	@Override
	public final double eval() {
		return Math.asin(a().eval());
	}
}