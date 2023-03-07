/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2023 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.ListIterator;

import org.junit.Test;

/**
 * TODO
 * 
 */
public class KthElementTest
{
	@Test
	public void testMedianByte()
	{
		final byte[] values = new byte[] { 2, -1, 1, 100, 123, 12 };
		final byte[] sortedValues = values.clone();

		final int i = 1;
		final int j = 3;
		final int k = i + ( j - i ) / 2;
		KthElement.kthElement( i, j, k, values );
		Arrays.sort( sortedValues, i, j + 1 );

		// the elements at the k-th positions should be equal
		assertEquals( values[ k ], sortedValues[ k ] );

		// the elements before the k-th should be equal or smaller than the k-th
		for ( int p = i; p < k; ++p )
			assertTrue( values[ p ] <= values[ k ] );

		// the elements after the k-th should be equal or greater than the k-th
		for ( int p = k + 1; p <= j; ++p )
			assertTrue( values[ p ] >= values[ k ] );

		// all elements should be contained in list, i.e., after sorting lists
		// should be identical
		Arrays.sort( values, i, j + 1 );
		assertArrayEquals( sortedValues, values );
	}

	@Test
	public void testMedianByteFull()
	{
		final byte[] values = new byte[] { 2, -1, 1, 100, 123, 12 };
		final byte[] sortedValues = values.clone();

		final int i = 0;
		final int j = values.length - 1;
		final int k = i + ( j - i ) / 2;
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

		// all elements should be contained in list, i.e., after sorting lists
		// should be identical
		Arrays.sort( values );
		assertArrayEquals( sortedValues, values );
	}

	@Test
	public void testMinByteFull()
	{
		final byte[] values = new byte[] { 2, -1, 1, 100, 123, 12 };
		final byte[] sortedValues = values.clone();

		final int i = 0;
		final int j = values.length - 1;
		final int k = 0;
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

		// all elements should be contained in list, i.e., after sorting lists
		// should be identical
		Arrays.sort( values );
		assertArrayEquals( sortedValues, values );
	}

	@Test
	public void testMaxByteFull()
	{
		final byte[] values = new byte[] { 2, -1, 1, 100, 123, 12 };
		final byte[] sortedValues = values.clone();

		final int i = 0;
		final int j = values.length - 1;
		final int k = j;
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

		// all elements should be contained in list, i.e., after sorting lists
		// should be identical
		Arrays.sort( values );
		assertArrayEquals( sortedValues, values );
	}

	@Test
	public void testMedianShortFull()
	{
		final short[] values = new short[] { 2, -1, 1, 100, 123, 12, 19, 12183, 123, 12, 6453, 233 };
		final short[] sortedValues = values.clone();

		final int i = 0;
		final int j = values.length - 1;
		final int k = i + ( j - i ) / 2;
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

		// all elements should be contained in list, i.e., after sorting lists
		// should be identical
		Arrays.sort( values );
		assertArrayEquals( sortedValues, values );
	}

	@Test
	public void testMedianIntFull()
	{
		final int[] values = new int[] { 2, 233 };
		final int[] sortedValues = values.clone();

		final int i = 0;
		final int j = values.length - 1;
		final int k = i + ( j - i ) / 2;
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

		// all elements should be contained in list, i.e., after sorting lists
		// should be identical
		Arrays.sort( values );
		assertArrayEquals( sortedValues, values );
	}

	@Test
	public void testMedianLongFull()
	{
		final long[] values = new long[] { 2, -123890123, 12, 6453, 233, 1, 1, 1, 1, 1 };
		final long[] sortedValues = values.clone();

		final int i = 0;
		final int j = values.length - 1;
		final int k = i + ( j - i ) / 2;
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

		// all elements should be contained in list, i.e., after sorting lists
		// should be identical
		Arrays.sort( values );
		assertArrayEquals( sortedValues, values );
	}

