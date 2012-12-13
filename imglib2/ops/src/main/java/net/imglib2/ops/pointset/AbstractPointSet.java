package net.imglib2.ops.pointset;

import net.imglib2.IterableRealInterval;
import net.imglib2.Positionable;
import net.imglib2.RealPositionable;

public abstract class AbstractPointSet implements PointSet {

	// -- instance variables --

	protected long[] minBounds;
	protected long[] maxBounds;

	// -- abstract methods --

	/**
	 * Returns the lower bound of the space containing the PointSet. This can be
	 * an expensive operation (potentially iterating the whole set to calculate).
	 * These results are cached when possible. Subsequent calls to translate()
	 * will invalidate bounds.
	 */
	abstract protected long[] findBoundMin();

	/**
	 * Returns the upper bound of the space containing the PointSet. This can be
	 * an expensive operation (potentially iterating the whole set to calculate).
	 * These results are cached when possible. Subsequent calls to translate()
	 * will invalidate bounds.
	 */
	abstract protected long[] findBoundMax();
	

	// -- IterableInterval methods --

	@Override
	public long[] firstElement() {
		return cursor().get();
	}

	@Override
	public Object iterationOrder() {
		return new Object(); // default to unknown order
	}

	@Override
	public boolean equalIterationOrder(IterableRealInterval<?> f) {
		return false;
	}

	@Override
	public PointSetIterator cursor() {
		return iterator();
	}

	@Override
	public PointSetIterator localizingCursor() {
		return iterator();
	}

	@Override
	public double realMin(int d) {
		maybeSetBounds();
		return minBounds[d];
	}

	@Override
	public void realMin(double[] min) {
		for (int i = 0; i < min.length; i++) {
			min[i] = min(i);
		}
	}

	@Override
	public void realMin(RealPositionable min) {
		for (int i = 0; i < min.numDimensions(); i++) {
			min.setPosition(min(i), i);
		}
	}

	@Override
	public double realMax(int d) {
		maybeSetBounds();
		return maxBounds[d];
	}

	@Override
	public void realMax(double[] max) {
		for (int i = 0; i < max.length; i++) {
			max[i] = max(i);
		}
	}

	@Override
	public void realMax(RealPositionable max) {
		for (int i = 0; i < max.numDimensions(); i++) {
			max.setPosition(max(i), i);
		}
	}

	@Override
	public long min(int d) {
		maybeSetBounds();
		return minBounds[d];
	}

	@Override
	public void min(long[] min) {
		for (int i = 0; i < min.length; i++) {
			min[i] = min(i);
		}
	}

	@Override
	public void min(Positionable min) {
		for (int i = 0; i < min.numDimensions(); i++) {
			min.setPosition(min(i), i);
		}
	}

	@Override
	public long max(int d) {
		maybeSetBounds();
		return maxBounds[d];
	}

	@Override
	public void max(long[] max) {
		for (int i = 0; i < max.length; i++) {
			max[i] = max(i);
		}
	}

	@Override
	public void max(Positionable max) {
		for (int i = 0; i < max.numDimensions(); i++) {
			max.setPosition(max(i), i);
		}
	}

	@Override
	public void dimensions(long[] dimensions) {
		for (int i = 0; i < dimensions.length; i++) {
			dimensions[i] = dimension(i);
		}
	}

	@Override
	public long dimension(int d) {
		maybeSetBounds();
		return maxBounds[d] - minBounds[d] + 1;
	}

	// -- protected api --
	
	protected void invalidateBounds() {
		maxBounds = null;
		minBounds = null;
	}

	// -- private helpers --

	private void maybeSetBounds() {
		if (boundsInvalid()) findBounds();
	}
	
	private boolean boundsInvalid() {
		return maxBounds == null || minBounds == null;
	}
	
	private void findBounds() {
		maxBounds = findBoundMax().clone();
		minBounds = findBoundMin().clone();
	}
	
}
