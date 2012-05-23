package net.imglib2.script.algorithm.integral;

import net.imglib2.Localizable;

public final class Histogram
{
	public final long[] bins;
	public final long[] maxPositions, minPositions;
	public long nPixels;
	
	/**
	 * 
	 * @param bins The ordered histogram bins.
	 * @param cellDimensions The dimensions of the area, volume, etc. from which the histogram was extracted.
	 */
	public Histogram(
			final long[] bins,
			final int numDimensions)
	{
		this.bins = bins;
		this.maxPositions = new long[numDimensions];
		this.minPositions = new long[numDimensions];
	}
	
	public final void clearBins() {
		for (int i=0; i<bins.length; ++i) {
			bins[i] = 0;
		}
	}
	
	public final void updatePixelCount() {
		nPixels = maxPositions[0] - minPositions[0];
		for (int d=1; d<maxPositions.length; ++d) {
			nPixels *= maxPositions[d] - minPositions[d];
		}
	}
	
	public final void initPositions(final Localizable l, final int offset) {
		for (int d=0; d<maxPositions.length; ++d) {
			final long p = l.getLongPosition(d);
			maxPositions[d] = p + offset;
			minPositions[d] = p + offset;
		}
	}
	
	public final void updatePositions(final long position, final int d) {
		maxPositions[d] = Math.max(maxPositions[d], position);
		minPositions[d] = Math.min(minPositions[d], position);
	}
}
