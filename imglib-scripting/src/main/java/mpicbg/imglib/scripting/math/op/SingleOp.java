package mpicbg.imglib.scripting.math.op;

import java.util.Set;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.math.fn.Operation;
import mpicbg.imglib.type.numeric.RealType;

public final class SingleOp< R extends RealType<R> > implements Op<R> {

	private final Operation<R> op, other;

	public SingleOp(final Operation<R> other, final Operation<R> op) {
		this.other = other;
		this.op = op;
	}

	@Override
	public final void compute(final R output) {
		other.compute(output);
		op.compute(output, null, output);
	}

	@Override
	public final void fwd() {
		other.fwd();
	}

	@Override
	public final void getImages(final Set<Image<? extends RealType<?>>> images) {
		other.getImages(images);
	}

	@Override
	public void init(final R ref) {
		other.init(ref);
	}
}