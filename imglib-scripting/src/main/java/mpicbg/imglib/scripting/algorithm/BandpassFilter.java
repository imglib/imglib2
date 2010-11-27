package mpicbg.imglib.scripting.algorithm;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.math.Compute;
import mpicbg.imglib.scripting.math.fn.IFunction;
import mpicbg.imglib.type.numeric.RealType;
import mpicbg.imglib.type.numeric.real.DoubleType;

/** A bandpass filter. */
public class BandpassFilter extends Process
{
	public <T extends RealType<T>> BandpassFilter(final Image<T> img, final int beginRadius, final int endRadius) throws Exception {
		super(new mpicbg.imglib.algorithm.fft.Bandpass<T>(img, beginRadius, endRadius));
	}

	public BandpassFilter(final IFunction fn, final int beginRadius, final int endRadius) throws Exception {
		<DoubleType>this(Compute.inDoubles(fn), beginRadius, endRadius);
	}
}