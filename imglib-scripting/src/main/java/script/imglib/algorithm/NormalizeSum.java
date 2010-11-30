package script.imglib.algorithm;

import script.imglib.math.Compute;
import script.imglib.math.fn.IFunction;
import mpicbg.imglib.algorithm.math.NormalizeImageFloat;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.RealType;
import mpicbg.imglib.type.numeric.real.FloatType;

public class NormalizeSum<T extends RealType<T>> extends Image<FloatType>
{
	public NormalizeSum(final Image<T> img) throws Exception {
		super(process(img).getContainer(), new FloatType());
	}

	@SuppressWarnings("unchecked")
	public NormalizeSum(final IFunction fn) throws Exception {
		this((Image)Compute.inFloats(fn));
	}

	static private final <R extends RealType<R>> Image<FloatType> process(final Image<R> img) throws Exception {
		NormalizeImageFloat<R> nir = new NormalizeImageFloat<R>(img);
		if (!nir.checkInput() || !nir.process()) {
			throw new Exception("Normalize: " + nir.getErrorMessage());
		}
		return nir.getResult();
	}
}
