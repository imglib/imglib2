package mpicbg.imglib.function.operations.op;

import mpicbg.imglib.function.operations.Operation;
import mpicbg.imglib.type.numeric.NumericType;

public final class OpN< A extends NumericType<A> > extends AOpN<A> {

	public OpN(final Operation<A> other, final Number val, final Operation<A> op) {
		super(other, val, op);
	}
	
	@Override
	public final void compute(final A output) {
		other.compute(tmp);
		op.compute(tmp, num, output);
	}
}
