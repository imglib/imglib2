package script.imglib.algorithm;

import script.imglib.math.Compute;
import script.imglib.math.fn.IFunction;
import mpicbg.imglib.algorithm.OutputAlgorithm;
import mpicbg.imglib.algorithm.floydsteinberg.FloydSteinbergDithering;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.logic.BitType;
import mpicbg.imglib.type.numeric.RealType;

/** Perform {@link FloydSteinbergDithering} on an image. */
public class Dither<T extends RealType<T>> extends Image<BitType>
{
	/** The dithering threshold is computed from the min and max values of the image;
	 *  see {@link FloydSteinbergDithering}. */
	public Dither(final Image<T> img) throws Exception {
		super(process(img).getContainer(), new BitType());
	}

	public Dither(final Image<T> img, final float ditheringThreshold) throws Exception {
		super(process(img, ditheringThreshold).getContainer(), new BitType());
	}

	@SuppressWarnings("unchecked")
	public Dither(final IFunction fn) throws Exception {
		this((Image)Compute.inDoubles(fn));
	}

	@SuppressWarnings("unchecked")
	public Dither(final IFunction fn, final float ditheringThreshold) throws Exception {
		this((Image)Compute.inDoubles(fn), ditheringThreshold);
	}

	static private final <R extends RealType<R>> Image<BitType> process(final Image<R> img, final float ditheringThreshold) throws Exception {
		return process(new FloydSteinbergDithering<R>(img, ditheringThreshold));
	}
	static private final <R extends RealType<R>> Image<BitType> process(final Image<R> img) throws Exception {
		return process(new FloydSteinbergDithering<R>(img));
	}
	static private final <R extends RealType<R>> Image<R> process(final OutputAlgorithm<R> oa) throws Exception {
		if (!oa.checkInput() || !oa.process()) {
			throw new Exception("Dither failed: " + oa.getErrorMessage());
		}
		return oa.getResult();
	}
}
