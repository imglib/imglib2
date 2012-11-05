package net.imglib2.ops.sandbox.sampling;

import net.imglib2.Cursor;
import net.imglib2.Sampler;
import net.imglib2.ops.condition.Condition;


public class ConditionalSampling extends AbstractSampling {

	private Sampling sampling;
	private Condition<double[]> cond;
	private boolean gatherStats = true;
	private long size = -1;
	private double[] min;
	private double[] max;

	public ConditionalSampling(Sampling sampling, Condition<double[]> cond) {
		super(sampling.numDimensions());
		this.sampling = sampling;
		this.cond = cond;
	}

	@Override
	public SamplingIterator iterator() {
		return new ConditionalSamplingIterator();
	}

	@Override
	public long size() {
		if (gatherStats) gatherStats();
		return size;
	}

	@Override
	public double realMin(int d) {
		if (gatherStats) gatherStats();
		return min[d];
	}

	@Override
	public double realMax(int d) {
		if (gatherStats) gatherStats();
		return max[d];
	}

	@Override
	public int numDimensions() {
		return n;
	}

	private void gatherStats() {
		gatherStats = false;
		size = 0;
		min = new double[n];
		max = new double[n];
		for (int i = 0; i < n; i++) {
			min[i] = Double.POSITIVE_INFINITY;
			max[i] = Double.NEGATIVE_INFINITY;
		}
		SamplingIterator iter = iterator();
		while (iter.hasNext()) {
			double[] pos = iter.next();
			size++;
			for (int i = 0; i < n; i++) {
				min[i] = Math.min(min[i], pos[i]);
				max[i] = Math.max(max[i], pos[i]);
			}
		}
	}

	private class ConditionalSamplingIterator extends AbstractSamplingIterator {

		private SamplingIterator iter;
		private boolean cached;

		public ConditionalSamplingIterator() {
			super(n);
			iter = sampling.iterator();
			cached = false;
		}

		@Override
		public Cursor<double[]> copyCursor() {
			return iterator();
		}

		@Override
		public Sampler<double[]> copy() {
			return iterator();
		}

		@Override
		public void fwd() {
			if (cached) {
				cached = false;
				return;
			}
			if (!positionToNext()) throw new IllegalArgumentException(
				"Cannot fwd() beyond end");
		}

		@Override
		public void reset() {
			iter.reset();
		}

		@Override
		public boolean hasNext() {
			if (cached) return true;
			return positionToNext();
		}

		private boolean positionToNext() {
			cached = false;
			while (iter.hasNext()) {
				double[] pos = iter.next();
				if (cond.isTrue(pos)) {
					cached = true;
					for (int i = 0; i < n; i++)
						curr[i] = pos[i];
					return true;
				}
			}
			return false;
		}
	}
}
