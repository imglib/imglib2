package net.imglib2.script.algorithm.integral.features;

import net.imglib2.type.numeric.RealType;

public class IHMax<T extends RealType<T>> extends IHAbstractFeature<T> {

	public IHMax() {}

	@Override
	public double get(final double min, final double max, final long[] histogram, final double[] binValues, long nPixels) {
		for (int i=histogram.length -1; i>-1; --i) {
			if (histogram[i] > 0) {
				return binValues[i];
			}
		}
		return max;
	}
}