	@Test
	public void testMedianFloatFull()
	{
		final float[] values = new float[] { 2, -123890123, 12, 6453, 233, 1, 1, 1, 1, 1 };
		final float[] sortedValues = values.clone();

		final int i = 0;
		final int j = values.length - 1;
		final int k = i + ( j - i ) / 2;
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

		// all elements should be contained in list, i.e., after sorting lists
		// should be identical
		Arrays.sort( values );
		assertArrayEquals( sortedValues, values, 0 );
	}

	@Test
	public void testMedianDoubleFull()
	{
		final double[] values = new double[] { 2, 453, 233, 1, 1, 1, 1, 1, 0.7 };
		final double[] sortedValues = values.clone();

		final int i = 0;
		final int j = values.length - 1;
		final int k = i + ( j - i ) / 2;
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

		// all elements should be contained in list, i.e., after sorting lists
		// should be identical
		Arrays.sort( values, i, j + 1 );
		assertArrayEquals( sortedValues, values, 0 );
	}

	@Test
	public void testMedianFloatObject()
	{
		final ArrayList< Float > values = new ArrayList< Float >();
		for ( final float f : new float[] { 123, 21, 12912, 321, 32, 12345, 249, 5823, 834, 10, 23, 329 } )
		{
			values.add( f );
		}
		@SuppressWarnings( "unchecked" )
		final ArrayList< Float > sortedValues = ( ArrayList< Float > ) values.clone();

		final int i = 4;
		final int j = 9;
		final int k = i + ( j - i ) / 2;
		KthElement.kthElement( i, j, k, values );
		Collections.sort( sortedValues.subList( i, j + 1 ) );

		// the elements at the k-th positions should be equal
		assertEquals( values.get( k ), sortedValues.get( k ), 0 );

		// the elements before the k-th should be equal or smaller than the k-th
		for ( int p = i; p < k; ++p )
			assertTrue( values.get( p ) <= values.get( k ) );

		// the elements after the k-th should be equal or greater than the k-th
		for ( int p = k + 1; p <= j; ++p )
			assertTrue( values.get( p ) >= values.get( k ) );

		// all elements should be contained in list, i.e., after sorting lists
		// should be identical
		Collections.sort( values.subList( i, j + 1 ) );
		for ( int p = i; p <= j; ++p )
			assertTrue( values.get( p ).equals( sortedValues.get( p ) ) );
	}

	@Test
	public void testMedianFloatObjectFull()
	{
		final ArrayList< Float > values = new ArrayList< Float >();
		for ( final float f : new float[] { 123, 21, 12912, 321, 32, 12345, 249, 5823, 834, 10, 23, 329 } )
		{
			values.add( f );
		}
		@SuppressWarnings( "unchecked" )
		final ArrayList< Float > sortedValues = ( ArrayList< Float > ) values.clone();

		final int i = 0;
		final int j = values.size() - 1;
		final int k = i + ( j - i ) / 2;
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

		// all elements should be contained in list, i.e., after sorting lists
		// should be identical
		Collections.sort( values );
		for ( int p = i; p <= j; ++p )
			assertTrue( values.get( p ).equals( sortedValues.get( p ) ) );
	}

	public static class ComparableComparator< T extends Comparable< T > > implements Comparator< T >
	{
		@Override
		public int compare( final T o1, final T o2 )
		{
			return o1.compareTo( o2 );
		}
	}

	@Test
	public void testMedianFloatObjectFullComparator()
	{
		final ArrayList< Float > values = new ArrayList< Float >();
		for ( final float f : new float[] { 123, 21, 12912, 321, 32, 12345, 249, 5823, 834, 10, 23, 329 } )
		{
			values.add( f );
		}
		@SuppressWarnings( "unchecked" )
		final ArrayList< Float > sortedValues = ( ArrayList< Float > ) values.clone();

		final int i = 0;
		final int j = values.size() - 1;
		final int k = i + ( j - i ) / 2;
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

		// all elements should be contained in list, i.e., after sorting lists
		// should be identical
		Collections.sort( values );
		for ( int p = i; p <= j; ++p )
			assertTrue( values.get( p ).equals( sortedValues.get( p ) ) );
	}

