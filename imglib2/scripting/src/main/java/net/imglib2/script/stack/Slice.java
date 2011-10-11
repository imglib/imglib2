package net.imglib2.script.stack;

import java.util.Collection;

import net.imglib2.IterableRealInterval;
import net.imglib2.RealCursor;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

public final class Slice<R extends RealType<R> & NativeType<R>> implements IFunction
{
	private final RealCursor<? extends RealType<?>> c;
	private final ArrayImg<R, ?> img;
	private final long slice;
	
	// Cannot be done: Views.hyperSlice returns an object that can only deliver a RandomAccess() cursor.
	// And if I restrict to ArrayImg as below, findImgs will return the original which has the wrong
	// value for the third dimension (should be 1).
	// Would have to be done by creating an empty img that is able to refer to the specific slice
	// of the original image.

	/**
	 * @param slice The slice (zero-based) to extract.
	 * */
	public Slice(final ArrayImg<R,?> img, final long slice) throws Exception {
		if (img.numDimensions() < 3) throw new Exception("Slice: need at least 3 dimensions!");
		
		this.c = img.cursor();
		this.c.jumpFwd(img.dimension(0) * img.dimension(1) * slice);

		this.img = img;
		this.slice = slice;
	}
	
	@Override
	public final double eval() {
		c.fwd();
		return c.get().getRealDouble();
	}

	@Override
	public final void findCursors(final Collection<RealCursor<?>> cursors) {
		cursors.add(this.c);
	}

	@Override
	public final void findImgs(final Collection<IterableRealInterval<?>> iris) {
		iris.add(this.img);
	}

	@Override
	public final IFunction duplicate() throws Exception {
		return new Slice<R>(this.img, this.slice);
	}
}
