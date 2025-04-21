/*
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

package net.imglib2.util;

import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

/**
 * TODO
 *
 */
public class Partition
{
	/**
	 * Partition a subarray of {@code values}.
	 *
	 * The element at index {@code j} is taken as the pivot value. The elements
	 * {@code [i,j]} are reordered, such that all elements before the pivot are
	 * smaller and all elements after the pivot are equal or larger than the
	 * pivot. The index of the pivot element is returned.
	 *
	 * @param i
	 *            index of first element of subarray
	 * @param j
	 *            index of last element of subarray
	 * @param values
	 *            array
	 * @return index of pivot element
	 */
	public static int partitionSubList( int i, int j, final byte[] values )
	{
		final int pivotIndex = j;
		final byte pivot = values[ j-- ];

		A: while ( true )
		{
			// move i forward while < pivot (and not at j)
			while ( i <= j )
			{
				final byte ti = values[ i ];
				if ( ti >= pivot )
					break;
				++i;
			}
			// now [i] is the place where the next value < pivot is to be
			// inserted

			if ( i > j )
				break;

			// move j backward while >= pivot (and not at i)
			while ( true )
			{
				final byte tj = values[ j ];
				if ( tj < pivot )
				{
					// swap [j] with [i]
					final byte tmp = values[ i ];
					values[ i ] = values[ j ];
					values[ j ] = tmp;
					++i;
					--j;
					break;
				}
				else if ( j == i )
				{
					break A;
				}
				--j;
			}
		}

		// we are done. put the pivot element here.
		if ( i != pivotIndex )
		{
			values[ pivotIndex ] = values[ i ];
			values[ i ] = pivot;
		}
		return i;
	}

	/**
	 * Partition a subarray of {@code values}.
	 *
	 * The element at index {@code j} is taken as the pivot value. The elements
	 * {@code [i,j]} are reordered, such that all elements before the pivot are
	 * smaller and all elements after the pivot are equal or larger than the
	 * pivot. The index of the pivot element is returned.
	 *
	 * @param i
	 *            index of first element of subarray
	 * @param j
	 *            index of last element of subarray
	 * @param values
	 *            array
	 * @return index of pivot element
	 */
	public static int partitionSubList( int i, int j, final short[] values )
	{
		final int pivotIndex = j;
		final short pivot = values[ j-- ];

		A: while ( true )
		{
			// move i forward while < pivot (and not at j)
			while ( i <= j )
			{
				final short ti = values[ i ];
				if ( ti >= pivot )
					break;
				++i;
			}
			// now [i] is the place where the next value < pivot is to be
			// inserted

			if ( i > j )
				break;

			// move j backward while >= pivot (and not at i)
			while ( true )
			{
				final short tj = values[ j ];
				if ( tj < pivot )
				{
					// swap [j] with [i]
					final short tmp = values[ i ];
					values[ i ] = values[ j ];
					values[ j ] = tmp;
					++i;
					--j;
					break;
				}
				else if ( j == i )
				{
					break A;
				}
				--j;
			}
		}

		// we are done. put the pivot element here.
		if ( i != pivotIndex )
		{
			values[ pivotIndex ] = values[ i ];
			values[ i ] = pivot;
		}
		return i;
	}

	/**
	 * Partition a subarray of {@code values}.
	 *
	 * The element at index {@code j} is taken as the pivot value. The elements
	 * {@code [i,j]} are reordered, such that all elements before the pivot are
	 * smaller and all elements after the pivot are equal or larger than the
	 * pivot. The index of the pivot element is returned.
	 *
	 * @param i
	 *            index of first element of subarray
	 * @param j
	 *            index of last element of subarray
	 * @param values
	 *            array
	 * @return index of pivot element
	 */
	public static int partitionSubList( int i, int j, final int[] values )
	{
		final int pivotIndex = j;
		final int pivot = values[ j-- ];

		A: while ( true )
		{
			// move i forward while < pivot (and not at j)
			while ( i <= j )
			{
				final int ti = values[ i ];
				if ( ti >= pivot )
					break;
				++i;
			}
			// now [i] is the place where the next value < pivot is to be
			// inserted

			if ( i > j )
				break;

			// move j backward while >= pivot (and not at i)
			while ( true )
			{
				final int tj = values[ j ];
				if ( tj < pivot )
				{
					// swap [j] with [i]
					final int tmp = values[ i ];
					values[ i ] = values[ j ];
					values[ j ] = tmp;
					++i;
					--j;
					break;
				}
				else if ( j == i )
				{
					break A;
				}
				--j;
			}
		}

		// we are done. put the pivot element here.
		if ( i != pivotIndex )
		{
			values[ pivotIndex ] = values[ i ];
			values[ i ] = pivot;
		}
		return i;
	}

