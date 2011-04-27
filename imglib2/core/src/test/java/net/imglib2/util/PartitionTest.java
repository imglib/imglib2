package net.imglib2.util;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.ListIterator;

import org.junit.Test;


public class PartitionTest
{	
	@Test
	public void testPartitionByte()
	{
		byte[] values = new byte[] {2, -1, 1};
		byte[] sortedValues = values.clone();
		Arrays.sort( sortedValues );

		final int i = 0;
		final int j = values.length - 1;
		final int p = Partition.partitionSubList( i, j, values );

		assertTrue ( p >= i && p <= j );

		for ( int k = i; k < p; ++k )
			assertTrue( values[ k ] < values[ p ] );

		for ( int k = p + 1; k < j; ++k )
			assertTrue( values[ k ] >= values[ p ] );
		
		Arrays.sort( values );
		assertArrayEquals( sortedValues, values );
	}

	@Test
	public void testPartitionShort()
	{
		short[] values = new short[] {2, 2138, 29, 123, 23, 23134, -123, 23134 };
		short[] sortedValues = values.clone();
		Arrays.sort( sortedValues );

		final int i = 0;
		final int j = values.length - 1;
		final int p = Partition.partitionSubList( i, j, values );

		assertTrue ( p >= i && p <= j );

		for ( int k = i; k < p; ++k )
			assertTrue( values[ k ] < values[ p ] );

		for ( int k = p + 1; k < j; ++k )
			assertTrue( values[ k ] >= values[ p ] );
		
		Arrays.sort( values );
		assertArrayEquals( sortedValues, values );
	}

	@Test
	public void testPartitionInt()
	{
		int[] values = new int[] {1, 3, 9, 100, 10, 10, 10, 9, 25, 4, 10};
		int[] sortedValues = values.clone();
		Arrays.sort( sortedValues );

		final int i = 0;
		final int j = values.length - 1;
		final int p = Partition.partitionSubList( i, j, values );

		assertTrue ( p >= i && p <= j );

		for ( int k = i; k < p; ++k )
			assertTrue( values[ k ] < values[ p ] );

		for ( int k = p + 1; k < j; ++k )
			assertTrue( values[ k ] >= values[ p ] );
		
		Arrays.sort( values );
		assertArrayEquals( sortedValues, values );
	}

	@Test
	public void testPartitionLong()
	{
		long[] values = new long[] {112312738};
		long[] sortedValues = values.clone();
		Arrays.sort( sortedValues );

		final int i = 0;
		final int j = values.length - 1;
		final int p = Partition.partitionSubList( i, j, values );

		assertTrue ( p >= i && p <= j );

		for ( int k = i; k < p; ++k )
			assertTrue( values[ k ] < values[ p ] );

		for ( int k = p + 1; k < j; ++k )
			assertTrue( values[ k ] >= values[ p ] );
		
		Arrays.sort( values );
		assertArrayEquals( sortedValues, values );
	}

	@Test
	public void testPartitionFloat()
	{
		float[] values = new float[] {123, 21, 12912, 321, 32, 12345, 249, 5823, 834, 10, 23, 329};
		float[] sortedValues = values.clone();
		Arrays.sort( sortedValues );

		final int i = 0;
		final int j = values.length - 1;
		final int p = Partition.partitionSubList( i, j, values );

		assertTrue ( p >= i && p <= j );

		for ( int k = i; k < p; ++k )
			assertTrue( values[ k ] < values[ p ] );

		for ( int k = p + 1; k < j; ++k )
			assertTrue( values[ k ] >= values[ p ] );
		
		Arrays.sort( values );
		assertArrayEquals( sortedValues, values, 0 );
	}

	@Test
	public void testPartitionDouble()
	{
		double[] values = new double[] {123, 21, 12912, 321, 32, 12345, 249, 5823, 834, 10, 23, 329};
		double[] sortedValues = values.clone();
		Arrays.sort( sortedValues );

		final int i = 0;
		final int j = values.length - 1;
		final int p = Partition.partitionSubList( i, j, values );

		assertTrue ( p >= i && p <= j );

		for ( int k = i; k < p; ++k )
			assertTrue( values[ k ] < values[ p ] );

		for ( int k = p + 1; k < j; ++k )
			assertTrue( values[ k ] >= values[ p ] );
		
		Arrays.sort( values );
		assertArrayEquals( sortedValues, values, 0 );
	}

	@Test
	public void testPartitionChar()
	{
		char[] values = new char[] {'b', 'a', 'x', 'c', 'c'};
		char[] sortedValues = values.clone();
		Arrays.sort( sortedValues );

		final int i = 0;
		final int j = values.length - 1;
		final int p = Partition.partitionSubList( i, j, values );

		assertTrue ( p >= i && p <= j );

		for ( int k = i; k < p; ++k )
			assertTrue( values[ k ] < values[ p ] );

		for ( int k = p + 1; k < j; ++k )
			assertTrue( values[ k ] >= values[ p ] );
		
		Arrays.sort( values );
		assertArrayEquals( sortedValues, values );
	}

