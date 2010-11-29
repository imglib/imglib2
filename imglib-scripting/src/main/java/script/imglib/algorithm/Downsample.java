package script.imglib.algorithm;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.RealType;

public class Downsample<T extends RealType<T>> extends Image<T> {

	public Downsample(final Image<T> img, final float factor) throws Exception {
		super(create(img, factor).getContainer(), img.createType(), "Downsampled");
	}

	static private final <R extends RealType<R>> Image<R> create(final Image<R> img, final float factor) throws Exception {
		final mpicbg.imglib.algorithm.gauss.DownSample<R> ds = new mpicbg.imglib.algorithm.gauss.DownSample<R>(img, factor);
		if (!ds.checkInput() || !ds.process()) {
			throw new Exception("Downsampling error: " + ds.getErrorMessage());
		}
		return ds.getResult();
	}
}