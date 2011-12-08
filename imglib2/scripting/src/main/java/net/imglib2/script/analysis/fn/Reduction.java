package net.imglib2.script.analysis.fn;

import net.imglib2.IterableRealInterval;
import net.imglib2.RealCursor;
import net.imglib2.type.numeric.RealType;

public class Reduction extends NumericResult<Double>
{
	private static final long serialVersionUID = 1435418604719008040L;

	@SuppressWarnings("boxing")
	public Reduction(final IterableRealInterval<? extends RealType<?>> img,  final ReduceFn fn) {
		super(compute(img, fn));
	}
	
	static public final double compute(final IterableRealInterval<? extends RealType<?>> img,  final ReduceFn fn) {
		final RealCursor<? extends RealType<?>> c = img.cursor();
		Double first = fn.initial();
		double r;
		if (null == first) {
			c.fwd();
			r = c.get().getRealDouble();
		} else {
			r = first.doubleValue();
		}
		while (c.hasNext()) {
			c.fwd();
			r = fn.reduce(r, c.get().getRealDouble());
		}
		return fn.end(r);
	}
}
