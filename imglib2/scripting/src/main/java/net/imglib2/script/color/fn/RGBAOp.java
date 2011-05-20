package net.imglib2.script.color.fn;

import java.util.Collection;

import net.imglib2.IterableRealInterval;
import net.imglib2.RealCursor;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.type.numeric.ARGBType;

/** Extracts the red pixel value. */
public abstract class RGBAOp implements IFunction {

	protected final IterableRealInterval<? extends ARGBType> img;
	protected final RealCursor<? extends ARGBType> c;

	public RGBAOp(final IterableRealInterval<? extends ARGBType> img) {
		this.img = img;
		this.c = img.cursor();
	}

	@Override
	public final void findCursors(final Collection<RealCursor<?>> cursors) {
		cursors.add(c);
	}

	@Override
	public IFunction duplicate() throws Exception
	{
		return getClass().getConstructor(IterableRealInterval.class).newInstance(img);
	}
	
	@Override
	public final void findImgs(final Collection<IterableRealInterval<?>> iris) {
		iris.add(img);
	}
}
