package net.imglib2.script.analysis.fn;

import net.imglib2.IterableRealInterval;
import net.imglib2.script.math.Compute;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.type.numeric.RealType;

public abstract class ReduceOperation extends NumericResult<Double> implements ReduceFn
{
	private static final long serialVersionUID = 1L;
	protected final long imgSize;

	public ReduceOperation(final IFunction fn) throws Exception {
		this(Compute.inFloats(fn));
	}
	
	@SuppressWarnings("boxing")
	public ReduceOperation(final IterableRealInterval<? extends RealType<?>> img) throws Exception {
		super();
		this.imgSize = img.size();
		super.d = new Reduction(img, this).doubleValue();
	}
	
	@Override
	public Double initial() {
		return null; // use the first value
	}

	@Override
	public double end(final double r) {
		return r;
	}
}
