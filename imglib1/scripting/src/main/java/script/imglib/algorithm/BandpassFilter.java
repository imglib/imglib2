package script.imglib.algorithm;

import script.imglib.math.Compute;
import script.imglib.math.fn.IFunction;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.RealType;

/** A bandpass filter. */
public class BandpassFilter<T extends RealType<T>> extends Image<T>
{
	public BandpassFilter(final Image<T> img, final int beginRadius, final int endRadius) throws Exception {
		super(process(img, beginRadius, endRadius).getContainer(), img.createType(), "Bandpass");
	}

	@SuppressWarnings("unchecked")
	public BandpassFilter(final IFunction fn, final int beginRadius, final int endRadius) throws Exception {
		this((Image)Compute.inDoubles(fn), beginRadius, endRadius);
	}

	static private final <T extends RealType<T>> Image<T> process(final Image<T> img, final int beginRadius, final int endRadius) throws Exception {
		mpicbg.imglib.algorithm.fft.Bandpass<T> bp = new mpicbg.imglib.algorithm.fft.Bandpass<T>(img, beginRadius, endRadius);
		if (!bp.checkInput() || !bp.process()) {
			throw new Exception(bp.getClass().getSimpleName() + " failed: " + bp.getErrorMessage());
		}
		return bp.getResult();
	}
}
