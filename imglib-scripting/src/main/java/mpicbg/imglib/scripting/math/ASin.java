package mpicbg.imglib.scripting.math;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.math.fn.Operation;
import mpicbg.imglib.scripting.math.fn.UnaryOperation;
import mpicbg.imglib.type.numeric.RealType;

public class ASin< R extends RealType<R> > extends UnaryOperation<R>
{
	public ASin(final Image<? extends RealType<?>> img) {
		super(img);
	}

	public ASin(final Operation<R> op) {
		super(op);
	}

	public ASin(final Number val) {
		super(val);
	}

	@Override
	public final void compute( final RealType<?> input1, final RealType<?> ignored, final R output ) {
		output.setReal(Math.asin(input1.getRealDouble()));
	}
}