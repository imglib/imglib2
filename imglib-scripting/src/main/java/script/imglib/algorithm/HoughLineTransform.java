package script.imglib.algorithm;

import script.imglib.math.Compute;
import script.imglib.math.fn.IFunction;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.RealType;
import mpicbg.imglib.type.numeric.integer.LongType;

public class HoughLineTransform<T extends RealType<T>> extends Image<LongType>
{
	/** A {@link mpicbg.imglib.algorithm.transformation.HoughLineTransform} with a LongType vote space.*/
	public HoughLineTransform(final Image<T> img) throws Exception {
		super(process(img).getContainer(), new LongType());
	}

	@SuppressWarnings("unchecked")
	public HoughLineTransform(final IFunction fn) throws Exception {
		this((Image)Compute.inDoubles(fn));
	}

	static private final <S extends RealType<S>> Image<LongType> process(final Image<S> img) throws Exception {
		mpicbg.imglib.algorithm.transformation.HoughLineTransform<LongType, S> h = 
			new mpicbg.imglib.algorithm.transformation.HoughLineTransform<LongType, S>(img, new LongType());
		if (!h.checkInput() || !h.process()) {
			throw new Exception("HoughLineTransform: " + h.getErrorMessage());
		}
		return h.getResult();
	}
}