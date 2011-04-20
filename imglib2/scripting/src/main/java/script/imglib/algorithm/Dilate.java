package script.imglib.algorithm;

import java.util.Arrays;

import mpicbg.imglib.algorithm.roi.MorphDilate;
import mpicbg.imglib.img.Img;
import mpicbg.imglib.type.numeric.RealType;
import script.imglib.algorithm.fn.AlgorithmUtil;
import script.imglib.algorithm.fn.ImgProxy;
import script.imglib.math.Compute;

/** Operates on an {@link Image} or an {@link IFunction}. */
public class Dilate<T extends RealType<T>> extends ImgProxy<T>
{
	@SuppressWarnings("unchecked")
	public Dilate(final Object fn) throws Exception {
		super(process(asImage(fn), 3));
	}

	@SuppressWarnings("unchecked")
	public Dilate(final Object fn, final Number side) throws Exception {
		super(process(asImage(fn), side.longValue()));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	static private final Img asImage(final Object fn) throws Exception {
		if (fn instanceof Img)
			return (Img) fn;
		return Compute.inFloats(AlgorithmUtil.wrap(fn));
	}

	static private final <R extends RealType<R>> Img<R> process(final Img<R> img, final long side) throws Exception {
		final long[] cell = new long[img.numDimensions()];
		Arrays.fill(cell, side);
		return process(img, cell);
	}

	static private final <R extends RealType<R>> Img<R> process(final Img<R> img, final long[] box) throws Exception {
		MorphDilate<R> mc = new MorphDilate<R>(img, box);
		if (!mc.checkInput() || !mc.process()) throw new Exception(mc.getErrorMessage());
		return mc.getResult();
	}
}

