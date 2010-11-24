package mpicbg.imglib.scripting.math.op;

import java.util.Set;

import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.math.fn.Operation;
import mpicbg.imglib.type.numeric.RealType;

public final class SingleI< R extends RealType<R> > implements Op<R>
{
	private final Operation<R> op;
	private final Cursor<? extends RealType<?>> c;

	public SingleI(final Image<? extends RealType<?>> img, final Operation<R> op) {
		this.c = img.createCursor();
		this.op = op;
	}

	@Override
	public final void compute(final R output) {
		op.compute(c.getType(), null, output);
	}

	@Override
	public final void fwd() {
		c.fwd();
	}

	@Override
	public final void getImages(final Set<Image<?>> images) {
		images.add(c.getImage());
	}

	@Override
	public final void init(final R ref) {}
}