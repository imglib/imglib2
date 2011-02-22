package script.imglib.math.fn;

import mpicbg.imglib.container.Img;
import mpicbg.imglib.type.numeric.RealType;

public class Util
{
	@SuppressWarnings("unchecked")
	static public final IFunction wrap(final Object ob) {
		if (ob instanceof Img<?>) return new ImageFunction((Img<? extends RealType<?>>)ob);
		if (ob instanceof IFunction) return (IFunction)ob;
		if (ob instanceof Number) return new NumberFunction((Number)ob);
		throw new IllegalArgumentException("Cannot compose a function with " + ob);
	}
}