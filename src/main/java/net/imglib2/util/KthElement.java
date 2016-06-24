/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2016 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package net.imglib2.util;

import static net.imglib2.util.Partition.partitionSubList;

import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

/**
 * TODO
 * 
 */
public class KthElement
{
	/**
	 * Partition a subarray of {@code values} such that the k-th smallest value
	 * is at position {@code k}, elements before the k-th are smaller or equal
	 * and elements after the k-th are larger or equal.
	 * 
	 * @param i
	 *            index of first element of subarray
	 * @param j
	 *            index of last element of subarray
	 * @param k
	 *            index for k-th smallest value. i <= k <= j.
	 * @param values
	 *            array
	 */
	public static void kthElement( int i, int j, final int k, final byte[] values )
	{
		while ( true )
		{
			final int pivotpos = partitionSubList( i, j, values );
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

	/**
	 * Partition an array of {@code values} such that the k-th smallest value is
	 * at position {@code k}, elements before the k-th are smaller or equal and
	 * elements after the k-th are larger or equal.
	 * 
	 * @param k
	 *            index for k-th smallest value. 0 <= k < values.length.
	 * @param values
	 *            array
	 */
	public static void kthElement( final int k, final byte[] values )
	{
		kthElement( 0, values.length - 1, k, values );
	}

	/**
	 * Partition a subarray of {@code values} such that the k-th smallest value
	 * is at position {@code k}, elements before the k-th are smaller or equal
	 * and elements after the k-th are larger or equal.
	 * 
	 * @param i
	 *            index of first element of subarray
	 * @param j
	 *            index of last element of subarray
	 * @param k
	 *            index for k-th smallest value. i <= k <= j.
	 * @param values
	 *            array
	 */
	public static void kthElement( int i, int j, final int k, final short[] values )
	{
		while ( true )
		{
			final int pivotpos = partitionSubList( i, j, values );
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

	/**
	 * Partition an array of {@code values} such that the k-th smallest value is
	 * at position {@code k}, elements before the k-th are smaller or equal and
	 * elements after the k-th are larger or equal.
	 * 
	 * @param k
	 *            index for k-th smallest value. 0 <= k < values.length.
	 * @param values
	 *            array
	 */
	public static void kthElement( final int k, final short[] values )
	{
		kthElement( 0, values.length - 1, k, values );
	}

	/**
	 * Partition a subarray of {@code values} such that the k-th smallest value
	 * is at position {@code k}, elements before the k-th are smaller or equal
	 * and elements after the k-th are larger or equal.
	 * 
	 * @param i
	 *            index of first element of subarray
	 * @param j
	 *            index of last element of subarray
	 * @param k
	 *            index for k-th smallest value. i <= k <= j.
	 * @param values
	 *            array
	 */
	public static void kthElement( int i, int j, final int k, final int[] values )
	{
		while ( true )
		{
			final int pivotpos = partitionSubList( i, j, values );
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

	/**
	 * Partition an array of {@code values} such that the k-th smallest value is
	 * at position {@code k}, elements before the k-th are smaller or equal and
	 * elements after the k-th are larger or equal.
	 * 
	 * @param k
	 *            index for k-th smallest value. 0 <= k < values.length.
	 * @param values
	 *            array
	 */
	public static void kthElement( final int k, final int[] values )
	{
		kthElement( 0, values.length - 1, k, values );
	}

	/**
	 * Partition a subarray of {@code values} such that the k-th smallest value
	 * is at position {@code k}, elements before the k-th are smaller or equal
	 * and elements after the k-th are larger or equal.
	 * 
	 * @param i
	 *            index of first element of subarray
	 * @param j
	 *            index of last element of subarray
	 * @param k
	 *            index for k-th smallest value. i <= k <= j.
	 * @param values
	 *            array
	 */
	public static void kthElement( int i, int j, final int k, final long[] values )
	{
		while ( true )
		{
			final int pivotpos = partitionSubList( i, j, values );
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

	/**
	 * Partition an array of {@code values} such that the k-th smallest value is
	 * at position {@code k}, elements before the k-th are smaller or equal and
	 * elements after the k-th are larger or equal.
	 * 
	 * @param k
	 *            index for k-th smallest value. 0 <= k < values.length.
	 * @param values
	 *            array
	 */
	public static void kthElement( final int k, final long[] values )
	{
		kthElement( 0, values.length - 1, k, values );
	}

	/**
	 * Partition a subarray of {@code values} such that the k-th smallest value
	 * is at position {@code k}, elements before the k-th are smaller or equal
	 * and elements after the k-th are larger or equal.
	 * 
	 * @param i
	 *            index of first element of subarray
	 * @param j
	 *            index of last element of subarray
	 * @param k
	 *            index for k-th smallest value. i <= k <= j.
	 * @param values
	 *            array
	 */
	public static void kthElement( int i, int j, final int k, final float[] values )
	{
		while ( true )
		{
			final int pivotpos = partitionSubList( i, j, values );
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

	/**
	 * Partition an array of {@code values} such that the k-th smallest value is
	 * at position {@code k}, elements before the k-th are smaller or equal and
	 * elements after the k-th are larger or equal.
	 * 
	 * @param k
	 *            index for k-th smallest value. 0 <= k < values.length.
	 * @param values
	 *            array
	 */
	public static void kthElement( final int k, final float[] values )
	{
		kthElement( 0, values.length - 1, k, values );
	}

	/**
	 * Partition a subarray of {@code values} such that the k-th smallest value
	 * is at position {@code k}, elements before the k-th are smaller or equal
	 * and elements after the k-th are larger or equal.
	 * 
	 * @param i
	 *            index of first element of subarray
	 * @param j
	 *            index of last element of subarray
	 * @param k
	 *            index for k-th smallest value. i <= k <= j.
	 * @param values
	 *            array
	 */
	public static void kthElement( int i, int j, final int k, final double[] values )
	{
		while ( true )
		{
			final int pivotpos = partitionSubList( i, j, values );
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

	/**
	 * Partition an array of {@code values} such that the k-th smallest value is
	 * at position {@code k}, elements before the k-th are smaller or equal and
	 * elements after the k-th are larger or equal.
	 * 
	 * @param k
	 *            index for k-th smallest value. 0 <= k < values.length.
	 * @param values
	 *            array
	 */
	public static void kthElement( final int k, final double[] values )
	{
		kthElement( 0, values.length - 1, k, values );
	}

	/**
	 * Partition a subarray of {@code values} such that the k-th smallest value
	 * is at position {@code k}, elements before the k-th are smaller or equal
	 * and elements after the k-th are larger or equal.
	 * 
	 * @param i
	 *            index of first element of subarray
	 * @param j
	 *            index of last element of subarray
	 * @param k
	 *            index for k-th smallest value. i <= k <= j.
	 * @param values
	 *            array
	 */
	public static void kthElement( int i, int j, final int k, final char[] values )
	{
		while ( true )
		{
			final int pivotpos = partitionSubList( i, j, values );
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

	/**
	 * Partition an array of {@code values} such that the k-th smallest value is
	 * at position {@code k}, elements before the k-th are smaller or equal and
	 * elements after the k-th are larger or equal.
	 * 
	 * @param k
	 *            index for k-th smallest value. 0 <= k < values.length.
	 * @param values
	 *            array
	 */
	public static void kthElement( final int k, final char[] values )
	{
		kthElement( 0, values.length - 1, k, values );
	}

	/**
	 * Partition a sublist of {@code values} such that the k-th smallest value
	 * is at position {@code k}, elements before the k-th are smaller or equal
	 * and elements after the k-th are larger or equal.
	 * 
	 * @param i
	 *            index of first element of subarray
	 * @param j
	 *            index of last element of subarray
	 * @param k
	 *            index for k-th smallest value. i <= k <= j.
	 * @param values
	 *            array
	 * @param compare
	 *            ordering function on T
	 */
	public static < T > void kthElement( int i, int j, final int k, final List< T > values, final Comparator< ? super T > comparator )
	{
		while ( true )
		{
			final int pivotpos = partitionSubList( i, j, values, comparator );
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

	/**
	 * Partition a list of {@code values} such that the k-th smallest value is
	 * at position {@code k}, elements before the k-th are smaller or equal and
	 * elements after the k-th are larger or equal.
	 * 
	 * @param k
	 *            index for k-th smallest value. 0 <= k < values.size().
	 * @param values
	 *            array
	 * @param compare
	 *            ordering function on T
	 */
	public static < T > void kthElement( final int k, final List< T > values, final Comparator< ? super T > comparator )
	{
		kthElement( 0, values.size() - 1, k, values, comparator );
	}

	/**
	 * Partition a sublist of {@code values} such that the k-th smallest value
	 * is at position {@code k}, elements before the k-th are smaller or equal
	 * and elements after the k-th are larger or equal.
	 * 
	 * @param i
	 *            index of first element of subarray
	 * @param j
	 *            index of last element of subarray
	 * @param k
	 *            index for k-th smallest value. i <= k <= j.
	 * @param values
	 *            array
	 */
	public static < T extends Comparable< T > > void kthElement( int i, int j, final int k, final List< T > values )
	{
		while ( true )
		{
			final int pivotpos = partitionSubList( i, j, values );
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

	/**
	 * Partition a list of {@code values} such that the k-th smallest value is
	 * at position {@code k}, elements before the k-th are smaller or equal and
	 * elements after the k-th are larger or equal.
	 * 
	 * @param k
	 *            index for k-th smallest value. 0 <= k < values.size().
	 * @param values
	 *            array
	 */
	public static < T extends Comparable< T > > void kthElement( final int k, final List< T > values )
	{
		kthElement( 0, values.size() - 1, k, values );
	}

	/**
	 * Partition a sublist such that the k-th smallest value is at position
	 * {@code k}, elements before the k-th are smaller or equal, and elements
	 * after the k-th are larger or equal.
	 * 
	 * <p>
	 * After the function returns, the iterator {@code i} is on the k-th
	 * element. That is, {@code i.next()} gives the (k+1)-th element.
	 * </p>
	 * 
	 * @param i
	 *            iterator pointing before first element of the sublist, that
	 *            is, {@code i.next()} gives you the first element.
	 * @param j
	 *            iterator pointing behind the last element of the sublist, that
	 *            is, {@code i.previous()} gives you the last element.
	 * @param k
	 *            index for k-th smallest value. i.nextIndex() <= k <=
	 *            j.previousIndex().
	 * @param compare
	 *            ordering function on T
	 */
	public static < T > void kthElement( final ListIterator< T > i, final ListIterator< T > j, final int k, final Comparator< ? super T > comparator )
	{
		while ( true )
		{
			final int iPos = i.nextIndex();
			final int jPos = j.previousIndex();
			partitionSubList( i, j, comparator );
			final int pivotpos = i.nextIndex() - 1;
			if ( pivotpos > k )
			{
				// partition lower half
				for ( int c = i.nextIndex() - iPos; c > 0; --c )
					i.previous();
				for ( int c = j.previousIndex() - ( pivotpos - 1 ); c > 0; --c )
					j.previous();
			}
			else if ( pivotpos < k )
			{
				// partition upper half
				for ( int c = i.nextIndex() - ( pivotpos + 1 ); c > 0; --c )
					i.previous();
				for ( int c = j.previousIndex() - jPos; c > 0; --c )
					j.previous();
			}
			else
				return;
		}
	}

	/**
	 * Partition a sublist such that the k-th smallest value is at position
	 * {@code k}, elements before the k-th are smaller or equal, and elements
	 * after the k-th are larger or equal.
	 * 
	 * <p>
	 * After the function returns, the iterator {@code i} is on the k-th
	 * element. That is, {@code i.next()} gives the (k+1)-th element.
	 * </p>
	 * 
	 * @param i
	 *            iterator pointing before first element of the sublist, that
	 *            is, {@code i.next()} gives you the first element.
	 * @param j
	 *            iterator pointing behind the last element of the sublist, that
	 *            is, {@code i.previous()} gives you the last element.
	 * @param k
	 *            index for k-th smallest value. i.nextIndex() <= k <=
	 *            j.previousIndex().
	 */
	public static < T extends Comparable< T > > void kthElement( final ListIterator< T > i, final ListIterator< T > j, final int k )
	{
		while ( true )
		{
			final int iPos = i.nextIndex();
			final int jPos = j.previousIndex();
			partitionSubList( i, j );
			final int pivotpos = i.nextIndex() - 1;
			if ( pivotpos > k )
			{
				// partition lower half
				for ( int c = i.nextIndex() - iPos; c > 0; --c )
					i.previous();
				for ( int c = j.previousIndex() - ( pivotpos - 1 ); c > 0; --c )
					j.previous();
			}
			else if ( pivotpos < k )
			{
				// partition upper half
				for ( int c = i.nextIndex() - ( pivotpos + 1 ); c > 0; --c )
					i.previous();
				for ( int c = j.previousIndex() - jPos; c > 0; --c )
					j.previous();
			}
			else
				return;
		}
	}

	/**
	 * Partition a sublist of {@code values} such that the k-th smallest value
	 * is at position {@code k}, elements before the k-th are smaller or equal
	 * and elements after the k-th are larger or equal.
	 * 
	 * <p>
	 * The {@code permutation} array is permuted in the same way as the list.
	 * Usually, this will be an array of indices, so that the partition
	 * operation can be mirrored in another list: Suppose, we have a list of
	 * keys and a lists (or several) of values. If we use {@code kthElement} to
	 * sort the keys, we want to reorder the values in the same manner. We pass
	 * an indices array [0, 1, 2, ...] and use the permutation of the indices to
	 * permute the values list.
	 * </p>
	 * 
	 * @param i
	 *            index of first element of subarray
	 * @param j
	 *            index of last element of subarray
	 * @param k
	 *            index for k-th smallest value. i <= k <= j.
	 * @param values
	 *            array
	 * @param permutation
	 *            elements of this array are permuted in the same way as the
	 *            elements in the values list
	 * @param compare
	 *            ordering function on T
	 */
	public static < T > void kthElement( int i, int j, final int k, final List< T > values, final int[] permutation, final Comparator< ? super T > comparator )
	{
		while ( true )
		{
			final int pivotpos = partitionSubList( i, j, values, permutation, comparator );
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

	/**
	 * Partition a list of {@code values} such that the k-th smallest value is
	 * at position {@code k}, elements before the k-th are smaller or equal and
	 * elements after the k-th are larger or equal.
	 * 
	 * <p>
	 * The {@code permutation} array is permuted in the same way as the list.
	 * Usually, this will be an array of indices, so that the partition
	 * operation can be mirrored in another list: Suppose, we have a list of
	 * keys and a lists (or several) of values. If we use {@code kthElement} to
	 * sort the keys, we want to reorder the values in the same manner. We pass
	 * an indices array [0, 1, 2, ...] and use the permutation of the indices to
	 * permute the values list.
	 * </p>
	 * 
	 * @param k
	 *            index for k-th smallest value. 0 <= k < values.size().
	 * @param values
	 *            array
	 * @param permutation
	 *            elements of this array are permuted in the same way as the
	 *            elements in the values list
	 * @param compare
	 *            ordering function on T
	 */
	public static < T > void kthElement( final int k, final List< T > values, final int[] permutation, final Comparator< ? super T > comparator )
	{
		kthElement( 0, values.size() - 1, k, values, permutation, comparator );
	}

	/**
	 * Partition a sublist of {@code values} such that the k-th smallest value
	 * is at position {@code k}, elements before the k-th are smaller or equal
	 * and elements after the k-th are larger or equal.
	 * 
	 * <p>
	 * The {@code permutation} array is permuted in the same way as the list.
	 * Usually, this will be an array of indices, so that the partition
	 * operation can be mirrored in another list: Suppose, we have a list of
	 * keys and a lists (or several) of values. If we use {@code kthElement} to
	 * sort the keys, we want to reorder the values in the same manner. We pass
	 * an indices array [0, 1, 2, ...] and use the permutation of the indices to
	 * permute the values list.
	 * </p>
	 * 
	 * @param i
	 *            index of first element of subarray
	 * @param j
	 *            index of last element of subarray
	 * @param k
	 *            index for k-th smallest value. i <= k <= j.
	 * @param values
	 *            array
	 * @param permutation
	 *            elements of this array are permuted in the same way as the
	 *            elements in the values list
	 */
	public static < T extends Comparable< T > > void kthElement( int i, int j, final int k, final List< T > values, final int[] permutation )
	{
		while ( true )
		{
			final int pivotpos = partitionSubList( i, j, values, permutation );
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

	/**
	 * Partition a list of {@code values} such that the k-th smallest value is
	 * at position {@code k}, elements before the k-th are smaller or equal and
	 * elements after the k-th are larger or equal.
	 * 
	 * <p>
	 * The {@code permutation} array is permuted in the same way as the list.
	 * Usually, this will be an array of indices, so that the partition
	 * operation can be mirrored in another list: Suppose, we have a list of
	 * keys and a lists (or several) of values. If we use {@code kthElement} to
	 * sort the keys, we want to reorder the values in the same manner. We pass
	 * an indices array [0, 1, 2, ...] and use the permutation of the indices to
	 * permute the values list.
	 * </p>
	 * 
	 * @param k
	 *            index for k-th smallest value. 0 <= k < values.size().
	 * @param values
	 *            array
	 * @param permutation
	 *            elements of this array are permuted in the same way as the
	 *            elements in the values list
	 */
	public static < T extends Comparable< T > > void kthElement( final int k, final List< T > values, final int[] permutation )
	{
		kthElement( 0, values.size() - 1, k, values, permutation );
	}

	/**
	 * Partition a sublist such that the k-th smallest value is at position
	 * {@code k}, elements before the k-th are smaller or equal, and elements
	 * after the k-th are larger or equal.
	 * 
	 * <p>
	 * After the function returns, the iterator {@code i} is on the k-th
	 * element. That is, {@code i.next()} gives the (k+1)-th element.
	 * </p>
	 * 
	 * <p>
	 * The {@code permutation} array is permuted in the same way as the list.
	 * Usually, this will be an array of indices, so that the partition
	 * operation can be mirrored in another list: Suppose, we have a list of
	 * keys and a lists (or several) of values. If we use {@code kthElement} to
	 * sort the keys, we want to reorder the values in the same manner. We pass
	 * an indices array [0, 1, 2, ...] and use the permutation of the indices to
	 * permute the values list.
	 * </p>
	 * 
	 * @param i
	 *            iterator pointing before first element of the sublist, that
	 *            is, {@code i.next()} gives you the first element.
	 * @param j
	 *            iterator pointing behind the last element of the sublist, that
	 *            is, {@code i.previous()} gives you the last element.
	 * @param k
	 *            index for k-th smallest value. i.nextIndex() <= k <=
	 *            j.previousIndex().
	 * @param permutation
	 *            elements of this array are permuted in the same way as the
	 *            elements in the values list
	 * @param compare
	 *            ordering function on T
	 */
	public static < T > void kthElement( final ListIterator< T > i, final ListIterator< T > j, final int k, final int[] permutation, final Comparator< ? super T > comparator )
	{
		while ( true )
		{
			final int iPos = i.nextIndex();
			final int jPos = j.previousIndex();
			partitionSubList( i, j, permutation, comparator );
			final int pivotpos = i.nextIndex() - 1;
			if ( pivotpos > k )
			{
				// partition lower half
				for ( int c = i.nextIndex() - iPos; c > 0; --c )
					i.previous();
				for ( int c = j.previousIndex() - ( pivotpos - 1 ); c > 0; --c )
					j.previous();
			}
			else if ( pivotpos < k )
			{
				// partition upper half
				for ( int c = i.nextIndex() - ( pivotpos + 1 ); c > 0; --c )
					i.previous();
				for ( int c = j.previousIndex() - jPos; c > 0; --c )
					j.previous();
			}
			else
				return;
		}
	}

	/**
	 * Partition a sublist such that the k-th smallest value is at position
	 * {@code k}, elements before the k-th are smaller or equal, and elements
	 * after the k-th are larger or equal.
	 * 
	 * <p>
	 * After the function returns, the iterator {@code i} is on the k-th
	 * element. That is, {@code i.next()} gives the (k+1)-th element.
	 * </p>
	 * 
	 * <p>
	 * The {@code permutation} array is permuted in the same way as the list.
	 * Usually, this will be an array of indices, so that the partition
	 * operation can be mirrored in another list: Suppose, we have a list of
	 * keys and a lists (or several) of values. If we use {@code kthElement} to
	 * sort the keys, we want to reorder the values in the same manner. We pass
	 * an indices array [0, 1, 2, ...] and use the permutation of the indices to
	 * permute the values list.
	 * </p>
	 * 
	 * @param i
	 *            iterator pointing before first element of the sublist, that
	 *            is, {@code i.next()} gives you the first element.
	 * @param j
	 *            iterator pointing behind the last element of the sublist, that
	 *            is, {@code i.previous()} gives you the last element.
	 * @param k
	 *            index for k-th smallest value. i.nextIndex() <= k <=
	 *            j.previousIndex().
	 * @param permutation
	 *            elements of this array are permuted in the same way as the
	 *            elements in the values list
	 */
	public static < T extends Comparable< T > > void kthElement( final ListIterator< T > i, final ListIterator< T > j, final int k, final int[] permutation )
	{
		while ( true )
		{
			final int iPos = i.nextIndex();
			final int jPos = j.previousIndex();
			partitionSubList( i, j, permutation );
			final int pivotpos = i.nextIndex() - 1;
			if ( pivotpos > k )
			{
				// partition lower half
				for ( int c = i.nextIndex() - iPos; c > 0; --c )
					i.previous();
				for ( int c = j.previousIndex() - ( pivotpos - 1 ); c > 0; --c )
					j.previous();
			}
			else if ( pivotpos < k )
			{
				// partition upper half
				for ( int c = i.nextIndex() - ( pivotpos + 1 ); c > 0; --c )
					i.previous();
				for ( int c = j.previousIndex() - jPos; c > 0; --c )
					j.previous();
			}
			else
				return;
		}
	}
}
