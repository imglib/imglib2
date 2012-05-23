package net.imglib2.script.algorithm.integral.histogram.features;

import net.imglib2.script.algorithm.integral.histogram.Histogram;
import net.imglib2.type.numeric.RealType;

public class IHMax<T extends RealType<T>> extends IHAbstractFeature<T> {

	public IHMax() {}

	@Override
	public double get(final Histogram histogram) {
		for (int i=histogram.bins.length -1; i>-1; --i) {
			if (histogram.bins[i] > 0) {
				return histogram.binValues[i];
			}
		}
		return histogram.max;
	}
}