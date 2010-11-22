package mpicbg.imglib.function.operations.op;

import java.util.Set;

import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.function.operations.Operation;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.RealType;

public final class OpI< R extends RealType<R> > implements Op<R> {

	private final Operation<R> op, other;
	private final Cursor<? extends RealType<?>> cr;
	private R tmp;

	public OpI(final Operation<R> other, final Image<? extends RealType<?>> right, final Operation<R> op) {
		this.cr = right.createCursor();
		this.other = other;
		this.op = op;
	}

	@Override
	public final void compute(final R output) {
		other.compute(tmp);
		op.compute(tmp, cr.getType(), output);
	}

	@Override
	public final void fwd() {
		other.fwd();
		cr.fwd();
	}

	@Override
	public final void getImages(final Set<Image<? extends RealType<?>>> images) {
		other.getImages(images);
		images.add(cr.getImage());
	}

	@Override
	public void init(final R ref) {
		tmp = ref.createVariable();
		other.init(ref);
	}
}
