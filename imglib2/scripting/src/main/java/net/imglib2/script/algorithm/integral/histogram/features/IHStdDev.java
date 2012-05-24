package net.imglib2.script.algorithm.integral.histogram.features;

import net.imglib2.script.algorithm.integral.histogram.Histogram;
import net.imglib2.type.numeric.RealType;

public class IHStdDev<T extends RealType<T>> extends IHAbstractUnitaryDependentFeature<T> {

	public IHStdDev() {}

	@Override
	public double get(final Histogram histogram, final double median) {
		double s = 0;
		for (int i=0; i<histogram.bins.length; ++i) {
			s += Math.pow((histogram.binValues[i] - median), 2) * histogram.bins[i];
		}
		// If nBins is a power of 2, and nPixels as well, then we could do bit shifting instead
		// but seems like a silly optimization
		return Math.sqrt(s / histogram.nPixels);
	}
}