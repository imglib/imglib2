package net.imglib2.script.algorithm;

import net.imglib2.script.algorithm.fn.ImgProxy;
import net.imglib2.script.math.Compute;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;

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
		net.imglib2.algorithm.fft.Bandpass<T> bp = new net.imglib2.algorithm.fft.Bandpass<T>(img, beginRadius, endRadius);
		if (!bp.checkInput() || !bp.process()) {
			throw new Exception(bp.getClass().getSimpleName() + " failed: " + bp.getErrorMessage());
		}
		return bp.getResult();
	}
}
