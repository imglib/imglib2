package mpicbg.imglib.function.operations.op;

import java.util.Set;

import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.function.operations.Operation;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.RealType;

public final class II< R extends RealType<R> > implements Op< R > {

	private final Operation<R> op;
	private final Cursor<? extends RealType<?>> cl, cr;

	public II(final Image<? extends RealType<?>> left, final Image<? extends RealType<?>> right, final Operation<R> op) {
		this.cl = left.createCursor();
		this.cr = right.createCursor();
		this.op = op;
	}

	@Override
	public final void compute(final R output) {
		op.compute(cl.getType(), cr.getType(), output);
	}

	@Override
	public final void fwd() {
		cl.fwd();
		cr.fwd();
	}

	@Override
	public final void getImages(final Set<Image<? extends RealType<?>>> images) {
		images.add(cl.getImage());
		images.add(cr.getImage());
	}

	@Override
	public void init(final R ref) {}
}