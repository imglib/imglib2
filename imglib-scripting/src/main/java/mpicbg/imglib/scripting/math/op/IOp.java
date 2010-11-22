package mpicbg.imglib.scripting.math.op;

import java.util.Set;

import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.math.fn.Operation;
import mpicbg.imglib.type.numeric.RealType;

public final class IOp< R extends RealType<R> > implements Op< R > {

	private final Operation<R> op, other;
	private final Cursor<? extends RealType<?>> cl;
	private R tmp;

	public IOp(final Image<? extends RealType<?>> left, final Operation<R> other, final Operation<R> op) {
		this.cl = left.createCursor();
		this.other = other;
		this.op = op;
	}

	@Override
	public final void compute(final R output) {
		other.compute(tmp);
		op.compute(cl.getType(), tmp, output);
	}

	@Override
	public final void fwd() {
		other.fwd();
		cl.fwd();
	}

	@Override
	public final void getImages(final Set<Image<? extends RealType<?>>> images) {
		images.add(cl.getImage());
		other.getImages(images);
	}

	@Override
	public void init(final R ref) {
		tmp = ref.createVariable();
		other.init(ref);
	}
}
