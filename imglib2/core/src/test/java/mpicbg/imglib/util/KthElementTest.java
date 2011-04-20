package mpicbg.imglib.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.ListIterator;

import org.junit.Test;


public class KthElementTest
{
	@Test
	public void testMedianByte()
	{
		byte[] values = new byte[] {2, -1, 1, 100, 123, 12};
		byte[] sortedValues = values.clone();

		final int i = 1;
		final int j = 3;
		int k = i + ( j - i ) / 2;
		KthElement.kthElement( i, j, k, values );
		Arrays.sort( sortedValues, i, j+1 );

		// the elements at the k-th positions should be equal 
		assertEquals( values[ k ], sortedValues[ k ] );

		// the elements before the k-th should be equal or smaller than the k-th
		for ( int p = i; p < k; ++p )
			assertTrue( values[ p ] <= values[ k ] );

		// the elements after the k-th should be equal or greater than the k-th
		for ( int p = k + 1; p <= j; ++p )
			assertTrue( values[ p ] >= values[ k ] );

		// all elements should be contained in list, i.e., after sorting lists should be identical
		Arrays.sort( values, i, j+1 );
		assertArrayEquals( sortedValues, values );
	}

	@Test
	public void testMedianByteFull()
	{
		byte[] values = new byte[] {2, -1, 1, 100, 123, 12};
		byte[] sortedValues = values.clone();

		final int i = 0;
		final int j = values.length - 1;
		int k = i + ( j - i ) / 2;
		KthElement.kthElement( k, values );
		Arrays.sort( sortedValues );

		// the elements at the k-th positions should be equal 
		assertEquals( values[ k ], sortedValues[ k ] );

		// the elements before the k-th should be equal or smaller than the k-th
		for ( int p = i; p < k; ++p )
			assertTrue( values[ p ] <= values[ k ] );

		// the elements after the k-th should be equal or greater than the k-th
		for ( int p = k + 1; p <= j; ++p )
			assertTrue( values[ p ] >= values[ k ] );

		// all elements should be contained in list, i.e., after sorting lists should be identical
		Arrays.sort( values );
		assertArrayEquals( sortedValues, values );
	}

	@Test
	public void testMinByteFull()
	{
		byte[] values = new byte[] {2, -1, 1, 100, 123, 12};
		byte[] sortedValues = values.clone();

		final int i = 0;
		final int j = values.length - 1;
		int k = 0;
		KthElement.kthElement( k, values );
		Arrays.sort( sortedValues );

		// the elements at the k-th positions should be equal 
		assertEquals( values[ k ], sortedValues[ k ] );

		// the elements before the k-th should be equal or smaller than the k-th
		for ( int p = i; p < k; ++p )
			assertTrue( values[ p ] <= values[ k ] );

		// the elements after the k-th should be equal or greater than the k-th
		for ( int p = k + 1; p <= j; ++p )
			assertTrue( values[ p ] >= values[ k ] );

		// all elements should be contained in list, i.e., after sorting lists should be identical
		Arrays.sort( values );
		assertArrayEquals( sortedValues, values );
	}

	@Test
	public void testMaxByteFull()
	{
		byte[] values = new byte[] {2, -1, 1, 100, 123, 12};
		byte[] sortedValues = values.clone();

		final int i = 0;
		final int j = values.length - 1;
		int k = j;
		KthElement.kthElement( k, values );
		Arrays.sort( sortedValues );

		// the elements at the k-th positions should be equal 
		assertEquals( values[ k ], sortedValues[ k ] );

		// the elements before the k-th should be equal or smaller than the k-th
		for ( int p = i; p < k; ++p )
			assertTrue( values[ p ] <= values[ k ] );

		// the elements after the k-th should be equal or greater than the k-th
		for ( int p = k + 1; p <= j; ++p )
			assertTrue( values[ p ] >= values[ k ] );

		// all elements should be contained in list, i.e., after sorting lists should be identical
		Arrays.sort( values );
		assertArrayEquals( sortedValues, values );
	}
	
