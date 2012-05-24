package net.imglib2.script.algorithm.integral.histogram;


public final class LinearHistogram extends Histogram
{
	
	private final double K;
	
	public LinearHistogram(
			final long[] bins,
			final int numDimensions,
			final double min,
			final double max)
	{
		super(bins, numDimensions, min, max);
		// Compute values of each bin
		this.K = bins.length -1;
		for (int i=0; i<binValues.length; ++i) {
			binValues[i] = min + (i / this.K) * range;
		}
	}

	@Override
	public final int computeBin(final double value) {
		return (int)(((Math.min(max, Math.max(min, value)) - min) / range) * K + 0.5);
	}

	@Override
	public Histogram clone() {
		return new LinearHistogram(bins.clone(), maxPositions.length, min, max);
	}
}
