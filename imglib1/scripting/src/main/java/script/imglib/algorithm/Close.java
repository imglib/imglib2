package script.imglib.algorithm;

import mpicbg.imglib.algorithm.roi.MorphClose;
import mpicbg.imglib.type.numeric.RealType;
import script.imglib.algorithm.fn.Morph;

/** Operates on an {@link Image} or an {@link IFunction}. */
public class Close<T extends RealType<T>> extends Morph<T>
{
	public Close(final Object fn) throws Exception {
		super(fn, MorphClose.class, Shape.CUBE, 3, 0, 0);
	}

	public Close(final Object fn, final Shape s,
			final Number shapeLength, final Number lengthDim, final Number outside) throws Exception {
		super(fn, MorphClose.class, s, shapeLength.intValue(), lengthDim.intValue(), outside.floatValue());
	}
}