	@Test
	public void testMedianShortFull()
	{
		short[] values = new short[] {2, -1, 1, 100, 123, 12, 19, 12183, 123, 12, 6453, 233};
		short[] sortedValues = values.clone();

		final int i = 0;
		final int j = values.length - 1;
		int k = i + ( j - i ) / 2;
		KthElement.kthElement( k, values );
		Arrays.sort( sortedValues );

		// the elements at the k-th positions should be equal 
		assertEquals( values[ k ], sortedValues[ k ] );

		// the elements before the k-th should be equal or smaller than the k-th
		for ( int p = i; p < k; ++p )
			assertTrue( values[ p ] <= values[ k ] );

		// the elements after the k-th should be equal or greater than the k-th
		for ( int p = k + 1; p <= j; ++p )
			assertTrue( values[ p ] >= values[ k ] );

		// all elements should be contained in list, i.e., after sorting lists should be identical
		Arrays.sort( values );
		assertArrayEquals( sortedValues, values );
	}

	@Test
	public void testMedianIntFull()
	{
		int[] values = new int[] {2, 233};
		int[] sortedValues = values.clone();

		final int i = 0;
		final int j = values.length - 1;
		int k = i + ( j - i ) / 2;
		KthElement.kthElement( k, values );
		Arrays.sort( sortedValues );

		// the elements at the k-th positions should be equal 
		assertEquals( values[ k ], sortedValues[ k ] );

		// the elements before the k-th should be equal or smaller than the k-th
		for ( int p = i; p < k; ++p )
			assertTrue( values[ p ] <= values[ k ] );

		// the elements after the k-th should be equal or greater than the k-th
		for ( int p = k + 1; p <= j; ++p )
			assertTrue( values[ p ] >= values[ k ] );

		// all elements should be contained in list, i.e., after sorting lists should be identical
		Arrays.sort( values );
		assertArrayEquals( sortedValues, values );
	}

	@Test
	public void testMedianLongFull()
	{
		long[] values = new long[] {2, -123890123, 12, 6453, 233, 1, 1, 1, 1, 1};
		long[] sortedValues = values.clone();

		final int i = 0;
		final int j = values.length - 1;
		int k = i + ( j - i ) / 2;
		KthElement.kthElement( k, values );
		Arrays.sort( sortedValues );

		// the elements at the k-th positions should be equal 
		assertEquals( values[ k ], sortedValues[ k ] );

		// the elements before the k-th should be equal or smaller than the k-th
		for ( int p = i; p < k; ++p )
			assertTrue( values[ p ] <= values[ k ] );

		// the elements after the k-th should be equal or greater than the k-th
		for ( int p = k + 1; p <= j; ++p )
			assertTrue( values[ p ] >= values[ k ] );

		// all elements should be contained in list, i.e., after sorting lists should be identical
		Arrays.sort( values );
		assertArrayEquals( sortedValues, values );
	}

	@Test
	public void testMedianFloatFull()
	{
		float[] values = new float[] {2, -123890123, 12, 6453, 233, 1, 1, 1, 1, 1};
		float[] sortedValues = values.clone();

		final int i = 0;
		final int j = values.length - 1;
		int k = i + ( j - i ) / 2;
		KthElement.kthElement( k, values );
		Arrays.sort( sortedValues );

		// the elements at the k-th positions should be equal 
		assertEquals( values[ k ], sortedValues[ k ], 0 );

		// the elements before the k-th should be equal or smaller than the k-th
		for ( int p = i; p < k; ++p )
			assertTrue( values[ p ] <= values[ k ] );

		// the elements after the k-th should be equal or greater than the k-th
		for ( int p = k + 1; p <= j; ++p )
			assertTrue( values[ p ] >= values[ k ] );

		// all elements should be contained in list, i.e., after sorting lists should be identical
		Arrays.sort( values );
		assertArrayEquals( sortedValues, values, 0 );
	}
	
