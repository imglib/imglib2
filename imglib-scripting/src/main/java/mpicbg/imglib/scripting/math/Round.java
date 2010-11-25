package mpicbg.imglib.scripting.math;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.math.fn.IFunction;
import mpicbg.imglib.scripting.math.fn.UnaryOperation;
import mpicbg.imglib.type.numeric.RealType;

public class Round extends UnaryOperation {

	public Round(final Image<? extends RealType<?>> img) {
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