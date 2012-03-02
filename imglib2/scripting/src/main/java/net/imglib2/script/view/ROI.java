package net.imglib2.script.view;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.script.algorithm.fn.RandomAccessibleIntervalImgProxy;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.view.Views;

/**
 * ROI is a view of the image, either smaller or larger.
 * If larger, the image should be an extension of an image that can provide data for the outside domain,
 * for example {@code new ROI(new ExtendMirrorDouble(...), ...)}.
 * 
 * @author Albert Cardona
 *
 * @param <R>
 */
public class ROI<R extends NumericType<R>> extends RandomAccessibleIntervalImgProxy<R>
{
	public ROI(final RandomAccessibleInterval<R> img, final long[] offset, final long[] dimensions) {
		super(Views.zeroMin(Views.interval(img, offset, asMax(offset, dimensions))));
	}

	public ROI(final RandomAccessibleIntervalImgProxy<R> proxy, final long[] offset, final long[] dimensions) {
		this(proxy.getRandomAccessibleInterval(), offset, dimensions);
	}

	private static final long[] asMax(final long[] offset, final long[] dimensions) {
		final long[] max = new long[offset.length];
		for (int i=0; i<offset.length; ++i)
			max[i] = offset[i] + dimensions[i] - 1;
		return max;
	}
}
