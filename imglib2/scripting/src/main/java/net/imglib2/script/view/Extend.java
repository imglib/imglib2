package net.imglib2.script.view;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.script.algorithm.fn.AlgorithmUtil;
import net.imglib2.script.algorithm.fn.RandomAccessibleIntervalImgProxy;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;

public class Extend<T extends RealType<T>> extends RandomAccessibleIntervalImgProxy<T>
{
	/** Infinitely extend the domain of the image with {@param value}. */
	public Extend(final RandomAccessibleInterval<T> img, final Number value) {
		super(Views.interval(Views.extendValue(img, AlgorithmUtil.type(img, value.doubleValue())), img));
	}

	/** Defaults to an out of bounds value of 0. */
	@SuppressWarnings("boxing")
	public Extend(final RandomAccessibleInterval<T> img) {
		this(img, 0);
	}

	/** Infinitely extend the domain of the image with {@param value}. */
	public Extend(final RandomAccessibleIntervalImgProxy<T> proxy, final Number value) {
		this(proxy.getRandomAccessibleInterval(), value);
	}

	/** Defaults to an out of bounds value of 0. */
	@SuppressWarnings("boxing")
	public Extend(final RandomAccessibleIntervalImgProxy<T> proxy) {
		this(proxy.getRandomAccessibleInterval(), 0);
	}
}