	/**
	 * Partition a subarray of {@code values}.
	 *
	 * The element at index {@code j} is taken as the pivot value. The elements
	 * {@code [i,j]} are reordered, such that all elements before the pivot are
	 * smaller and all elements after the pivot are equal or larger than the
	 * pivot. The index of the pivot element is returned.
	 *
	 * @param i
	 *            index of first element of subarray
	 * @param j
	 *            index of last element of subarray
	 * @param values
	 *            array
	 * @return index of pivot element
	 */
	public static int partitionSubList( int i, int j, final long[] values )
	{
		final int pivotIndex = j;
		final long pivot = values[ j-- ];

		A: while ( true )
		{
			// move i forward while < pivot (and not at j)
			while ( i <= j )
			{
				final long ti = values[ i ];
				if ( ti >= pivot )
					break;
				++i;
			}
			// now [i] is the place where the next value < pivot is to be
			// inserted

			if ( i > j )
				break;

			// move j backward while >= pivot (and not at i)
			while ( true )
			{
				final long tj = values[ j ];
				if ( tj < pivot )
				{
					// swap [j] with [i]
					final long tmp = values[ i ];
					values[ i ] = values[ j ];
					values[ j ] = tmp;
					++i;
					--j;
					break;
				}
				else if ( j == i )
				{
					break A;
				}
				--j;
			}
		}

		// we are done. put the pivot element here.
		if ( i != pivotIndex )
		{
			values[ pivotIndex ] = values[ i ];
			values[ i ] = pivot;
		}
		return i;
	}

	/**
	 * Partition a subarray of {@code values}.
	 *
	 * The element at index {@code j} is taken as the pivot value. The elements
	 * {@code [i,j]} are reordered, such that all elements before the pivot are
	 * smaller and all elements after the pivot are equal or larger than the
	 * pivot. The index of the pivot element is returned.
	 *
	 * @param i
	 *            index of first element of subarray
	 * @param j
	 *            index of last element of subarray
	 * @param values
	 *            array
	 * @return index of pivot element
	 */
	public static int partitionSubList( int i, int j, final float[] values )
	{
		final int pivotIndex = j;
		final float pivot = values[ j-- ];

		A: while ( true )
		{
			// move i forward while < pivot (and not at j)
			while ( i <= j )
			{
				final float ti = values[ i ];
				if ( ti >= pivot )
					break;
				++i;
			}
			// now [i] is the place where the next value < pivot is to be
			// inserted

			if ( i > j )
				break;

			// move j backward while >= pivot (and not at i)
			while ( true )
			{
				final float tj = values[ j ];
				if ( tj < pivot )
				{
					// swap [j] with [i]
					final float tmp = values[ i ];
					values[ i ] = values[ j ];
					values[ j ] = tmp;
					++i;
					--j;
					break;
				}
				else if ( j == i )
				{
					break A;
				}
				--j;
			}
		}

		// we are done. put the pivot element here.
		if ( i != pivotIndex )
		{
			values[ pivotIndex ] = values[ i ];
			values[ i ] = pivot;
		}
		return i;
	}

