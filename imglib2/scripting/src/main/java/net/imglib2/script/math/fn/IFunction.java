package net.imglib2.script.math.fn;

import java.util.Collection;

import net.imglib2.Cursor;
import net.imglib2.img.Img;

public interface IFunction {
	/** Evaluate this function and return a result in double floating-point precision. */
	public double eval();
	/** Put any cursors in use by this function (and any nested functions) in @param cursors.*/
	public void findCursors(Collection<Cursor<?>> cursors);
	/** Put any cursors in use by this function (and any nested functions) in @param cursors.*/
	public void findImgs(Collection<Img<?>> imgs);

	public IFunction duplicate() throws Exception;
}