	@Test
	public void testMedianFloatObjectIterator()
	{
		final ArrayList< Float > values = new ArrayList< Float >();
		for ( final float f : new float[] { 123, 21, 12912, 321, 32, 12345, 249, 5823, 834, 10, 23, 329 } )
		{
			values.add( f );
		}
		@SuppressWarnings( "unchecked" )
		final ArrayList< Float > sortedValues = ( ArrayList< Float > ) values.clone();

		final int i = 4;
		final int j = 9;
		final ListIterator< Float > iIterator = values.listIterator( i );
		final ListIterator< Float > jIterator = values.listIterator( j + 1 );
		final int k = i + ( j - i ) / 2;
		KthElement.kthElement( iIterator, jIterator, k );
		Collections.sort( sortedValues.subList( i, j + 1 ) );

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

		// all elements should be contained in list, i.e., after sorting lists
		// should be identical
		Collections.sort( values.subList( i, j + 1 ) );
		for ( int p = i; p <= j; ++p )
			assertTrue( values.get( p ).equals( sortedValues.get( p ) ) );
	}

	@Test
	public void testMedianFloatObjectFullIteratorComparator()
	{
		final ArrayList< Float > values = new ArrayList< Float >();
		for ( final float f : new float[] { 123, 21, 12912, 321, 32, 12345, 249, 5823, 834, 10, 23, 329 } )
		{
			values.add( f );
		}
		@SuppressWarnings( "unchecked" )
		final ArrayList< Float > sortedValues = ( ArrayList< Float > ) values.clone();

		final int i = 0;
		final int j = values.size() - 1;
		final ListIterator< Float > iIterator = values.listIterator( i );
		final ListIterator< Float > jIterator = values.listIterator( j + 1 );
		final int k = i + ( j - i ) / 2;
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

		// all elements should be contained in list, i.e., after sorting lists
		// should be identical
		Collections.sort( values );
		for ( int p = i; p <= j; ++p )
			assertTrue( values.get( p ).equals( sortedValues.get( p ) ) );
	}

	@Test
	public void testMedianFloatObjectFullPermutation()
	{
		final ArrayList< Float > values = new ArrayList< Float >();
		for ( final float f : new float[] { 123, 21, 12912, 321, 32, 12345, 249, 5823, 834, 10, 23, 329 } )
		{
			values.add( f );
		}
		@SuppressWarnings( "unchecked" )
		final ArrayList< Float > origvalues = ( ArrayList< Float > ) values.clone();

		final int[] permutation = new int[ values.size() ];
		for ( int k = 0; k < permutation.length; ++k )
			permutation[ k ] = k;

		final int i = 0;
		final int j = values.size() - 1;
		final int k = i + ( j - i ) / 2;
		KthElement.kthElement( k, values, permutation );

		// the elements before the k-th should be equal or smaller than the k-th
		for ( int p = i; p < k; ++p )
			assertTrue( values.get( p ) <= values.get( k ) );

		// the elements after the k-th should be equal or greater than the k-th
		for ( int p = k + 1; p <= j; ++p )
			assertTrue( values.get( p ) >= values.get( k ) );

		for ( int p = 0; p < permutation.length; ++p )
			assertTrue( values.get( p ).equals( origvalues.get( permutation[ p ] ) ) );
	}

