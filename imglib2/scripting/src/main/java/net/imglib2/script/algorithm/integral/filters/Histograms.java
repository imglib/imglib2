package net.imglib2.script.algorithm.integral.filters;

import net.imglib2.Cursor;
import net.imglib2.Point;
import net.imglib2.RandomAccess;
import net.imglib2.Sampler;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.script.algorithm.integral.IntegralHistogram;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.integer.LongType;
import net.imglib2.view.Views;

/** A {@link RandomAccess} with dimensions N-1, over an {@link Img} of dimensions N that contains the {@link IntegralHistogram}. */
public class Histograms<T extends IntegerType<T>> extends Point implements RandomAccess<Img<LongType>>, Cursor<Img<LongType>>
{
	/** Current index when used as a Cursor. */
	private long index = -1;
	/** Number of samples that can be retrieved. */
	private final long size;
	/** Reference image containing the integral histogram. */
	private final Img<T> integralHistogram;
	/** RandomAccess over the integralHistogram, with a mirroring strategy for out of bounds. */
	private final RandomAccess<T> ra;
	/** Dimensions of the window to be centered at each sample, and for which the histogram is computed. */
	private final long[] window;
	/** Reusable histogram, returned at every call to get(). */
	private final Img<LongType> hist;
	/** Cursor over hist. */
	private final Cursor<LongType> histCursor;
	/** All the corner points from which the histogram is computed.
	 * Derived from window and specified as relative positive and negative offsets for each dimension. */
	private final Point[] offsets;

	public Histograms(final Img<T> integralHistogram, final long[] window) {
		super(integralHistogram.numDimensions() -1);
		this.integralHistogram = integralHistogram;
		//
		long s = 1;
		for (int d=0; d<numDimensions(); ++d) s *= (integralHistogram.dimension(d) -1); // skip leading zeros
		this.size = s;
		//
		this.ra = Views.extendMirrorDouble(integralHistogram).randomAccess();
		this.window = window;
		// The histogram to return at every sample (at every call to get())
		this.hist = new ArrayImgFactory<LongType>().create(
				new long[]{this.integralHistogram.dimension(this.integralHistogram.numDimensions() -1)}, // -1 to skip leading zeros in integralHistogram
				new LongType());
		this.histCursor = this.hist.cursor();
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
				offsets[i].setPosition(sign * window[d] / 2, d);
				++i; // done before flipping sign, so coords will be (almost) in order
				if (0 == i % flip) sign *= -1;
			}
			++d;
		}
	}

	/** Returns the histogram at each location. The same instance of {@code Img<T>} is returned every time. */
	@Override
	public Img<LongType> get() {
		final long[] currentPosition = new long[integralHistogram.numDimensions()];
		this.localize(currentPosition);
		
		for (int o=0; o<offsets.length; ++o) {
			final Point offset = offsets[o];
			long sign = 1;
			for (int d=0; d<numDimensions(); ++d) {
				ra.setPosition(currentPosition[d] + 1 + offset.getLongPosition(d), d); // +1 to jump over empty values
				sign *= Math.signum(offset.getLongPosition(d));
			}
			histCursor.reset();
			while (histCursor.hasNext()) {
				histCursor.fwd();
				ra.setPosition(histCursor.getLongPosition(0), numDimensions()); // numDimensions() coincides with the index of the last dimension of the integral histogram image
				histCursor.get().set(histCursor.get().get() + sign * ra.get().getIntegerLong());
			}
		}
		
		// TODO hist could be just a naked long[] array. No need to make it an Img<LongType>
		
		return hist;
	}

	@Override
	public Sampler<Img<LongType>> copy() {
		return new Histograms<T>(this.integralHistogram, window);
	}

	@Override
	public RandomAccess<Img<LongType>> copyRandomAccess() {
		return new Histograms<T>(this.integralHistogram, window);
	}

	@Override
	public void jumpFwd(long steps) {
		index += steps;
	}

	@Override
	public void fwd() {
		++index;
	}

	@Override
	public void reset() {
		index = -1;
	}

	@Override
	public boolean hasNext() {
		return index < size;
	}

	@Override
	public Img<LongType> next() {
		fwd();
		return get();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Cursor<Img<LongType>> copyCursor() {
		return new Histograms<T>(integralHistogram, window);
	}
}