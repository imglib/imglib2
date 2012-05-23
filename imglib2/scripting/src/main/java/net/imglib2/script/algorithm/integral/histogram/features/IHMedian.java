package net.imglib2.script.algorithm.integral.histogram.features;

import net.imglib2.script.algorithm.integral.histogram.Histogram;
import net.imglib2.type.numeric.RealType;

public class IHMedian<T extends RealType<T>> extends IHAbstractFeature<T> {

	public IHMedian() {}

	@Override
	public double get(final Histogram histogram) {
		final long halfNPixels = histogram.nPixels / 2;
		final double range = histogram.max - histogram.min;
		final double K = histogram.bins.length -1;
		long count = 0;
		for (int i=0; i<histogram.bins.length; ++i) {
			// Find the approximate median value
			count += histogram.bins[i];
			if (count > halfNPixels) {
				// The min
				// + the value of the current ith bin in the histogram
				// + half the size of a bin
				return histogram.min + (i / K) * range + (range / K) / 2;
			}
		}
		return 0; // never happens, histogram cannot be empty
	}
}