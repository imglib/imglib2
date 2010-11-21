package mpicbg.imglib.function.operations.op;

import java.util.Set;

import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.function.operations.Operation;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.NumericType;

public final class II< A extends NumericType<A> > implements Op< A > {

	private final Operation<A> op;
	private final Cursor<A> cl, cr;

	public II(final Image<A> left, final Image<A> right, final Operation<A> op) {
		this.cl = left.createCursor();
		this.cr = right.createCursor();
		this.op = op;
	}

	@Override
	public final void compute(final A output) {
		op.compute(cl.getType(), cr.getType(), output);
	}

	@Override
	public final void fwd() {
		cl.fwd();
		cr.fwd();
	}

	@Override
	public final void getImages(final Set<Image<A>> images) {
		images.add(cl.getImage());
		images.add(cr.getImage());
	}

	@Override
	public void init(final A ref) {}
}
