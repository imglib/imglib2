package script.imglib.math;

import script.imglib.math.fn.IFunction;
import script.imglib.math.fn.UnaryOperation;
import mpicbg.imglib.img.Img;
import mpicbg.imglib.type.numeric.RealType;

public class ASin extends UnaryOperation {

	public ASin(final Img<? extends RealType<?>> img) {
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
