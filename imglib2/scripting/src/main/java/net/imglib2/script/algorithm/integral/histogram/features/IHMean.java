package net.imglib2.script.algorithm.integral.histogram.features;

import net.imglib2.script.algorithm.integral.histogram.Histogram;
import net.imglib2.type.numeric.RealType;

public class IHMean<T extends RealType<T>> extends IHAbstractFeature<T> {

	public IHMean() {}

	@Override
	public double get(final Histogram histogram) {
		double sum = 0;
		for (int i=0; i<histogram.bins.length; ++i) {
			sum += histogram.bins[i] * histogram.binValues[i];
		}
		return sum / histogram.nPixels;
	}
}