package net.imglib2.script.algorithm.integral.features;

import net.imglib2.type.numeric.RealType;

public class IHMin<T extends RealType<T>> extends IHAbstractFeature<T> {

	public IHMin() {}

	@Override
	public double get(final double min, final double max,
			final long[] histogram, final double[] binValues, final long nPixels) {
		for (int i=0; i<histogram.length; ++i) {
			if (histogram[i] > 0) {
				return binValues[i];
			}
		}
		return min;
	}
}
