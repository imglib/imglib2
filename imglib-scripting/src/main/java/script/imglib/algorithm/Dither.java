package script.imglib.algorithm;

import script.imglib.math.Compute;
import script.imglib.math.fn.IFunction;
import mpicbg.imglib.algorithm.OutputAlgorithm;
import mpicbg.imglib.algorithm.floydsteinberg.FloydSteinbergDithering;
import mpicbg.imglib.container.Img;
import mpicbg.imglib.type.logic.BitType;
import mpicbg.imglib.type.numeric.RealType;

/** Perform {@link FloydSteinbergDithering} on an image. */
public class Dither<T extends RealType<T>> extends ImgProxy<BitType>
{
	/** The dithering threshold is computed from the min and max values of the image;
	 *  see {@link FloydSteinbergDithering}. */
	public Dither(final Img<T> img) throws Exception {
		super(process(img).getContainer(), new BitType());
	}

	public Dither(final Img<T> img, final float ditheringThreshold) throws Exception {
		super(process(img, ditheringThreshold).getContainer(), new BitType());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Dither(final IFunction fn) throws Exception {
		this((Img)Compute.inDoubles(fn));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Dither(final IFunction fn, final float ditheringThreshold) throws Exception {
		this((Img)Compute.inDoubles(fn), ditheringThreshold);
	}

	static private final <R extends RealType<R>> Img<BitType> process(final Img<R> img, final float ditheringThreshold) throws Exception {
		return process(new FloydSteinbergDithering<R>(img, ditheringThreshold));
	}
	static private final <R extends RealType<R>> Img<BitType> process(final Img<R> img) throws Exception {
		return process(new FloydSteinbergDithering<R>(img));
	}
	static private final <R extends RealType<R>> Img<R> process(final OutputAlgorithm<R> oa) throws Exception {
		if (!oa.checkInput() || !oa.process()) {
			throw new Exception("Dither failed: " + oa.getErrorMessage());
		}
		return oa.getResult();
	}
}