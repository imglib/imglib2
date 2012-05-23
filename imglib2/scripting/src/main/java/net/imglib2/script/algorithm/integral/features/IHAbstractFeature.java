package net.imglib2.script.algorithm.integral.features;

import net.imglib2.type.numeric.RealType;

public abstract class IHAbstractFeature<T extends RealType<T>> implements IHFeature<T>
{
	@Override
	public void get(final double min, final double max,
			final long[] histogram, final double[] binValues, final long nPixels, final T output) {
		output.setReal(get(min, max, histogram, binValues, nPixels));
	}

	@Override
	public double get(double min, double max, long[] bins, double[] binValues, long nPixels, double other) {
		return get(min, max, bins, binValues, nPixels);
	}
	
	@Override
	public void get(double min, double max, long[] histogram, double[] binValues, long nPixels, double other, T output) {
		get(min, max, histogram, binValues, nPixels, output);
	}
}
