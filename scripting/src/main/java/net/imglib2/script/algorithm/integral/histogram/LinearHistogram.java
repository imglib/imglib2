package net.imglib2.script.algorithm.integral.histogram;

import net.imglib2.type.numeric.RealType;

/**
 * 
 * @author Albert Cardona
 *
 * @param <T> The {@link RealType} of the data of the image from which the histogram is computed.
 */
public final class LinearHistogram<T extends RealType<T>> extends Histogram<T>
{
	
	// For clarity and perhaps for performance
	private final double K, dmin, dmax, drange;
	
	/** @see Histogram */
	public LinearHistogram(
			final int nBins,
			final int numDimensions,
			final T min,
			final T max)
	{
		super(nBins, numDimensions, min, max);
		// Compute values of each bin
		this.K = nBins -1;
		int i = -1;
		for (final T bin : binValues) {
			bin.set(range);
			bin.mul(++i / this.K);
			bin.add(min);
		}
		dmin = min.getRealDouble();
		dmax = max.getRealDouble();
		drange = range.getRealDouble();
	}

	@Override
	public final long computeBin(final T value) {
		return (long)(((Math.min(dmax, Math.max(dmin, value.getRealDouble())) - dmin) / drange) * K + 0.5);
	}

	@Override
	public Histogram<T> clone() {
		return new LinearHistogram<T>(nBins(), maxPositions.length, min, max);
	}
}
