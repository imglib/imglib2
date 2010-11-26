package mpicbg.imglib.scripting.math;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.math.fn.IFunction;
import mpicbg.imglib.scripting.math.fn.UnaryOperation;
import mpicbg.imglib.type.numeric.RealType;

public class ACos extends UnaryOperation {

	public ACos(final Image<? extends RealType<?>> img) {
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