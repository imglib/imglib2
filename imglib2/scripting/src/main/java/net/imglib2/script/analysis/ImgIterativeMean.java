package net.imglib2.script.analysis;

import net.imglib2.IterableRealInterval;
import net.imglib2.script.analysis.fn.ReduceOperation;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.type.numeric.RealType;

/** Add every value divided by the total number of values, to avoid overflow.
 * 
 * @see ImgMean, Reduction, ReduceFn
 * @author Albert Cardona
 */
public class ImgIterativeMean extends ReduceOperation
{
	private static final long serialVersionUID = 1L;

	public ImgIterativeMean(final IFunction fn) throws Exception {
		super(fn);
	}
	
	public ImgIterativeMean(final IterableRealInterval<? extends RealType<?>> img) throws Exception {
		super(img);
	}

	@Override
	public
	final double reduce(final double r, final double v) {
		return r + v / imgSize;
	}
}