	/**
	 * Partition a subarray of {@code values}.
	 *
	 * The element at index {@code j} is taken as the pivot value. The elements
	 * {@code [i,j]} are reordered, such that all elements before the pivot are
	 * smaller and all elements after the pivot are equal or larger than the
	 * pivot. The index of the pivot element is returned.
	 *
	 * @param i
	 *            index of first element of subarray
	 * @param j
	 *            index of last element of subarray
	 * @param values
	 *            array
	 * @return index of pivot element
	 */
	public static int partitionSubList( int i, int j, final double[] values )
	{
		final int pivotIndex = j;
		final double pivot = values[ j-- ];

		A: while ( true )
		{
			// move i forward while < pivot (and not at j)
			while ( i <= j )
			{
				final double ti = values[ i ];
				if ( ti >= pivot )
					break;
				++i;
			}
			// now [i] is the place where the next value < pivot is to be
			// inserted

			if ( i > j )
				break;

			// move j backward while >= pivot (and not at i)
			while ( true )
			{
				final double tj = values[ j ];
				if ( tj < pivot )
				{
					// swap [j] with [i]
					final double tmp = values[ i ];
					values[ i ] = values[ j ];
					values[ j ] = tmp;
					++i;
					--j;
					break;
				}
				else if ( j == i )
				{
					break A;
				}
				--j;
			}
		}

		// we are done. put the pivot element here.
		if ( i != pivotIndex )
		{
			values[ pivotIndex ] = values[ i ];
			values[ i ] = pivot;
		}
		return i;
	}

	/**
	 * Partition a subarray of {@code values}.
	 *
	 * The element at index {@code j} is taken as the pivot value. The elements
	 * {@code [i,j]} are reordered, such that all elements before the pivot are
	 * smaller and all elements after the pivot are equal or larger than the
	 * pivot. The index of the pivot element is returned.
	 *
	 * @param i
	 *            index of first element of subarray
	 * @param j
	 *            index of last element of subarray
	 * @param values
	 *            array
	 * @return index of pivot element
	 */
	public static int partitionSubList( int i, int j, final char[] values )
	{
		final int pivotIndex = j;
		final char pivot = values[ j-- ];

		A: while ( true )
		{
			// move i forward while < pivot (and not at j)
			while ( i <= j )
			{
				final char ti = values[ i ];
				if ( ti >= pivot )
					break;
				++i;
			}
			// now [i] is the place where the next value < pivot is to be
			// inserted

			if ( i > j )
				break;

			// move j backward while >= pivot (and not at i)
			while ( true )
			{
				final char tj = values[ j ];
				if ( tj < pivot )
				{
					// swap [j] with [i]
					final char tmp = values[ i ];
					values[ i ] = values[ j ];
					values[ j ] = tmp;
					++i;
					--j;
					break;
				}
				else if ( j == i )
				{
					break A;
				}
				--j;
			}
		}

		// we are done. put the pivot element here.
		if ( i != pivotIndex )
		{
			values[ pivotIndex ] = values[ i ];
			values[ i ] = pivot;
		}
		return i;
	}

	/**
	 * Partition a sublist of {@code values}.
	 *
	 * The element at index {@code j} is taken as the pivot value. The elements
	 * {@code [i,j]} are reordered, such that all elements before the pivot are
	 * smaller and all elements after the pivot are equal or larger than the
	 * pivot. The index of the pivot element is returned.
	 *
	 * @param i
	 *            index of first element of the sublist
	 * @param j
	 *            index of last element of the sublist
	 * @param values
	 *            the list
	 * @param compare
	 *            ordering function on T
	 * @return index of pivot element
	 */
	public static < T > int partitionSubList( int i, int j, final List< T > values, final Comparator< ? super T > compare )
	{
		final int pivotIndex = j;
		final T pivot = values.get( j-- );

		A: while ( true )
		{
			// move i forward while < pivot (and not at j)
			while ( i <= j )
			{
				final T ti = values.get( i );
				if ( compare.compare( ti, pivot ) >= 0 )
					break;
				++i;
			}
			// now [i] is the place where the next value < pivot is to be
			// inserted

			if ( i > j )
				break;

			// move j backward while >= pivot (and not at i)
			while ( true )
			{
				final T tj = values.get( j );
				if ( compare.compare( tj, pivot ) < 0 )
				{
					// swap [j] with [i]
					final T tmp = values.get( i );
					values.set( i, values.get( j ) );
					values.set( j, tmp );
					++i;
					--j;
					break;
				}
				else if ( j == i )
				{
					break A;
				}
				--j;
			}
		}

		// we are done. put the pivot element here.
		// check whether the element at iLastIndex is <
		if ( i != pivotIndex )
		{
			values.set( pivotIndex, values.get( i ) );
			values.set( i, pivot );
		}
		return i;
	}

