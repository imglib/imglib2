package net.imglib2.kdtree;

import net.imglib2.FinalRealInterval;
import net.imglib2.RealInterval;


/**
 * Stores the positions of the nodes in a KDTree and provides access to them.
 * <p>
 * Currently, there are two implementations:
 * - {@link Nested} stores the positions as a {@code double[][]} where {@code
 *   positions[d][i]} is dimension {@code d} of the {@code i}-th point. This
 *   allows for a total of 2^31-8 nodes but doesn't keep the positions
 *   contiguous in memory.
 * - {@link Flat} stores the positions as a {@code double[]}where {@code
 *   positions[d + i*n]} is dimension {@code d} of the {@code i}-th point,
 *   with {@code n} the number of dimensions. This means that the positions
 *   are contiguous in memory but the number of nodes is limited to (2^31-8)/n.
 *  <p>
 * {@link #getNestedPositions()} returns positions in nested {@code double[][]}
 * (which is created if class is {@link Flat}). {@link #getFlatPositions()}
 * returns flat {@code double[]} if class is {@link Flat}, otherwise {@code null}.
 */
public abstract class KDTreePositions {

	protected final int numDimensions;

	protected final int numPoints;
	private volatile RealInterval boundingBox;

	static class Nested extends KDTreePositions {
		private final double[][] positions;

		Nested(final double[][] positions) {
			super(positions.length, positions[0].length);
			this.positions = positions;
		}

		@Override public double get(final int i, final int d) {
			return positions[d][i];
		}

		@Override
		public double[] getFlatPositions() {
			// positions in this case might be too large to fit in a single array
			return null;
		}

		@Override
		public double[][] getNestedPositions() {
			return positions;
		}

		@Override
		protected RealInterval createBoundingBox() {
			final double[] min = new double[numDimensions];
			final double[] max = new double[numDimensions];
			KDTreeUtils.computeMinMax(positions, min, max);
			return FinalRealInterval.wrap(min, max);
		}
	}

	static class Flat extends KDTreePositions {
		private final double[] positions;

		Flat(final double[] positions, final int numDimensions) {
			super(numDimensions, positions.length / numDimensions);
			this.positions = positions;
		}

		@Override
		public double get(final int i, final int d) {
			return positions[numDimensions * i + d];
		}

		@Override
		public double[] getFlatPositions() {
			return positions;
		}

		@Override
		public double[][] getNestedPositions() {
			return KDTreeUtils.unflatten(positions, numDimensions);
		}

		@Override
		protected RealInterval createBoundingBox() {
			final double[] min = new double[numDimensions];
			final double[] max = new double[numDimensions];
			KDTreeUtils.computeMinMax(positions, min, max);
			return FinalRealInterval.wrap(min, max);
		}
	}

	KDTreePositions(final int numDimensions, final int numPoints) {
		this.numDimensions = numDimensions;
		this.numPoints = numPoints;
	}

	/**
	 * Get the coordinates of the node {@code i} in dimension {@code d}.
	 *
	 * @return the coordinate
	 */
	public abstract double get(final int i, final int d);

	/**
	 * Get positions of points in the tree as a flat {@code double[]} array
	 * where {@code positions[d + i*n]} is dimension {@code d} of the {@code i}-th
	 * point.
	 * <p>
	 * For serialisation and usage by the tree.
	 * <p>
	 * Internal storage may be nested in a {@code double[][]} array. In
	 * this case, there may be too many points to fit in a single {@code
	 * double[]} array, so {@code null} is returned.
	 */
	public abstract double[] getFlatPositions();

	/**
	 * Get positions of points in the tree as a nested {@code double[][]} array
	 * where {@code positions[d][i]} is dimension {@code d} of the {@code i}-th
	 * point.
	 * <p>
	 * For serialisation and usage by the tree.
	 * <p>
	 * Internal storage may be flattened into single {@code double[]} array. In
	 * this case, the nested {@code double[][]} array is created here.
	 */
	public abstract double[][] getNestedPositions();

	protected abstract RealInterval createBoundingBox();

	public int numDimensions() {
		return numDimensions;
	}

	public int numPoints() {
		return numPoints;
	}

	public int size() {
		return numPoints;
	}

	public RealInterval boundingBox() {
		if (boundingBox == null)
			boundingBox = createBoundingBox();
		return boundingBox;
	}

	public static KDTreePositions create(final double[][] positions) {
		return new Nested( positions );
	}

	public static KDTreePositions create(final double[] positions, final int numDimensions) {
		return new Flat(positions, numDimensions);
	}
}
