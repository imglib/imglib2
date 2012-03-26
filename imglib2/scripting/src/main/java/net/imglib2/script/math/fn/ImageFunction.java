package net.imglib2.script.math.fn;

import java.util.Collection;
import java.util.Iterator;

import net.imglib2.IterableRealInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealCursor;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;

/** A function to that returns every pixel of a given {@link Img}
 *  at every call to {@link #eval()}.
 *  If the image given as argument to the constructors is not among those
 *  known to be flat-iterable, but it's a {@link RandomAccessibleInterval},
 *  then it will be wrapped accordingly to ensure flat iteration. */
public final class ImageFunction<T extends RealType<T>> implements IFunction, Iterator<T>, Iterable<T> {

	private final IterableRealInterval<T> img;
	private final RealCursor<T> c;

	public ImageFunction(final IterableRealInterval<T> img) {
		this.img = Util.flatIterable(img);
		this.c = img.cursor();
	}

	public ImageFunction(final RandomAccessibleInterval<T> rai) {
		this.img = Util.flatIterable(rai);
		this.c = img.cursor();
	}

	public ImageFunction(final Img<T> img) {
		this.img = Util.flatIterable(img);
		this.c = img.cursor();
	}

	@Override
	public final double eval() {
		c.fwd();
		return c.get().getRealDouble();
	}
	
	/** Same as {@link #eval()} but returns the {@link RealType}. */
	@Override
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

	@Override
	public final boolean hasNext() {
		return c.hasNext();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public final Iterator<T> iterator() {
		return this;
	}
}
