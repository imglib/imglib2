package net.imglib2.script.math.fn;

import net.imglib2.IterableRealInterval;
import net.imglib2.RealInterval;
import net.imglib2.type.numeric.RealType;

public class Util
{
	@SuppressWarnings({ "unchecked", "rawtypes" })
	static public final IFunction wrap(final Object ob) {
		if (ob instanceof IterableRealInterval<?>) return new ImageFunction((IterableRealInterval<? extends RealType<?>>)ob);
		if (ob instanceof IFunction) return (IFunction)ob;
		if (ob instanceof Number) return new NumberFunction((Number)ob);
		throw new IllegalArgumentException("Cannot compose a function with " + ob);
	}
	
	static public final long[] intervalDimensions(final RealInterval iri) {
		final long[] dim = new long[iri.numDimensions()];
		for (int d=0; d<dim.length; ++d)
			dim[d] = (long) (iri.realMax(d) - iri.realMin(d) + 1);
		return dim;
	}

	public static final long size(final IterableRealInterval<?> b) {
		long size = 1;
		for (int i=b.numDimensions() -1; i > -1; --i)
			size *= (b.realMax(i) - b.realMin(i) + 1);
		return size;
	}
}