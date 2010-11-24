package mpicbg.imglib.scripting.math;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.math.fn.Operation;
import mpicbg.imglib.scripting.math.fn.UnaryOperation;
import mpicbg.imglib.type.numeric.RealType;

public class Round< R extends RealType<R> > extends UnaryOperation<R>
{
	public Round(final Image<? extends RealType<?>> img) {
		super(img);
	}

	public Round(final Operation<R> op) {
		super(op);
	}

	public Round(final Number val) {
		super(val);
	}

	@Override
	public final void compute( final RealType<?> input1, final RealType<?> ignored, final R output ) {
		output.setReal(Math.round(input1.getRealDouble()));
	}
}