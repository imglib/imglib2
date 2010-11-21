package mpicbg.imglib.function.operations.op;

import java.util.Set;

import mpicbg.imglib.function.operations.Operation;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.NumericType;

public final class OpOpOp< A extends NumericType<A> > implements Op<A> {

	private final Operation<A> other1, other2, op;

	public OpOpOp(final Operation<A> other1, final Operation<A> other2, final Operation<A> op) {
		this.other1 = other1;
		this.other2 = other2;
		this.op = op;
	}

	@Override
	public final void compute(final A output) {
		other1.compute(output);
		final A output2 = output.copy();
		other2.compute(output2);
		op.compute(output, output2, output);
	}

	@Override
	public final void fwd() {
		other1.fwd();
		other2.fwd();
	}

	@Override
	public void getImages(final Set<Image<A>> images) {
		other1.getImages(images);
		other2.getImages(images);
	}
}