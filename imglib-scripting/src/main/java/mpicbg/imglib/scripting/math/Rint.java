package mpicbg.imglib.scripting.math;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.math.fn.Operation;
import mpicbg.imglib.scripting.math.fn.UnaryOperation;
import mpicbg.imglib.type.numeric.RealType;

/** Returns the double value that is closest in value to the argument and is equal to a mathematical integer. */
public class Rint< R extends RealType<R> > extends UnaryOperation<R>
{
	public Rint(final Image<? extends RealType<?>> img) {
		super(img);
	}

	public Rint(final Operation<R> op) {
		super(op);
	}

	public Rint(final Number val) {
		super(val);
	}

	@Override
	public final void compute( final RealType<?> input1, final RealType<?> ignored, final R output ) {
		output.setReal(Math.rint(input1.getRealDouble()));
	}
}

