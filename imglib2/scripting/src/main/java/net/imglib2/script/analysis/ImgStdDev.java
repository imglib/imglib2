package net.imglib2.script.analysis;

import net.imglib2.IterableRealInterval;
import net.imglib2.script.analysis.fn.ReduceOperation;
import net.imglib2.script.math.Compute;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.type.numeric.RealType;

/** Compute the standard deviation of the image.
 * 
 * @author Albert Cardona
 */
public class ImgStdDev extends ReduceOperation
{
	private static final long serialVersionUID = 1L;
	private final double mean;

	public ImgStdDev(final IFunction fn) throws Exception {
		this(Compute.inFloats(fn));
	}
	
	public ImgStdDev(final IFunction fn, final Number mean) throws Exception {
		this(Compute.inFloats(fn), mean);
	}
	
	public ImgStdDev(final IterableRealInterval<? extends RealType<?>> img) throws Exception {
		this(img, new ImgMean(img));
	}

	public ImgStdDev(final IterableRealInterval<? extends RealType<?>> img, final Number mean) throws Exception {
		super(img);
		this.mean = mean.doubleValue();
	}

	@Override
	public
	final double reduce(final double r, final double v) {
		return r + Math.pow(v - mean, 2);
	}
	
	@Override
	public
	final double end(final double r) {
		return r / (imgSize -1);
	}
}
