package mpicbg.imglib.scripting.math;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.math.fn.Operation;
import mpicbg.imglib.scripting.math.fn.UnaryOperation;
import mpicbg.imglib.type.numeric.RealType;

/** Returns the signum function of the argument; zero if the argument is zero, 1.0f if the argument is greater than zero, -1.0f if the argument is less than zero. */
public class Signum< R extends RealType<R> > extends UnaryOperation<R>
{
	public Signum(final Image<? extends RealType<?>> img) {
		super(img);
	}

	public Signum(final Operation<R> op) {
		super(op);
	}

	public Signum(final Number val) {
		super(val);
	}

	@Override
	public final void compute( final RealType<?> input1, final RealType<?> ignored, final R output ) {
		output.setReal(Math.signum(input1.getRealDouble()));
	}
}