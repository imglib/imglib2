package mpicbg.imglib.function.operations.op;

import mpicbg.imglib.function.operations.Operation;
import mpicbg.imglib.type.numeric.NumericType;

public class NOp< A extends NumericType<A> > extends AOpN<A> {

	public NOp(final Number val, final Operation<A> other, final Operation<A> op) {
		super(other, val, op);
	}

	@Override
	public void compute(final A output) {
		other.compute(output);
		op.compute(num, output, output);
	}
}