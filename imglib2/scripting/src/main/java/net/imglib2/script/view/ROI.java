package net.imglib2.script.view;

import net.imglib2.RandomAccessible;
import net.imglib2.script.algorithm.fn.RandomAccessibleIntervalImgProxy;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.view.Views;

public class ROI<R extends NumericType<R>> extends RandomAccessibleIntervalImgProxy<R>
{
	/**
	 * A new image which is just a view of the domain between {@param min} and {@param max}
	 * so that the dimensions of the image are now (max - min + 1) for every dimension.
	 */
	public ROI(final RandomAccessible<R> img, final long[] min, final long[] max) {
		super(Views.zeroMin(Views.interval(img, min, max)));
	}

	public ROI(final RandomAccessibleIntervalImgProxy<R> proxy, final long[] min, final long[] max) {
		this(proxy.getRandomAccessibleInterval(), min, max);
	}
}
