package net.imglib2.script.math.fn;

import java.util.Collection;

import net.imglib2.IterableRealInterval;
import net.imglib2.RealCursor;
import net.imglib2.type.numeric.RealType;

/** A function to that returns every pixel of a given {@link Image}
 *  at every call to {@link eval}. */
public final class ImageFunction implements IFunction {

	private final IterableRealInterval<? extends RealType<?>> img;
	private final RealCursor<? extends RealType<?>> c;

	public ImageFunction(final IterableRealInterval<? extends RealType<?>> img) {
		this.img = img;
		this.c = img.cursor();
	}

	@Override
	public final double eval() {
		c.fwd();
		return c.get().getRealDouble();
	}

	@Override
	public final void findCursors(final Collection<RealCursor<?>> cursors) {
		cursors.add(c);
	}

	@Override
	public IFunction duplicate()
	{
		return new ImageFunction(img);
	}

	@Override
	public void findImgs(Collection<IterableRealInterval<?>> iris) {
		iris.add(this.img);
	}
}