	/**
	 * Partition a sublist of {@code values}.
	 *
	 * The element at index {@code j} is taken as the pivot value. The elements
	 * {@code [i,j]} are reordered, such that all elements before the pivot are
	 * smaller and all elements after the pivot are equal or larger than the
	 * pivot. The index of the pivot element is returned.
	 *
	 * @param i
	 *            index of first element of the sublist
	 * @param j
	 *            index of last element of the sublist
	 * @param values
	 *            the list
	 * @return index of pivot element
	 */
	public static < T extends Comparable< T > > int partitionSubList( int i, int j, final List< T > values )
	{
		final int pivotIndex = j;
		final T pivot = values.get( j-- );

		A: while ( true )
		{
			// move i forward while < pivot (and not at j)
			while ( i <= j )
			{
				final T ti = values.get( i );
				if ( ti.compareTo( pivot ) >= 0 )
					break;
				++i;
			}
			// now [i] is the place where the next value < pivot is to be
			// inserted

			if ( i > j )
				break;

			// move j backward while >= pivot (and not at i)
			while ( true )
			{
				final T tj = values.get( j );
				if ( tj.compareTo( pivot ) < 0 )
				{
					// swap [j] with [i]
					final T tmp = values.get( i );
					values.set( i, values.get( j ) );
					values.set( j, tmp );
					++i;
					--j;
					break;
				}
				else if ( j == i )
				{
					break A;
				}
				--j;
			}
		}

		// we are done. put the pivot element here.
		// check whether the element at iLastIndex is <
		if ( i != pivotIndex )
		{
			values.set( pivotIndex, values.get( i ) );
			values.set( i, pivot );
		}
		return i;
	}

	/**
	 * Partition a sublist.
	 *
	 * The element at {@code j} is taken as the pivot value. The elements
	 * {@code [i,j]} are reordered, such that all elements before the pivot are
	 * smaller and all elements after the pivot are equal or larger than the
	 * pivot. The index of the pivot element is returned.
	 *
	 * <p>
	 * After the function returns, the iterator {@code i} is on the pivot
	 * element. That is, {@code i.next()} gives the element <em>after</em> the
	 * pivot.
	 * </p>
	 *
	 * @param i
	 *            iterator pointing before first element of the sublist, that
	 *            is, {@code i.next()} gives you the first element.
	 * @param j
	 *            iterator pointing behind the last element of the sublist, that
	 *            is, {@code i.previous()} gives you the last element.
	 * @param compare
	 *            ordering function on T
	 */
	public static < T > void partitionSubList( final ListIterator< T > i, final ListIterator< T > j, final Comparator< ? super T > compare )
	{
		final int pivotIndex = j.previousIndex();
		final T pivot = j.previous();

		A: while ( true )
		{
			// move i forward while < pivot (and not at j)
			while ( i.nextIndex() - 1 <= j.previousIndex() )
			{
				final T ti = i.next();
				if ( compare.compare( ti, pivot ) >= 0 )
				{
					i.previous();
					break;
				}
			}
			// now [i] is the place where the next value < pivot is to be
			// inserted
			// (via i.next() i.set()

			if ( i.nextIndex() > j.previousIndex() )
				break;

			// move j backward while >= pivot (and not at i)
			while ( true )
			{
				final T tj = j.previous();
				if ( compare.compare( tj, pivot ) < 0 )
				{
					// swap [j] with [i]
					final T ti = i.next();
					i.set( tj );
					j.set( ti );
					break;
				}
				else if ( j.previousIndex() == i.nextIndex() - 1 )
				{
					break A;
				}
			}
		}

		// we are done. put the pivot element here.
		if ( i.nextIndex() - 1 != pivotIndex )
		{
			for ( int c = pivotIndex - j.nextIndex() + 1; c > 0; --c )
				j.next();
			j.set( i.next() );
			i.set( pivot );
		}
		return;
	}

