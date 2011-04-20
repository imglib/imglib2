package script.imglib.algorithm;

import script.imglib.algorithm.fn.ImgProxy;
import script.imglib.math.Compute;
import script.imglib.math.fn.IFunction;
import mpicbg.imglib.img.Img;
import mpicbg.imglib.type.numeric.RealType;

/** A bandpass filter. */
public class BandpassFilter<T extends RealType<T>> extends ImgProxy<T>
{
	public BandpassFilter(final Img<T> img, final int beginRadius, final int endRadius) throws Exception {
		super(process(img, beginRadius, endRadius));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public BandpassFilter(final IFunction fn, final int beginRadius, final int endRadius) throws Exception {
		this((Img)Compute.inDoubles(fn), beginRadius, endRadius);
	}

	static private final <T extends RealType<T>> Img<T> process(final Img<T> img, final int beginRadius, final int endRadius) throws Exception {
		mpicbg.imglib.algorithm.fft.Bandpass<T> bp = new mpicbg.imglib.algorithm.fft.Bandpass<T>(img, beginRadius, endRadius);
		if (!bp.checkInput() || !bp.process()) {
			throw new Exception(bp.getClass().getSimpleName() + " failed: " + bp.getErrorMessage());
		}
		return bp.getResult();
	}
}
