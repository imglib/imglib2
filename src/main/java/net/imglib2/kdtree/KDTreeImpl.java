package net.imglib2.kdtree;

import net.imglib2.RealLocalizable;

import static net.imglib2.kdtree.KDTreeUtils.leftChildIndex;
import static net.imglib2.kdtree.KDTreeUtils.parentIndex;
import static net.imglib2.kdtree.KDTreeUtils.rightChildIndex;

/**
 * Represents the tree structure, and provides access to node coordinates (but
 * not values).
 * <p>
 * The nodes in the tree are arranged in Eytzinger layout (children of i are at
 * 2i and 2i+1). Additionally, pivot indices are chosen such that "leaf layers"
 * are filled from the left.
 * <p>
 * For example 10 nodes will always be arranged like this:
 * <pre>
 *            0
 *         /     \
 *       1         2
 *     /   \     /   \
 *    3     4   5     6
 *   / \   /
 *  7   8 9
 * </pre>
 *
 * never like this:
 * <pre>
 *            0
 *         /     \
 *       1         2
 *     /   \     /   \
 *    3     4   5     6
 *   /         /     /
 *  7         8     9
 * </pre>
 *
 * By choosing pivots in this way, the tree structure is fully
 * determined. For every node index, the child indices can be calculated
 * without dependent reads. And iff the calculated child index is less
 * than the number of nodes, the child exists.
 */
public abstract class KDTreeImpl
{
	final int numDimensions;

	private final int numPoints;

	static class Nested extends KDTreeImpl
	{
		private final double[][] positions;

		Nested( final double[][] positions )
		{
			super( positions.length, positions[ 0 ].length );
			this.positions = positions;
		}

		@Override
		public double getDoublePosition( final int i, final int d )
		{
			return positions[ d ][ i ];
		}
	}

	static class Flat extends KDTreeImpl
	{
		private final double[] positions;

		Flat( final double[] positions, final int numDimensions )
		{
			super( numDimensions, positions.length / numDimensions );
			this.positions = positions;
		}

		@Override
		public double getDoublePosition( final int i, final int d )
		{
			return positions[ numDimensions * i + d ];
		}
	}

	KDTreeImpl( final int numDimensions, final int numPoints )
	{
		this.numDimensions = numDimensions;
		this.numPoints = numPoints;
	}

	/**
	 * Get the root node of the tree.
	 *
	 * @return index of the root node
	 */
	public int root()
	{
		return 0;
	}

	/**
	 * Get the left child of node {@code i}.
	 *
	 * @param i
	 * 		node index
	 *
	 * @return index of left child or {@code -1} if no left child exists
	 */
	public int left( final int i )
	{
		return ifExists( leftChildIndex( i ) );
	}

	/**
	 * Get the right child of node {@code i}.
	 *
	 * @param i
	 * 		node index
	 *
	 * @return index of right child or {@code -1} if no right child exists
	 */
	public int right( final int i )
	{
		return ifExists( rightChildIndex( i ) );
	}

	/**
	 * Get the parent of node {@code i}.
	 *
	 * @param i
	 * 		node index
	 *
	 * @return index of parent
	 */
	public int parent( final int i )
	{
		return i == root() ? -1 : parentIndex( i );
	}

	/**
	 * If a node with index {@code i} exists, returns {@code i}.
	 * Otherwise, returns {@code -1}.
	 */
	private int ifExists( final int i )
	{
		return i < numPoints ? i : -1;
	}

	/**
	 * Get the dimension along which node {@code i} divides the space.
	 *
	 * @param i
	 * 		node index
	 *
	 * @return splitting dimension.
	 */
	public int splitDimension( final int i )
	{
		return ( 31 - Integer.numberOfLeadingZeros( i + 1 ) ) % numDimensions;
	}

	public abstract double getDoublePosition( final int i, final int d );

	/**
	 * Compute the squared distance from node {@code i} to {@code pos}.
	 */
	public float squDistance( final int i, final float[] pos )
	{
		float sum = 0;
		for ( int d = 0; d < numDimensions; ++d )
		{
			final float diff = pos[ d ] - ( float ) getDoublePosition( i, d );
			sum += diff * diff;
		}
		return sum;
	}

	/**
	 * Compute the squared distance from node {@code i} to {@code pos}.
	 */
	public double squDistance( final int i, final double[] pos )
	{
		double sum = 0;
		for ( int d = 0; d < numDimensions; ++d )
		{
			final double diff = pos[ d ] - getDoublePosition( i, d );
			sum += diff * diff;
		}
		return sum;
	}

	/**
	 * Compute the squared distance from node {@code i} to {@code pos}.
	 */
	public double squDistance( final int i, final RealLocalizable pos )
	{
		double sum = 0;
		for ( int d = 0; d < numDimensions; ++d )
		{
			final double diff = pos.getDoublePosition( d ) - getDoublePosition( i, d );
			sum += diff * diff;
		}
		return sum;
	}

	public int numDimensions()
	{
		return numDimensions;
	}

	public int size()
	{
		return numPoints;
	}

	public int depth()
	{
		return 32 - Integer.numberOfLeadingZeros( numPoints );
	}

	public static KDTreeImpl create( final double[][] positions )
	{
		return new Nested( positions );
	}

	public static KDTreeImpl create( final double[] positions, final int numDimensions )
	{
		return new Flat( positions, numDimensions );
	}
}