	/**
	 * Partition a sublist.
	 *
	 * The element at {@code j} is taken as the pivot value. The elements
	 * {@code [i,j]} are reordered, such that all elements before the pivot are
	 * smaller, and all elements after the pivot are equal or larger than the
	 * pivot. The index of the pivot element is returned.
	 *
	 * <p>
	 * After the function returns, the iterator {@code i} is on the pivot
	 * element. That is, {@code i.next()} gives the element <em>after</em> the
	 * pivot.
	 * </p>
	 *
	 * @param i
	 *            iterator pointing before first element of the sublist, that
	 *            is, {@code i.next()} gives you the first element.
	 * @param j
	 *            iterator pointing behind the last element of the sublist, that
	 *            is, {@code i.previous()} gives you the last element.
	 */
	public static < T extends Comparable< T > > void partitionSubList( final ListIterator< T > i, final ListIterator< T > j )
	{
		final int pivotIndex = j.previousIndex();
		final T pivot = j.previous();

		A: while ( true )
		{
			// move i forward while < pivot (and not at j)
			while ( i.nextIndex() - 1 <= j.previousIndex() )
			{
				final T ti = i.next();
				if ( ti.compareTo( pivot ) >= 0 )
				{
					i.previous();
					break;
				}
			}
			// now [i] is the place where the next value < pivot is to be
			// inserted
			// (via i.next() i.set()

			if ( i.nextIndex() > j.previousIndex() )
				break;

			// move j backward while >= pivot (and not at i)
			while ( true )
			{
				final T tj = j.previous();
				if ( tj.compareTo( pivot ) < 0 )
				{
					// swap [j] with [i]
					final T ti = i.next();
					i.set( tj );
					j.set( ti );
					break;
				}
				else if ( j.previousIndex() == i.nextIndex() - 1 )
				{
					break A;
				}
			}
		}

		// we are done. put the pivot element here.
		if ( i.nextIndex() - 1 != pivotIndex )
		{
			for ( int c = pivotIndex - j.nextIndex() + 1; c > 0; --c )
				j.next();
			j.set( i.next() );
			i.set( pivot );
		}
		return;
	}

	/**
	 * Partition a sublist of {@code values}.
	 *
	 * The element at index {@code j} is taken as the pivot value. The elements
	 * {@code [i,j]} are reordered, such that all elements before the pivot are
	 * smaller and all elements after the pivot are equal or larger than the
	 * pivot. The index of the pivot element is returned.
	 *
	 * <p>
	 * The {@code permutation} array is permuted in the same way as the list.
	 * Usually, this will be an array of indices, so that the partition
	 * operation can be mirrored in another list: Suppose, we have a list of
	 * keys and a lists (or several) of values. If we use
	 * {@code partitionSubList} to sort the keys, we want to reorder the values
	 * in the same manner. We pass an indices array [0, 1, 2, ...] and use the
	 * permutation of the indices to permute the values list.
	 * </p>
	 *
	 * @param i
	 *            index of first element of the sublist
	 * @param j
	 *            index of last element of the sublist
	 * @param values
	 *            the list
	 * @param permutation
	 *            elements of this array are permuted in the same way as the
	 *            elements in the values list
	 * @param compare
	 *            ordering function on T
	 * @return index of pivot element
	 */
	public static < T > int partitionSubList( int i, int j, final List< T > values, final int[] permutation, final Comparator< ? super T > compare )
	{
		final int pivotIndex = j;
		final int permutationPivot = permutation[ j ];
		final T pivot = values.get( j-- );

		A: while ( true )
		{
			// move i forward while < pivot (and not at j)
			while ( i <= j )
			{
				final T ti = values.get( i );
				if ( compare.compare( ti, pivot ) >= 0 )
					break;
				++i;
			}
			// now [i] is the place where the next value < pivot is to be
			// inserted

			if ( i > j )
				break;

			// move j backward while >= pivot (and not at i)
			while ( true )
			{
				final T tj = values.get( j );
				if ( compare.compare( tj, pivot ) < 0 )
				{
					// swap [j] with [i]
					final int indicesTmp = permutation[ i ];
					permutation[ i ] = permutation[ j ];
					permutation[ j ] = indicesTmp;
					final T tmp = values.get( i );
					values.set( i, values.get( j ) );
					values.set( j, tmp );
					++i;
					--j;
					break;
				}
				else if ( j == i )
				{
					break A;
				}
				--j;
			}
		}

		// we are done. put the pivot element here.
		// check whether the element at iLastIndex is <
		if ( i != pivotIndex )
		{
			values.set( pivotIndex, values.get( i ) );
			values.set( i, pivot );
			permutation[ pivotIndex ] = permutation[ i ];
			permutation[ i ] = permutationPivot;
		}
		return i;
	}

