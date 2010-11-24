package mpicbg.imglib.scripting.math;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.math.fn.Operation;
import mpicbg.imglib.scripting.math.fn.UnaryOperation;
import mpicbg.imglib.type.numeric.RealType;

public class Cosh< R extends RealType<R> > extends UnaryOperation<R>
{
	public Cosh(final Image<? extends RealType<?>> img) {
		super(img);
	}

	public Cosh(final Operation<R> op) {
		super(op);
	}

	public Cosh(final Number val) {
		super(val);
	}

	@Override
	public final void compute( final RealType<?> input1, final RealType<?> ignored, final R output ) {
		output.setReal(Math.cosh(input1.getRealDouble()));
	}
}