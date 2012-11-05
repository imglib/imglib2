package net.imglib2.ops.sandbox.sampling;

import net.imglib2.IterableRealInterval;
import net.imglib2.RealCursor;
import net.imglib2.RealPositionable;


public abstract class AbstractSampling implements Sampling {

	protected final int n;

	public AbstractSampling(int n) {
		this.n = n;
	}

	@Override
	public RealCursor<double[]> cursor() {
		return iterator();
	}

	@Override
	public RealCursor<double[]> localizingCursor() {
		return iterator();
	}

	@Override
	public double[] firstElement() {
		double[] first = new double[numDimensions()];
		realMin(first);
		return first;
	}

	@Override
	public Object iterationOrder() {
		return new Object();
	}

	@Override
	public boolean equalIterationOrder(IterableRealInterval<?> f) {
		return false;
	}

	@Override
	public void realMin(double[] min) {
		for (int i = 0; i < min.length; i++)
			min[i] = realMin(i);
	}

	@Override
	public void realMin(RealPositionable min) {
		for (int i = 0; i < min.numDimensions(); i++)
			min.setPosition(realMin(i), i);
	}

	@Override
	public void realMax(double[] max) {
		for (int i = 0; i < max.length; i++)
			max[i] = realMax(i);
	}

	@Override
	public void realMax(RealPositionable max) {
		for (int i = 0; i < max.numDimensions(); i++)
			max.setPosition(realMax(i), i);
	}

}
