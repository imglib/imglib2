package net.imglib2.script.algorithm.integral.histogram.features;

import net.imglib2.script.algorithm.integral.histogram.Histogram;
import net.imglib2.type.numeric.RealType;

public class IHMin<T extends RealType<T>> extends IHAbstractFeature<T> {

	public IHMin() {}

	@Override
	public double get(final Histogram histogram) {
		for (int i=0; i<histogram.bins.length; ++i) {
			if (histogram.bins[i] > 0) {
				return histogram.binValues[i];
			}
		}
		return histogram.min;
	}
}
