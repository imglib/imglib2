package mpicbg.imglib.function.operations.op;

import mpicbg.imglib.function.operations.Operation;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.NumericType;

public class NI< A extends NumericType<A> > extends AIN<A> {

	public NI(final Number val, final Image<A> right, final Operation<A> op) {
		super(right, val, op);
	}

	@Override
	public final void compute(A output) {
		op.compute(num, c.getType(), output);
	}
}