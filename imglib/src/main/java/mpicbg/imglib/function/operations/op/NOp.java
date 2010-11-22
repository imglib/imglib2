package mpicbg.imglib.function.operations.op;

import mpicbg.imglib.function.operations.Operation;
import mpicbg.imglib.type.numeric.RealType;

public final class NOp< R extends RealType<R> > extends AOpN<R> {

	public NOp(final Number val, final Operation<R> other, final Operation<R> op) {
		super(other, val, op);
	}

	@Override
	public final void compute(final R output) {
		other.compute(tmp);
		op.compute(num, tmp, output);
	}
}
