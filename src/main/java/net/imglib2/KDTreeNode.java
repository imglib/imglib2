package net.imglib2;

import java.util.function.IntFunction;

/**
 * Proxy for a node in a KDTree. A KDTreeNode has coordinates and a value. It
 * provides the coordinates via the {@link RealLocalizable} interface. It
 * provides the value via {@link Sampler#get()}.
 *
 * @param <T>
 *            value type.
 *
 * @author Tobias Pietzsch
 */
public class KDTreeNode< T > implements RealLocalizable, Sampler< T >
{
	private final KDTree< T > tree;

	private int nodeIndex;

	private IntFunction< T > values;

	KDTreeNode( final KDTree< T > tree )
	{
		this.tree = tree;
	}

	/**
	 * Make this proxy refer to the given {@code nodeIndex} in the associated tree.
	 *
	 * @return {@code this}
	 */
	public KDTreeNode< T > setNodeIndex( final int nodeIndex )
	{
		this.nodeIndex = nodeIndex;
		return this;
	}

	/**
	 * Get the {@code nodeIndex} which this proxy currently refers to.
	 */
	public int nodeIndex() {
		return nodeIndex;
	}

	/**
	 * Left child of this node. All nodes x in the left subtree have
	 * {@code x.pos[splitDimension] <= this.pos[splitDimension]}.
	 *
	 * @deprecated
	 * {@link KDTreeNode} is now a re-usable proxy (like {@code NativeType}).
	 * To work with existing code, {@link KDTreeNode#left()}, {@link
	 * KDTreeNode#right()}, {@link KDTree#getRoot()} etc create new objects in each
	 * call, instead of re-using existing proxies.
	 * Code using that should be rewritten to reuse proxies, if possible.
	 */
	@Deprecated
	public KDTreeNode< T > left()
	{
		return tree.left( this );
	}

	/**
	 * Right child of this node. All nodes x in the right subtree have
	 * {@code x.pos[splitDimension] >= this.pos[splitDimension]}.
	 *
	 * @deprecated
	 * {@link KDTreeNode} is now a re-usable proxy (like {@code NativeType}).
	 * To work with existing code, {@link KDTreeNode#left()}, {@link
	 * KDTreeNode#right()}, {@link KDTree#getRoot()} etc create new objects in each
	 * call, instead of re-using existing proxies.
	 * Code using that should be rewritten to reuse proxies, if possible.
	 */
	@Deprecated
	public KDTreeNode< T > right()
	{
		return tree.right( this );
	}

	/**
	 * Get the dimension along which this node divides the space.
	 *
	 * @return splitting dimension.
	 */
	public final int getSplitDimension()
	{
		return tree.impl.splitDimension( nodeIndex );
	}

	/**
	 * Get the position along {@link net.imglib2.KDTreeNode#getSplitDimension()} where this
	 * node divides the space.
	 *
	 * @return splitting position.
	 */
	public final double getSplitCoordinate()
	{
		return getDoublePosition( getSplitDimension() );
	}

	@Override
	public int numDimensions()
	{
		return tree.numDimensions();
	}

	@Override
	public double getDoublePosition( final int d )
	{
		return tree.impl.getDoublePosition( nodeIndex, d );
	}

	@Override
	public T get()
	{
		if ( values == null )
			values = tree.treeData().valuesSupplier().get();
		return values.apply( nodeIndex );
	}

	@Override
	public KDTreeNode< T > copy()
	{
		final KDTreeNode< T > copy = new KDTreeNode<>( tree );
		copy.setNodeIndex( nodeIndex );
		return copy;
	}

	/**
	 * Compute the squared distance from p to this node.
	 */
	public final float squDistanceTo( final float[] p )
	{
		return tree.impl.squDistance( nodeIndex, p );
	}

	/**
	 * Compute the squared distance from p to this node.
	 */
	public double squDistanceTo( final double[] p )
	{
		return tree.impl.squDistance( nodeIndex, p );
	}

	/**
	 * Compute the squared distance from p to this node.
	 */
	public final double squDistanceTo( final RealLocalizable p )
	{
		return tree.impl.squDistance( nodeIndex, p );
	}
}
