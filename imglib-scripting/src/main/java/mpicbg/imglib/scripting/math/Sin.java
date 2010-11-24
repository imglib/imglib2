package mpicbg.imglib.scripting.math;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.math.fn.Operation;
import mpicbg.imglib.scripting.math.fn.UnaryOperation;
import mpicbg.imglib.type.numeric.RealType;

/** Returns the Sin function of the argument; zero if the argument is zero, 1.0f if the argument is greater than zero, -1.0f if the argument is less than zero. */
public class Sin< R extends RealType<R> > extends UnaryOperation<R>
{
	public Sin(final Image<? extends RealType<?>> img) {
		super(img);
	}

	public Sin(final Operation<R> op) {
		super(op);
	}

	public Sin(final Number val) {
		super(val);
	}

	@Override
	public final void compute( final RealType<?> input1, final RealType<?> ignored, final R output ) {
		output.setReal(Math.sin(input1.getRealDouble()));
	}
}