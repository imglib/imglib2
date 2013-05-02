
package net.imglib2.script.algorithm;

import net.imglib2.script.algorithm.fn.ImgProxy;
import net.imglib2.script.math.Compute;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.algorithm.math.NormalizeImageFloat;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;

/**
 * TODO
 *
 */
public class NormalizeSum<T extends RealType<T>> extends ImgProxy<FloatType>
{
	public NormalizeSum(final Img<T> img) throws Exception {
		super(process(img));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public NormalizeSum(final IFunction fn) throws Exception {
		this((Img)Compute.inFloats(fn));
	}

	static private final <R extends RealType<R>> Img<FloatType> process(final Img<R> img) throws Exception {
		NormalizeImageFloat<R> nir = new NormalizeImageFloat<R>(img);
		if (!nir.checkInput() || !nir.process()) {
			throw new Exception("Normalize: " + nir.getErrorMessage());
		}
		return nir.getResult();
	}
}
