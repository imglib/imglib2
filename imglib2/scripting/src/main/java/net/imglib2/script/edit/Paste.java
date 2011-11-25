package net.imglib2.script.edit;

import java.util.ArrayList;

import net.imglib2.AbstractInterval;
import net.imglib2.Interval;
import net.imglib2.IterableRealInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.script.algorithm.fn.AlgorithmUtil;
import net.imglib2.script.algorithm.fn.RandomAccessibleIntervalImgProxy;
import net.imglib2.script.math.Add;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.script.math.fn.Util;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;

public class Paste extends Add
{
	/**
	 * @param <T> The {@link Type} of the source image.
	 * @param <R> The {@link Type} of the target image.
	 * @param source The image to paste, which can be smaller than the {@param target} image.
	 * @param target The image that receives the pasting.
	 * @param offset The offset from origin for the pasting operation.
	 * @throws Exception
	 */
	public <T extends RealType<T>, R extends RealType<R>> Paste(
			final RandomAccessibleInterval<T> source,
			final IterableRealInterval<R> target,
			final long[] offset) throws Exception {
		super(
			new RandomAccessibleIntervalImgProxy<T>(
					Views.interval(
							Views.translate(Views.extendValue(source, AlgorithmUtil.type(source, 0)), offset),
							new AbstractInterval(Util.extractDimensions(target)){})),
			target);
	}
	
	/**
	 * @param <T> The {@link Type} of the source image.
	 * @param source The image to paste, which can be smaller than the {@param target} image.
	 * @param target The function that expresses the target image.
	 * @param offset The offset from origin for the pasting operation.
	 * @throws Exception
	 */
	public <T extends RealType<T>> Paste(
			final RandomAccessibleInterval<T> source,
			final IFunction target,
			final long[] offset) throws Exception {
		super(
			new RandomAccessibleIntervalImgProxy<T>(
					Views.interval(
							Views.translate(Views.extendValue(source, AlgorithmUtil.type(source, 0)), offset),
							extractDimensions(target))),
			target);
	}

	private static final Interval extractDimensions(final IFunction target) {
		ArrayList<IterableRealInterval<?>> iris = new ArrayList<IterableRealInterval<?>>();
		target.findImgs(iris);
		return new AbstractInterval(
				iris.isEmpty() ?
						new long[]{1}
						: Util.extractDimensions(iris.get(0))) {};
	}
	
	/** For cloning this {@link IFunction}. */
	public Paste(final IFunction a, final IFunction b) {
		super(a, b);
	}
}
