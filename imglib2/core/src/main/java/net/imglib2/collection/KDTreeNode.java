package net.imglib2.collection;

import net.imglib2.RealLocalizable;
import net.imglib2.Sampler;

/**
 * Abstract base class for nodes in a KDTree. A KDTreeNode has coordinates and a
 * value. It provides the coordinates via the {@link RealLocalizable} interface.
 * It provides the value via {@link Sampler#get()}.
 * 
 * @param <T>
 *            value type.
 * 
 * @author Tobias Pietzsch
 */
public abstract class KDTreeNode< T > implements RealLocalizable, Sampler< T >
{
	/**
	 * number of dimensions of the space (that is, k).
	 */
	protected final int n;

	/**
	 * coordinates of the node.
	 */
	protected final double[] pos;

	/**
	 * dimension along which this node divides the space.
	 */
	protected final int splitDimension;

	/**
	 * Left child of this node. All nodes x in the left subtree have
	 * x.pos[splitDimension] <= this.pos[splitDimension].
	 */
	public final KDTreeNode< T > left;

	/**
	 * Right child of this node. All nodes x in the right subtree have
	 * x.pos[splitDimension] >= this.pos[splitDimension].
	 */
	public final KDTreeNode< T > right;

	/**
	 * @param position
	 *            coordinates of this node
	 * @param dimension
	 *            dimension along which this node divides the space
	 * @param left
	 *            left child node
	 * @param right
	 *            right child node
	 */
	public KDTreeNode( RealLocalizable position, int dimension, final KDTreeNode< T > left, final KDTreeNode< T > right )
	{
		this.n = position.numDimensions();
		this.pos = new double[ n ];
		position.localize( pos );
		this.splitDimension = dimension;
		this.left = left;
		this.right = right;
	}

	protected KDTreeNode( final KDTreeNode< T > node )
	{
		this.n = node.n;
		this.pos = node.pos.clone();
		this.splitDimension = node.splitDimension;
		this.left = node.left;
		this.right = node.right;
	}

	/**
	 * Get the dimension along which this node divides the space.
	 * 
	 * @return splitting dimension.
	 */
	public final int getSplitDimension()
	{
		return splitDimension;
	}

	/**
	 * Get the position along {@link KDTreeNode#getSplitDimension()} where this node divides the space.
	 * 
	 * @return splitting position.
	 */
	public final double getSplitCoordinate()
	{
		return pos[ splitDimension ];
	}

	@Override
	public final int numDimensions()
	{
		return n;
	}

	@Override
	public final void localize( float[] position )
	{
		for ( int d = 0; d < n; ++d )
			position[ d ] = ( float ) pos[ d ];
	}

	@Override
	public final void localize( double[] position )
	{
		for ( int d = 0; d < n; ++d )
			position[ d ] = pos[ d ];
	}

	@Override
	public final float getFloatPosition( int d )
	{
		return ( float ) pos[ d ];
	}

	@Override
	public final double getDoublePosition( int d )
	{
		return pos[ d ];
	}

	@Override
	public abstract KDTreeNode< T > copy();

	/**
	 * Compute the squared distance from p to this node.
	 */
	public final float squDistanceTo( final float[] p )
	{
		float sum = 0;
		for ( int d = 0; d < n; ++d )
		{
			sum += ( pos[ d ] - p[ d ] ) * ( pos[ d ] - p[ d ] );
		}
		return sum;
	}

	/**
	 * Compute the squared distance from p to this node.
	 */
	public final double squDistanceTo( final double[] p )
	{
		double sum = 0;
		for ( int d = 0; d < n; ++d )
		{
			sum += ( pos[ d ] - p[ d ] ) * ( pos[ d ] - p[ d ] );
		}
		return sum;
	}

	/**
	 * Compute the squared distance from p to this node.
	 */
	public final double squDistanceTo( final RealLocalizable p )
	{
		double sum = 0;
		for ( int d = 0; d < n; ++d )
		{
			sum += ( pos[ d ] - p.getDoublePosition( d ) ) * ( pos[ d ] - p.getDoublePosition( d ) );
		}
		return sum;
	}
}
