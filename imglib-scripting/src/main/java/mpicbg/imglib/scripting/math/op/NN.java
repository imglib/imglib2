package mpicbg.imglib.scripting.math.op;

import java.util.Set;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.math.fn.Operation;
import mpicbg.imglib.type.numeric.RealType;

/** At init(), computes and caches the results. */
public final class NN< R extends RealType<R> > extends AN<R> implements Op<R> {

	private final Operation<R> op;
	private double val1, val2, result;
	
	public NN(final Number num1, final Number num2, final Operation<R> op) {
		this.op = op;
		// Cannot call num1.doubleValue(): byte would not be unsigned byte
		this.val1 = asType(num1).getRealDouble();
		this.val2 = asType(num2).getRealDouble();
	}

	@Override
	public final void compute(final R output) {
		output.setReal(result);
	}

	@Override
	public final void fwd() {}

	@Override
	public final void getImages(final Set<Image<?>> images) {}

	@Override
	public final void init(final R ref) {
		final R n1 = ref.createVariable(),
				n2 = ref.createVariable();
		n1.setReal(val1);
		n2.setReal(val2);
		op.compute(n1, n2, n1);
		result = n1.getRealDouble();
	}
}