package net.imglib2.script.view;

import net.imglib2.RandomAccessible;
import net.imglib2.script.algorithm.fn.RandomAccessibleIntervalImgProxy;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.view.Views;

public class ROI<R extends NumericType<R>> extends RandomAccessibleIntervalImgProxy<R>
{
	public ROI(final RandomAccessible<R> img, final long[] min, final long[] max) {
		super(Views.interval(img, min, max));
	}
}
