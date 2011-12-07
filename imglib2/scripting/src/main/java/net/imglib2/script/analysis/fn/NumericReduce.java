package net.imglib2.script.analysis.fn;

import net.imglib2.IterableRealInterval;
import net.imglib2.RealCursor;
import net.imglib2.script.math.Compute;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.type.numeric.RealType;

public abstract class NumericReduce extends NumericResult<Double>
{
	private static final long serialVersionUID = 1L;
	protected final long imgSize;

	public NumericReduce(final IFunction fn) throws Exception {
		this(Compute.inFloats(fn));
	}
	
	@SuppressWarnings("boxing")
	public NumericReduce(final IterableRealInterval<? extends RealType<?>> img) throws Exception {
		super();
		this.imgSize = img.size();
		super.d = compute(img);
	}

	private final double compute(final IterableRealInterval<? extends RealType<?>> img) throws Exception {
		final RealCursor<? extends RealType<?>> c = img.cursor();
		c.fwd();
		double r = c.get().getRealDouble();
		while (c.hasNext()) {
			c.fwd();
			r = reduce(r, c.get().getRealDouble());
		}
		return end(r);
	}

	protected double end(final double r) {
		return r;
	}

	abstract protected double reduce(double r, double v);
}
