package script.imglib.algorithm;

import script.imglib.algorithm.fn.ImgProxy;
import script.imglib.math.Compute;
import script.imglib.math.fn.IFunction;
import mpicbg.imglib.img.Img;
import mpicbg.imglib.type.numeric.RealType;
import mpicbg.imglib.type.numeric.integer.LongType;

public class HoughLineTransform<T extends RealType<T>> extends ImgProxy<LongType>
{
	/** A {@link mpicbg.imglib.algorithm.transformation.HoughLineTransform} with a LongType vote space.*/
	public HoughLineTransform(final Img<T> img) throws Exception {
		super(process(img));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public HoughLineTransform(final IFunction fn) throws Exception {
		this((Img)Compute.inDoubles(fn));
	}

	static private final <S extends RealType<S>> Img<LongType> process(final Img<S> img) throws Exception {
		mpicbg.imglib.algorithm.transformation.HoughLineTransform<LongType, S> h = 
			new mpicbg.imglib.algorithm.transformation.HoughLineTransform<LongType, S>(img, new LongType());
		if (!h.checkInput() || !h.process()) {
			throw new Exception("HoughLineTransform: " + h.getErrorMessage());
		}
		return h.getResult();
	}
}
