package net.imglib2.kdtree;

import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

import net.imglib2.EuclideanSpace;
import net.imglib2.RealLocalizable;
import net.imglib2.util.KthElement;
import net.imglib2.util.Pair;

public class KDTreeWithTwoLists< T > implements EuclideanSpace //TODO: , IterableRealInterval< T >
{
	/**
	 * the number of dimensions.
	 */
	final protected int n;

	final protected Node< T > root;

	/**
	 * Construct a KDTree from the elements in the given list.
	 */
	public < L extends RealLocalizable > KDTreeWithTwoLists( final List< T > values, final List< L > positions )
	{
		assert values.size() == positions.size();

		if (values == positions)
		{
			// TODO
		}
		
		this.n = positions.get( 0 ).numDimensions();

		// test that dimensionality is preserved
		assert( verifyDimensions( positions, n ) );

		ListIteratorPair< T, L > first = new ListIteratorPair< T, L >( values.listIterator(), positions.listIterator() );
		ListIteratorPair< T, L > last = new ListIteratorPair< T, L >( values.listIterator( values.size() ), positions.listIterator( positions.size() ) );
		root = makeNode( first, last, 0 );
	}

	protected static <T extends RealLocalizable > boolean verifyDimensions( final List< T > elements, final int n )
	{
		for ( final T element : elements )
			if ( element.numDimensions() != n )
				return false;
		return true;
	}

	/**
	 * Wrap a pair of ListIterators into a common ListIterator on pairs of elements from both lists.
	 * This is useful if we have two lists whose elements are coupled by their index.
	 * For example a KDTree can be constructed with a list of coordinates and a list of corresponding values.
	 * When sorting the coordinate list, the values list should be modified accordingly.
	 * 
	 * @author Tobias Pietzsch
	 */
	public static final class ListIteratorPair< L1, L2 > implements ListIterator< Pair< L1, L2 > >
	{
		protected final ListIterator< L1 > iterator1;
		protected final ListIterator< L2 > iterator2;
		
		public ListIteratorPair( ListIterator< L1 > iterator1, ListIterator< L2 > iterator2 )
		{
			this.iterator1 = iterator1;
			this.iterator2 = iterator2;
		}

		@Override
		public boolean hasNext()
		{
			return iterator1.hasNext();
		}

		@Override
		public Pair< L1, L2 > next()
		{
			return new Pair< L1, L2 >( iterator1.next(), iterator2.next() );
		}

		@Override
		public boolean hasPrevious()
		{
			return iterator1.hasPrevious();
		}

		@Override
		public Pair< L1, L2 > previous()
		{
			return new Pair< L1, L2 >( iterator1.previous(), iterator2.previous() );
		}

		@Override
		public int nextIndex()
		{
			return iterator1.nextIndex();
		}

		@Override
		public int previousIndex()
		{
			return iterator1.previousIndex();
		}

		@Override
		public void remove()
		{
			iterator1.remove();
			iterator2.remove();
		}

		@Override
		public void set( final Pair< L1, L2 > pair )
		{
			iterator1.set( pair.a );
			iterator2.set( pair.b );
		}

		@Override
		public void add( Pair< L1, L2 > pair )
		{
			iterator1.add( pair.a );
			iterator2.add( pair.b );
		}
	}
	
	public static final class PairDimComparator< T, L extends RealLocalizable > implements Comparator< Pair< T, L > >
	{
		final int d;
		public PairDimComparator( int d )
		{
			this.d = d;
		}

		@Override
		public int compare( Pair< T, L > o1, Pair< T, L > o2 )
		{
			final float diff = o1.b.getFloatPosition( d ) - o2.b.getFloatPosition( d );
			return ( diff < 0 ) ? -1 : ( diff > 0 ? 1 : 0);
		}		
	}
	
	protected < L extends RealLocalizable > Node< T > makeNode( final ListIteratorPair< T, L > first, final ListIteratorPair< T, L > last, final int d )
	{
		final int i = first.nextIndex();
		final int j = last.previousIndex();
		if ( j > i ) {
			final int k = i + (j - i) / 2;
			KthElement.kthElement( first, last, k, new PairDimComparator< T, L >( d ) );
			first.previous();
			Pair< T, L > current = first.next();
	
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

			return new Node< T >( current.a, current.b, d, left, right );
		}
		else if ( j == i )
		{
			Pair< T, L > current = first.next();
			return new Node< T >( current.a, current.b, d, null, null );
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

		return indent + "- " + node.toString() + "\n"
			+ toString( node.left, indent + "  " )
			+ toString( node.right, indent + "  " );
	}

	public String toString()
	{
		return toString( root, "" );
	}}
