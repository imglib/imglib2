package net.imglib2.script.algorithm.integral.features;

import net.imglib2.type.numeric.RealType;

public class IHStdDev<T extends RealType<T>> extends IHAbstractFeature<T> {

	public IHStdDev() {}

	@Override
	public double get(final double min, final double max,
			final long[] histogram, final double[] binValues,
			final long nPixels, final double median) {
		double s = 0;
		for (int i=0; i<histogram.length; ++i) {
			s += Math.pow((binValues[i] - median), 2) * histogram[i];
		}
		return Math.sqrt(s / nPixels);
	}

	@Override
	public double get(double min, double max, long[] bins, double[] binValues,
			long nPixels) {
		// Could compute it, but the point is to avoid doing so
		throw new UnsupportedOperationException("Must define median!");
	}
}