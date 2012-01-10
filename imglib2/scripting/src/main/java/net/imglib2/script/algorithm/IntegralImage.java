package net.imglib2.script.algorithm;

import net.imglib2.IterableInterval;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

/** Integral image that stores sums with double floating-point precision.
 * Will overflow if any sum is larger than Double.MAX_VALUE.
 * 
 * @author Albert cardona
 */
public class IntegralImage extends TypedIntegralImage<DoubleType>
{
	public <R extends RealType<R>> IntegralImage(final IterableInterval<R> img) {
		super(img, new DoubleType());
	}
}
