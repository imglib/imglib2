package net.imglib2.script.algorithm.integral.histogram.features;

import net.imglib2.script.algorithm.integral.histogram.Histogram;
import net.imglib2.type.numeric.RealType;

public abstract class IHAbstractUnitaryDependentFeature<T extends RealType<T>> implements
		IHUnaryDependentFeature<T> {
	@Override
	public void get(Histogram histogram, T output, double value) {
		output.setReal(get(histogram, value));
	}
}
