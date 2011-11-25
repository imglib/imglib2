package net.imglib2.script.view;

import java.util.List;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.script.algorithm.fn.AlgorithmUtil;
import net.imglib2.script.algorithm.fn.RandomAccessibleIntervalImgProxy;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;

public class ExtendPeriodic<T extends RealType<T>> extends RandomAccessibleIntervalImgProxy<T>
{
	public ExtendPeriodic(final RandomAccessibleInterval<T> img, final long[] offset, final long[] dimension) {
		super(Views.offsetInterval(Views.extendPeriodic(img), offset, dimension));
	}
	
	public ExtendPeriodic(final RandomAccessibleInterval<T> img, final List<Number> offset, final List<Number> dimension) {
		this(img, AlgorithmUtil.asLongArray(offset), AlgorithmUtil.asLongArray(dimension));
	}
	
	public ExtendPeriodic(final RandomAccessibleInterval<T> img, final long[] dimension) {
		super(Views.offsetInterval(
				Views.extendPeriodic(img),
				new long[Math.max(img.numDimensions(), dimension.length)],
				dimension));
	}
	
	public ExtendPeriodic(final RandomAccessibleInterval<T> img, final List<Number> dimension) {
		this(img, AlgorithmUtil.asLongArray(dimension));
	}
	
	public ExtendPeriodic(final RandomAccessibleInterval<T> img) {
		super(Views.interval(Views.extendPeriodic(img), img));
	}

}
