package mpicbg.imglib.scripting.math;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.math.fn.BinaryOperation;
import mpicbg.imglib.scripting.math.fn.Operation;
import mpicbg.imglib.type.numeric.RealType;

public class Max< R extends RealType<R> > extends BinaryOperation<R>
{
	public Max(final Image<? extends RealType<?>> left, final Image<? extends RealType<?>> right) {
		super(left, right);
	}

	public Max(final Operation<R> op, final Image<? extends RealType<?>> right) {
		super(op, right);
	}

	public Max(final Image<? extends RealType<?>> left, final Operation<R> op) {
		super(left, op);
	}

	public Max(final Operation<R> op1, final Operation<R> op2) {
		super(op1, op2);
	}
	
	public Max(final Image<? extends RealType<?>> left, final Number val) {
		super(left, val);
	}

	public Max(final Number val,final Image<? extends RealType<?>> right) {
		super(val, right);
	}

	public Max(final Operation<R> left, final Number val) {
		super(left, val);
	}

	public Max(final Number val,final Operation<R> right) {
		super(val, right);
	}
	
	public Max(final Number val1, final Number val2) {
		super(val1, val2);
	}

	@Override
	public final void compute( final RealType<?> input1, final RealType<?> input2, final R output ) {
		output.setReal(Math.max(input1.getRealDouble(), input2.getRealDouble()));
	}
}