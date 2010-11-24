package mpicbg.imglib.scripting.math;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.math.fn.Operation;
import mpicbg.imglib.scripting.math.fn.UnaryOperation;
import mpicbg.imglib.type.numeric.RealType;

/** Returns the natural logarithm (base e) of a double value. */
public class Log< R extends RealType<R> > extends UnaryOperation<R>
{
	public Log(final Image<? extends RealType<?>> img) {
		super(img);
	}

	public Log(final Operation<R> op) {
		super(op);
	}

	public Log(final Number val) {
		super(val);
	}

	@Override
	public final void compute( final RealType<?> input1, final RealType<?> ignored, final R output ) {
		output.setReal(Math.log(input1.getRealDouble()));
	}
}