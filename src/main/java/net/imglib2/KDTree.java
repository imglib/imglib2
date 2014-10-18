/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package net.imglib2;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

import net.imglib2.util.KthElement;

/**
 * KDTree to access values at RealLocalizable positions.
 * 
 * @param <T>
 *            type of values stored in the tree.
 * 
 * @author Tobias Pietzsch
 */
public class KDTree< T > implements EuclideanSpace, IterableRealInterval< T >
{
	/**
	 * the number of dimensions.
	 */
	final protected int n;

	final protected KDTreeNode< T > root;

	/**
	 * the number of nodes in the tree.
	 */
	final protected long size;

	/**
	 * minimum of each dimension.
	 */
	final protected double[] min;

	/**
	 * maximum of each dimension.
	 */
	final protected double[] max;

	/**
	 * A KDTreeNode that stores it's value as a reference.
	 */
	protected final static class ValueNode< T > extends KDTreeNode< T >
	{
		protected final T value;

		/**
		 * @param value
		 *            reference to the node's value
		 * @param position
		 *            coordinates of this node
		 * @param dimension
		 *            dimension along which this node divides the space
		 * @param left
		 *            left child node
		 * @param right
		 *            right child node
		 */
		public ValueNode( final T value, final RealLocalizable position, final int dimension, final ValueNode< T > left, final ValueNode< T > right )
		{
			super( position, dimension, left, right );
			this.value = value;
		}

		protected ValueNode( final ValueNode< T > node )
		{
			super( node );
			this.value = node.value;
		}

		@Override
		public T get()
		{
			return value;
		}

		@Override
		public ValueNode< T > copy()
		{
			return new ValueNode< T >( this );
		}

		@Override
		public String toString()
		{
			return "node " + getSplitDimension() + " ? " + getSplitCoordinate() + " | " + value;
		}
	}

	/**
	 * A KDTreeNode that stores it's value as a Sampler.
	 */
	protected static final class SamplerNode< T > extends KDTreeNode< T >
	{
		protected final Sampler< T > sampler;

		/**
		 * @param sampler
		 *            a sampler providing the node's value
		 * @param position
		 *            coordinates of this node
		 * @param dimension
		 *            dimension along which this node divides the space
		 * @param left
		 *            left child node
		 * @param right
		 *            right child node
		 */
		public SamplerNode( final Sampler< T > sampler, final RealLocalizable position, final int dimension, final SamplerNode< T > left, final SamplerNode< T > right )
		{
			super( position, dimension, left, right );
			this.sampler = sampler;
		}

		protected SamplerNode( final SamplerNode< T > node )
		{
			super( node );
			this.sampler = node.sampler.copy();
		}

		@Override
		public T get()
		{
			return sampler.get();
		}

		@Override
		public SamplerNode< T > copy()
		{
			return new SamplerNode< T >( this );
		}

