package mpicbg.imglib.scripting.math2.fn;

import java.util.Set;

import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.RealType;

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
	public final void findImages(final Set<Image<?>> images) {
		images.add(c.getImage());
	}
}