	@Test
	public void testMedianDoubleFull()
	{
		double[] values = new double[] {2, 453, 233, 1, 1, 1, 1, 1, 0.7};
		double[] sortedValues = values.clone();

		final int i = 0;
		final int j = values.length - 1;
		int k = i + ( j - i ) / 2;
		KthElement.kthElement( k, values );
		Arrays.sort( sortedValues );

		// the elements at the k-th positions should be equal 
		assertEquals( values[ k ], sortedValues[ k ], 0 );

		// the elements before the k-th should be equal or smaller than the k-th
		for ( int p = i; p < k; ++p )
			assertTrue( values[ p ] <= values[ k ] );

		// the elements after the k-th should be equal or greater than the k-th
		for ( int p = k + 1; p <= j; ++p )
			assertTrue( values[ p ] >= values[ k ] );

		// all elements should be contained in list, i.e., after sorting lists should be identical
		Arrays.sort( values, i, j+1 );
		assertArrayEquals( sortedValues, values, 0 );
	}
	
	@Test
	public void testMedianFloatObject()
	{
		ArrayList< Float > values = new ArrayList< Float >();
		for( float f : new float[] {123, 21, 12912, 321, 32, 12345, 249, 5823, 834, 10, 23, 329 } ) {
			values.add( f );
		}
		@SuppressWarnings( "unchecked" )
		ArrayList< Float > sortedValues = ( ArrayList< Float > ) values.clone();

		final int i = 4;
		final int j = 9;
		int k = i + ( j - i ) / 2;
		KthElement.kthElement( i, j, k, values );
		Collections.sort( sortedValues.subList( i, j+1 ) );

		// the elements at the k-th positions should be equal 
		assertEquals( values.get( k ), sortedValues.get( k ), 0 );

		// the elements before the k-th should be equal or smaller than the k-th
		for ( int p = i; p < k; ++p )
			assertTrue( values.get( p ) <= values.get( k ) );

		// the elements after the k-th should be equal or greater than the k-th
		for ( int p = k + 1; p <= j; ++p )
			assertTrue( values.get( p ) >= values.get( k ) );

		// all elements should be contained in list, i.e., after sorting lists should be identical
		Collections.sort( values.subList( i, j+1 ) );
		for ( int p = i; p <= j; ++p )
			assertTrue( values.get( p ).equals( sortedValues.get( p ) ) );
	}
	
	@Test
	public void testMedianFloatObjectFull()
	{
		ArrayList< Float > values = new ArrayList< Float >();
		for( float f : new float[] {123, 21, 12912, 321, 32, 12345, 249, 5823, 834, 10, 23, 329 } ) {
			values.add( f );
		}
		@SuppressWarnings( "unchecked" )
		ArrayList< Float > sortedValues = ( ArrayList< Float > ) values.clone();

		final int i = 0;
		final int j = values.size() - 1;
		int k = i + ( j - i ) / 2;
		KthElement.kthElement( k, values );
		Collections.sort( sortedValues );

		// the elements at the k-th positions should be equal 
		assertEquals( values.get( k ), sortedValues.get( k ), 0 );

		// the elements before the k-th should be equal or smaller than the k-th
		for ( int p = i; p < k; ++p )
			assertTrue( values.get( p ) <= values.get( k ) );

		// the elements after the k-th should be equal or greater than the k-th
		for ( int p = k + 1; p <= j; ++p )
			assertTrue( values.get( p ) >= values.get( k ) );

		// all elements should be contained in list, i.e., after sorting lists should be identical
		Collections.sort( values );
		for ( int p = i; p <= j; ++p )
			assertTrue( values.get( p ).equals( sortedValues.get( p ) ) );
	}
	
	public static class ComparableComparator< T extends Comparable< T > > implements Comparator< T >
	{
		@Override
		public int compare( T o1, T o2 )
		{
			return o1.compareTo( o2 );
		}
	}

