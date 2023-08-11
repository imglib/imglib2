package net.imglib2.kdtree;

import net.imglib2.RealLocalizable;

import static net.imglib2.kdtree.KDTreeUtils.leftChildIndex;
import static net.imglib2.kdtree.KDTreeUtils.parentIndex;
import static net.imglib2.kdtree.KDTreeUtils.rightChildIndex;

/**
 * Stores the positions of the nodes in a KDTree and provides access to them.
 * <p>
 * Currently, there are two implementations:
 * - {@link Nested} stores the positions as a {@code double[][]}, allowing a
 *   total of 2^31-8 nodes but doesn't keep the positions contiguous in memory.
 * - {@link Flat} stores the positions as a {@code double[]}, meaning that the
 *   positions are contiguous in memory but the number of nodes is limited to
 *   (2^31-8)/numDimensions.
 */
public abstract class KDTreePositions {

	protected final int numDimensions;

	protected final int numPoints;

	static class Nested extends KDTreePositions {
		private final double[][] positions;

		Nested(final double[][] positions) {
			super(positions.length, positions[0].length);
			this.positions = positions;
		}

		@Override
		public double get(final int i, final int d) {
			return positions[d][i];
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

	public int numDimensions() {
		return numDimensions;
	}

	public int size() {
		return numPoints;
	}

	public static KDTreePositions create(final double[][] positions) {
		return new Nested( positions );
	}

	public static KDTreePositions create(final double[] positions, final int numDimensions) {
		return new Flat(positions, numDimensions);
	}
}
