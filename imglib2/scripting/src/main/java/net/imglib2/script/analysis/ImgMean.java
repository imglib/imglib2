package net.imglib2.script.analysis;

import net.imglib2.IterableRealInterval;
import net.imglib2.script.analysis.fn.NumericReduce;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.type.numeric.RealType;

/** Add all values and when done divide by the total number of values.
 * 
 * @see ImgIterativeMean
 * @author Albert Cardona
 */
public class ImgMean extends NumericReduce
{
	private static final long serialVersionUID = 1L;

	public ImgMean(final IFunction fn) throws Exception {
		super(fn);
	}
	
	public ImgMean(final IterableRealInterval<? extends RealType<?>> img) throws Exception {
		super(img);
	}

	@Override
	protected final double reduce(final double r, final double v) {
		return r + v;
	}
	
	@Override
	protected final double end(final double r) {
		return r / imgSize;
	}
}
