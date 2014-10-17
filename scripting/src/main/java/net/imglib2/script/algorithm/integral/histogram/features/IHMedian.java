package net.imglib2.script.algorithm.integral.histogram.features;

import net.imglib2.script.algorithm.integral.histogram.Histogram;
import net.imglib2.type.numeric.RealType;

/**
 * 
 * @author Albert Cardona
 *
 * @param <T> The type of the image from which the {@link Histogram} is computed.
 * @see IHUnaryFeature
 */
public class IHMedian<T extends RealType<T>> implements IHUnaryFeature<T> {

	public IHMedian() {}

	@Override
	public void compute(final Histogram<T> histogram, final T output) {
		final long halfNPixels = histogram.nPixels / 2;
		long count = 0;
		for (int i=0; i<histogram.bins.length; ++i) {
			// Find the approximate median value
			count += histogram.bins[i];
			if (count > halfNPixels) {
				output.set(histogram.binValue(i));
				break;
			}
		}
	}
}