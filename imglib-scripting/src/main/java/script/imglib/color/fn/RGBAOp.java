package script.imglib.color.fn;

import java.util.Collection;

import script.imglib.math.fn.IFunction;
import mpicbg.imglib.container.Img;
import mpicbg.imglib.container.ImgCursor;
import mpicbg.imglib.type.numeric.ARGBType;

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