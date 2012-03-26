package script.imglib.algorithm;

import script.imglib.math.Compute;
import script.imglib.math.fn.IFunction;
import mpicbg.imglib.algorithm.roi.StructuringElement;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyMirrorFactory;
import mpicbg.imglib.type.numeric.RealType;

public class MedianFilter<T extends RealType<T>> extends Image<T>
{
	/** A median filter with an {@link OutOfBoundsStrategyMirrorFactory}. */
	public MedianFilter(final Image<T> img, final float radius) throws Exception {
		this(img, radius, new OutOfBoundsStrategyMirrorFactory<T>());
	}

	public MedianFilter(final Image<T> img, final float radius, final OutOfBoundsStrategyFactory<T> oobs) throws Exception {
		super(process(img, radius, oobs).getContainer(), img.createType());
	}

	/** A median filter with an {@link OutOfBoundsStrategyMirrorFactory}. */
	@SuppressWarnings("unchecked")
	public MedianFilter(final IFunction fn, final float radius) throws Exception {
		this((Image)Compute.inDoubles(fn), radius, new OutOfBoundsStrategyMirrorFactory<T>());
	}

	@SuppressWarnings("unchecked")
	public MedianFilter(final IFunction fn, final float radius, final OutOfBoundsStrategyFactory<T> oobs) throws Exception {
		this((Image)Compute.inDoubles(fn), radius, oobs);
	}

	static private final <S extends RealType<S>> Image<S> process(final Image<S> img, final float radius, final OutOfBoundsStrategyFactory<S> oobs) throws Exception {
		final mpicbg.imglib.algorithm.roi.MedianFilter<S> mf =
			new mpicbg.imglib.algorithm.roi.MedianFilter<S>(img, StructuringElement.createBall(img.getNumDimensions(), radius), oobs);
		// TODO: mf.checkInput() returns false even if the image is processed fine.
		if (!mf.process()) {
			throw new Exception("MedianFilter: " + mf.getErrorMessage());
		}
		return mf.getResult();
	}
}
