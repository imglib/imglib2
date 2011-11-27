package net.imglib2.script.math.fn;

import java.util.Collection;

import net.imglib2.IterableRealInterval;
import net.imglib2.RealCursor;
import net.imglib2.type.numeric.RealType;

/** A function to that returns every pixel of a given {@link Image}
 *  at every call to {@link eval}. */
public final class ImageFunction<T extends RealType<T>> implements IFunction {

	private final IterableRealInterval<T> img;
	private final RealCursor<T> c;

	public ImageFunction(final IterableRealInterval<T> img) {
		this.img = img;
		this.c = img.cursor();
	}

	@Override
	public final double eval() {
		c.fwd();
		return c.get().getRealDouble();
	}
	
	/** Same as {@link #eval()} but returns the {@link RealType}. */
	public final T next() {
		c.fwd();
		return c.get();
	}

	@Override
	public final void findCursors(final Collection<RealCursor<?>> cursors) {
		cursors.add(c);
	}

	@Override
	public ImageFunction<T> duplicate()
	{
		return new ImageFunction<T>(img);
	}

	@Override
	public void findImgs(Collection<IterableRealInterval<?>> iris) {
		iris.add(this.img);
	}
}