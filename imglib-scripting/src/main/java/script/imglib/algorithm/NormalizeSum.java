package script.imglib.algorithm;

import script.imglib.algorithm.fn.ImgProxy;
import script.imglib.math.Compute;
import script.imglib.math.fn.IFunction;
import mpicbg.imglib.algorithm.math.NormalizeImageFloat;
import mpicbg.imglib.img.Img;
import mpicbg.imglib.type.numeric.RealType;
import mpicbg.imglib.type.numeric.real.FloatType;

public class NormalizeSum<T extends RealType<T>> extends ImgProxy<FloatType>
{
	public NormalizeSum(final Img<T> img) throws Exception {
		super(process(img));
	}

	@SuppressWarnings("unchecked")
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
