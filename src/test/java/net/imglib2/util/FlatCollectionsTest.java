/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2019 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.list.ListImg;
import net.imglib2.type.logic.NativeBoolType;
import net.imglib2.type.numeric.integer.ByteType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.LongType;
import net.imglib2.type.numeric.integer.ShortType;
import net.imglib2.type.numeric.integer.UnsignedLongType;
import net.imglib2.type.numeric.real.DoubleType;

import org.junit.Test;

/**
 * Tests for {@link FlatCollections}.
 *
 * @author Curtis Rueden
 */
public final class FlatCollectionsTest
{

	private final Img< String > imgString = new ListImg<>( Arrays.asList( "A1", "A2", "A3", "B1", "B2", "B3", "C1", "C2", "C3", "D1", "D2", "D3" ), 3, 4 );

	private final Img< NativeBoolType > imgBoolean = ArrayImgs.booleans( new boolean[] { true, true, false, false, false, true, true, false }, 2, 4 );

	private final Img< ByteType > imgByte = ArrayImgs.bytes( new byte[] { 7, 3, 0, 56, -66, -1, 33, 127, 1, 99, 42, -128 }, 3, 4 );

	private final Img< DoubleType > imgDouble = ArrayImgs.doubles( new double[] { 0.1, 0.02, 0.003, 0.0004, 0.00005, 0.000006, 0.0000007, 0.00000008, 0.000000009, 0, -0.9, -0.08 }, 3, 4 );

	private final Img< IntType > imgInt = ArrayImgs.ints( new int[] { 1, 10, 100, 1_000, 10_000, 100_000, 1_000_000, 10_000_000, 100_000_000, 1_000_000_000, -1_000_000_000, -100_000_000 }, 3, 4 );

	private final Img< LongType > imgLong = ArrayImgs.longs( new long[] { 1, 10, 100, 1_000, 10_000, 100_000, 1_000_000, 10_000_000, 100_000_000, 1_000_000_000, 10_000_000_000L, 100_000_000_000L }, 3, 4 );

	private final Img< ShortType > imgShort = ArrayImgs.shorts( new short[] { 32767, 32766, 32765, 32764, 32763, 10000, 10001, 10002, 10003, -32768, -32767, -32766 }, 2, 3, 2 );

	private final Img< UnsignedLongType > imgBig = ArrayImgs.unsignedLongs( new long[] { 2 * Long.MAX_VALUE, 3 * Long.MAX_VALUE, 4 * Long.MAX_VALUE, Long.MIN_VALUE, Long.MIN_VALUE / 2, Long.MIN_VALUE / 3 }, 3, 2 );

	@Test
	public void testCollection()
	{
		assertImageEqualsCollection( imgString, FlatCollections.collection( imgString, s -> s ), Objects::equals );
	}

	@Test
	public void testBooleanCollection()
	{
		assertImageEqualsCollection( imgBoolean, FlatCollections.booleanCollection( imgBoolean ), ( e, a ) -> e.get() == a );
	}

	@Test
	public void testByteCollection()
	{
		assertImageEqualsCollection( imgByte, FlatCollections.byteCollection( imgByte ), ( e, a ) -> e.get() == a );
	}

	@Test
	public void testDoubleCollection()
	{
		assertImageEqualsCollection( imgDouble, FlatCollections.doubleCollection( imgDouble ), ( e, a ) -> e.get() == a );
		assertImageEqualsCollection( imgByte, FlatCollections.doubleCollection( imgByte ), ( e, a ) -> e.getRealDouble() == a );
		assertImageEqualsCollection( imgShort, FlatCollections.doubleCollection( imgShort ), ( e, a ) -> e.getRealDouble() == a );
	}

	@Test
	public void testFloatCollection()
	{
		assertImageEqualsCollection( imgByte, FlatCollections.floatCollection( imgByte ), ( e, a ) -> e.getRealFloat() == a );
		assertImageEqualsCollection( imgDouble, FlatCollections.floatCollection( imgDouble ), ( e, a ) -> e.getRealFloat() == a );
		assertImageEqualsCollection( imgShort, FlatCollections.floatCollection( imgShort ), ( e, a ) -> e.getRealFloat() == a );
	}

	@Test
	public void testIntegerCollection()
	{
		assertImageEqualsCollection( imgByte, FlatCollections.integerCollection( imgByte ), ( e, a ) -> e.getInteger() == a );
		assertImageEqualsCollection( imgInt, FlatCollections.integerCollection( imgInt ), ( e, a ) -> e.get() == a );
		assertImageEqualsCollection( imgLong, FlatCollections.integerCollection( imgLong ), ( e, a ) -> e.getInteger() == a ); // lossy
		assertImageEqualsCollection( imgShort, FlatCollections.integerCollection( imgShort ), ( e, a ) -> e.getInteger() == a );
		assertImageEqualsCollection( imgBig, FlatCollections.integerCollection( imgBig ), ( e, a ) -> e.getInteger() == a ); // lossy
	}

	@Test
	public void testLongCollection()
	{
		assertImageEqualsCollection( imgByte, FlatCollections.longCollection( imgByte ), ( e, a ) -> e.getIntegerLong() == a );
		assertImageEqualsCollection( imgLong, FlatCollections.longCollection( imgLong ), ( e, a ) -> e.get() == a );
		assertImageEqualsCollection( imgShort, FlatCollections.longCollection( imgShort ), ( e, a ) -> e.getIntegerLong() == a );
		assertImageEqualsCollection( imgBig, FlatCollections.longCollection( imgBig ), ( e, a ) -> e.getIntegerLong() == a ); // lossy
	}

