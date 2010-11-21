package mpicbg.imglib.function.operations.op;

import java.util.Set;

import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.function.operations.Operation;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.NumericType;

public final class IOp< A extends NumericType<A> > implements Op< A > {

	private final Operation<A> op, other;
	private final Cursor<A> cl;
	private A tmp;

	public IOp(final Image<A> left, final Operation<A> other, final Operation<A> op) {
		this.cl = left.createCursor();
		this.other = other;
		this.op = op;
	}

	@Override
	public final void compute(final A output) {
		other.compute(tmp);
		op.compute(cl.getType(), tmp, output);
	}

	@Override
	public final void fwd() {
		other.fwd();
		cl.fwd();
	}

	@Override
	public final void getImages(final Set<Image<A>> images) {
		images.add(cl.getImage());
		other.getImages(images);
	}

	@Override
	public void init(final A ref) {
		tmp = ref.createVariable();
		other.init(ref);
	}
}
