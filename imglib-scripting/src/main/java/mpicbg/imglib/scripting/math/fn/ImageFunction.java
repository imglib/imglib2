package mpicbg.imglib.scripting.math.fn;

import java.util.Collection;

import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.RealType;

/** A function to that returns every pixel of a given {@link Image}
 *  at every call to {@link eval}. */
public final class ImageFunction implements IFunction {

	private final Cursor<? extends RealType<?>> c;

	public ImageFunction(final Image<? extends RealType<?>> img) {
		this.c = img.createCursor();
	}

	@Override
	public final double eval() {
		c.fwd();
		return c.getType().getRealDouble();
	}

	@Override
	public final void findCursors(final Collection<Cursor<?>> cursors) {
		cursors.add(c);
	}
}