		@Override
		public String toString()
		{
			return "node " + getSplitDimension() + " ? " + getSplitCoordinate() + " | " + sampler.get();
		}
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
	 *            a list of values
	 * @param positions
	 *            a list of positions corresponding to the values
	 */
	public < L extends RealLocalizable > KDTree( final List< T > values, final List< L > positions )
	{
		assert values.size() == positions.size();

		this.n = positions.get( 0 ).numDimensions();
		this.size = positions.size();

		// test that dimensionality is preserved
		assert ( verifyDimensions( positions, n ) );

		this.min = new double[ n ];
		this.max = new double[ n ];
		for ( int d = 0; d < n; ++d )
		{
			min[ d ] = Double.MAX_VALUE;
			max[ d ] = -Double.MAX_VALUE;
		}
		for ( final L position : positions )
		{
			for ( int d = 0; d < n; ++d )
			{
				final double x = position.getDoublePosition( d );

				if ( x < min[ d ] )
					min[ d ] = x;
				if ( x > max[ d ] )
					max[ d ] = x;
			}
		}

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

	/**
	 * Construct a KDTree from the elements of the given
	 * {@link IterableRealInterval}.
	 * 
	 * @param interval
	 *            elements in the tree are obtained by iterating this
	 */
	public KDTree( final IterableRealInterval< T > interval )
	{
		this.n = interval.numDimensions();
		this.size = interval.size();
		this.min = new double[ n ];
		interval.realMin( this.min );
		this.max = new double[ n ];
		interval.realMax( this.max );
		final ArrayList< RealCursor< T > > values = new ArrayList< RealCursor< T > >( ( int ) interval.size() );
		final RealCursor< T > cursor = interval.localizingCursor();
		while ( cursor.hasNext() )
		{
			cursor.next();
			values.add( cursor.copyCursor() );
		}
		root = makeSamplerNode( values, 0, values.size() - 1, 0 );
	}

	/**
	 * Check whether all positions in the positions list have dimension n.
	 * 
	 * @return true, if all positions have dimension n.
	 */
	protected static < L extends RealLocalizable > boolean verifyDimensions( final List< L > positions, final int n )
	{
		for ( final L position : positions )
			if ( position.numDimensions() != n )
				return false;
		return true;
	}

	/**
	 * Compare RealLocalizables by comparing their coordinates in dimension d.
	 */
	public static final class DimComparator< L extends RealLocalizable > implements Comparator< L >
	{
		final int d;

		public DimComparator( final int d )
		{
			this.d = d;
		}

		@Override
		public int compare( final L o1, final L o2 )
		{
			final float diff = o1.getFloatPosition( d ) - o2.getFloatPosition( d );
			return ( diff < 0 ) ? -1 : ( diff > 0 ? 1 : 0 );
		}
	}

	/**
	 * Construct the tree by recursively adding nodes. The sublist of positions
	 * between indices i and j (inclusive) is split at the median element with
	 * respect to coordinates in the given dimension d. The median becomes the
	 * new node which is returned. The left and right partitions of the sublist
	 * are processed recursively and form the left and right subtrees of the
	 * node.
	 * 
	 * @param positions
	 *            list of positions
	 * @param i
	 *            start index of sublist to process
	 * @param j
	 *            end index of sublist to process
	 * @param d
	 *            dimension along which to split the sublist
	 * @param values
	 *            list of values corresponding to permuted positions
	 * @param permutation
	 *            the index of the values element at index k is permutation[k]
	 * @return a new node containing the subtree of the given sublist of
	 *         positions.
	 */
	protected < L extends RealLocalizable > ValueNode< T > makeNode( final List< L > positions, final int i, final int j, final int d, final List< T > values, final int[] permutation )
	{
		if ( j > i )
		{
			final int k = i + ( j - i ) / 2;
			KthElement.kthElement( i, j, k, positions, permutation, new DimComparator< L >( d ) );

			final int dChild = ( d + 1 == n ) ? 0 : d + 1;
			return new ValueNode< T >( values.get( permutation[ k ] ), positions.get( k ), d, makeNode( positions, i, k - 1, dChild, values, permutation ), makeNode( positions, k + 1, j, dChild, values, permutation ) );
		}
		else if ( j == i )
		{
			return new ValueNode< T >( values.get( permutation[ i ] ), positions.get( i ), d, null, null );
		}
		else
		{
			return null;
		}
	}

	/**
	 * Construct the tree by recursively adding nodes. The sublist of positions
	 * between iterators first and last is split at the median element with
	 * respect to coordinates in the given dimension d. The median becomes the
	 * new node which is returned. The left and right partitions of the sublist
	 * are processed recursively and form the left and right subtrees of the
	 * node.
	 * 
	 * @param first
	 *            first element of the sublist of positions
	 * @param last
	 *            last element of the sublist of positions
	 * @param d
	 *            dimension along which to split the sublist
	 * @param values
	 *            list of values corresponding to permuted positions
	 * @param permutation
	 *            the index of the values element at index k is permutation[k]
	 * @return a new node containing the subtree of the given sublist of
	 *         positions.
	 */
	protected < L extends RealLocalizable > ValueNode< T > makeNode( final ListIterator< L > first, final ListIterator< L > last, final int d, final List< T > values, final int[] permutation )
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
			final ValueNode< T > right = makeNode( first, last, dChild, values, permutation );

			// Node< T > left = makeNode( elements, i, k - 1, dChild );
			for ( int c = first.nextIndex() - i; c > 0; --c )
				first.previous();
			for ( int c = last.nextIndex() - k; c > 0; --c )
				last.previous();
			final ValueNode< T > left = makeNode( first, last, dChild, values, permutation );

			return new ValueNode< T >( values.get( permutation[ k ] ), current, d, left, right );
		}
		else if ( j == i )
		{
			final L current = first.next();
			return new ValueNode< T >( values.get( permutation[ i ] ), current, d, null, null );
		}
		else
		{
			return null;
		}
	}

