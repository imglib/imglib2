package net.imglib2.script.algorithm.integral.features;

import net.imglib2.type.numeric.RealType;

public class IHMean<T extends RealType<T>> extends IHAbstractFeature<T> {

	public IHMean() {}

	@Override
	public double get(final double min, final double max,
			final long[] histogram, final double[] binValues, long nPixels) {
		double sum = 0;
		for (int i=0; i<histogram.length; ++i) {
			sum += histogram[i] * binValues[i];
		}
		return sum / nPixels;
	}
}