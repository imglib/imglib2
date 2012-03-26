package net.imglib2.script.algorithm;

import net.imglib2.algorithm.integral.IntegralImgDouble;
import net.imglib2.converter.Converter;
import net.imglib2.img.Img;
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
	public <T extends RealType<T>> IntegralImage(final Img<T> img) {
		super(process(img));
	}

	private static final <T extends RealType<T>> Img<DoubleType> process(
			final Img<T> img) {
		final IntegralImgDouble<T> o =
			new IntegralImgDouble<T>(img, new DoubleType(),
				new Converter<T, DoubleType>() {
					@Override
					public final void convert(final T input, final DoubleType output) {
						output.set(input.getRealDouble());
					}
				});
		o.process();
		return o.getResult();
	}
}
