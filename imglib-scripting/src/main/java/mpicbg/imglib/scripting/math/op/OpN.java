package mpicbg.imglib.scripting.math.op;

import mpicbg.imglib.scripting.math.fn.Operation;
import mpicbg.imglib.type.numeric.RealType;

public final class OpN< A extends RealType<A> > extends AOpN<A> {

	public OpN(final Operation<A> other, final Number val, final Operation<A> op) {
		super(other, val, op);
	}
	
	@Override
	public final void compute(final A output) {
		other.compute(tmp);
		op.compute(tmp, num, output);
	}
}
