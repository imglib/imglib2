/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2020 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package net.imglib2.loops;

import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.util.IntervalIndexer;
import net.imglib2.util.Intervals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IntervalChunks
{
	/**
	 * Returns a list of disjoint intervals, that covers the given interval.
	 * <p>
	 * Due to the divisibility of whole numbers, it not always easy to
	 * return the correct number of intervals, in that case this method
	 * returns a larger number of intervals.
	 */
	public static List< Interval > chunkInterval( Interval interval, int numberOfChunks )
	{
		long[] chunkDimensions = suggestChunkSize( Intervals.dimensionsAsLongArray( interval ), numberOfChunks );
		return generateGrid( interval, chunkDimensions );
	}

	/**
	 * Suggest a chunk size that can be used, to divide an interval of the
	 * given dimensions, into the given number of chunks.
	 */
	static long[] suggestChunkSize( long[] dimensions, long numberOfChunks )
	{
		long[] chunkSize = new long[ dimensions.length ];
		for ( int i = dimensions.length - 1; i >= 0; i-- )
		{
			chunkSize[ i ] = Math.max( 1, dimensions[ i ] / numberOfChunks );
			final long divisions = divideAndRoundUp( dimensions[ i ], chunkSize[ i ] );
			numberOfChunks = divideAndRoundUp( numberOfChunks, divisions );
		}
		return chunkSize;
	}

	/**
	 * Returns a list of disjoint intervals, that covers the given interval.
	 * The dimension of returned intervals are equal to the given cellDimensions.
	 * With the exception of the intervals that are at the border of the given interval.
	 * These can be smaller.
	 */
	static List< Interval > generateGrid( Interval interval, long[] cellDimensions )
	{
		final int n = interval.numDimensions();
		long[] totalMin = Intervals.minAsLongArray( interval );
		long[] totalMax = Intervals.maxAsLongArray( interval );
		long[] dimensions = Intervals.dimensionsAsLongArray( interval );
		long[] cellNumbers = new long[ dimensions.length ];
		Arrays.setAll(cellNumbers, d -> divideAndRoundUp( dimensions[ d ], cellDimensions[ d ] ) );
		long elements = Intervals.numElements( cellNumbers );
		long[] cellIndicies = new long[ n ];
		long[] min = new long[ n ];
		long[] max = new long[ n ];
		List< Interval > result = new ArrayList<>();
		for ( long cell = 0; cell < elements; cell++ )
		{
			IntervalIndexer.indexToPosition( cell, cellNumbers, cellIndicies );
			for ( int d = 0; d < n; d++ )
			{
				min[ d ] = totalMin[ d ] + cellIndicies[ d ] * cellDimensions[ d ];
				max[ d ] = Math.min( totalMax[ d ], min[ d ] + cellDimensions[ d ] - 1 );
			}
			result.add( new FinalInterval( min, max ) );
		}
		return result;
	}

	/**
	 * Returns a divided by b, and round up.
	 */
	static long divideAndRoundUp( long a, long b )
	{
		if ( a < 0 || b < 0 )
			throw new UnsupportedOperationException();
		return ( a + b - 1 ) / b;
	}
}