	/**
	 * {@see #makeNode(List, int, int, int, List, int[])}. Here, no values are
	 * attached to the nodes (or rather the positions are the values).
	 * 
	 * @param elements
	 *            list of elements (positions and values at the same time)
	 * @param i
	 *            start index of sublist to process
	 * @param j
	 *            end index of sublist to process
	 * @param d
	 *            dimension along which to split the sublist
	 */
	@SuppressWarnings( "unchecked" )
	protected < L extends RealLocalizable > ValueNode< T > makeNode( final List< L > elements, final int i, final int j, final int d )
	{
		if ( j > i )
		{
			final int k = i + ( j - i ) / 2;
			KthElement.kthElement( i, j, k, elements, new DimComparator< L >( d ) );

			final int dChild = ( d + 1 == n ) ? 0 : d + 1;
			return new ValueNode< T >( ( T ) elements.get( k ), elements.get( k ), d, makeNode( elements, i, k - 1, dChild ), makeNode( elements, k + 1, j, dChild ) );
		}
		else if ( j == i )
		{
			return new ValueNode< T >( ( T ) elements.get( i ), elements.get( i ), d, null, null );
		}
		else
		{
			return null;
		}
	}

	/**
	 * {@see #makeNode(ListIterator, ListIterator, int, List, int[])}. Here, no
	 * values are attached to the nodes (or rather the positions are the
	 * values).
	 * 
	 * @param first
	 *            first element of the sublist to process
	 * @param last
	 *            last element of the sublist to process
	 * @param d
	 *            dimension along which to split the sublist
	 */
	@SuppressWarnings( "unchecked" )
	protected < L extends RealLocalizable > ValueNode< T > makeNode( final ListIterator< L > first, final ListIterator< L > last, final int d )
	{
		final int i = first.nextIndex();
		final int j = last.previousIndex();
		if ( j > i )
		{
			final int k = i + ( j - i ) / 2;
			KthElement.kthElement( first, last, k, new DimComparator< L >( d ) );
			first.previous();
			final L current = first.next();

			final int dChild = ( d + 1 == n ) ? 0 : d + 1;

			// Node< T > right = makeNode( elements, k + 1, j, dChild );
			for ( int c = j - last.previousIndex(); c > 0; --c )
				last.next();
			final ValueNode< T > right = makeNode( first, last, dChild );

			// Node< T > left = makeNode( elements, i, k - 1, dChild );
			for ( int c = first.nextIndex() - i; c > 0; --c )
				first.previous();
			for ( int c = last.nextIndex() - k; c > 0; --c )
				last.previous();
			final ValueNode< T > left = makeNode( first, last, dChild );

			return new ValueNode< T >( ( T ) current, current, d, left, right );
		}
		else if ( j == i )
		{
			final L current = first.next();
			return new ValueNode< T >( ( T ) current, current, d, null, null );
		}
		else
		{
			return null;
		}
	}

	/**
	 * Construct the tree by recursively adding nodes. The sublist of elements
	 * between indices i and j (inclusive) is split at the median element with
	 * respect to coordinates in the given dimension d. The median becomes the
	 * new node which is returned. The left and right partitions of the sublist
	 * are processed recursively and form the left and right subtrees of the
	 * node. (The elements of the list are RealCursors which provide coordinates
	 * and values.)
	 * 
	 * @param elements
	 *            list of elements (positions and values at the same time)
	 * @param i
	 *            start index of sublist to process
	 * @param j
	 *            end index of sublist to process
	 * @param d
	 *            dimension along which to split the sublist
	 * @return a new node containing the subtree of the given sublist of
	 *         elements
	 */
	protected SamplerNode< T > makeSamplerNode( final List< RealCursor< T > > elements, final int i, final int j, final int d )
	{
		if ( j > i )
		{
			final int k = i + ( j - i ) / 2;
			KthElement.kthElement( i, j, k, elements, new DimComparator< RealCursor< T > >( d ) );

			final int dChild = ( d + 1 == n ) ? 0 : d + 1;
			return new SamplerNode< T >( elements.get( k ), elements.get( k ), d, makeSamplerNode( elements, i, k - 1, dChild ), makeSamplerNode( elements, k + 1, j, dChild ) );
		}
		else if ( j == i )
		{
			return new SamplerNode< T >( elements.get( i ), elements.get( i ), d, null, null );
		}
		else
		{
			return null;
		}
	}

