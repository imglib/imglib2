/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package net.imglib2.ops.operation.subset.views;

import java.util.ArrayList;
import java.util.List;

import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.meta.AxisType;
import net.imglib2.meta.CalibratedSpace;
import net.imglib2.ops.util.metadata.CalibratedSpaceImpl;
import net.imglib2.type.Type;
import net.imglib2.util.Intervals;
import net.imglib2.view.IterableRandomAccessibleInterval;
import net.imglib2.view.Views;

/**
 * @author Christian Dietz (University of Konstanz)
 */
public class SubsetViews {

	/**
	 * See SubsetViews.subsetView(...) Difference: If possible an optimized
	 * {@link Cursor} will be created.
	 * 
	 * @param <T>
	 *            TODO
	 * @param src
	 *            Source {@link RandomAccessibleInterval}
	 * @param interval
	 *            Interval defining dimensionality of resulting
	 *            {@link IterableRandomAccessibleInterval}
	 */
	public static final <T extends Type<T>> IterableRandomAccessibleInterval<T> iterableSubsetView(
			final RandomAccessibleInterval<T> src, final Interval interval) {
		return new IterableSubsetView<T>(src, interval);
	}

	/**
	 * View on interval of a source. If wanted, dims with size 1 are removed.
	 * 
	 * @param src
	 *            The source {@link RandomAccessibleInterval}
	 * @param interval
	 *            Interval
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
		if (Intervals.contains(src, interval))
			res = Views.offsetInterval(src, interval);
		else
			throw new IllegalArgumentException(
					"Interval must fit into src in SubsetViews.subsetView(...)");

		for (int d = interval.numDimensions() - 1; d >= 0; --d)
			if (interval.dimension(d) == 1 && res.numDimensions() > 1)
				res = Views.hyperSlice(res, d, 0);

		return res;
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
			final RandomAccessibleInterval<T> src, CalibratedSpace srcSpace,
			final Interval target, CalibratedSpace targetSpace) {

		// must hold, if not: most likely an implementation error
		assert (srcSpace.numDimensions() == src.numDimensions() && target
				.numDimensions() == targetSpace.numDimensions());

		// Check direction of conversion
		if (intervalEquals(src, target) && spaceEquals(srcSpace, targetSpace))
			return src;

		// Init result vars
		RandomAccessibleInterval<T> res = src;
		CalibratedSpace resSpace = new CalibratedSpaceImpl(
				target.numDimensions());

		// 1. Step remove axis from source which can't be found in target
		AxisType[] dispensable = getDeltaAxisTypes(targetSpace, srcSpace);
		for (int d = dispensable.length - 1; d >= 0; --d) {
			int idx = srcSpace.getAxisIndex(dispensable[d]);
			res = Views.hyperSlice(res, idx, 0);
		}

		int i = 0;
		outer: for (int d = 0; d < srcSpace.numDimensions(); d++) {
			for (AxisType type : dispensable) {
				if (d == srcSpace.getAxisIndex(type)) {
					continue outer;
				}
			}

			resSpace.setAxis(srcSpace.axis(d), i++);
		}

		// 2. Add Axis which are available in target but not in source
		AxisType[] missing = getDeltaAxisTypes(srcSpace, targetSpace);

		// Dimensions are added and resSpace is synchronized with res
		i = srcSpace.numDimensions() - dispensable.length;
		for (final AxisType type : missing) {
			final int idx = targetSpace.getAxisIndex(type);
			res = Views.addDimension(res, target.min(idx), target.max(idx));
			resSpace.setAxis(type, i++);
		}

		// res should have the same size, but with different metadata
		assert (res.numDimensions() == targetSpace.numDimensions());

		// 3. Permutate axis if necessary
		RandomAccessible<T> resRndAccessible = res;
		for (int d = 0; d < res.numDimensions(); d++) {
			int srcIdx = resSpace.getAxisIndex(targetSpace.axis(d));

			if (srcIdx != d) {
				resRndAccessible = Views.permute(resRndAccessible, srcIdx, d);

				// also permutate calibrated space
				AxisType tmp = resSpace.axis(d);
				resSpace.setAxis(targetSpace.axis(d), d);
				resSpace.setAxis(tmp, srcIdx);
			}
		}

		return Views.interval(
				Views.extendBorder(Views.interval(resRndAccessible, target)),
				target);
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
				res = Views.addDimension(res, target.min(d), target.max(d));
			}
		} else {
			for (int d = res.numDimensions() - 1; d >= target.numDimensions(); --d)
				res = Views.hyperSlice(res, d, 0);
		}

		long[] resDims = new long[res.numDimensions()];
		res.dimensions(resDims);

		return Views.interval(Views.extendBorder(res), target);

	}

	private static boolean spaceEquals(CalibratedSpace srcSpace,
			CalibratedSpace targetSpace) {

		if (srcSpace.numDimensions() != targetSpace.numDimensions())
			return false;

		for (int d = 0; d < srcSpace.numDimensions(); d++) {
			if (!srcSpace.axis(d).equals(targetSpace.axis(d)))
				return false;
		}
		return true;
	}

	/*
	 * Calculate the delta axis which are missing in the smaller space. From the
	 * smallest index of axistype to the biggest
	 */
	private synchronized static AxisType[] getDeltaAxisTypes(
			CalibratedSpace sourceSpace, CalibratedSpace targetSpace) {

		List<AxisType> delta = new ArrayList<AxisType>();
		for (int d = 0; d < targetSpace.numDimensions(); d++) {
			AxisType axisType = targetSpace.axis(d);
			if (sourceSpace.getAxisIndex(axisType) == -1) {
				delta.add(axisType);
			}
		}
		return delta.toArray(new AxisType[delta.size()]);
	}

	/**
	 * Checks whether to intervals have same dimensionality.
	 */
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

//	public static void main(String[] args) {
//		ImgPlus<BitType> a = new ImgPlus<BitType>(
//				new ArrayImgFactory<BitType>().create(
//						new long[] { 10, 15, 5, 2 }, new BitType()));
//
//		a.setAxis(Axes.get("X"), 0);
//		a.setAxis(Axes.get("Y"), 1);
//		a.setAxis(Axes.get("T"), 2);
//
//		ImgPlus<BitType> b = new ImgPlus<BitType>(
//				new ArrayImgFactory<BitType>().create(
//						new long[] { 15, 5, 10, 1 }, new BitType()));
//
//		b.setAxis(Axes.get("Y"), 0);
//		b.setAxis(Axes.get("T"), 1);
//		b.setAxis(Axes.get("Channel"), 2);
//		b.setAxis(Axes.get("X"), 3);
//
//		RandomAccessibleInterval<BitType> res = SubsetViews
//				.synchronizeDimensionality(a, a, b, b);
//
//	}

}
