package mpicbg.imglib.scripting.algorithm;

import mpicbg.imglib.algorithm.math.NormalizeImageFloat;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.math.Compute;
import mpicbg.imglib.scripting.math.fn.IFunction;
import mpicbg.imglib.type.numeric.RealType;
import mpicbg.imglib.type.numeric.real.DoubleType;

public class Normalize extends Process
{
	public <T extends RealType<T>> Normalize(final Image<T> img) throws Exception {
		super(new NormalizeImageFloat<T>(img));
	}

	public Normalize(final IFunction fn) throws Exception {
		<DoubleType>this(Compute.inDoubles(fn));
	}
}
