package script.imglib.algorithm;

import mpicbg.imglib.algorithm.roi.MorphDilate;
import mpicbg.imglib.type.numeric.RealType;
import script.imglib.algorithm.fn.Morph;

/** Operates on an {@link Image} or an {@link IFunction}. */
public class Dilate<T extends RealType<T>> extends Morph<T>
{
	public Dilate(final Object fn) throws Exception {
		super(fn, MorphDilate.class, Shape.CUBE, 3, 0, 0);
	}

	public Dilate(final Object fn, final Shape s, final Number shapeLength,
			final Number lengthDim, final Number outside) throws Exception {
		super(fn, MorphDilate.class, s, shapeLength.intValue(), lengthDim.intValue(), outside.floatValue());
	}
}
