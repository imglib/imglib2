package mpicbg.imglib.scripting.math;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.math.fn.Operation;
import mpicbg.imglib.scripting.math.fn.UnaryOperation;
import mpicbg.imglib.type.numeric.RealType;

/**  Returns the natural logarithm of the sum of the argument and 1. */
public class Log1p< R extends RealType<R> > extends UnaryOperation<R>
{
	public Log1p(final Image<? extends RealType<?>> img) {
		super(img);
	}

	public Log1p(final Operation<R> op) {
		super(op);
	}

	public Log1p(final Number val) {
		super(val);
	}

	@Override
	public final void compute( final RealType<?> input1, final RealType<?> ignored, final R output ) {
		output.setReal(Math.log1p(input1.getRealDouble()));
	}
}