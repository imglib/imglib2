package net.imglib2.script.view;

import java.util.List;

import net.imglib2.img.Img;
import net.imglib2.script.algorithm.fn.AlgorithmUtil;
import net.imglib2.script.algorithm.fn.RandomAccessibleIntervalImgProxy;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;

public class ExtendMirroringDouble<T extends RealType<T>> extends RandomAccessibleIntervalImgProxy<T>
{
	public ExtendMirroringDouble(final Img<T> img, final long[] offset, final long[] dimension) {
		super(Views.offsetInterval(Views.extendMirrorDouble(img), offset, dimension));
	}
	
	public ExtendMirroringDouble(final Img<T> img, final List<Number> offset, final List<Number> dimension) {
		this(img, AlgorithmUtil.asLongArray(offset), AlgorithmUtil.asLongArray(dimension));
	}

	public ExtendMirroringDouble(final Img<T> img, final long[] dimension) {
		super(Views.offsetInterval(
				Views.extendMirrorDouble(img),
				new long[Math.max(img.numDimensions(), dimension.length)],
				dimension));
	}
	
	public ExtendMirroringDouble(final Img<T> img, final List<Number> dimension) {
		this(img, AlgorithmUtil.asLongArray(dimension));
	}

	public ExtendMirroringDouble(final Img<T> img) {
		super(Views.interval(Views.extendMirrorDouble(img), img));
	}
}