	@Test
	public void testMedianFloatObjectFullComparator()
	{
		ArrayList< Float > values = new ArrayList< Float >();
		for( float f : new float[] {123, 21, 12912, 321, 32, 12345, 249, 5823, 834, 10, 23, 329 } ) {
			values.add( f );
		}
		@SuppressWarnings( "unchecked" )
		ArrayList< Float > sortedValues = ( ArrayList< Float > ) values.clone();

		final int i = 0;
		final int j = values.size() - 1;
		int k = i + ( j - i ) / 2;
		KthElement.kthElement( k, values, new ComparableComparator< Float >() );
		Collections.sort( sortedValues );

		// the elements at the k-th positions should be equal 
		assertEquals( values.get( k ), sortedValues.get( k ), 0 );

		// the elements before the k-th should be equal or smaller than the k-th
		for ( int p = i; p < k; ++p )
			assertTrue( values.get( p ) <= values.get( k ) );

		// the elements after the k-th should be equal or greater than the k-th
		for ( int p = k + 1; p <= j; ++p )
			assertTrue( values.get( p ) >= values.get( k ) );

		// all elements should be contained in list, i.e., after sorting lists should be identical
		Collections.sort( values );
		for ( int p = i; p <= j; ++p )
			assertTrue( values.get( p ).equals( sortedValues.get( p ) ) );
	}
	
	@Test
	public void testMedianFloatObjectIterator()
	{
		ArrayList< Float > values = new ArrayList< Float >();
		for( float f : new float[] {123, 21, 12912, 321, 32, 12345, 249, 5823, 834, 10, 23, 329 } ) {
			values.add( f );
		}
		@SuppressWarnings( "unchecked" )
		ArrayList< Float > sortedValues = ( ArrayList< Float > ) values.clone();

		final int i = 4;
		final int j = 9;
		ListIterator< Float > iIterator = values.listIterator( i );
		ListIterator< Float > jIterator = values.listIterator( j + 1 );
		int k = i + ( j - i ) / 2;
		KthElement.kthElement( iIterator, jIterator, k );
		Collections.sort( sortedValues.subList( i, j+1 ) );
		
		// iIterator should be at k
		assertEquals( iIterator.nextIndex() - 1, k );

		// the elements at the k-th positions should be equal 
		assertEquals( values.get( k ), sortedValues.get( k ), 0 );

		// the elements before the k-th should be equal or smaller than the k-th
		for ( int p = i; p < k; ++p )
			assertTrue( values.get( p ) <= values.get( k ) );

		// the elements after the k-th should be equal or greater than the k-th
		for ( int p = k + 1; p <= j; ++p )
			assertTrue( values.get( p ) >= values.get( k ) );

		// all elements should be contained in list, i.e., after sorting lists should be identical
		Collections.sort( values.subList( i, j+1 ) );
		for ( int p = i; p <= j; ++p )
			assertTrue( values.get( p ).equals( sortedValues.get( p ) ) );
	}

	@Test
	public void testMedianFloatObjectFullIteratorComparator()
	{
		ArrayList< Float > values = new ArrayList< Float >();
		for( float f : new float[] {123, 21, 12912, 321, 32, 12345, 249, 5823, 834, 10, 23, 329 } ) {
			values.add( f );
		}
		@SuppressWarnings( "unchecked" )
		ArrayList< Float > sortedValues = ( ArrayList< Float > ) values.clone();

		final int i = 0;
		final int j = values.size() - 1;
		ListIterator< Float > iIterator = values.listIterator( i );
		ListIterator< Float > jIterator = values.listIterator( j + 1 );
		int k = i + ( j - i ) / 2;
		KthElement.kthElement( iIterator, jIterator, k, new ComparableComparator< Float >() );
		Collections.sort( sortedValues );

		// the elements at the k-th positions should be equal 
		assertEquals( values.get( k ), sortedValues.get( k ), 0 );

		// the elements before the k-th should be equal or smaller than the k-th
		for ( int p = i; p < k; ++p )
			assertTrue( values.get( p ) <= values.get( k ) );

		// the elements after the k-th should be equal or greater than the k-th
		for ( int p = k + 1; p <= j; ++p )
			assertTrue( values.get( p ) >= values.get( k ) );

		// all elements should be contained in list, i.e., after sorting lists should be identical
		Collections.sort( values );
		for ( int p = i; p <= j; ++p )
			assertTrue( values.get( p ).equals( sortedValues.get( p ) ) );
	}

}
