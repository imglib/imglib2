package script.imglib.algorithm;

import java.util.Arrays;

import script.imglib.algorithm.fn.ImgProxy;
import script.imglib.math.Compute;
import script.imglib.math.fn.IFunction;
import mpicbg.imglib.img.Img;
import mpicbg.imglib.outofbounds.OutOfBoundsFactory;
import mpicbg.imglib.outofbounds.OutOfBoundsMirrorFactory;
import mpicbg.imglib.type.numeric.RealType;

public class MedianFilter<T extends RealType<T>> extends ImgProxy<T>
{
	/** A median filter with an {@link OutOfBoundsStrategyMirrorFactory}. */
	public MedianFilter(final Img<T> img, final Number radius) throws Exception {
		this(img, radius, new OutOfBoundsMirrorFactory<T,Img<T>>(OutOfBoundsMirrorFactory.Boundary.SINGLE));
	}

	public MedianFilter(final Img<T> img, final Number radius, final OutOfBoundsFactory<T,Img<T>> oobs) throws Exception {
		super(process(img, radius, oobs));
	}

	/** A median filter with an {@link OutOfBoundsStrategyMirrorFactory}. */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public MedianFilter(final IFunction fn, final Number radius) throws Exception {
		this((Img)Compute.inDoubles(fn), radius, new OutOfBoundsMirrorFactory<T,Img<T>>(OutOfBoundsMirrorFactory.Boundary.SINGLE));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public MedianFilter(final IFunction fn, final Number radius, final OutOfBoundsFactory<T,Img<T>> oobs) throws Exception {
		this((Img)Compute.inDoubles(fn), radius, oobs);
	}

	static private final <S extends RealType<S>> Img<S> process(final Img<S> img, final Number radius, final OutOfBoundsFactory<S,Img<S>> oobs) throws Exception {
		long[] sides = new long[img.numDimensions()];
		Arrays.fill(sides, radius.longValue());
		final mpicbg.imglib.algorithm.roi.MedianFilter<S> mf = new mpicbg.imglib.algorithm.roi.MedianFilter<S>(img, sides, oobs);
		if (!mf.process()) {
			throw new Exception("MedianFilter: " + mf.getErrorMessage());
		}
		return mf.getResult();
	}
}
