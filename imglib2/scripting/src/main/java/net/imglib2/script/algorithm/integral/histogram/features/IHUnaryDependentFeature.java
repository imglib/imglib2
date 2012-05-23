package net.imglib2.script.algorithm.integral.histogram.features;

import net.imglib2.script.algorithm.integral.histogram.Histogram;

public interface IHUnaryDependentFeature<T> extends IHUnaryFeature<T>
{
	public double get(Histogram histogram, double value);
}