	/**
	 * Get the root node.
	 * 
	 * @return the root node.
	 */
	public KDTreeNode< T > getRoot()
	{
		return root;
	}

	@Override
	public int numDimensions()
	{
		return n;
	}

	public String toString( final KDTreeNode< T > left, final String indent )
	{
		if ( left == null )
			return "";

		return indent + "- " + left.toString() + "\n" + toString( left.left, indent + "  " ) + toString( left.right, indent + "  " );
	}

	@Override
	public String toString()
	{
		return toString( root, "" );
	}

	@Override
	public double realMin( final int d )
	{
		return min[ d ];
	}

	@Override
	public void realMin( final double[] m )
	{
		for ( int d = 0; d < n; ++d )
			m[ d ] = min[ d ];
	}

	@Override
	public void realMin( final RealPositionable m )
	{
		m.setPosition( min );
	}

	@Override
	public double realMax( final int d )
	{
		return max[ d ];
	}

	@Override
	public void realMax( final double[] m )
	{
		for ( int d = 0; d < n; ++d )
			m[ d ] = max[ d ];
	}

	@Override
	public void realMax( final RealPositionable m )
	{
		m.setPosition( max );
	}

	@Override
	public long size()
	{
		return size;
	}

	@Override
	public Object iterationOrder()
	{
		return this; // iteration order is only compatible with ourselves
	}

	public final class KDTreeCursor implements RealCursor< T >
	{
		private final KDTree< T > tree;

		private final ArrayDeque< KDTreeNode< T > > nodes;

		private KDTreeNode< T > currentNode;

		public KDTreeCursor( final KDTree< T > kdtree )
		{
			this.tree = kdtree;
			final int capacity = 2 + ( int ) ( Math.log( kdtree.size() ) / Math.log( 2 ) );
			this.nodes = new ArrayDeque< KDTreeNode< T > >( capacity );
			reset();
		}

		public KDTreeCursor( final KDTreeCursor c )
		{
			this.tree = c.tree;
			this.nodes = c.nodes.clone();
			this.currentNode = c.currentNode;
		}

		@Override
		public void localize( final float[] position )
		{
			currentNode.localize( position );
		}

		@Override
		public void localize( final double[] position )
		{
			currentNode.localize( position );
		}

		@Override
		public float getFloatPosition( final int d )
		{
			return currentNode.getFloatPosition( d );
		}

		@Override
		public double getDoublePosition( final int d )
		{
			return currentNode.getDoublePosition( d );
		}

		@Override
		public int numDimensions()
		{
			return n;
		}

		@Override
		public T get()
		{
			return currentNode.get();
		}

		@Override
		public KDTreeCursor copy()
		{
			return new KDTreeCursor( this );
		}

		@Override
		public void jumpFwd( final long steps )
		{
			for ( long i = 0; i < steps; ++i )
				fwd();
		}

		@Override
		public void fwd()
		{
			if ( nodes.isEmpty() )
				currentNode = null;
			else
			{
				currentNode = nodes.pop();
				if ( currentNode.left != null )
					nodes.push( currentNode.left );
				if ( currentNode.right != null )
					nodes.push( currentNode.right );
			}
		}

		@Override
		public void reset()
		{
			nodes.clear();
			nodes.push( tree.getRoot() );
			currentNode = null;
		}

		@Override
		public boolean hasNext()
		{
			return !nodes.isEmpty();
		}

		@Override
		public T next()
		{
			fwd();
			return get();
		}

		@Override
		public void remove()
		{
			// NB: no action.
		}

		@Override
		public KDTreeCursor copyCursor()
		{
			return copy();
		}

	}

	@Override
	public KDTreeCursor iterator()
	{
		return new KDTreeCursor( this );
	}

	@Override
	public KDTreeCursor cursor()
	{
		return new KDTreeCursor( this );
	}

	@Override
	public KDTreeCursor localizingCursor()
	{
		return new KDTreeCursor( this );
	}

	@Override
	public T firstElement()
	{
		return iterator().next();
	}
}
