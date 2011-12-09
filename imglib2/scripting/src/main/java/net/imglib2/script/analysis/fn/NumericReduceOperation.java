package net.imglib2.script.analysis.fn;

import net.imglib2.IterableRealInterval;
import net.imglib2.script.analysis.ReduceFn;
import net.imglib2.script.analysis.Reduction;
import net.imglib2.script.math.Compute;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.type.numeric.RealType;

/** Call {@link #run()} to execute.
 * 
 * @author Albert Cardona
 *
 */
public abstract class NumericReduceOperation extends NumericResult<Double> implements ReduceFn
{
	private static final long serialVersionUID = 1L;
	protected final long imgSize;
	protected final IterableRealInterval<? extends RealType<?>> img;

	public NumericReduceOperation(final IFunction fn) throws Exception {
		this(Compute.inFloats(fn));
	}

	public NumericReduceOperation(final IterableRealInterval<? extends RealType<?>> img) throws Exception {
		super();
		this.img = img;
		this.imgSize = img.size();
	}

	@SuppressWarnings("boxing")
	protected void invoke() {
		super.d = Reduction.compute(img, this);
	}

	// Reasonable default
	@Override
	public Double initial() {
		return null; // use the first value
	}

	// Reasonable default
	@Override
	public double end(final double r) {
		return r;
	}
}
