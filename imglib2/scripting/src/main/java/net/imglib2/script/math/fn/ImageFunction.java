package net.imglib2.script.math.fn;

import java.util.Collection;

import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;

/** A function to that returns every pixel of a given {@link Image}
 *  at every call to {@link eval}. */
public final class ImageFunction implements IFunction {

	private final Img<? extends RealType<?>> img;
	private final Cursor<? extends RealType<?>> c;

	public ImageFunction(final Img<? extends RealType<?>> img) {
		this.img = img;
		this.c = img.cursor();
	}

	@Override
	public final double eval() {
		c.fwd();
		return c.get().getRealDouble();
	}

	@Override
	public final void findCursors(final Collection<Cursor<?>> cursors) {
		cursors.add(c);
	}

	@Override
	public IFunction duplicate()
	{
		return new ImageFunction(img);
	}

	@Override
	public void findImgs(Collection<Img<?>> imgs) {
		imgs.add(this.img);
	}
}