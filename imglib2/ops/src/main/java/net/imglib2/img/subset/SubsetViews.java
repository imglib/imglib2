package net.imglib2.img.subset;

import net.imglib2.Interval;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.transform.integer.MixedTransform;
import net.imglib2.type.Type;
import net.imglib2.util.Util;
import net.imglib2.view.IntervalView;
import net.imglib2.view.IterableRandomAccessibleInterval;
import net.imglib2.view.MixedTransformView;
import net.imglib2.view.Views;

/**
 * @author dietzc, hornm (University of Konstanz)
 * 
 */
public class SubsetViews {

	public static final <T extends Type<T>> IterableRandomAccessibleInterval<T> iterableSubsetView(
			final RandomAccessibleInterval<T> src, final Interval interval) {
		return new IterableSubsetView<T>(src, interval);
	}

	public static <T> MixedTransformView<T> permutate(
			final RandomAccessible<T> randomAccessible, final int fromAxis,
			final int toAxis) {
		final int n = randomAccessible.numDimensions();
		final int[] component = new int[n];
		for (int e = 0; e < n; ++e)
			component[e] = e;
		component[fromAxis] = toAxis;
		component[toAxis] = fromAxis;
		final MixedTransform t = new MixedTransform(n, n);
		t.setComponentMapping(component);
		return new MixedTransformView<T>(randomAccessible, t);
	}

	/**
	 * View on interval of a source. If wanted, dims with size 1 are removed.
	 * 
	 * @param src
	 *            The source {@link RandomAccessibleInterval}
	 * @param interval
	 *            Interval
	 * @param keepDimsWithSizeOne
	 *            If false, dimensions with size one will be virtually removed
	 *            from the resulting view
	 * @return
	 */
	public static final <T extends Type<T>> RandomAccessibleInterval<T> subsetView(
			final RandomAccessibleInterval<T> src, final Interval interval) {

		boolean oneSizedDims = false;

		for (int d = 0; d < interval.numDimensions(); d++) {
			if (interval.dimension(d) == 1) {
				oneSizedDims = true;
				break;
			}
		}

		if (intervalEquals(src, interval) && !oneSizedDims)
			return src;

		RandomAccessibleInterval<T> res;
		if (Util.contains(src, interval))
			res = Views.offsetInterval(src, interval);
		else
			throw new IllegalArgumentException(
					"Interval must fit into src in SubsetViews.subsetView(...)");

		for (int d = interval.numDimensions() - 1; d >= 0; --d)
			if (interval.dimension(d) == 1 && res.numDimensions() > 1)
				res = Views.hyperSlice(res, d, 0);

		return res;
	}

	public static <T> MixedTransformView<T> addDimension(
			final RandomAccessible<T> view) {
		final int m = view.numDimensions();
		final int n = m + 1;
		final MixedTransform t = new MixedTransform(n, m);
		return new MixedTransformView<T>(view, t);
	}

	public static <T> IntervalView<T> addDimension(
			final RandomAccessibleInterval<T> view, final long minOfNewDim,
			final long maxOfNewDim) {
		final int m = view.numDimensions();
		final long[] min = new long[m + 1];
		final long[] max = new long[m + 1];
		for (int d = 0; d < m; ++d) {
			min[d] = view.min(d);
			max[d] = view.max(d);
		}
		min[m] = minOfNewDim;
		max[m] = maxOfNewDim;
		return Views.interval(addDimension(view), min, max);
	}

	/**
	 * {@link RandomAccessibleInterval} with same sice as target is returned
	 * 
	 * @param src
	 *            {@link RandomAccessibleInterval} to be adjusted
	 * @param target
	 *            {@link Interval} describing the resulting sizes
	 * @return Adjusted {@link RandomAccessibleInterval}
	 */
	public static <T> RandomAccessibleInterval<T> synchronizeDimensionality(
			final RandomAccessibleInterval<T> src, final Interval target) {
		RandomAccessibleInterval<T> res = src;

		// Check direction of conversion
		if (intervalEquals(src, target))
			return res;

		// adjust dimensions
		if (res.numDimensions() < target.numDimensions()) {
			for (int d = res.numDimensions(); d < target.numDimensions(); d++) {
				res = addDimension(res, target.min(d), target.max(d));
			}
		} else {
			for (int d = res.numDimensions() - 1; d >= target.numDimensions(); --d)
				res = Views.hyperSlice(res, d, 0);
		}

		long[] resDims = new long[res.numDimensions()];
		res.dimensions(resDims);

		return Views.interval(Views.extendBorder(res), target);

	}

	public static synchronized boolean intervalEquals(Interval a, Interval b) {

		if (a.numDimensions() != b.numDimensions()) {
			return false;
		}

		for (int d = 0; d < a.numDimensions(); d++) {
			if (a.min(d) != b.min(d) || a.max(d) != b.max(d))
				return false;
		}

		return true;
	}
}
