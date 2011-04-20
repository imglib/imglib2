package script.imglib.math;

import script.imglib.math.fn.IFunction;
import script.imglib.math.fn.UnaryOperation;
import mpicbg.imglib.img.Img;
import mpicbg.imglib.type.numeric.RealType;

public class ACos extends UnaryOperation {

	public ACos(final Img<? extends RealType<?>> img) {
		super(img);
	}
	public ACos(final IFunction fn) {
		super(fn);
	}
	public ACos(final Number val) {
		super(val);
	}

	@Override
	public final double eval() {
		return Math.acos(a().eval());
	}
}
