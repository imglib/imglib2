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
package net.imglib2.ops.operation;

import java.util.concurrent.ExecutorService;

import net.imglib2.Interval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.ImgPlus;
import net.imglib2.labeling.Labeling;
import net.imglib2.labeling.NativeImgLabeling;
import net.imglib2.ops.operation.interval.binary.IntervalsFromDimSelection;
import net.imglib2.ops.operation.metadata.unary.CopyCalibratedSpace;
import net.imglib2.ops.operation.metadata.unary.CopyImageMetadata;
import net.imglib2.ops.operation.metadata.unary.CopyMetadata;
import net.imglib2.ops.operation.metadata.unary.CopyNamed;
import net.imglib2.ops.operation.metadata.unary.CopySourced;
import net.imglib2.ops.operation.subset.views.ImgPlusView;
import net.imglib2.ops.operation.subset.views.ImgView;
import net.imglib2.ops.operation.subset.views.LabelingView;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.Intervals;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;

@SuppressWarnings("unchecked")
public final class SubsetOperations {

	public static <T extends Type<T>, U extends Type<U>, V extends RealType<V>, A extends RandomAccessibleInterval<T>, B extends RandomAccessibleInterval<U>, C extends RandomAccessibleInterval<V>> C iterate(
			BinaryOperation<A, B, C> op, int[] selectedDims, A in1, B in2, C out) {
		return iterate(op, selectedDims, in1, in2, out, null);
	}

	public static <T extends Type<T>, U extends Type<U>, V extends RealType<V>, A extends RandomAccessibleInterval<T>, B extends RandomAccessibleInterval<U>, C extends RandomAccessibleInterval<V>> C iterate(
			BinaryOperation<A, B, C> op, int[] selectedDims, A in1, B in2,
			C out, ExecutorService service) {

		Interval[] inIntervals1 = IntervalsFromDimSelection.compute(
				selectedDims, in1);

		Interval[] inIntervals2 = IntervalsFromDimSelection.compute(
				selectedDims, in2);

		Interval[] outIntervals = IntervalsFromDimSelection.compute(
				selectedDims, out);

		RandomAccessibleInterval<T>[] inRes1 = new RandomAccessibleInterval[inIntervals1.length];
		RandomAccessibleInterval<U>[] inRes2 = new RandomAccessibleInterval[inIntervals2.length];
		RandomAccessibleInterval<V>[] outRes = new RandomAccessibleInterval[outIntervals.length];

		for (int k = 0; k < inIntervals1.length; k++) {
			inRes1[k] = create(in1, inIntervals1[k]);
			inRes2[k] = create(in2, inIntervals2[k]);
			outRes[k] = create(out, outIntervals[k]);
		}

		MultithreadedOps.run(op, (A[]) inRes1, (B[]) inRes2, (C[]) outRes,
				service);

		return out;
	}

	public static <T extends Type<T>, U extends Type<U>, A extends RandomAccessibleInterval<T>, B extends RandomAccessibleInterval<U>> B iterate(
			UnaryOperation<A, B> op, int[] selectedDims, A in, B out) {
		return iterate(op, selectedDims, in, out, null);
	}

	public static <T extends Type<T>, U extends Type<U>, A extends RandomAccessibleInterval<T>, B extends RandomAccessibleInterval<U>> B iterate(
			UnaryOperation<A, B> op, int[] selectedDims, A in, B out,
			ExecutorService service) {

		Interval[] inIntervals = IntervalsFromDimSelection.compute(
				selectedDims, in);

		Interval[] outIntervals = IntervalsFromDimSelection.compute(
				selectedDims, out);

		RandomAccessibleInterval<T>[] inRes = new RandomAccessibleInterval[inIntervals.length];
		RandomAccessibleInterval<U>[] outRes = new RandomAccessibleInterval[outIntervals.length];

		for (int k = 0; k < inIntervals.length; k++) {
			inRes[k] = create(in, inIntervals[k]);
			outRes[k] = create(out, outIntervals[k]);
		}

		MultithreadedOps.run(op, (A[]) inRes, (B[]) outRes, service);
		return out;
	}

	// TODO: Ask Tobias. This is ugly.
	@SuppressWarnings({ "rawtypes" })
	private synchronized static <T extends Type<T>, I extends RandomAccessibleInterval<T>> I create(
			final I in, final Interval i) {

		RandomAccessibleInterval<T> subsetview = subsetview(in, i);

		if (in instanceof Labeling) {
			return (I) new LabelingView(subsetview,
					((NativeImgLabeling) in).factory());
		} else if (in instanceof ImgPlus) {
			ImgPlusView<T> imgPlusView = new ImgPlusView<T>(subsetview,
					((ImgPlus) in).factory());
			new CopyMetadata(new CopyNamed(), new CopySourced(),
					new CopyImageMetadata(), new CopyCalibratedSpace(i))
					.compute((ImgPlus) in, imgPlusView);
			return (I) imgPlusView;
		} else if (in instanceof Img) {
			return (I) new ImgView<T>(subsetview, ((Img) in).factory());
		}

		return (I) subsetview;
	}

	public static <T extends Type<T>> RandomAccessibleInterval<T> subsetview(
			RandomAccessibleInterval<T> in, Interval i) {

		boolean oneSizedDims = false;

		for (int d = 0; d < in.numDimensions(); d++) {
			if (in.dimension(d) == 1) {
				oneSizedDims = true;
				break;
			}
		}

		if (Intervals.equals(in, i) && !oneSizedDims)
			return in;

		IntervalView<T> res;
		if (Intervals.contains(in, i))
			res = Views.offsetInterval(in, i);
		else
			throw new IllegalArgumentException(
					"Interval must fit into src in SubsetViews.subsetView(...)");

		for (int d = i.numDimensions() - 1; d >= 0; --d)
			if (i.dimension(d) == 1 && res.numDimensions() > 1)
				res = Views.hyperSlice(res, d, 0);

		return res;
	}

}
