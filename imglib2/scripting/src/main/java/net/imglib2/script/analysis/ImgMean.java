package net.imglib2.script.analysis;

import net.imglib2.IterableRealInterval;
import net.imglib2.script.analysis.fn.NumericReduceOperation;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.type.numeric.RealType;

/** Add all values and when done divide by the total number of values.
 * 
 * @see ImgIterativeMean, Reduction, ReduceFn
 * @author Albert Cardona
 */
public final class ImgMean extends NumericReduceOperation
{
	private static final long serialVersionUID = 1L;

	public ImgMean(final IFunction fn) throws Exception {
		super(fn);
		invoke();
	}
	
	public ImgMean(final IterableRealInterval<? extends RealType<?>> img) throws Exception {
		super(img);
		invoke();
	}

	@Override
	public final double reduce(final double r, final double v) {
		return r + v;
	}
	
	@Override
	public final double end(final double r) {
		return r / imgSize;
	}
}
