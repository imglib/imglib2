/*-
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2018 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

import java.util.Arrays;
import java.util.stream.LongStream;

import org.junit.Assert;
import org.junit.Test;

import net.imglib2.FinalInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.integer.LongType;

public class HyperSlicesViewTest {

	final static long[] sizes = new long[]{ 2, 3, 4, 5, 6 };

	final static private long[] hyperSlice2Array(
			final RandomAccess< HyperSlice< LongType > > hyperSlicesAccess,
			final int... axes )
	{
		final long[] max = new long[ axes.length ];
		for ( int d = 0; d < axes.length; ++d )
			max[ d ] = sizes[ axes[ d ] ] - 1;

		final RandomAccessibleInterval< LongType > hyperSlice =
				Views.interval(
						hyperSlicesAccess.get(),
						new FinalInterval( new long[ axes.length ], max ) );

		long n = 1;
		for ( final long s : max )
			n *= s + 1;

		final long[] list = new long[ ( int )n ];

		int i = 0;
		for ( final LongType t : Views.iterable( hyperSlice ) )
			list[ i++ ] = t.get();

		return list;
	}

	final static private String printHyperSlice(
			final RandomAccess< HyperSlice< LongType > > hyperSlicesAccess,
			final int... axes )
	{
		final long[] max = new long[ axes.length ];
		for ( int d = 0; d < axes.length; ++d )
			max[ d ] = sizes[ axes[ d ] ] - 1;

		final RandomAccessibleInterval< LongType > hyperSlice =
				Views.interval(
						hyperSlicesAccess.get(),
						new FinalInterval( new long[ axes.length ], max ) );

		long n = 1;
		for ( final long s : max )
			n *= s + 1;

		final long[] list = new long[ ( int )n ];

		int i = 0;
		for ( final LongType t : Views.iterable( hyperSlice ) )
			list[ i++ ] = t.get();

		return Arrays.toString( list );
	}

	@Test
	public void test()
	{
		final long n = LongStream.of( sizes ).reduce( 1, ( x, y ) -> x * y );

		final long[] data = new long[ ( int )n ];
		for ( int d = 0; d < n ; ++d )
			data[ d ] = d;

		final RandomAccessibleInterval< LongType > source = ArrayImgs.longs( data, sizes );
		RandomAccessibleInterval< LongType > slice = source;
		while ( slice.numDimensions() > 2 )
			slice = Views.hyperSlice( slice, slice.numDimensions() - 1, 0 );

		final HyperSlicesView< LongType > hyperSlices = new HyperSlicesView< LongType >( source, 2, 3 );
		final RandomAccess< HyperSlice< LongType > > hyperSlicesAccess = hyperSlices.randomAccess();

		final long[][] expecteds = new long[][]{
			{0, 6, 12, 18, 24, 30, 36, 42, 48, 54, 60, 66, 72, 78, 84, 90, 96, 102, 108, 114},
			{1, 7, 13, 19, 25, 31, 37, 43, 49, 55, 61, 67, 73, 79, 85, 91, 97, 103, 109, 115},
			{2, 8, 14, 20, 26, 32, 38, 44, 50, 56, 62, 68, 74, 80, 86, 92, 98, 104, 110, 116},
			{3, 9, 15, 21, 27, 33, 39, 45, 51, 57, 63, 69, 75, 81, 87, 93, 99, 105, 111, 117}
		};

		for ( int d = 0; hyperSlicesAccess.getLongPosition( 0 ) < sizes[ 2 ]; hyperSlicesAccess.fwd( 0 ), ++d )
		{
			Assert.assertArrayEquals( expecteds[ d ], hyperSlice2Array(hyperSlicesAccess, 2, 3 ) );
		}
	}

}
