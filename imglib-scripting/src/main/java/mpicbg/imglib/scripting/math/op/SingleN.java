package mpicbg.imglib.scripting.math.op;

import java.util.Set;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.math.fn.Operation;
import mpicbg.imglib.type.numeric.RealType;

/** Computes and caches the result, and sets that to the output always. */
public final class SingleN< R extends RealType<R> > extends AN<R> implements Op<R> {

	protected final Operation<R> op;
	protected final RealType<?> num;
	protected double result;

	public SingleN(final Number val, final Operation<R> op) {
		this.op = op;
		this.num = asType(val);
	}

	@Override
	public final void fwd() {}

	@Override
	public final void getImages(final Set<Image<?>> images) {}

	@Override
	public final void init(final R ref) {
		final R r = ref.createVariable();
		op.compute(num, null, r);
		result = r.getRealDouble();
	}

	@Override
	public final void compute(final R output) {
		output.setReal(result);
	}
}