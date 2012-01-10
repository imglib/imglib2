package net.imglib2.script.view;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.script.algorithm.fn.RandomAccessibleIntervalImgProxy;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;

public class ExtendMirrorDouble<T extends RealType<T>> extends RandomAccessibleIntervalImgProxy<T>
{
	public ExtendMirrorDouble(final RandomAccessibleInterval<T> img) {
		super(Views.interval(Views.extendMirrorDouble(img), img));
	}
	
	public ExtendMirrorDouble(final RandomAccessibleIntervalImgProxy<T> proxy) {
		this(proxy.getRandomAccessibleInterval());
	}
}
