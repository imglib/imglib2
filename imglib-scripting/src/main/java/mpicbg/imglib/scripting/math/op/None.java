package mpicbg.imglib.scripting.math.op;

import java.util.Set;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.math.fn.Operation;
import mpicbg.imglib.type.numeric.RealType;

public final class None<R extends RealType<R>> implements Op<R> {

	private final Operation<R> op;

	public None(final Operation<R> op) {
		this.op = op;
	}

	@Override
	public final void compute(final R output) {
		op.compute(null, null, output);
	}

	@Override
	public final void fwd() {}

	@Override
	public final void getImages(final Set<Image<?>> images) {}

	@Override
	public final void init(final R ref) {}
}
