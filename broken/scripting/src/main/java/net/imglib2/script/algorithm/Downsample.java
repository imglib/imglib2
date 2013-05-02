
package net.imglib2.script.algorithm;

import net.imglib2.script.algorithm.fn.ImgProxy;
import net.imglib2.script.math.Compute;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;

/**
 * TODO
 *
 */
public class Downsample<T extends RealType<T>> extends ImgProxy<T> {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Downsample(final IFunction fn, final float factor) throws Exception {
		this((Img)Compute.inFloats(fn), factor);
	}

	public Downsample(final Img<T> img, final float factor) throws Exception {
		super(create(img, factor));
	}

	static private final <R extends RealType<R>> Img<R> create(final Img<R> img, final float factor) throws Exception {
		final net.imglib2.algorithm.gauss.DownSample<R> ds = new net.imglib2.algorithm.gauss.DownSample<R>(img, factor);
		if (!ds.checkInput() || !ds.process()) {
			throw new Exception("Downsampling error: " + ds.getErrorMessage());
		}
		return ds.getResult();
	}
}
