package net.imglib2.script.analysis;

import net.imglib2.IterableRealInterval;
import net.imglib2.script.analysis.fn.ReduceOperation;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.type.numeric.RealType;

/** Find the minimum value.
 * 
 * @see ImgMax, Reduction, ReduceFn
 * @author Albert Cardona
 */
public class ImgMin extends ReduceOperation
{
	private static final long serialVersionUID = 1L;

	public ImgMin(final IFunction fn) throws Exception {
		super(fn);
	}
	
	public ImgMin(final IterableRealInterval<? extends RealType<?>> img) throws Exception {
		super(img);
	}

	@Override
	public
	final double reduce(final double r, final double v) {
		return Math.min(r, v);
	}
}