package mpicbg.imglib.function.operations.op;

import java.util.Set;

import mpicbg.imglib.function.operations.Operation;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.RealType;

public final class OpOp< A extends RealType<A> > implements Op<A> {

	private final Operation<A> other1, other2, op;
	private A tmp1, tmp2;

	public OpOp(final Operation<A> other1, final Operation<A> other2, final Operation<A> op) {
		this.other1 = other1;
		this.other2 = other2;
		this.op = op;
	}

	@Override
	public final void compute(final A output) {
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
	public final void getImages(final Set<Image<A>> images) {
		other1.getImages(images);
		other2.getImages(images);
	}
	
	@Override
	public final void init(final A ref) {
		this.tmp1 = ref.createVariable();
		this.tmp2 = ref.createVariable();
		other1.init(ref);
		other2.init(ref);
	}
}