	@Test
	public void testShortCollection()
	{
		assertImageEqualsCollection( imgShort, FlatCollections.shortCollection( imgShort ), ( e, a ) -> e.get() == a );
	}

	@Test
	public void testBigIntegerCollection()
	{
		assertImageEqualsCollection( imgBig, FlatCollections.bigIntegerCollection( imgBig ), ( e, a ) -> Objects.equals( e.getBigInteger(), a ) );
	}

	@Test
	public void testList()
	{
		assertImageEqualsCollection( imgString, FlatCollections.list( imgString, s -> s ), Objects::equals );
	}

	@Test
	public void testBooleanList()
	{
		assertImageEqualsCollection( imgBoolean, FlatCollections.booleanList( imgBoolean ), ( e, a ) -> e.get() == a );
	}

	@Test
	public void testByteList()
	{
		assertImageEqualsCollection( imgByte, FlatCollections.byteList( imgByte ), ( e, a ) -> e.get() == a );
	}

	@Test
	public void testDoubleList()
	{
		assertImageEqualsCollection( imgDouble, FlatCollections.doubleList( imgDouble ), ( e, a ) -> e.get() == a );
		assertImageEqualsCollection( imgByte, FlatCollections.doubleList( imgByte ), ( e, a ) -> e.getRealDouble() == a );
		assertImageEqualsCollection( imgShort, FlatCollections.doubleList( imgShort ), ( e, a ) -> e.getRealDouble() == a );
	}

	@Test
	public void testFloatList()
	{
		assertImageEqualsCollection( imgByte, FlatCollections.floatList( imgByte ), ( e, a ) -> e.getRealFloat() == a );
		assertImageEqualsCollection( imgDouble, FlatCollections.floatList( imgDouble ), ( e, a ) -> e.getRealFloat() == a );
		assertImageEqualsCollection( imgShort, FlatCollections.floatList( imgShort ), ( e, a ) -> e.getRealFloat() == a );
	}

	@Test
	public void testIntegerList()
	{
		assertImageEqualsCollection( imgByte, FlatCollections.integerList( imgByte ), ( e, a ) -> e.getInteger() == a );
		assertImageEqualsCollection( imgInt, FlatCollections.integerList( imgInt ), ( e, a ) -> e.get() == a );
		assertImageEqualsCollection( imgLong, FlatCollections.integerList( imgLong ), ( e, a ) -> e.getInteger() == a ); // lossy
		assertImageEqualsCollection( imgShort, FlatCollections.integerList( imgShort ), ( e, a ) -> e.getInteger() == a );
		assertImageEqualsCollection( imgBig, FlatCollections.integerList( imgBig ), ( e, a ) -> e.getInteger() == a ); // lossy
	}

	@Test
	public void testLongList()
	{
		assertImageEqualsCollection( imgByte, FlatCollections.longList( imgByte ), ( e, a ) -> e.getIntegerLong() == a );
		assertImageEqualsCollection( imgLong, FlatCollections.longList( imgLong ), ( e, a ) -> e.get() == a );
		assertImageEqualsCollection( imgShort, FlatCollections.longList( imgShort ), ( e, a ) -> e.getIntegerLong() == a );
		assertImageEqualsCollection( imgBig, FlatCollections.longList( imgBig ), ( e, a ) -> e.getIntegerLong() == a ); // lossy
	}

	@Test
	public void testShortList()
	{
		assertImageEqualsCollection( imgShort, FlatCollections.shortList( imgShort ), ( e, a ) -> e.get() == a );
	}

	@Test
	public void testBigIntegerList()
	{
		assertImageEqualsCollection( imgBig, FlatCollections.bigIntegerList( imgBig ), ( e, a ) -> Objects.equals( e.getBigInteger(), a ) );
	}

	private < T, E > void assertImageEqualsCollection( final Img< T > image, final Collection< E > collection, final BiPredicate< T, E > equality )
	{
		assertIterationsEqual( image, collection, equality );
		assertEquals( Intervals.numElements( image ), collection.size() );
		if ( collection instanceof List )
			assertRandomAccessEqual( image, ( List< E > ) collection, equality );
	}

	private < E, A > void assertIterationsEqual( final Iterable< E > expected, final Iterable< A > actual, final BiPredicate< E, A > equality )
	{
		final Iterator< E > e = expected.iterator();
		final Iterator< A > a = actual.iterator();
		while ( e.hasNext() )
		{
			assertTrue( "Fewer elements than expected", a.hasNext() );
			final E ev = e.next();
			final A av = a.next();
			assertTrue( "Unequal elements: expected=" + ev + ", actual=" + av, equality.test( ev, av ) );
		}
		assertFalse( "More elements than expected", a.hasNext() );
	}

	private < T, E > void assertRandomAccessEqual( final Img< T > image, final List< E > list, final BiPredicate< T, E > equality )
	{
		final List< Integer > range = IntStream.range( 0, list.size() ).boxed().collect( Collectors.toList() );
		Collections.shuffle( range, new Random( 0xdeadbeef ) );
		final RandomAccess< T > access = image.randomAccess();
		for ( int i = 0; i < list.size(); i++ )
		{
			final int index = range.get( i );
			IntervalIndexer.indexToPosition( index, image, access );
			final T iv = access.get();
			final E lv = list.get( index );
			assertTrue( "Unequal elements: index=" + index + ", image=" + iv + ", list=" + lv, equality.test( iv, lv ) );
		}
	}
}
