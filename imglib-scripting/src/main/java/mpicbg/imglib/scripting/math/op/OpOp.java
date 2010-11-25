package mpicbg.imglib.scripting.math.op;

import java.util.Set;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.math.fn.Operation;
import mpicbg.imglib.type.numeric.RealType;

public final class OpOp< R extends RealType<R> > implements Op<R> {

	private final Operation<R> other1, other2, op;
	private R tmp1, tmp2;

	public OpOp(final Operation<R> other1, final Operation<R> other2, final Operation<R> op) {
		this.other1 = other1;
		this.other2 = other2;
		this.op = op;
	}

	@Override
	public final void compute(final R output) {
		other1.compute(tmp1);
		other2.compute(tmp2);
		op.compute(tmp1, tmp2, output);
	}

	@Override
	public final void fwd() {
		other1.fwd();
		other2.fwd();
	}

	@Override
	public final void getImages(final Set<Image<?>> images) {
		other1.getImages(images);
		other2.getImages(images);
	}
	
	@Override
	public final void init(final R ref) {
		this.tmp1 = ref.createVariable();
		this.tmp2 = ref.createVariable();
		other1.init(ref);
		other2.init(ref);
	}
}