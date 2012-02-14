package net.imglib2.script.algorithm;

import net.imglib2.IterableInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.script.algorithm.fn.ImgProxy;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

/** Integral image that stores sums with double floating-point precision.
 * Will overflow if any sum is larger than Double.MAX_VALUE.
 * 
 * @author Albert cardona
 * @see net.imglib2.algorithm.integral.IntegralImage
 */
public class IntegralImage extends ImgProxy<DoubleType>
{
	public IntegralImage(final IterableInterval<? extends RealType<?>> img) {
		super(process(img));
	}

	private static final Img<DoubleType> process(
			final IterableInterval<? extends RealType<?>> img) {
		final net.imglib2.algorithm.integral.IntegralImage<DoubleType> o =
			new net.imglib2.algorithm.integral.IntegralImage<DoubleType>(
				img, new DoubleType(), new ArrayImgFactory<DoubleType>());
		o.process();
		return o.getResult();
	}
}
