package net.imglib2.script.algorithm.integral.features;

import net.imglib2.type.numeric.RealType;

public class IHMedian<T extends RealType<T>> extends IHAbstractFeature<T> {

	public IHMedian() {}

	@Override
	public double get(final double min, final double max,
			final long[] histogram, final double[] binValues, long nPixels) {
		final long halfNPixels = nPixels / 2;
		final double range = max - min;
		final double K = histogram.length -1;
		long count = 0;
		for (int i=0; i<histogram.length; ++i) {
			// Find the approximate median value
			count += histogram[i];
			if (count > halfNPixels) {
				// The min
				// + the value of the current ith bin in the histogram
				// + half the size of a bin
				return min + (i / K) * range + (range / K) / 2;
			}
		}
		return 0; // never happens, histogram cannot be empty
	}
}