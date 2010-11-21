package mpicbg.imglib.function.operations.op;

import mpicbg.imglib.function.operations.Operation;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.NumericType;

public final class IN< A extends NumericType<A> > extends AIN<A> {

	public IN(final Image<A> left, final Number val, final Operation<A> op) {
		super(left, val, op);
	}

	@Override
	public final void compute(final A output) {
		op.compute(c.getType(), num, output);
	}
}
