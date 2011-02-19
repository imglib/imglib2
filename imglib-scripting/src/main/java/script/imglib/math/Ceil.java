package script.imglib.math;

import script.imglib.math.fn.IFunction;
import script.imglib.math.fn.UnaryOperation;
import mpicbg.imglib.container.Img;
import mpicbg.imglib.type.numeric.RealType;

public class Ceil extends UnaryOperation {

	public Ceil(final Img<? extends RealType<?>> img) {
		super(img);
	}
	public Ceil(final IFunction fn) {
		super(fn);
	}
	public Ceil(final Number val) {
		super(val);
	}

	@Override
	public final double eval() {
		return Math.ceil(a().eval());
	}
}