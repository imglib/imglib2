package net.imglib2.script.view;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.script.algorithm.fn.RandomAccessibleIntervalImgProxy;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;

public class ExtendPeriodic<T extends RealType<T>> extends RandomAccessibleIntervalImgProxy<T>
{	
	public ExtendPeriodic(final RandomAccessibleInterval<T> img) {
		super(Views.interval(Views.extendPeriodic(img), img));
	}
	
	public ExtendPeriodic(final RandomAccessibleIntervalImgProxy<T> proxy) {
		this(proxy.getRandomAccessibleInterval());
	}
}
