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
public class IHMax<T extends RealType<T>> implements IHUnaryFeature<T> {

	public IHMax() {}

	@Override
	public void compute(final Histogram<T> histogram, final T output) {
		for (int i=histogram.bins.length -1; i>-1; --i) {
			if (histogram.bins[i] > 0) {
				output.set(histogram.binValue(i));
				return;
			}
		}
		output.set(histogram.max);
	}
}