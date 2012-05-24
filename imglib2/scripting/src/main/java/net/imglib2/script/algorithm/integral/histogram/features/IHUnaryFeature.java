package net.imglib2.script.algorithm.integral.histogram.features;

import net.imglib2.script.algorithm.integral.histogram.Histogram;

public interface IHUnaryFeature<T>
{
	public double get(Histogram histogram);
	
	public void get(Histogram histogram, T output);
}
