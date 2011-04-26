package net.imglib2.kdtree;

import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

import net.imglib2.EuclideanSpace;
import net.imglib2.RealLocalizable;
import net.imglib2.util.KthElement;

public class KDTree< T > implements EuclideanSpace // TODO: , IterableRealInterval< T >
{
	/**
	 * the number of dimensions.
	 */
	final protected int n;

	final protected Node< T > root;

	/**
	 * Construct a KDTree from the elements in the given list.
	 * 
	 * <p>
	 * Note that the constructor can be called with the same list for both
	 * {@code values == positions} if {@code T extends RealLocalizable}.
	 * </p>
	 * 
	 * @param values
	 *            a list of values
	 * @param positions
	 *            a list of positions corresponding to the values
	 */
	public < L extends RealLocalizable > KDTree( final List< T > values, final List< L > positions )
	{
		assert values.size() == positions.size();

		this.n = positions.get( 0 ).numDimensions();

		// test that dimensionality is preserved
		assert ( verifyDimensions( positions, n ) );

		if ( values == positions )
		{
			if ( positions instanceof java.util.RandomAccess )
				root = makeNode( positions, 0, positions.size() - 1, 0 );
			else
				root = makeNode( positions.listIterator(), positions.listIterator( positions.size() ), 0 );
		}
		else
		{
			final int[] permutation = new int[ positions.size() ];
			for ( int k = 0; k < permutation.length; ++k )
				permutation[ k ] = k;

			if ( positions instanceof java.util.RandomAccess )
				root = makeNode( positions, 0, positions.size() - 1, 0, values, permutation );
			else
				root = makeNode( positions.listIterator(), positions.listIterator( positions.size() ), 0, values, permutation );
		}
	}

	protected static < L extends RealLocalizable > boolean verifyDimensions( final List< L > positions, final int n )
	{
		for ( final L position : positions )
			if ( position.numDimensions() != n )
				return false;
		return true;
	}

	public static final class DimComparator< L extends RealLocalizable > implements Comparator< L >
	{
		final int d;

		public DimComparator( int d )
		{
			this.d = d;
		}

		@Override
		public int compare( L o1, L o2 )
		{
			final float diff = o1.getFloatPosition( d ) - o2.getFloatPosition( d );
			return ( diff < 0 ) ? -1 : ( diff > 0 ? 1 : 0 );
		}
	}

	protected < L extends RealLocalizable > Node< T > makeNode( final List< L > positions, final int i, final int j, final int d, final List< T > values, final int[] permutation )
	{
		if ( j > i )
		{
			final int k = i + ( j - i ) / 2;
			KthElement.kthElement( i, j, k, positions, permutation, new DimComparator< L >( d ) );

			final int dChild = ( d + 1 == n ) ? 0 : d + 1;
			return new Node< T >( values.get( permutation[ k ] ), positions.get( k ), d, makeNode( positions, i, k - 1, dChild, values, permutation ), makeNode( positions, k + 1, j, dChild, values, permutation ) );
		}
		else if ( j == i )
		{
			return new Node< T >( values.get( permutation[ i ] ), positions.get( i ), d, null, null );
		}
		else
		{
			return null;
		}
	}

	protected < L extends RealLocalizable > Node< T > makeNode( final ListIterator< L > first, final ListIterator< L > last, final int d, final List< T > values, final int[] permutation )
	{
		final int i = first.nextIndex();
		final int j = last.previousIndex();
		if ( j > i )
		{
			final int k = i + ( j - i ) / 2;
			KthElement.kthElement( first, last, k, permutation, new DimComparator< L >( d ) );
			first.previous();
			final L current = first.next();

			final int dChild = ( d + 1 == n ) ? 0 : d + 1;

			// Node< T > right = makeNode( elements, k + 1, j, dChild );
			for ( int c = j - last.previousIndex(); c > 0; --c )
				last.next();
			Node< T > right = makeNode( first, last, dChild, values, permutation );

			// Node< T > left = makeNode( elements, i, k - 1, dChild );
			for ( int c = first.nextIndex() - i; c > 0; --c )
				first.previous();
			for ( int c = last.nextIndex() - k; c > 0; --c )
				last.previous();
			Node< T > left = makeNode( first, last, dChild, values, permutation );

			return new Node< T >( values.get( permutation[ k ] ), current, d, left, right );
		}
		else if ( j == i )
		{
			final L current = first.next();
			return new Node< T >( values.get( permutation[ i ] ), current, d, null, null );
		}
		else
		{
			return null;
		}
	}

	@SuppressWarnings( "unchecked" )
	protected < L extends RealLocalizable > Node< T > makeNode( final List< L > elements, final int i, final int j, final int d )
	{
		if ( j > i )
		{
			final int k = i + ( j - i ) / 2;
			KthElement.kthElement( i, j, k, elements, new DimComparator< L >( d ) );

			final int dChild = ( d + 1 == n ) ? 0 : d + 1;
			return new Node< T >( ( T ) elements.get( k ), elements.get( k ), d, makeNode( elements, i, k - 1, dChild ), makeNode( elements, k + 1, j, dChild ) );
		}
		else if ( j == i )
		{
			return new Node< T >( ( T ) elements.get( i ), elements.get( i ), d, null, null );
		}
		else
		{
			return null;
		}
	}

	@SuppressWarnings( "unchecked" )
	protected < L extends RealLocalizable > Node< T > makeNode( final ListIterator< L > first, final ListIterator< L > last, final int d )
	{
		final int i = first.nextIndex();
		final int j = last.previousIndex();
		if ( j > i )
		{
			final int k = i + ( j - i ) / 2;
			KthElement.kthElement( first, last, k, new DimComparator< L >( d ) );
			first.previous();
			L current = first.next();

			final int dChild = ( d + 1 == n ) ? 0 : d + 1;

			// Node< T > right = makeNode( elements, k + 1, j, dChild );
			for ( int c = j - last.previousIndex(); c > 0; --c )
				last.next();
			Node< T > right = makeNode( first, last, dChild );

			// Node< T > left = makeNode( elements, i, k - 1, dChild );
			for ( int c = first.nextIndex() - i; c > 0; --c )
				first.previous();
			for ( int c = last.nextIndex() - k; c > 0; --c )
				last.previous();
			Node< T > left = makeNode( first, last, dChild );

			return new Node< T >( ( T ) current, current, d, left, right );
		}
		else if ( j == i )
		{
			L current = first.next();
			return new Node< T >( ( T ) current, current, d, null, null );
		}
		else
		{
			return null;
		}
	}

	public Node< T > getRoot()
	{
		return root;
	}

	@Override
	public int numDimensions()
	{
		return n;
	}

	public String toString( Node< T > node, String indent )
	{
		if ( node == null )
			return "";

		return indent + "- " + node.toString() + "\n" + toString( node.left, indent + "  " ) + toString( node.right, indent + "  " );
	}

	public String toString()
	{
		return toString( root, "" );
	}
}
