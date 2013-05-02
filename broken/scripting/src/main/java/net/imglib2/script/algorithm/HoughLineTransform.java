
package net.imglib2.script.algorithm;

import net.imglib2.script.algorithm.fn.ImgProxy;
import net.imglib2.script.math.Compute;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.LongType;

/**
 * TODO
 *
 */
public class HoughLineTransform<T extends RealType<T>> extends ImgProxy<LongType>
{
	/** A {@link net.imglib2.algorithm.transformation.HoughLineTransform} with a LongType vote space.*/
	public HoughLineTransform(final Img<T> img) throws Exception {
		super(process(img));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public HoughLineTransform(final IFunction fn) throws Exception {
		this((Img)Compute.inDoubles(fn));
	}

	static private final <S extends RealType<S>> Img<LongType> process(final Img<S> img) throws Exception {
		net.imglib2.algorithm.transformation.HoughLineTransform<LongType, S> h = 
			new net.imglib2.algorithm.transformation.HoughLineTransform<LongType, S>(img, new LongType());
		if (!h.checkInput() || !h.process()) {
			throw new Exception("HoughLineTransform: " + h.getErrorMessage());
		}
		return h.getResult();
	}
}
