package net.imglib2.script.color.fn;

import java.util.Collection;

import net.imglib2.script.math.fn.IFunction;
import net.imglib2.img.Img;
import net.imglib2.img.ImgCursor;
import net.imglib2.type.numeric.ARGBType;

/** Extracts the red pixel value. */
public abstract class RGBAOp implements IFunction {

	protected final ImgCursor<? extends ARGBType> c;

	public RGBAOp(final Img<? extends ARGBType> img) {
		this.c = img.cursor();
	}

	@Override
	public final void findCursors(final Collection<ImgCursor<?>> cursors) {
		cursors.add(c);
	}

	@Override
	public IFunction duplicate() throws Exception
	{
		return getClass().getConstructor(Img.class).newInstance(c.getImg());
	}
}
