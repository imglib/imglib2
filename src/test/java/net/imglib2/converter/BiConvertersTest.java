/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2024 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package net.imglib2.converter;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Test;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.interpolation.randomaccess.NearestNeighborInterpolatorFactory;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.view.Views;

/**
 *
 *
 * @author Stephan Saalfeld
 */
public class BiConvertersTest
{
	final Random rnd = new Random();

	final static byte[] testValues = new byte[ 20 * 30 * 4 ];
	{
		rnd.nextBytes( testValues );
	}

	final static int[] dataA = new int[ 20 * 30 ];

	final static int[] dataB = new int[ 20 * 30 ];
	{
		for ( int i = 0; i < dataA.length; ++i )
		{
			dataA[ i ] = rnd.nextInt() / 2;
			dataB[ i ] = rnd.nextInt() / 2;
		}
	}

	@Test
	public void testBiConverters()
	{
		final ArrayImg< IntType, ? > sourceA = ArrayImgs.ints( dataA, 20, 30 );
		final ArrayImg< IntType, ? > sourceB = ArrayImgs.ints( dataB, 20, 30 );
		final RandomAccessibleInterval< IntType > sumRAI = Converters.convertRAI(
				sourceA,
				sourceB,
				( a, b, c ) -> c.set( a.get() + b.get() ),
				new IntType() );
		final IterableInterval< IntType > sumIterable = Converters.convert(
				( IterableInterval< IntType > ) sourceA,
				sourceB,
				( a, b, c ) -> c.set( a.get() + b.get() ),
				new IntType() );
		final RandomAccessibleInterval< IntType > sumRA = Views.interval(
				Converters.convert(
						Views.extendBorder( sourceA ),
						Views.extendBorder( sourceB ),
						( a, b, c ) -> c.set( a.get() + b.get() ),
						new IntType() ),
				sourceA );
		final RandomAccessibleInterval< IntType > sumRRA = Views.interval(
				Views.raster(
						Converters.convert(
							Views.interpolate( Views.extendBorder( sourceA ), new NearestNeighborInterpolatorFactory<>() ),
							Views.interpolate( Views.extendBorder( sourceB ), new NearestNeighborInterpolatorFactory<>() ),
							( a, b, c ) -> c.set( a.get() + b.get() ),
							new IntType() )),
				sourceA );

		final Cursor< IntType > sumCursorRAI = Views.iterable( sumRAI ).cursor();
		final Cursor< IntType > sumCursorIterable = sumIterable.cursor();
		final Cursor< IntType > sumCursorRA = Views.iterable( sumRA ).cursor();
		final Cursor< IntType > sumCursorRRA = Views.iterable( sumRRA ).cursor();
		int i = 0;
		while ( sumCursorRAI.hasNext() )
		{
			assertEquals( sumCursorRAI.next().get(), dataA[ i ] + dataB[ i ] );
			assertEquals( sumCursorIterable.next().get(), dataA[ i ] + dataB[ i ] );
			assertEquals( sumCursorRA.next().get(), dataA[ i ] + dataB[ i ] );
			assertEquals( sumCursorRRA.next().get(), dataA[ i ] + dataB[ i ] );
			++i;
		}
	}

	@Test
	public void testBiConvertersToArray()
	{
		final ArrayImg< IntType, ? > sourceA = ArrayImgs.ints( dataA, 20, 30 );
		final ArrayImg< IntType, ? > sourceB = ArrayImgs.ints( dataB, 20, 30 );
		final RandomAccessibleInterval< int[] > sumRAI = Converters.convertRAI2(
				sourceA,
				sourceB,
				( a, b, c ) -> {
					c[ 0 ] = a.get();
					c[ 1 ] = b.get();
				},
				() -> new int[ 2 ] );
		final IterableInterval< int[] > sumIterable = Converters.convert2(
				( IterableInterval< IntType > ) sourceA,
				sourceB,
				( a, b, c ) -> {
					c[ 0 ] = a.get();
					c[ 1 ] = b.get();
				},
				() -> new int[ 2 ] );
		final RandomAccessibleInterval< int[] > sumRA = Views.interval(
				Converters.convert2(
						Views.extendBorder( sourceA ),
						Views.extendBorder( sourceB ),
						( a, b, c ) -> {
							c[ 0 ] = a.get();
							c[ 1 ] = b.get();
						},
						() -> new int[ 2 ] ),
				sourceA );
		final RandomAccessibleInterval< int[] > sumRRA = Views.interval(
				Views.raster(
						Converters.convert2(
							Views.interpolate( Views.extendBorder( sourceA ), new NearestNeighborInterpolatorFactory<>() ),
							Views.interpolate( Views.extendBorder( sourceB ), new NearestNeighborInterpolatorFactory<>() ),
							( a, b, c ) -> {
								c[ 0 ] = a.get();
								c[ 1 ] = b.get();
							},
							() -> new int[ 2 ] ) ),
				sourceA );

		final Cursor< int[] > sumCursorRAI = Views.iterable( sumRAI ).cursor();
		final Cursor< int[] > sumCursorIterable = sumIterable.cursor();
		final Cursor< int[] > sumCursorRA = Views.iterable( sumRA ).cursor();
		final Cursor< int[] > sumCursorRRA = Views.iterable( sumRRA ).cursor();
		int i = 0;
		while ( sumCursorRAI.hasNext() )
		{
			int[] t = sumCursorRAI.next();
			assertEquals( t[0], dataA[ i ] );
			assertEquals( t[1], dataB[ i ] );

			t = sumCursorIterable.next();
			assertEquals( t[ 0 ], dataA[ i ] );
			assertEquals( t[ 1 ], dataB[ i ] );

			t = sumCursorRA.next();
			assertEquals( t[ 0 ], dataA[ i ] );
			assertEquals( t[ 1 ], dataB[ i ] );

			t = sumCursorRRA.next();
			assertEquals( t[ 0 ], dataA[ i ] );
			assertEquals( t[ 1 ], dataB[ i ] );
			++i;
		}
	}
}
