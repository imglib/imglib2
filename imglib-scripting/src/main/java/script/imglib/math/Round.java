package script.imglib.math;

import script.imglib.math.fn.IFunction;
import script.imglib.math.fn.UnaryOperation;
import mpicbg.imglib.img.Img;
import mpicbg.imglib.type.numeric.RealType;

public class Round extends UnaryOperation {

	public Round(final Img<? extends RealType<?>> img) {
		super(img);
	}
	public Round(final IFunction fn) {
		super(fn);
	}
	public Round(final Number val) {
		super(val);
	}

	@Override
	public final double eval() {
		return Math.round(a().eval());
	}
}
