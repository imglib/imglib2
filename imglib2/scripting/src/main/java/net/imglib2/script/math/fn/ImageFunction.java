package net.imglib2.script.math.fn;

import java.util.Collection;

import net.imglib2.img.Img;
import net.imglib2.img.ImgCursor;
import net.imglib2.type.numeric.RealType;

/** A function to that returns every pixel of a given {@link Image}
 *  at every call to {@link eval}. */
public final class ImageFunction implements IFunction {

	private final ImgCursor<? extends RealType<?>> c;

	public ImageFunction(final Img<? extends RealType<?>> img) {
		this.c = img.cursor();
	}

	@Override
	public final double eval() {
		c.fwd();
		return c.get().getRealDouble();
	}

	@Override
	public final void findCursors(final Collection<ImgCursor<?>> cursors) {
		cursors.add(c);
	}

	@Override
	public IFunction duplicate()
	{
		return new ImageFunction(c.getImg());
	}
}
