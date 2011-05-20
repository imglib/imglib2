package net.imglib2.script.math.fn;

import net.imglib2.IterableRealInterval;
import net.imglib2.type.numeric.RealType;

public class Util
{
	@SuppressWarnings("unchecked")
	static public final IFunction wrap(final Object ob) {
		if (ob instanceof IterableRealInterval<?>) return new ImageFunction((IterableRealInterval<? extends RealType<?>>)ob);
		if (ob instanceof IFunction) return (IFunction)ob;
		if (ob instanceof Number) return new NumberFunction((Number)ob);
		throw new IllegalArgumentException("Cannot compose a function with " + ob);
	}
}