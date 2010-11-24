package mpicbg.imglib.scripting.math.op;

import java.util.Set;

import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.math.fn.Operation;
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
	public final void getImages(final Set<Image<?>> images) {
		images.add(cl.getImage());
		images.add(cr.getImage());
	}

	@Override
	public final void init(final R ref) {}
}