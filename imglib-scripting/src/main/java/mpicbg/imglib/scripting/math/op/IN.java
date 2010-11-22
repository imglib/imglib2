package mpicbg.imglib.scripting.math.op;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.math.fn.Operation;
import mpicbg.imglib.type.numeric.RealType;

public final class IN< R extends RealType<R> > extends AIN<R> {

	public IN(final Image<? extends RealType<?>> left, final Number val, final Operation<R> op) {
		super(left, val, op);
	}

	@Override
	public final void compute(final R output) {
		op.compute(c.getType(), num, output);
	}
}