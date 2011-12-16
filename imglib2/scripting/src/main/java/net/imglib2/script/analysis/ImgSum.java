package net.imglib2.script.analysis;

import net.imglib2.IterableRealInterval;
import net.imglib2.script.analysis.fn.NumericReduceOperation;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.type.numeric.RealType;

public class ImgSum extends NumericReduceOperation
{
	private static final long serialVersionUID = 5795756184695000089L;

	public ImgSum(final IFunction fn) throws Exception {
		super(fn);
		invoke();
	}
	
	public ImgSum(final IterableRealInterval<? extends RealType<?>> img) throws Exception {
		super(img);
		invoke();
	}

	@Override
	public final double reduce(final double r, final double v) {
		return r + v;
	}
}
