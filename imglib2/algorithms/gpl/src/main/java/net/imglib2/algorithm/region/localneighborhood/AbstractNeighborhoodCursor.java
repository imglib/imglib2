package net.imglib2.algorithm.region.localneighborhood;

import net.imglib2.Bounded;
import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.outofbounds.OutOfBounds;

public abstract class AbstractNeighborhoodCursor<T> implements Cursor<T>,
		Bounded {

	protected AbstractNeighborhood<T, ? extends RandomAccessibleInterval<T>> neighborhood;
	protected final OutOfBounds<T> ra;

	/*
	 * CONSTRUCTOR
	 */

	public AbstractNeighborhoodCursor(
			AbstractNeighborhood<T, ? extends RandomAccessibleInterval<T>> neighborhood) {
		this.neighborhood = neighborhood;
		this.ra = neighborhood.extendedSource.randomAccess();
	}

	/*
	 * METHODS
	 */

	@Override
	public void localize(float[] position) {
		ra.localize(position);
	}

	@Override
	public void localize(double[] position) {
		ra.localize(position);
	}

	@Override
	public float getFloatPosition(int d) {
		return ra.getFloatPosition(d);
	}

	@Override
	public double getDoublePosition(int d) {
		return ra.getDoublePosition(d);
	}

	@Override
	public int numDimensions() {
		return ra.numDimensions();
	}

	@Override
	public T get() {
		return ra.get();
	}

	/**
	 * This dummy method just calls {@link #fwd()} multiple times.
	 */
	@Override
	public void jumpFwd(long steps) {
		for (int i = 0; i < steps; i++) {
			fwd();
		}
	}

	@Override
	public T next() {
		fwd();
		return ra.get();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException(
				"remove() is not implemented for "
						+ getClass().getCanonicalName());
	}

	@Override
	public void localize(int[] position) {
		ra.localize(position);
	}

	@Override
	public void localize(long[] position) {
		ra.localize(position);
	}

	@Override
	public int getIntPosition(int d) {
		return ra.getIntPosition(d);
	}

	@Override
	public long getLongPosition(int d) {
		return ra.getLongPosition(d);
	}

	@Override
	public boolean isOutOfBounds() {
		return ra.isOutOfBounds();
	}

}