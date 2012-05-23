package net.imglib2.script.algorithm.integral.histogram;

import net.imglib2.Cursor;
import net.imglib2.Point;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.util.IntervalIndexer;

/** A {@link RandomAccess} with dimensions N-1, over an {@link Img} of dimensions N
 * that contains the {@link IntegralHistogram}. */
public class Histograms<T extends IntegerType<T> & NativeType<T>> extends Point implements RandomAccess<Histogram>, Cursor<Histogram>
{
	/** Index of last sample that can be retrieved. */
	private final long lastIndex;
	/** Reference image containing the integral histogram. */
	private final Img<T> integralHistogram;
	/** RandomAccess over the integralHistogram, with a mirroring strategy for out of bounds. */
	private final RandomAccess<T> ra;
	/** Radius in each dimension. */
	private final long[] radius;
	/** Reusable histogram, returned at every call to get(). */
	private final Histogram histogram;
	/** All the corner points from which the histogram is computed.
	 * Derived from window and specified as relative positive and negative offsets for each dimension. */
	private final Point[] offsets;
	/** Correlated with offsets, holds the sign to add or subtract the histogram at that Point. */
	private final int[] signs;
	
	/** The dimensions over which this Cursor/RandomAccess is defined, which are one less than
	 * in the integralHistogram, and each dimension one less as well. */
	private final long[] dimensions;

	public Histograms(
			final Img<T> integralHistogram,
			final Histogram histogram,
			final long[] radius) {
		super(integralHistogram.numDimensions() -1);
		this.integralHistogram = integralHistogram;
		this.radius = radius;
		
		// Establish the dimensions where this Cursor/RandomAccess is defined:
		this.dimensions = new long[n];
		for (int d=0; d<n; ++d) this.dimensions[d] = integralHistogram.dimension(d) -1;
		
		// Compute the size of the underlying, original image from which the integralHistogram was computed:
		long s = 1;
		for (int d=0; d<n; ++d) s *= this.dimensions[d];
		// ... and set the lastIndex, for Cursor.hasNext:
		this.lastIndex = s - 1;
		
		// Set starting index at -1
		reset();
		
		// TODO can't do out of bounds with integral images!
		// Instead, I have to send the coords back to the nearest existing within the domain.
		this.ra = this.integralHistogram.randomAccess();
		
		// The histogram to return at every sample (at every call to get())
		//this.hist = new long[(int)this.integralHistogram.dimension(this.integralHistogram.numDimensions() -1)];// the size is the number of histogram bins
		
		this.histogram = histogram;
		
		// N-dimensional corner coordinates, relative to any one pixel location
		this.offsets = new Point[(int)Math.pow(2, numDimensions())];
		for (int i=0; i<offsets.length; ++i) {
			offsets[i] = new Point(numDimensions());
		}
		int d = 0;
		while (d < numDimensions()) {
			final int flip = (int)Math.pow(2, d);
			int sign = -1;
			for (int i=0; i<offsets.length;) {
				long delta = radius[d];
				// increasing is inclusive, but decreasing is exclusive. This way, a radius of zero also works.
				offsets[i].setPosition(sign * delta + (-1 == sign ? -1 : 0), d);
				++i; // done before flipping sign, so coords will be (almost) in order
				if (0 == i % flip) sign *= -1;
			}
			++d;
		}

		// Compute the sign of each corner
		this.signs = new int[offsets.length];
		for (int o=0; o<offsets.length; ++o) {
			// Count the number of negative signs
			signs[o] = 0;
			for (d=0; d<numDimensions(); ++d) {
				signs[o] += offsets[o].getLongPosition(d) < 0 ? 1 : 0;
			}
			// Set the proper sign
			signs[o] = signs[o] % 2 != 0 ? -1 : 1;
		}
	}

	private final long inside(final long pos, final int d) {
		return Math.min(integralHistogram.dimension(d) -1, Math.max(0, pos));
	}

	/** Returns the histogram at each location. The same instance of {@code Img<T>} is returned every time. */
	@Override
	public Histogram get() {
		// Set all bins to zero
		histogram.clearBins();
		// Setup to compute cell dimensions
		histogram.initPositions(this, 1);
		//
		for (int o=0; o<offsets.length; ++o) {
			for (int d=0; d<n; ++d) {
				// position[d] is here
				// + 1 to move over the leading zeros in integralHistogram
				// + offset to go to the right corner
				final long pos = inside(position[d] + 1 + offsets[o].getLongPosition(d), d);
				histogram.updatePositions(pos, d);
				ra.setPosition(pos, d);
			}
			for (int i=0; i<histogram.bins.length; ++i) {
				ra.setPosition(i, n); // n coincides with the index of the last dimension of the integral histogram image
				histogram.bins[i] += signs[o] * ra.get().getIntegerLong();
			}
		}
		
		histogram.updatePixelCount();

		return histogram;
	}

	@Override
	public Histograms<T> copy() {
		return copyRandomAccess();
	}

	@Override
	public Histograms<T> copyRandomAccess() {
		return new Histograms<T>(this.integralHistogram, histogram, radius);
	}

	@Override
	public void jumpFwd(long steps) {
		IntervalIndexer.indexToPosition(IntervalIndexer.positionToIndex(position, dimensions) + steps, dimensions, position);
	}

	@Override
	public void fwd() {
		jumpFwd(1);
	}

	@Override
	public void reset() {
		position[0] = -1;
		for (int d=1; d<n; ++d) position[d] = 0;
	}

	@Override
	public boolean hasNext() {
		return IntervalIndexer.positionToIndex(position, dimensions) < lastIndex;
	}

	@Override
	public Histogram next() {
		fwd();
		return get();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Histograms<T> copyCursor() {
		return copy();
	}
}