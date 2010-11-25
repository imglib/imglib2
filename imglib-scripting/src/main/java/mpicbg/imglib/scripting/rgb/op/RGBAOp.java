package mpicbg.imglib.scripting.rgb.op;

import java.util.Set;

import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.math.fn.Operation;
import mpicbg.imglib.type.numeric.RealType;
import mpicbg.imglib.type.numeric.RGBALegacyType;

/** Extracts the red pixel value. */
public abstract class RGBAOp<R extends RealType<R> > implements Operation<R> {

	protected final Cursor<? extends RGBALegacyType> c;

	public RGBAOp(final Image<? extends RGBALegacyType> img) {
		this.c = img.createCursor();
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

	@Override
	public final void compute(final RealType<?> ignored1, final RealType<?> ignored2, final R output) {
		// Won't be used, but just in case
		compute(output);
	}
}