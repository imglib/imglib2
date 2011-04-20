package net.imglib2.util;

import static net.imglib2.util.Partition.partitionSubList;

import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

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
	public static void kthElement( int i, int j, int k, byte[] values )
	{
		while ( true )
		{
			int pivotpos = partitionSubList( i, j, values );
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
	public static void kthElement( int k, byte[] values )
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
	public static void kthElement( int i, int j, int k, short[] values )
	{
		while ( true )
		{
			int pivotpos = partitionSubList( i, j, values );
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
	public static void kthElement( int k, short[] values )
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
	public static void kthElement( int i, int j, int k, int[] values )
	{
		while ( true )
		{
			int pivotpos = partitionSubList( i, j, values );
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
	public static void kthElement( int k, int[] values )
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
	public static void kthElement( int i, int j, int k, long[] values )
	{
		while ( true )
		{
			int pivotpos = partitionSubList( i, j, values );
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
	public static void kthElement( int k, long[] values )
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
	public static void kthElement( int i, int j, int k, float[] values )
	{
		while ( true )
		{
			int pivotpos = partitionSubList( i, j, values );
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
	public static void kthElement( int k, float[] values )
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
	public static void kthElement( int i, int j, int k, double[] values )
	{
		while ( true )
		{
			int pivotpos = partitionSubList( i, j, values );
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
	public static void kthElement( int k, double[] values )
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
	public static void kthElement( int i, int j, int k, char[] values )
	{
		while ( true )
		{
			int pivotpos = partitionSubList( i, j, values );
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
	public static void kthElement( int k, char[] values )
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
	public static < T > void kthElement( int i, int j, int k, List< T > values, Comparator< ? super T > comparator )
	{
		while ( true )
		{
			int pivotpos = partitionSubList( i, j, values, comparator );
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
	public static < T > void kthElement( int k, List< T > values, Comparator< ? super T > comparator )
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
	public static < T extends Comparable< T > > void kthElement( int i, int j, int k, List< T > values )
	{
		while ( true )
		{
			int pivotpos = partitionSubList( i, j, values );
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
	public static < T extends Comparable< T > > void kthElement( int k, List< T > values )
	{
		kthElement( 0, values.size() - 1, k, values );
	}

	/**
	 * Partition a sublist such that the k-th smallest value
	 * is at position {@code k}, elements before the k-th are smaller or equal,
	 * and elements after the k-th are larger or equal.
	 * 
	 * <p>
	 * After the function returns, the iterator {@code i} is on the k-th element.
	 * That is, {@code i.next()} gives the (k+1)-th element.
	 * </p>
	 *
	 * @param i
	 *            iterator pointing before first element of the sublist, that
	 *            is, {@code i.next()} gives you the first element.
	 * @param j
	 *            iterator pointing behind the last element of the sublist, that
	 *            is, {@code i.previous()} gives you the last element.
	 * @param k
	 *            index for k-th smallest value. i.nextIndex() <= k <= j.previousIndex().
	 * @param compare
	 *            ordering function on T
	 */
	public static < T > void kthElement( ListIterator< T > i, ListIterator< T > j, int k, Comparator< ? super T > comparator )
	{
		while ( true )
		{
			int iPos = i.nextIndex();
			int jPos = j.previousIndex();
			partitionSubList( i, j, comparator );
			int pivotpos = i.nextIndex() - 1;
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
	 * Partition a sublist such that the k-th smallest value
	 * is at position {@code k}, elements before the k-th are smaller or equal,
	 * and elements after the k-th are larger or equal.
	 * 
	 * <p>
	 * After the function returns, the iterator {@code i} is on the k-th element.
	 * That is, {@code i.next()} gives the (k+1)-th element.
	 * </p>
	 *
	 * @param i
	 *            iterator pointing before first element of the sublist, that
	 *            is, {@code i.next()} gives you the first element.
	 * @param j
	 *            iterator pointing behind the last element of the sublist, that
	 *            is, {@code i.previous()} gives you the last element.
	 * @param k
	 *            index for k-th smallest value. i.nextIndex() <= k <= j.previousIndex().
	 */
	public static < T extends Comparable< T > > void kthElement( ListIterator< T > i, ListIterator< T > j, int k )
	{
		while ( true )
		{
			int iPos = i.nextIndex();
			int jPos = j.previousIndex();
			partitionSubList( i, j );
			int pivotpos = i.nextIndex() - 1;
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
