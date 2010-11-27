package mpicbg.imglib.scripting.algorithm;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.math.Compute;
import mpicbg.imglib.scripting.math.fn.IFunction;
import mpicbg.imglib.type.numeric.RealType;
import mpicbg.imglib.type.numeric.integer.LongType;
import mpicbg.imglib.type.numeric.real.DoubleType;

public class HoughLineTransform extends Process
{
	/** A {@link mpicbg.imglib.algorithm.transformation.HoughLineTransform} with a LongType vote space.*/
	public <T extends RealType<T>> HoughLineTransform(final Image<T> img) throws Exception {
		super(new mpicbg.imglib.algorithm.transformation.HoughLineTransform<LongType, T>(img, new LongType()));
	}

	public HoughLineTransform(final IFunction fn) throws Exception {
		<DoubleType>this(Compute.inDoubles(fn));
	}
}