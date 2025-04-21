/*-
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2025 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
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
package net.imglib2.kdtree;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.imglib2.RandomAccess;
import net.imglib2.RealLocalizable;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.NativeType;

final class KDTreeUtils
{
	static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

	/**
	 * If the tree is flattened into an array the left child of node at
	 * index {@code i} has index {@code 2 * i + 1}.
	 */
	static int leftChildIndex( final int i )
	{
		return 2 * i + 1;
	}

	/**
	 * If the tree is flattened into an array the right child of node at
	 * index {@code i} has index {@code 2 * i + 2}.
	 */
	static int rightChildIndex( final int i )
	{
		return 2 * i + 2;
	}

	/**
	 * If the tree is flattened into an array the parent of node at
	 * index {@code i} has index {@code (i - 1) / 2} (except for the
	 * root node {@code i==0}).
	 */
	static int parentIndex( final int i )
	{
		return ( i - 1 ) / 2;
	}

	/**
	 * Copy the coordinates of the given {@code points} to a new {@code double[][] positions} array.
	 * The coordinate in dimension {@code d} of the {@code i}th point is stored at {@code positions[d][i]}.
	 * That is, {@code positions[0]} has all X coordinates, {@code positions[0]} has all Y coordinates, and so on.
	 */
	static double[][] initPositions(
			final int numDimensions,
			final int numPoints,
			final Iterable< ? extends RealLocalizable > points )
	{
		final double[][] positions = new double[ numDimensions ][ numPoints ];
		final Iterator< ? extends RealLocalizable > ipos =  points.iterator();
		for ( int i = 0; i < numPoints; ++i )
		{
			if ( !ipos.hasNext() )
				throw new IllegalArgumentException( "positions Iterable is empty" );
			final RealLocalizable pos = ipos.next();
			for ( int d = 0; d < numDimensions; d++ )
				positions[ d ][ i ] = pos.getDoublePosition( d );
		}
		return positions;
	}

	/**
	 * Sort the given points into a k-d tree.
	 * <p>
	 * The tree is given as a flat (heap-like) array of point indices, {@code int[] tree}.
	 * The index of the point chosen as the root node is {@code tree[0]}.
	 * The coordinates of the root node are at {@code positions[d][tree[0]]}.
	 * <p>
	 * The indices of the children (less-or-equal and greater-or-equal, respectively) of root are at {@code tree[1]} and {@code tree[2]}, and so on.
	 *
	 * @param positions
	 * 		The coordinates for the {@code i}th point are stored at {@code positions[d][i]} where {@code d} is the dimension.
	 * 	    See {@link #initPositions(int, int, Iterable)}.
	 *
	 * @return flattened tree of point indices
	 */
	static int[] makeTree( double[][] positions )
	{
		return new MakeTree( positions ).tree;
	}

	/**
	 * Re-order the node {@code positions} to form a tree corresponding to the index array {@code tree'={0,1,2,...}}.
	 *
	 * @param positions
	 * @param tree
	 *
	 * @return
	 */
	static double[][] reorder( double[][] positions, int[] tree )
	{
		final int numDimensions = positions.length;
		final int numPoints = positions[ 0 ].length;
		assert tree.length == numPoints;
		final double[][] reordered = new double[ numDimensions ][];
		Arrays.setAll( reordered, d -> reorder( positions[ d ], tree ) );
		return reordered;
	}

	/**
	 * Create a new {@code double[]} array that contains the elements of {@code
	 * values}, ordered such that {@code values[order[i]]} is at index {@code i}.
	 */
	static double[] reorder( final double[] values, final int[] order )
	{
		final int size = order.length;
		final double[] reordered = new double[ size ];
		Arrays.setAll( reordered, i -> values[ order[ i ] ] );
		return reordered;
	}

	/**
	 * Create a new {@code int[]} array that contains the elements of {@code
	 * values}, ordered such that {@code values[order[i]]} is at index {@code i}.
	 */
	static int[] reorder( final int[] values, final int[] order )
	{
		final int size = order.length;
		final int[] reordered = new int[ size ];
		Arrays.setAll( reordered, i -> values[ order[ i ] ] );
		return reordered;
	}

	/**
	 * Re-order the node {@code positions} to form a tree corresponding to the index array {@code tree={0,1,2,...}}.
	 * Then flatten the result into a 1-D array, interleaving coordinates in all dimensions.
	 *
	 * @param positions
	 * @param tree
	 *
	 * @return
	 */
	static double[] reorderToFlatLayout( final double[][] positions, final int[] tree )
	{
		final int numDimensions = positions.length;
		final int numPoints = positions[ 0 ].length;
		assert tree.length == numPoints;
		if ( ( long ) numDimensions * numPoints > MAX_ARRAY_SIZE )
			throw new IllegalArgumentException( "positions[][] is too large to be stored in a flat array" );
		final double[] reordered = new double[ numDimensions * numPoints ];
		for ( int i = 0; i < numPoints; ++i )
			for ( int d = 0; d < numDimensions; ++d )
				reordered[ numDimensions * i + d ] = positions[ d ][ tree[ i ] ];
		return reordered;
	}

	/**
	 * Flatten the nested {@code positions} array.
	 *
	 * @param positions positions in nested layout
	 * @return positions in flattened layout
	 */
	static double[] flatten( double[][] positions )
	{
		final int numDimensions = positions.length;
		final int numPoints = positions[ 0 ].length;
		final double[] flattened = new double[ numDimensions * numPoints ];
		for ( int i = 0; i < numPoints; ++i )
			for ( int d = 0; d < numDimensions; ++d )
				flattened[ numDimensions * i + d ] = positions[ d ][ i ];
		return flattened;

	}

	/**
	 * Transform flat {@code positions} array into a nested {@code
	 * double[numDimensions][numPoints]} array.
	 * <p>
	 * With flat layout, positions are stored as a flat {@code double[]} array,
	 * where {@code positions[d + i*n]} is dimension {@code d} of the {@code
	 * i}-th point, with {@code n} the number of dimensions.
	 * <p>
	 * With nested layout, positions are stored as a nested {@code double[][]}
	 * array where {@code positions[d][i]} is dimension {@code d} of the {@code
	 * i}-th point.
	 *
	 * @param positions
	 * 		positions in flattened layout
	 * @param n
	 * 		number of dimensions
	 *
	 * @return positions in nested layout
	 */
	static double[][] unflatten( double[] positions, final int n )
	{
		final int numPoints = positions.length / n;
		final double[][] unflattened = new double[ n ][ numPoints ];
		for (int i = 0; i < positions.length; ++i )
		{
			final int d = i % n;
			unflattened[ d ][ i / n ] = positions[ i ];
		}
		return unflattened;

	}

	/**
	 * Compute bounding box of positions in nested layout
	 */
	static void computeMinMax(final double[][] positions, final double[] min, final double[] max) {
		final int n = min.length;
		for (int d = 0; d < n; d++) {
			double maxd = Double.NEGATIVE_INFINITY;
			double mind = Double.POSITIVE_INFINITY;
			for (double v : positions[d]) {
				if (v < mind) {
					mind = v;
				}
				if (v > maxd) {
					maxd = v;
				}
			}
			min[d] = mind;
			max[d] = maxd;
		}
	}

	/**
	 * Compute bounding box of positions in flat layout
	 */
	static void computeMinMax(final double[] flatPositions, final double[] min, final double[] max) {
		final int n = min.length;
		Arrays.fill(max, Double.NEGATIVE_INFINITY);
		Arrays.fill(min, Double.POSITIVE_INFINITY);
		int d = 0;
		for (double v : flatPositions) {
			if (v < min[d]) {
				min[d] = v;
			}
			if (v > max[d]) {
				max[d] = v;
			}
			d = (d + 1) % n;
		}
	}

	/**
	 * Invert the given permutation {@code tree}.
	 * <p>
	 * For example, {@code tree = {3, 4, 1, 0, 5, 2}} indicates that coordinates
	 * and value for the node at heap index {@code i} can be found at index
	 * {@code tree[i]} in the respective input list.
	 * <p>
	 * The inverse, {@code inv = {3, 4, 1, 0, 5, 2}} indicates that coordinates
	 * and value at index {@code i} in the respective input list belong to the
	 * node at heap index {@code inv[i]}.
	 *
	 * @param tree a permutation
	 * @return the inverse permutation
	 */
	static int[] invert( int[] tree )
	{
		// For example:
		// i =          0  1  2  3  4  5
		// tree =      {3, 4, 1, 0, 5, 2}
		// output =    {3, 2, 5, 0, 1, 4}

		final int[] inv = new int[ tree.length ];
		for ( int i = 0; i < tree.length; i++ )
			inv[tree[i]] = i;
		return inv;
	}

	/**
	 * Re-order the node {@code values} to form a tree corresponding to the index array {@code tree'={0,1,2,...}}.
	 * The tree is given as an {@link #invert(int[]) inverted permutation}, so that we can iterate through the {@code values} in order, putting each at the right index in the returned {@code List}.
	 *
	 * @param invtree
	 * @param values
	 * @param <T>
	 *
	 * @return
	 */
	static < T > List< T > orderValuesList(
			final int[] invtree,
			final Iterable< T > values )
	{
		final int size = invtree.length;
		@SuppressWarnings( "unchecked" )
		final T[] orderedValues = ( T[] ) new Object[ size ];
		final Iterator< T > ival = values.iterator();
		for ( final int i : invtree )
		{
			if ( !ival.hasNext() )
				throw new IllegalArgumentException( "provided values Iterable has fewer elements than required" );
			orderedValues[ i ] = ival.next();
		}
		return Arrays.asList( orderedValues );
	}

	/**
	 * Re-order the node {@code values} to form a tree corresponding to the index array {@code tree'={0,1,2,...}}.
	 * The tree is given as an {@link #invert(int[]) inverted permutation}, so that we can iterate through the {@code values} in order, putting each at the right index in the returned 1D {@code Img}.
	 *
	 * @param invtree
	 * @param values
	 * @param <T>
	 *
	 * @return
	 */
	static < T extends NativeType< T > > Img< T > orderValuesImg(
			final int[] invtree,
			final Iterable< T > values )
	{
		final int size = invtree.length;
		final Img< T > img = new ArrayImgFactory<>( getType( values ) ).create( size );
		final RandomAccess< T > orderedValues = img.randomAccess();
		final Iterator< T > ival = values.iterator();
		for ( final int i : invtree )
		{
			if ( !ival.hasNext() )
				throw new IllegalArgumentException( "provided values Iterable has fewer elements than required" );
			orderedValues.setPositionAndGet( i ).set( ival.next() );
		}
		return img;
	}

	/**
	 * Returns the first element of {@code values}.
	 *
	 * @throws IllegalArgumentException
	 * 		if {@code values} has no elements.
	 */
	static < T > T getType( Iterable< T > values )
	{
		final Iterator< T > ival = values.iterator();
		if ( !ival.hasNext() )
			throw new IllegalArgumentException( "values Iterable is empty" );
		return ival.next();
	}

	/**
	 * Returns the number of dimensions of the first element of {@code positions}.
	 * If {@code positions} has no elements, returns {@code 0}.
	 *
	 * @param positions
	 * 		list of points
	 *
	 * @return number of dimensions of the first point
	 */
	static int getNumDimensions( Iterable< ? extends RealLocalizable > positions )
	{
		final Iterator< ? extends RealLocalizable > ipos = positions.iterator();
		return ipos.hasNext() ? ipos.next().numDimensions() : 0;
	}

	/**
	 * Swap {@code order[i]} and {@code order[j]}.
	 */
	private static void swap( final int i, final int j, final int[] order )
	{
		final int tmp = order[ i ];
		order[ i ] = order[ j ];
		order[ j ] = tmp;
	}

	/**
	 * Partition a sublist. The list is given by an immutable array of {@code
	 * values}, and an index array that represents the {@code order} of values
	 * in the list. This method only rearranges the {@code order} array.
	 * <p>
	 * A pivot element is chosen by median-of-three method. Then {@code
	 * [i,j]} is reordered, such that all elements before the pivot are
	 * smaller-equal and all elements after the pivot are larger-equal the
	 * pivot. The index of the pivot element is returned.
	 *
	 * @param i
	 *            index of first element of the sublist
	 * @param j
	 *            index of last element of the sublist
	 * @param values
	 * 			  the array of values of list elements.
	 * @param order
	 *            order of list elements. E.g., {@code order[0]=3} means that {@code values[3]} is the first element of the list.
	 * @return index of pivot element
	 */
	static int partition( int i, int j, final double[] values, final int[] order )
	{
		final int len = j - i + 1;
		if ( len <= 2 )
		{
			if ( len <= 0 )
				throw new IllegalArgumentException();
			if ( values[ order[ i ] ] > values[ order[ j ] ] )
				swap( i, j, order );
			return i;
		}
		else
		{
			final int m = ( i + j ) / 2;
			if ( values[ order[ i ] ] > values[ order[ m ] ] )
				swap( i, m, order );
			if ( values[ order[ i ] ] > values[ order[ j ] ] )
				swap( i, j, order );
			if ( values[ order[ m ] ] > values[ order[ j ] ] )
				swap( m, j, order );
			swap( m, i + 1, order );
			final int p = ++i;
			final double pivot = values[ order[ p ] ];
			while ( true )
			{
				while ( values[ order[ ++i ] ] < pivot )
					;
				while ( values[ order[ --j ] ] > pivot )
					;
				if ( j < i )
					break;
				swap( i, j, order );
			}
			swap( p, j, order );
			return j;
		}
	}

	/**
	 * Sort a sublist. The list is given by an immutable array of {@code
	 * values}, and an index array that represents the {@code order} of values
	 * in the list. This method only rearranges the {@code order} array.
	 *
	 * @param i
	 *            index of first element of the sublist
	 * @param j
	 *            index of last element of the sublist
	 * @param values
	 * 			  the array of values of list elements.
	 * @param order
	 *            order of list elements. E.g., {@code order[0]=3} means that {@code values[3]} is the first element of the list.
	 */
	static void quicksort( final int i, final int j, final double[] values, final int[] order )
	{
		if ( 0 <= i && i < j )
		{
			final int p = partition( i, j, values, order );
			quicksort( i, p - 1, values, order );
			quicksort( p + 1, j, values, order );
		}
	}

	private static final class MakeTree
	{
		private final int numDimensions;

		private final int numPoints;

		/**
		 * The coordinates for the {@code i}th point are stored at {@code positions[d][k]} where {@code d} is the dimension.
		 */
		private final double[][] positions;

		/**
		 * Temporary array to keep track of elements.
		 * Initialized to {@code 0, 1, ... } and then permuted when sorting the elements into a tree.
		 */
		private final int[] indices;

		/**
		 * Node indices in a flattened (heap-like) array.
		 * For example: the index of the root node is {@code tree[0]}.
		 * The coordinates of the root node are at {@code positions[d][tree[0]]} where {@code d} is the dimension.
		 * The children of the root node are at {@code tree[1]} and {@code tree[2]}, and so on.
		 */
		private final int[] tree;

		private MakeTree( final double[][] positions )
		{
			this.positions = positions;
			numDimensions = positions.length;
			numPoints = positions[ 0 ].length;
			indices = new int[ numPoints ];
			tree = new int[ numPoints ];
			Arrays.setAll( indices, j -> j );
			makeNode( 0, numPoints - 1, 0, 0 );
		}

		/**
		 * Calculate pivot index such that the tree will be arranged in a way that
		 * "leaf layers" are filled from the left.
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
		private static int pivot( final int len )
		{
			final int h = Integer.highestOneBit( len );
			final int h2 = h >> 1;
			return ( len - h >= h2 )
					? h - 1
					: len - h2;
		}

		private void makeNode( final int i, final int j, final int d, final int nodeIndex )
		{
			if ( j > i )
			{
				final int k = i + pivot( j - i + 1 );
				kthElement( i, j, k, d );
				tree[ nodeIndex ] = indices[ k ];
				final int dChild = ( d + 1 ) % numDimensions;
				makeNode( i, k - 1, dChild, leftChildIndex( nodeIndex ) );
				makeNode( k + 1, j, dChild, rightChildIndex( nodeIndex ) );
			}
			else if ( j == i )
			{
				tree[ nodeIndex ] = indices[ i ];
			}
		}

		/**
		 * Partition a sublist of Nodes by their coordinate in the specified
		 * dimension, such that the k-th smallest value is at position {@code
		 * k}, elements before the k-th are smaller or equal and elements after
		 * the k-th are larger or equal.
		 *
		 * @param i
		 *            index of first element of the sublist
		 * @param j
		 *            index of last element of the sublist
		 * @param k
		 *            index for k-th smallest value. {@code i <= k <= j}.
		 * @param compare_d
		 *            dimension by which to order the sublist
		 */
		private void kthElement( int i, int j, final int k, final int compare_d )
		{
			while ( true )
			{
				final int pivotpos = partition( i, j, positions[ compare_d ], indices );
				if ( pivotpos > k )
				{
					// partition lower half
					j = pivotpos - 1;
				}
				else if ( pivotpos < k )
				{
					// partition upper half
					i = pivotpos + 1;
				}
				else
					return;
			}
		}
	}

	private KDTreeUtils() {}
}
