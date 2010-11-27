package mpicbg.imglib.scripting.algorithm;

import mpicbg.imglib.algorithm.roi.StructuringElement;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyMirrorFactory;
import mpicbg.imglib.scripting.math.Compute;
import mpicbg.imglib.scripting.math.fn.IFunction;
import mpicbg.imglib.type.numeric.RealType;
import mpicbg.imglib.type.numeric.real.DoubleType;

public class MedianFilter extends Process
{
	/** A median filter with an {@link OutOfBoundsStrategyMirrorFactory}. */
	public <T extends RealType<T>> MedianFilter(final Image<T> img, final float radius) throws Exception {
		this(img, radius, new OutOfBoundsStrategyMirrorFactory<T>());
	}

	public <T extends RealType<T>> MedianFilter(final Image<T> img, final float radius, final OutOfBoundsStrategyFactory<T> oobs) throws Exception {
		super(new mpicbg.imglib.algorithm.roi.MedianFilter<T>(img, StructuringElement.createBall(img.getNumDimensions(), radius), oobs));
	}

	public MedianFilter(final IFunction fn, final float radius) throws Exception {
		<DoubleType>this(Compute.inDoubles(fn), radius, new OutOfBoundsStrategyMirrorFactory<DoubleType>());
	}

	public MedianFilter(final IFunction fn, final float radius, final OutOfBoundsStrategyMirrorFactory<DoubleType> oobs) throws Exception {
		<DoubleType>this(Compute.inDoubles(fn), radius, oobs);
	}
}