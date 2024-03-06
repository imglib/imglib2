package net.imglib2;

import java.util.List;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import net.imglib2.converter.AbstractConvertedIterableRealInterval;
import net.imglib2.converter.AbstractConvertedRealCursor;
import net.imglib2.kdtree.KDTreeData;
import net.imglib2.kdtree.KDTreeImpl;

import static net.imglib2.kdtree.KDTreeData.PositionsLayout.FLAT;

public class KDTree< T > implements EuclideanSpace, IterableRealInterval< T >
{
	private final KDTreeData< T > treeData;

	final KDTreeImpl impl;

	/**
	 * Access to underlying data for serialization.
	 */
	public KDTreeData< T > treeData()
	{
		return treeData;
	}

	/**
	 * Access to pure coordinate kD Tree implementation.
	 */
	public KDTreeImpl impl()
	{
		return impl;
	}

	/**
	 * Construct a KDTree from the elements in the given list.
	 *
	 * <p>
	 * Note that the constructor can be called with the same list for both
	 * {@code values == positions} if {@code T extends RealLocalizable}.
	 * </p>
	 *
	 * @param values
	 * 		a list of values
	 * @param positions
	 * 		a list of positions corresponding to the values
	 */
	public < L extends RealLocalizable > KDTree( final List< T > values, final List< L > positions )
	{
		this( verifySize(values, positions), values, positions );
	}

	private static int verifySize( final List< ? > values, final List< ? > positions )
	{
		if ( values.size() != positions.size() )
			throw new IllegalArgumentException( "The list of values and the list of positions provided to KDTree should have the same size." );
		if ( positions.isEmpty() )
			throw new IllegalArgumentException( "List of positions is empty. At least one point is requires to construct a KDTree." );
		return values.size();
	}

	/**
	 * Construct a KDTree from the elements of the given
	 * {@link IterableRealInterval}.
	 *
	 * @param interval
	 *            elements in the tree are obtained by iterating this
	 */
	public KDTree( final IterableRealInterval< T > interval )
	{
		this( verifySize( interval ), interval, positionsIterable( interval ) );
	}

	private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

	private static int verifySize( final IterableRealInterval< ? > interval )
	{
		final long size = interval.size();
		if ( size > MAX_ARRAY_SIZE )
			throw new IllegalArgumentException( "Interval contains too many points to store in KDTree" );
		else if ( size <= 0 )
			throw new IllegalArgumentException( "Interval is empty. At least one point is requires to construct a KDTree." );
		return ( int ) size;
	}

	private static < A > Iterable< RealLocalizable > positionsIterable( IterableRealInterval< A > sourceInterval )
	{
		return new AbstractConvertedIterableRealInterval< A, RealLocalizable >( sourceInterval )
		{

			class Cursor extends AbstractConvertedRealCursor< A, RealLocalizable >
			{
				Cursor( final RealCursor< A > source )
				{
					super( source );
				}

				@Override
				public RealLocalizable get()
				{
					return source;
				}

				@Override
				public Cursor copy()
				{
					return new Cursor( source.copy() );
				}
			}

			@Override
			public AbstractConvertedRealCursor< A, RealLocalizable > cursor()
			{
				return new Cursor( sourceInterval.cursor() );
			}

			@Override
			public AbstractConvertedRealCursor< A, RealLocalizable > localizingCursor()
			{
				return new Cursor( sourceInterval.localizingCursor() );
			}
		};
	}

	public < L extends RealLocalizable > KDTree( final int numPoints, final Iterable< T > values, final Iterable< L > positions )
	{
		// TODO make storeValuesAsNativeImg a parameter
		this( KDTreeData.create( numPoints, values, positions, true ) );
	}

	// construct with pre-built data, e.g., from deserialization
	public KDTree( final KDTreeData< T > data )
	{
		treeData = data;
		impl = ( data.layout() == FLAT )
				? KDTreeImpl.create( data.flatPositions(), data.numDimensions() )
				: KDTreeImpl.create( data.positions() );
	}

	/**
	 * Get the root node.
	 *
	 * @return the root node.
	 *
	 * @deprecated
	 * {@link KDTreeNode} is now a re-usable proxy (like {@code NativeType}).
	 * To work with existing code, {@link KDTreeNode#left()}, {@link
	 * KDTreeNode#right()}, {@link KDTree#getRoot()} etc create new objects in each
	 * call, instead of re-using existing proxies.
	 * Code using that should be rewritten to reuse proxies, if possible.
	 */
	@Deprecated
	public KDTreeNode< T > getRoot()
	{
		return new KDTreeNode<>( this ).setNodeIndex( impl.root() );
	}

	@Override
	public int numDimensions()
	{
		return impl.numDimensions();
	}

	@Override
	public double realMin( final int d )
	{
		return treeData.boundingBox().realMin( d );
	}

	@Override
	public double realMax( final int d )
	{
		return treeData.boundingBox().realMax( d );
	}

	@Override
	public KDTreeCursor cursor()
	{
		return new KDTreeCursor();
	}

	public final class KDTreeCursor extends KDTreeNode< T > implements RealCursor< T >
	{
		KDTreeCursor()
		{
			super( KDTree.this );
			reset();
		}

		@Override
		public void fwd()
		{
			setNodeIndex( nodeIndex() + 1 );
		}

		@Override
		public void reset()
		{
			setNodeIndex( -1 );
		}

		@Override
		public boolean hasNext()
		{
			return nodeIndex() < impl.size() - 1;
		}

		@Override
		public KDTreeCursor copy()
		{
			final KDTreeCursor copy = new KDTreeCursor();
			copy.setNodeIndex( nodeIndex() );
			return copy;
		}
	}

	@Override
	public KDTreeCursor localizingCursor()
	{
		return cursor();
	}

	@Override
	public KDTreeCursor iterator()
	{
		return cursor();
	}

	@Override
	public long size()
	{
		return impl.size();
	}

	@Override
	public Object iterationOrder()
	{
		return this; // iteration order is only compatible with ourselves
	}

	@Override
	public String toString()
	{
		return toString( impl.root(), "", createNode() );
	}

	private String toString( final int node, final String indent, final KDTreeNode< T > ref )
	{
		if ( node < 0 )
			return "";
		return indent + "- " + ref.setNodeIndex( node ).toString() + "\n"
				+ toString( impl.left( node ), indent + "  ", ref )
				+ toString( impl.right( node ), indent + "  ", ref );
	}

	/**
	 * Create a re-usable {@link KDTreeNode} proxy linked to this tree.
	 * {@link KDTreeNode#setNodeIndex(int)} can be used to point the proxy to a
	 * particular node in the tree.
	 */
	public KDTreeNode< T > createNode() {
		return new KDTreeNode<>( this );
	}

	KDTreeNode< T > left( final KDTreeNode< T > parent )
	{
		final int c = impl.left( parent.nodeIndex() );
		return c < 0 ? null : createNode().setNodeIndex( c );
	}

	KDTreeNode< T > right( final KDTreeNode< T > parent )
	{
		final int c = impl.right( parent.nodeIndex() );
		return c < 0 ? null : createNode().setNodeIndex( c );
	}
}
