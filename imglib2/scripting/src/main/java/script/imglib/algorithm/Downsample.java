package script.imglib.algorithm;

import script.imglib.algorithm.fn.ImgProxy;
import script.imglib.math.Compute;
import script.imglib.math.fn.IFunction;
import mpicbg.imglib.img.Img;
import mpicbg.imglib.type.numeric.RealType;

public class Downsample<T extends RealType<T>> extends ImgProxy<T> {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Downsample(final IFunction fn, final float factor) throws Exception {
		this((Img)Compute.inFloats(fn), factor);
	}

	public Downsample(final Img<T> img, final float factor) throws Exception {
		super(create(img, factor));
	}

	static private final <R extends RealType<R>> Img<R> create(final Img<R> img, final float factor) throws Exception {
		final mpicbg.imglib.algorithm.gauss.DownSample<R> ds = new mpicbg.imglib.algorithm.gauss.DownSample<R>(img, factor);
		if (!ds.checkInput() || !ds.process()) {
			throw new Exception("Downsampling error: " + ds.getErrorMessage());
		}
		return ds.getResult();
	}
}
