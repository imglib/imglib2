package mpicbg.imglib.function.operations.op;

import mpicbg.imglib.function.operations.Operation;
import mpicbg.imglib.type.numeric.RealType;

public final class NOp< A extends RealType<A> > extends AOpN<A> {

	public NOp(final Number val, final Operation<A> other, final Operation<A> op) {
		super(other, val, op);
	}

	@Override
	public final void compute(final A output) {
		other.compute(tmp);
		op.compute(num, tmp, output);
	}
}
