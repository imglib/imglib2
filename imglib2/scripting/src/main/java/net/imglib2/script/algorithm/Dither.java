package net.imglib2.script.algorithm;

import net.imglib2.script.algorithm.fn.ImgProxy;
import net.imglib2.script.math.Compute;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.algorithm.floydsteinberg.FloydSteinbergDithering;
import net.imglib2.img.Img;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;

/** Perform {@link FloydSteinbergDithering} on an image. */
public class Dither<T extends RealType<T>> extends ImgProxy<BitType>
{
	/** The dithering threshold is computed from the min and max values of the image;
	 *  see {@link FloydSteinbergDithering}. */
	public Dither(final Img<T> img) throws Exception {
		super(process(img));
	}

	public Dither(final Img<T> img, final float ditheringThreshold) throws Exception {
		super(process(img, ditheringThreshold));
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
	static private final <R extends RealType<R>> Img<R> process(final OutputAlgorithm<Img<R>> oa) throws Exception {
		if (!oa.checkInput() || !oa.process()) {
			throw new Exception("Dither failed: " + oa.getErrorMessage());
		}
		return oa.getResult();
	}
}