	/**
	 * Partition a sublist of {@code values}.
	 *
	 * The element at index {@code j} is taken as the pivot value. The elements
	 * {@code [i,j]} are reordered, such that all elements before the pivot are
	 * smaller and all elements after the pivot are equal or larger than the
	 * pivot. The index of the pivot element is returned.
	 *
	 * <p>
	 * The {@code permutation} array is permuted in the same way as the list.
	 * Usually, this will be an array of indices, so that the partition
	 * operation can be mirrored in another list: Suppose, we have a list of
	 * keys and a lists (or several) of values. If we use
	 * {@code partitionSubList} to sort the keys, we want to reorder the values
	 * in the same manner. We pass an indices array [0, 1, 2, ...] and use the
	 * permutation of the indices to permute the values list.
	 * </p>
	 *
	 * @param i
	 *            index of first element of the sublist
	 * @param j
	 *            index of last element of the sublist
	 * @param values
	 *            the list
	 * @param permutation
	 *            elements of this array are permuted in the same way as the
	 *            elements in the values list
	 * @return index of pivot element
	 */
	public static < T extends Comparable< T > > int partitionSubList( int i, int j, final List< T > values, final int[] permutation )
	{
		final int pivotIndex = j;
		final int permutationPivot = permutation[ j ];
		final T pivot = values.get( j-- );

		A: while ( true )
		{
			// move i forward while < pivot (and not at j)
			while ( i <= j )
			{
				final T ti = values.get( i );
				if ( ti.compareTo( pivot ) >= 0 )
					break;
				++i;
			}
			// now [i] is the place where the next value < pivot is to be
			// inserted

			if ( i > j )
				break;

			// move j backward while >= pivot (and not at i)
			while ( true )
			{
				final T tj = values.get( j );
				if ( tj.compareTo( pivot ) < 0 )
				{
					// swap [j] with [i]
					final int indicesTmp = permutation[ i ];
					permutation[ i ] = permutation[ j ];
					permutation[ j ] = indicesTmp;
					final T tmp = values.get( i );
					values.set( i, values.get( j ) );
					values.set( j, tmp );
					++i;
					--j;
					break;
				}
				else if ( j == i )
				{
					break A;
				}
				--j;
			}
		}

		// we are done. put the pivot element here.
		// check whether the element at iLastIndex is <
		if ( i != pivotIndex )
		{
			values.set( pivotIndex, values.get( i ) );
			values.set( i, pivot );
			permutation[ pivotIndex ] = permutation[ i ];
			permutation[ i ] = permutationPivot;
		}
		return i;
	}

	/**
	 * Partition a sublist.
	 *
	 * The element at {@code j} is taken as the pivot value. The elements
	 * {@code [i,j]} are reordered, such that all elements before the pivot are
	 * smaller and all elements after the pivot are equal or larger than the
	 * pivot. The index of the pivot element is returned.
	 *
	 * <p>
	 * After the function returns, the iterator {@code i} is on the pivot
	 * element. That is, {@code i.next()} gives the element <em>after</em> the
	 * pivot.
	 * </p>
	 *
	 * <p>
	 * The {@code permutation} array is permuted in the same way as the list.
	 * Usually, this will be an array of indices, so that the partition
	 * operation can be mirrored in another list: Suppose, we have a list of
	 * keys and a lists (or several) of values. If we use
	 * {@code partitionSubList} to sort the keys, we want to reorder the values
	 * in the same manner. We pass an indices array [0, 1, 2, ...] and use the
	 * permutation of the indices to permute the values list.
	 * </p>
	 *
	 * @param i
	 *            iterator pointing before first element of the sublist, that
	 *            is, {@code i.next()} gives you the first element.
	 * @param j
	 *            iterator pointing behind the last element of the sublist, that
	 *            is, {@code i.previous()} gives you the last element.
	 * @param permutation
	 *            elements of this array are permuted in the same way as the
	 *            elements in the values list
	 * @param compare
	 *            ordering function on T
	 */
	public static < T > void partitionSubList( final ListIterator< T > i, final ListIterator< T > j, final int[] permutation, final Comparator< ? super T > compare )
	{
		final int pivotIndex = j.previousIndex();
		final int permutationPivot = permutation[ pivotIndex ];
		final T pivot = j.previous();

		A: while ( true )
		{
			// move i forward while < pivot (and not at j)
			while ( i.nextIndex() - 1 <= j.previousIndex() )
			{
				final T ti = i.next();
				if ( compare.compare( ti, pivot ) >= 0 )
				{
					i.previous();
					break;
				}
			}
			// now [i] is the place where the next value < pivot is to be
			// inserted
			// (via i.next() i.set()

			if ( i.nextIndex() > j.previousIndex() )
				break;

			// move j backward while >= pivot (and not at i)
			while ( true )
			{
				final T tj = j.previous();
				if ( compare.compare( tj, pivot ) < 0 )
				{
					// swap [j] with [i]
					final int iIndex = i.nextIndex();
					final int jIndex = j.nextIndex();
					final int indicesTmp = permutation[ iIndex ];
					permutation[ iIndex ] = permutation[ jIndex ];
					permutation[ jIndex ] = indicesTmp;
					final T ti = i.next();
					i.set( tj );
					j.set( ti );
					break;
				}
				else if ( j.previousIndex() == i.nextIndex() - 1 )
				{
					break A;
				}
			}
		}

		// we are done. put the pivot element here.
		if ( i.nextIndex() - 1 != pivotIndex )
		{
			for ( int c = pivotIndex - j.nextIndex() + 1; c > 0; --c )
				j.next();
			final int iIndex = i.nextIndex();
			j.set( i.next() );
			i.set( pivot );
			permutation[ pivotIndex ] = permutation[ iIndex ];
			permutation[ iIndex ] = permutationPivot;
		}
		return;
	}

