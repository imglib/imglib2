package script.imglib.algorithm.fn;

import script.imglib.color.fn.ColorFunction;
import script.imglib.math.Compute;
import script.imglib.math.fn.IFunction;
import mpicbg.imglib.image.Image;

public class AlgorithmUtil
{
	/** Wraps Image, ColorFunction and IFunction, but not numbers. */
	@SuppressWarnings("unchecked")
	static public final Image wrap(final Object ob) throws Exception {
		if (ob instanceof Image<?>) return (Image)ob;
		if (ob instanceof ColorFunction) return Compute.inRGBA((ColorFunction)ob);
		if (ob instanceof IFunction) return Compute.inDoubles((IFunction)ob);
		throw new Exception("Cannot create an image from " + ob.getClass());
	}
}