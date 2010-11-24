package mpicbg.imglib.scripting.math;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.math.fn.Operation;
import mpicbg.imglib.scripting.math.fn.UnaryOperation;
import mpicbg.imglib.type.numeric.RealType;

/* Returns the hyperbolic sine of a double value. */
public class Sinh< R extends RealType<R> > extends UnaryOperation<R>
{
	public Sinh(final Image<? extends RealType<?>> img) {
		super(img);
	}

	public Sinh(final Operation<R> op) {
		super(op);
	}

	public Sinh(final Number val) {
		super(val);
	}

	@Override
	public final void compute( final RealType<?> input1, final RealType<?> ignored, final R output ) {
		output.setReal(Math.sinh(input1.getRealDouble()));
	}
}