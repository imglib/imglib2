package mpicbg.imglib.scripting.math;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.math.fn.Operation;
import mpicbg.imglib.scripting.math.fn.UnaryOperation;
import mpicbg.imglib.type.numeric.RealType;

public class Expm1< R extends RealType<R> > extends UnaryOperation<R>
{
	public Expm1(final Image<? extends RealType<?>> img) {
		super(img);
	}

	public Expm1(final Operation<R> op) {
		super(op);
	}

	public Expm1(final Number val) {
		super(val);
	}

	@Override
	public final void compute( final RealType<?> input1, final RealType<?> ignored, final R output ) {
		output.setReal(Math.expm1(input1.getRealDouble()));
	}
}