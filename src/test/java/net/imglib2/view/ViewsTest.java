/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2022 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package net.imglib2.view;

import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.Type;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.LongType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.util.Intervals;
import net.imglib2.util.Localizables;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

public class ViewsTest
{
	@Test
	public void testMoveAxisUp()
	{
		RandomAccessible< Localizable > input = Localizables.randomAccessible( 4 );
		RandomAccessible< Localizable > view = Views.moveAxis( input, 1, 3 );
		RandomAccess< Localizable > ra = view.randomAccess();
		ra.setPosition( new long[] { 1, 3, 4, 2 } );
		assertArrayEquals( new long[] { 1, 2, 3, 4 }, ra.get().positionAsLongArray() );
	}

	@Test
	public void testMoveAxisDown()
	{
		RandomAccessible< Localizable > input = Localizables.randomAccessible( 4 );
		RandomAccessible< Localizable > view = Views.moveAxis( input, 3, 1 );
		RandomAccess< Localizable > ra = view.randomAccess();
		ra.setPosition( new long[] { 1, 4, 2, 3 } );
		assertArrayEquals( new long[] { 1, 2, 3, 4 }, ra.get().positionAsLongArray() );
	}

	@Test
	public void testMoveAxisUpForInteval()
	{
		Img< ? > img = ArrayImgs.bytes( 1, 2, 3, 4 );
		RandomAccessibleInterval< ? > view = Views.moveAxis( img, 1, 3 );
		assertArrayEquals( new long[] { 1, 3, 4, 2 }, Intervals.dimensionsAsLongArray( view ) );
	}

	@Test
	public void testMoveAxisDownForInteval()
	{
		Img< ? > img = ArrayImgs.bytes( 1, 2, 3, 4 );
		RandomAccessibleInterval< ? > view = Views.moveAxis( img, 3, 1 );
		assertArrayEquals( new long[] { 1, 4, 2, 3 }, Intervals.dimensionsAsLongArray( view ) );
	}

	@Test
	public void testExtendValue()
	{
		final long[] dims = { 2, 3, 4 };
		final long[] border = { 1, 1, 1 };

		testExtendFloatingPoint( dims, 0.0, 1.0, 2.0f );
		testExtendInteger( dims, 0L, 1L, 2 );
		testExtendBoolean( dims, true, false );

		testExpandFloatingPoint( dims, 0.0, 1.0, 2.0f, border );
		testExpandInteger( dims, 0L, 1L, 2, border );
		testExpandBoolean( dims, false, true, border );

	}

	private static void testExtendFloatingPoint(
			final long[] dims,
			final double insideValue,
			final double doubleExtension,
			final float floatExtension )
	{
		final RandomAccessibleInterval< DoubleType > rai = ArrayImgs.doubles( dims );
		Views.iterable( rai ).forEach( px -> px.setReal( insideValue ) );
		testValueExtended(
				Views.extendValue( rai, doubleExtension ),
				rai,
				Intervals.expand( rai, 1 ),
				new DoubleType( insideValue ),
				new DoubleType( doubleExtension ) );
		testValueExtended(
				Views.extendValue( rai, floatExtension ),
				rai,
				Intervals.expand( rai, 1 ),
				new DoubleType( insideValue ),
				new DoubleType( floatExtension ) );
	}

	private static void testExpandFloatingPoint(
			final long[] dims,
			final double insideValue,
			final double doubleExtension,
			final float floatExtension,
			final long... border )
	{
		final RandomAccessibleInterval< DoubleType > rai = ArrayImgs.doubles( dims );
		Views.iterable( rai ).forEach( px -> px.setReal( insideValue ) );
		testValueExtended(
				Views.expandValue( rai, doubleExtension, border ),
				rai,
				Intervals.expand( rai, border ),
				new DoubleType( insideValue ),
				new DoubleType( doubleExtension ) );
		testValueExtended(
				Views.expandValue( rai, floatExtension, border ),
				rai,
				Intervals.expand( rai, border ),
				new DoubleType( insideValue ),
				new DoubleType( floatExtension ) );
	}

	private static void testExtendInteger(
			final long[] dims,
			final long insideValue,
			final long longExtension,
			final int intExtension )
	{
		final RandomAccessibleInterval< LongType > rai = ArrayImgs.longs( dims );
		Views.iterable( rai ).forEach( px -> px.setInteger( insideValue ) );
		testValueExtended(
				Views.extendValue( rai, longExtension ),
				rai,
				Intervals.expand( rai, 1 ),
				new LongType( insideValue ),
				new LongType( longExtension ) );
		testValueExtended(
				Views.extendValue( rai, intExtension ),
				rai,
				Intervals.expand( rai, 1 ),
				new LongType( insideValue ),
				new LongType( intExtension ) );
	}

	private static void testExpandInteger(
			final long[] dims,
			final long insideValue,
			final long longExtension,
			final int intExtension,
			final long... border )
	{
		final RandomAccessibleInterval< LongType > rai = ArrayImgs.longs( dims );
		Views.iterable( rai ).forEach( px -> px.setInteger( insideValue ) );
		testValueExtended(
				Views.expandValue( rai, longExtension, border ),
				rai,
				Intervals.expand( rai, border ),
				new LongType( insideValue ),
				new LongType( longExtension ) );
		testValueExtended(
				Views.expandValue( rai, intExtension, border ),
				rai,
				Intervals.expand( rai, border ),
				new LongType( insideValue ),
				new LongType( intExtension ) );
	}

	private static void testExtendBoolean(
			final long[] dims,
			final boolean insideValue,
			final boolean extension )
	{
		final RandomAccessibleInterval< BitType > rai = ArrayImgs.bits( dims );
		Views.iterable( rai ).forEach( px -> px.set( insideValue ) );
		testValueExtended(
				Views.extendValue( rai, extension ),
				rai,
				Intervals.expand( rai, 1 ),
				new BitType( insideValue ),
				new BitType( extension ) );
	}

	private static void testExpandBoolean(
			final long[] dims,
			final boolean insideValue,
			final boolean extension,
			final long... border )
	{
		final RandomAccessibleInterval< BitType > rai = ArrayImgs.bits( dims );
		Views.iterable( rai ).forEach( px -> px.set( insideValue ) );
		testValueExtended(
				Views.expandValue( rai, extension, border ),
				rai,
				Intervals.expand( rai, border ),
				new BitType( insideValue ),
				new BitType( extension ) );
	}

	private static < T extends Type< T > > void testValueExtended(
			final RandomAccessible< T > accessible,
			final Interval inside,
			final Interval total,
			final T insideValue,
			final T outsideValue )
	{
		final Cursor< T > cursor = Views.interval( accessible, total ).cursor();
		while ( cursor.hasNext() )
		{
			final T value = cursor.next();
			assertTrue( value.valueEquals( Intervals.contains( inside, cursor ) ? insideValue : outsideValue ) );
		}
	}
}
