package net.imglib2.img.subset;

import net.imglib2.Cursor;
import net.imglib2.FlatIterationOrder;
import net.imglib2.Interval;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.Type;
import net.imglib2.util.IntervalIndexer;
import net.imglib2.view.IterableRandomAccessibleInterval;
import net.imglib2.view.Views;

public class IterableSubsetView<T extends Type<T>> extends
		IterableRandomAccessibleInterval<T> {

	private boolean isOptimizable;

	private int planeOffset;

	private int numPlaneDims;

	private RandomAccessibleInterval<T> src;

	@SuppressWarnings("unchecked")
	public IterableSubsetView(RandomAccessibleInterval<T> src, Interval interval) {
		super(SubsetViews.subsetView(src, interval));
		this.src = src;

		isOptimizable = false;
		planeOffset = 1;

		if (!SubsetViews.intervalEquals(this, interval)) {

			if (src instanceof IterableSubsetView) {
				src = ((IterableSubsetView<T>) src).src;
			}

			if ((src instanceof IterableInterval)
					&& ((IterableInterval<T>) src).iterationOrder() instanceof FlatIterationOrder) {
				isOptimizable = true;
				for (int d = 0; d < interval.numDimensions(); d++) {
					if (interval.dimension(d) > 1) {

						// TODO: this can be handled in the
						// IterableSubsetViewCursor
						// (hasNext and fwd must be generalized)
						if (interval.dimension(d) != src.dimension(d)) {
							isOptimizable = false;
							break;
						}

						numPlaneDims++;

						if (numPlaneDims != d + 1) {
							isOptimizable = false;
							break;
						}
					}

				}

				if (isOptimizable) {

					long[] iterDims = new long[src.numDimensions()
							- numPlaneDims];
					long[] cubePos = iterDims.clone();
					for (int d = numPlaneDims; d < src.numDimensions(); d++) {
						iterDims[d - numPlaneDims] = src.dimension(d);
						cubePos[d - numPlaneDims] = interval.min(d);
					}

					if (iterDims.length == 0) {
						planeOffset = 0;
					} else {
						planeOffset = (int) (IntervalIndexer.positionToIndex(
								cubePos, iterDims) * super.size());

					}
				}
			}
		}
	}

	@Override
	public Cursor<T> cursor() {
		if (isOptimizable)
			return new IterableSubsetViewCursor<T>(
					Views.iterable(src).cursor(), (int) super.size(),
					planeOffset, numPlaneDims);
		else
			return Views.iterable(super.interval).cursor();
	}

	@Override
	public Cursor<T> localizingCursor() {
		if (isOptimizable)
			return new IterableSubsetViewCursor<T>(Views.iterable(src)
					.localizingCursor(), (int) super.size(), planeOffset,
					numPlaneDims);
		else
			return Views.iterable(super.interval).cursor();
	}

}
