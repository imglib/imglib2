package mpicbg.imglib.scripting.math;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.math.fn.BinaryOperation;
import mpicbg.imglib.scripting.math.fn.Operation;
import mpicbg.imglib.type.numeric.RealType;

/** Xor two float values by first casting them to int.
 *  While the casting may look outrageous, that's what ImageJ does
 *  and is thus the expected behavior. In the future, we may be able
 *  to provide a type converter that reads actual integers from integer
 *  images when possible. */
public class Xor< R extends RealType<R> > extends BinaryOperation<R>
{
	public Xor(final Image<? extends RealType<?>> left, final Image<? extends RealType<?>> right) {
		super(left, right);
	}

	public Xor(final Operation<R> op, final Image<? extends RealType<?>> right) {
		super(op, right);
	}

	public Xor(final Image<? extends RealType<?>> left, final Operation<R> op) {
		super(left, op);
	}

	public Xor(final Operation<R> op1, final Operation<R> op2) {
		super(op1, op2);
	}
	
	public Xor(final Image<? extends RealType<?>> left, final Number val) {
		super(left, val);
	}

	public Xor(final Number val,final Image<? extends RealType<?>> right) {
		super(val, right);
	}

	public Xor(final Operation<R> left, final Number val) {
		super(left, val);
	}

	public Xor(final Number val,final Operation<R> right) {
		super(val, right);
	}
	
	public Xor(final Number val1, final Number val2) {
		super(val1, val2);
	}

	@Override
	public final void compute( final RealType<?> input1, final RealType<?> input2, final R output ) {
		output.setReal( ((int)input1.getRealDouble()) ^ ((int)input2.getRealDouble()) );
	}
}