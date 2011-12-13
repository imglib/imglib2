package net.imglib2.script.analysis;

import net.imglib2.IterableRealInterval;
import net.imglib2.RealCursor;
import net.imglib2.script.analysis.fn.NumericResult;
import net.imglib2.type.numeric.RealType;

public class Reduction extends NumericResult<Double>
{
	private static final long serialVersionUID = 1435418604719008040L;

	@SuppressWarnings("boxing")
	public Reduction(final IterableRealInterval<? extends RealType<?>> img,  final ReduceFn fn) {
		super(reduce(img, fn));
	}

	static public final double reduce(final IterableRealInterval<? extends RealType<?>> img,  final ReduceFn fn) {
		final RealCursor<? extends RealType<?>> c = img.cursor();
		Double initial = fn.initial();
		double r;
		if (null == initial) {
			c.fwd();
			r = c.get().getRealDouble();
		} else {
			r = initial.doubleValue();
		}
		while (c.hasNext()) {
			c.fwd();
			r = fn.reduce(r, c.get().getRealDouble());
		}
		return fn.end(r);
	}
}
