package net.imglib2.algorithm.region.localneighborhood;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.outofbounds.OutOfBoundsMirrorFactory;
import net.imglib2.outofbounds.OutOfBoundsMirrorFactory.Boundary;

public class EllipseNeighborhood<T, IN extends RandomAccessibleInterval<T>> extends AbstractNeighborhood<T, IN> {
	
	/*
	 * CONSTRUCTORS
	 */
	
	public EllipseNeighborhood(final IN source, final long[] center, final long[] radiuses, final OutOfBoundsFactory<T, IN> outOfBounds) {
		super(source.numDimensions(), outOfBounds);
		setSpan(radiuses);
		setPosition(center);
		updateSource(source);
	}

	public EllipseNeighborhood(final IN source, final long[] center, final long[] radiuses) {
		this(source, center, radiuses, new OutOfBoundsMirrorFactory<T, IN>(Boundary.DOUBLE));
	}
	
	/*
	 * METHODS
	 */
	
	
	@Override
	public long size() {
		long pixel_count = 0;
		final int[] local_rxs = new int [ (int) (span[1]  +  1) ];
		int local_rx;

		Utils.getXYEllipseBounds((int) span[0], (int) span[1], local_rxs);
		local_rx = local_rxs[0]; // middle line
		pixel_count += 2 * local_rx + 1;
		for (int i = 1; i <= span[1]; i++) {
			local_rx = local_rxs[i];
			pixel_count += 2 * (2 * local_rx + 1); // Twice because we mirror
		}

		return pixel_count;	
	}

	@Override
	public EllipseCursor<T> cursor() {
		return new EllipseCursor<T>(this);
	}

	@Override
	public EllipseCursor<T> localizingCursor() {
		return cursor();
	}

	@Override
	public EllipseCursor<T> iterator() {
		return cursor();
	}

	@Override
	public EllipseNeighborhood<T, IN> copy() {
		return new EllipseNeighborhood<T, IN>(source, center, span, outOfBounds);
	}

}
