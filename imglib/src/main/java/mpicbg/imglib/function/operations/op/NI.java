package mpicbg.imglib.function.operations.op;

import mpicbg.imglib.function.operations.Operation;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.RealType;

public final class NI< R extends RealType<R> > extends AIN<R> {

	public NI(final Number val, final Image<? extends RealType<?>> right, final Operation<R> op) {
		super(right, val, op);
	}

	@Override
	public final void compute(final R output) {
		op.compute(num, c.getType(), output);
	}
}
