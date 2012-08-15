package net.imglib2.algorithm.region.localneighborhood;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.region.hypersphere.HyperSphereCursor;
import net.imglib2.outofbounds.OutOfBoundsFactory;

public class HyperSphereNeighborhood<T, IN extends RandomAccessibleInterval<T>>
		extends AbstractNeighborhood<T, IN> {

	private long radius;

	/*
	 * CONSTRUCTORS
	 */
	public HyperSphereNeighborhood(final IN source,
			final OutOfBoundsFactory<T, IN> outOfBounds, final long radius) {
		super(source.numDimensions(), outOfBounds);
		this.radius = radius;
	}

	/*
	 * CONSTRUCTORS
	 */
	public HyperSphereNeighborhood(final int numDims,
			final OutOfBoundsFactory<T, IN> outOfBounds, final long radius) {
		super(numDims, outOfBounds);
		this.radius = radius;
	}

	@Override
	public HyperSphereCursor<T> cursor() {
		return new HyperSphereCursor<T>(extendedSource, center, radius);
	}

	@Override
	public HyperSphereCursor<T> localizingCursor() {
		return cursor();
	}

	@Override
	public HyperSphereCursor<T> iterator() {
		return cursor();
	}

	@Override
	public long size() {
		return computeSize();
	}

	/**
	 * Compute the number of elements for iteration
	 */
	protected long computeSize() {
		final HyperSphereCursor<T> cursor = new HyperSphereCursor<T>(source,
				this.center, radius);

		// "compute number of pixels"
		long size = 0;
		while (cursor.hasNext()) {
			cursor.fwd();
			++size;
		}

		return size;
	}

	@Override
	public AbstractNeighborhood<T, IN> copy() {
		if (source != null)
			return new HyperSphereNeighborhood<T, IN>(source, outOfBounds,
					radius);
		else
			return new HyperSphereNeighborhood<T, IN>(n, outOfBounds, radius);
	}

}
