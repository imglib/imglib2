package net.imglib2.script.algorithm.integral.features;

import net.imglib2.type.numeric.RealType;

public abstract class IHAbstractFeature<T extends RealType<T>> implements IHFeature<T>
{
	@Override
	public void get(final double min, final double max,
			final long[] histogram, final double[] binValues, final long nPixels, final T output) {
		output.setReal(get(min, max, histogram, binValues, nPixels));
	}
}
