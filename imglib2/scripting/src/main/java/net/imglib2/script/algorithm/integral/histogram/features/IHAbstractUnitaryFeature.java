package net.imglib2.script.algorithm.integral.histogram.features;

import net.imglib2.script.algorithm.integral.histogram.Histogram;
import net.imglib2.type.numeric.RealType;

public abstract class IHAbstractUnitaryFeature<T extends RealType<T>> implements IHUnaryFeature<T>
{
	@Override
	public void get(final Histogram histogram, final T output) {
		output.setReal(get(histogram));
	}
}
