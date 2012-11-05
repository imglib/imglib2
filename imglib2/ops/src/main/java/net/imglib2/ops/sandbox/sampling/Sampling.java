package net.imglib2.ops.sandbox.sampling;

import net.imglib2.IterableRealInterval;

public interface Sampling extends IterableRealInterval<double[]> {

	@Override
	SamplingIterator iterator();
}