	@Test
	public void testPartitionFloatObject()
	{
		ArrayList< Float > values = new ArrayList< Float >();
		for( float f : new float[] {123, 21, 12912, 321, 32, 12345, 249, 5823, 834, 10, 23, 329 } ) {
			values.add( f );
		}
		@SuppressWarnings( "unchecked" )
		ArrayList< Float > sortedValues = ( ArrayList< Float > ) values.clone();
		Collections.sort( sortedValues );

		final int i = 0;
		final int j = values.size() - 1;
		final int p = Partition.partitionSubList( i, j, values );

		assertTrue ( p >= i && p <= j );

		for ( int k = i; k < p; ++k )
			assertTrue( values.get( k ).compareTo( values.get( p ) ) < 0 );

		for ( int k = p + 1; k < j; ++k )
			assertTrue( values.get( k ).compareTo( values.get( p ) ) >= 0 );
		
		Collections.sort( values );
		for ( int k = i; k <= j; ++k )
			assertTrue( values.get( k ).equals( sortedValues.get( k ) ) );
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
	public void testPartitionFloatObjectComparator()
	{
		ArrayList< Float > values = new ArrayList< Float >();
		for( float f : new float[] {123, 21, 12912, 321, 32, 12345, 249, 5823, 834, 10, 23, 329 } ) {
			values.add( f );
		}
		@SuppressWarnings( "unchecked" )
		ArrayList< Float > sortedValues = ( ArrayList< Float > ) values.clone();
		Collections.sort( sortedValues );

		final int i = 0;
		final int j = values.size() - 1;
		final int p = Partition.partitionSubList( i, j, values, new ComparableComparator< Float >() );

		assertTrue ( p >= i && p <= j );

		for ( int k = i; k < p; ++k )
			assertTrue( values.get( k ).compareTo( values.get( p ) ) < 0 );

		for ( int k = p + 1; k < j; ++k )
			assertTrue( values.get( k ).compareTo( values.get( p ) ) >= 0 );
		
		Collections.sort( values );
		for ( int k = i; k <= j; ++k )
			assertTrue( values.get( k ).equals( sortedValues.get( k ) ) );
	}

	@Test
	public void testPartitionFloatObjectIterator()
	{
		ArrayList< Float > values = new ArrayList< Float >();
		for( float f : new float[] {123, 21, 12912, 321, 32, 12345, 249, 5823, 834, 10, 23, 329 } ) {
			values.add( f );
		}
		@SuppressWarnings( "unchecked" )
		ArrayList< Float > sortedValues = ( ArrayList< Float > ) values.clone();
		Collections.sort( sortedValues );

		ListIterator< Float > iIterator = values.listIterator();
		ListIterator< Float > jIterator = values.listIterator( values.size() );
		final int i = 0;
		final int j = values.size() - 1;
		Partition.partitionSubList( iIterator, jIterator );
		final int p = iIterator.nextIndex() - 1;

		assertTrue ( p >= i && p <= j );

		for ( int k = i; k < p; ++k )
			assertTrue( values.get( k ).compareTo( values.get( p ) ) < 0 );

		for ( int k = p + 1; k < j; ++k )
			assertTrue( values.get( k ).compareTo( values.get( p ) ) >= 0 );
		
		Collections.sort( values );
		for ( int k = i; k <= j; ++k )
			assertTrue( values.get( k ).equals( sortedValues.get( k ) ) );
	}

	@Test
	public void testPartitionFloatObjectIteratorComparator()
	{
		ArrayList< Float > values = new ArrayList< Float >();
		for( float f : new float[] {123, 21, 12912, 321, 32, 12345, 249, 5823, 834, 10, 23, 329 } ) {
			values.add( f );
		}
		@SuppressWarnings( "unchecked" )
		ArrayList< Float > sortedValues = ( ArrayList< Float > ) values.clone();
		Collections.sort( sortedValues );

		ListIterator< Float > iIterator = values.listIterator();
		ListIterator< Float > jIterator = values.listIterator( values.size() );
		final int i = 0;
		final int j = values.size() - 1;
		Partition.partitionSubList( iIterator, jIterator, new ComparableComparator< Float >() );
		final int p = iIterator.nextIndex() - 1;

		assertTrue ( p >= i && p <= j );

		for ( int k = i; k < p; ++k )
			assertTrue( values.get( k ).compareTo( values.get( p ) ) < 0 );

		for ( int k = p + 1; k < j; ++k )
			assertTrue( values.get( k ).compareTo( values.get( p ) ) >= 0 );
		
		Collections.sort( values );
		for ( int k = i; k <= j; ++k )
			assertTrue( values.get( k ).equals( sortedValues.get( k ) ) );
	}

	@Test
	public void testPartitionFloatObjectPermutation()
	{
		ArrayList< Float > values = new ArrayList< Float >();
		for( float f : new float[] {123, 21, 12912, 321, 32, 12345, 249, 5823, 834, 10, 23, 329, 1, 1, 1, 100 } ) {
			values.add( f );
		}
		@SuppressWarnings( "unchecked" )
		ArrayList< Float > origvalues = ( ArrayList< Float > ) values.clone();
		
		final int[] permutation = new int[ values.size() ];
		for( int k = 0; k < permutation.length; ++k )
			permutation[ k ] = k;

		final int i = 0;
		final int j = values.size() - 1;
		final int p = Partition.partitionSubList( i, j, values, permutation );

		assertTrue ( p >= i && p <= j );

		for ( int k = i; k < p; ++k )
			assertTrue( values.get( k ).compareTo( values.get( p ) ) < 0 );

		for ( int k = p + 1; k < j; ++k )
			assertTrue( values.get( k ).compareTo( values.get( p ) ) >= 0 );

		for( int k = 0; k < permutation.length; ++k )
			assertTrue( values.get( k ).equals( origvalues.get( permutation[ k ] ) ) );
	}

	@Test
	public void testPartitionFloatObjectComparatorPermutation()
	{
		ArrayList< Float > values = new ArrayList< Float >();
		for( float f : new float[] {123, 21, 12912, 321, 32, 12345, 249, 5823, 834, 10, 23, 329 } ) {
			values.add( f );
		}
		@SuppressWarnings( "unchecked" )
		ArrayList< Float > origvalues = ( ArrayList< Float > ) values.clone();
		
		final int[] permutation = new int[ values.size() ];
		for( int k = 0; k < permutation.length; ++k )
			permutation[ k ] = k;

		final int i = 0;
		final int j = values.size() - 1;
		final int p = Partition.partitionSubList( i, j, values, permutation, new ComparableComparator< Float >() );

		assertTrue ( p >= i && p <= j );

		for ( int k = i; k < p; ++k )
			assertTrue( values.get( k ).compareTo( values.get( p ) ) < 0 );

		for ( int k = p + 1; k < j; ++k )
			assertTrue( values.get( k ).compareTo( values.get( p ) ) >= 0 );
		
		for( int k = 0; k < permutation.length; ++k )
			assertTrue( values.get( k ).equals( origvalues.get( permutation[ k ] ) ) );
	}

	@Test
	public void testPartitionFloatObjectIteratorPermutation()
	{
		ArrayList< Float > values = new ArrayList< Float >();
		for( float f : new float[] {123, 21, 12912, 321, 32, 12345, 249, 5823, 834, 10, 23, 329 } ) {
			values.add( f );
		}
		@SuppressWarnings( "unchecked" )
		ArrayList< Float > origvalues = ( ArrayList< Float > ) values.clone();

		final int[] permutation = new int[ values.size() ];
		for( int k = 0; k < permutation.length; ++k )
			permutation[ k ] = k;

		ListIterator< Float > iIterator = values.listIterator();
		ListIterator< Float > jIterator = values.listIterator( values.size() );
		final int i = 0;
		final int j = values.size() - 1;
		Partition.partitionSubList( iIterator, jIterator, permutation );
		final int p = iIterator.nextIndex() - 1;

		assertTrue ( p >= i && p <= j );

		for ( int k = i; k < p; ++k )
			assertTrue( values.get( k ).compareTo( values.get( p ) ) < 0 );

		for ( int k = p + 1; k < j; ++k )
			assertTrue( values.get( k ).compareTo( values.get( p ) ) >= 0 );
		
		for( int k = 0; k < permutation.length; ++k )
			assertTrue( values.get( k ).equals( origvalues.get( permutation[ k ] ) ) );
	}

	@Test
	public void testPartitionFloatObjectIteratorComparatorPermutation()
	{
		ArrayList< Float > values = new ArrayList< Float >();
		for( float f : new float[] {123, 21, 12912, 321, 32, 12345, 249, 5823, 834, 10, 23, 329 } ) {
			values.add( f );
		}
		@SuppressWarnings( "unchecked" )
		ArrayList< Float > origvalues = ( ArrayList< Float > ) values.clone();

		final int[] permutation = new int[ values.size() ];
		for( int k = 0; k < permutation.length; ++k )
			permutation[ k ] = k;

		ListIterator< Float > iIterator = values.listIterator();
		ListIterator< Float > jIterator = values.listIterator( values.size() );
		final int i = 0;
		final int j = values.size() - 1;
		Partition.partitionSubList( iIterator, jIterator, permutation, new ComparableComparator< Float >() );
		final int p = iIterator.nextIndex() - 1;

		assertTrue ( p >= i && p <= j );

		for ( int k = i; k < p; ++k )
			assertTrue( values.get( k ).compareTo( values.get( p ) ) < 0 );

		for ( int k = p + 1; k < j; ++k )
			assertTrue( values.get( k ).compareTo( values.get( p ) ) >= 0 );
		
		for( int k = 0; k < permutation.length; ++k )
			assertTrue( values.get( k ).equals( origvalues.get( permutation[ k ] ) ) );
	}

}
