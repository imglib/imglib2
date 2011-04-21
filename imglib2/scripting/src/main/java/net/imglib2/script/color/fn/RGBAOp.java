package net.imglib2.script.color.fn;

import java.util.Collection;

import net.imglib2.Cursor;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.ARGBType;

/** Extracts the red pixel value. */
public abstract class RGBAOp implements IFunction {

	protected final Img<? extends ARGBType> img;
	protected final Cursor<? extends ARGBType> c;

	public RGBAOp(final Img<? extends ARGBType> img) {
		this.img = img;
		this.c = img.cursor();
	}

	@Override
	public final void findCursors(final Collection<Cursor<?>> cursors) {
		cursors.add(c);
	}

	@Override
	public IFunction duplicate() throws Exception
	{
		return getClass().getConstructor(Img.class).newInstance(img);
	}
	
	@Override
	public final void findImgs(final Collection<Img<?>> imgs) {
		imgs.add(img);
	}
}