	@Test
	public void testMedianFloatObjectFullComparatorPermutation()
	{
		final ArrayList< Float > values = new ArrayList< Float >();
		for ( final float f : new float[] { 123, 21, 12912, 321, 32, 12345, 249, 5823, 834, 10, 23, 329 } )
		{
			values.add( f );
		}
		@SuppressWarnings( "unchecked" )
		final ArrayList< Float > origvalues = ( ArrayList< Float > ) values.clone();

		final int[] permutation = new int[ values.size() ];
		for ( int k = 0; k < permutation.length; ++k )
			permutation[ k ] = k;

		final int i = 0;
		final int j = values.size() - 1;
		final int k = i + ( j - i ) / 2;
		KthElement.kthElement( k, values, permutation, new ComparableComparator< Float >() );

		// the elements before the k-th should be equal or smaller than the k-th
		for ( int p = i; p < k; ++p )
			assertTrue( values.get( p ) <= values.get( k ) );

		// the elements after the k-th should be equal or greater than the k-th
		for ( int p = k + 1; p <= j; ++p )
			assertTrue( values.get( p ) >= values.get( k ) );

		for ( int p = 0; p < permutation.length; ++p )
			assertTrue( values.get( p ).equals( origvalues.get( permutation[ p ] ) ) );
	}

	@Test
	public void testMedianFloatObjectFullIteratorComparatorPermutation()
	{
		final ArrayList< Float > values = new ArrayList< Float >();
		for ( final float f : new float[] { 123, 21, 12912, 321, 32, 12345, 249, 5823, 834, 10, 23, 329 } )
		{
			values.add( f );
		}
		@SuppressWarnings( "unchecked" )
		final ArrayList< Float > origvalues = ( ArrayList< Float > ) values.clone();

		final int[] permutation = new int[ values.size() ];
		for ( int k = 0; k < permutation.length; ++k )
			permutation[ k ] = k;

		final int i = 0;
		final int j = values.size() - 1;
		final ListIterator< Float > iIterator = values.listIterator( i );
		final ListIterator< Float > jIterator = values.listIterator( j + 1 );
		final int k = i + ( j - i ) / 2;
		KthElement.kthElement( iIterator, jIterator, k, permutation, new ComparableComparator< Float >() );

		// the elements before the k-th should be equal or smaller than the k-th
		for ( int p = i; p < k; ++p )
			assertTrue( values.get( p ) <= values.get( k ) );

		// the elements after the k-th should be equal or greater than the k-th
		for ( int p = k + 1; p <= j; ++p )
			assertTrue( values.get( p ) >= values.get( k ) );

		for ( int p = 0; p < permutation.length; ++p )
			assertTrue( values.get( p ).equals( origvalues.get( permutation[ p ] ) ) );
	}

	@Test
	public void testMedianFloatObjectFullIteratorPermutation()
	{
		final ArrayList< Float > values = new ArrayList< Float >();
		for ( final float f : new float[] { 123, 21, 12912, 321, 32, 12345, 249, 5823, 834, 10, 23, 329 } )
		{
			values.add( f );
		}
		@SuppressWarnings( "unchecked" )
		final ArrayList< Float > origvalues = ( ArrayList< Float > ) values.clone();

		final int[] permutation = new int[ values.size() ];
		for ( int k = 0; k < permutation.length; ++k )
			permutation[ k ] = k;

		final int i = 0;
		final int j = values.size() - 1;
		final ListIterator< Float > iIterator = values.listIterator( i );
		final ListIterator< Float > jIterator = values.listIterator( j + 1 );
		final int k = i + ( j - i ) / 2;
		KthElement.kthElement( iIterator, jIterator, k, permutation );

		// the elements before the k-th should be equal or smaller than the k-th
		for ( int p = i; p < k; ++p )
			assertTrue( values.get( p ) <= values.get( k ) );

		// the elements after the k-th should be equal or greater than the k-th
		for ( int p = k + 1; p <= j; ++p )
			assertTrue( values.get( p ) >= values.get( k ) );

		for ( int p = 0; p < permutation.length; ++p )
			assertTrue( values.get( p ).equals( origvalues.get( permutation[ p ] ) ) );
	}
}
