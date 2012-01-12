package net.imglib2.ops.sandbox;

import net.imglib2.IterableInterval;


public interface RealFunction<T> {
	void evaluate(double[] coordinate, IterableInterval<T> interval, T output);
	T createVariable();
}
