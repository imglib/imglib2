package mpicbg.imglib.scripting.algorithm;

import mpicbg.imglib.algorithm.math.NormalizeImageFloat;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.math.Compute;
import mpicbg.imglib.scripting.math.fn.IFunction;
import mpicbg.imglib.type.numeric.RealType;
import mpicbg.imglib.type.numeric.real.FloatType;

public class Normalize<T extends RealType<T>> extends Image<FloatType>
{
	public Normalize(final Image<T> img) throws Exception {
		super(process(img).getContainer(), new FloatType());
	}

	@SuppressWarnings("unchecked")
	public Normalize(final IFunction fn) throws Exception {
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