	/**
	 * Partition a sublist.
	 *
	 * The element at {@code j} is taken as the pivot value. The elements
	 * {@code [i,j]} are reordered, such that all elements before the pivot are
	 * smaller, and all elements after the pivot are equal or larger than the
	 * pivot. The index of the pivot element is returned.
	 *
	 * <p>
	 * After the function returns, the iterator {@code i} is on the pivot
	 * element. That is, {@code i.next()} gives the element <em>after</em> the
	 * pivot.
	 * </p>
	 *
	 * @param i
	 *            iterator pointing before first element of the sublist, that
	 *            is, {@code i.next()} gives you the first element.
	 * @param j
	 *            iterator pointing behind the last element of the sublist, that
	 *            is, {@code i.previous()} gives you the last element.
	 * @param permutation
	 *            elements of this array are permuted in the same way as the
	 *            elements in the values list
	 */
	public static < T extends Comparable< T > > void partitionSubList( final ListIterator< T > i, final ListIterator< T > j, final int[] permutation )
	{
		final int pivotIndex = j.previousIndex();
		final int permutationPivot = permutation[ pivotIndex ];
		final T pivot = j.previous();

		A: while ( true )
		{
			// move i forward while < pivot (and not at j)
			while ( i.nextIndex() - 1 <= j.previousIndex() )
			{
				final T ti = i.next();
				if ( ti.compareTo( pivot ) >= 0 )
				{
					i.previous();
					break;
				}
			}
			// now [i] is the place where the next value < pivot is to be
			// inserted
			// (via i.next() i.set()

			if ( i.nextIndex() > j.previousIndex() )
				break;

			// move j backward while >= pivot (and not at i)
			while ( true )
			{
				final T tj = j.previous();
				if ( tj.compareTo( pivot ) < 0 )
				{
					// swap [j] with [i]
					final int iIndex = i.nextIndex();
					final int jIndex = j.nextIndex();
					final int indicesTmp = permutation[ iIndex ];
					permutation[ iIndex ] = permutation[ jIndex ];
					permutation[ jIndex ] = indicesTmp;
					final T ti = i.next();
					i.set( tj );
					j.set( ti );
					break;
				}
				else if ( j.previousIndex() == i.nextIndex() - 1 )
				{
					break A;
				}
			}
		}

		// we are done. put the pivot element here.
		if ( i.nextIndex() - 1 != pivotIndex )
		{
			for ( int c = pivotIndex - j.nextIndex() + 1; c > 0; --c )
				j.next();
			final int iIndex = i.nextIndex();
			j.set( i.next() );
			i.set( pivot );
			permutation[ pivotIndex ] = permutation[ iIndex ];
			permutation[ iIndex ] = permutationPivot;
		}
		return;
	}
}
