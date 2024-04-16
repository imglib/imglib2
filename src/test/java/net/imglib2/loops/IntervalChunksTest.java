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

package net.imglib2.loops;

import net.imglib2.Interval;
import net.imglib2.util.Intervals;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static net.imglib2.test.ImgLib2Assert.assertIntervalEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class IntervalChunksTest
{
	@Test
	public void testDivideAndRoundUp()
	{
		assertEquals( 0, IntervalChunks.divideAndRoundUp( 0, 5 ) );
		assertEquals( 1, IntervalChunks.divideAndRoundUp( 1, 5 ) );
		assertEquals( 1, IntervalChunks.divideAndRoundUp( 5, 5 ) );
		assertEquals( 2, IntervalChunks.divideAndRoundUp( 6, 5 ) );
	}

	@Test
	public void testSuggestCellSize()
	{
		assertArrayEquals( array ( 20 ), IntervalChunks.suggestChunkSize( array ( 40 ), 2 ) );
		assertArrayEquals( array ( 5 ), IntervalChunks.suggestChunkSize( array ( 100 ), 19 ) );
		assertArrayEquals( array ( 1 ), IntervalChunks.suggestChunkSize( array ( 10 ), 1000 ) );
		assertArrayEquals( array ( 10, 10 ), IntervalChunks.suggestChunkSize( array ( 10, 10 ), 1 ) );
		assertArrayEquals( array ( 10, 5 ), IntervalChunks.suggestChunkSize( array ( 10, 10 ), 2 ) );
		assertArrayEquals( array ( 5, 1 ), IntervalChunks.suggestChunkSize( array ( 10, 10 ), 20 ) );
		assertArrayEquals( array ( 5, 1 ), IntervalChunks.suggestChunkSize( array ( 10, 10 ), 17 ) );
	}

	@Test
	public void testGenerateGrid() {
		Interval total = Intervals.createMinSize( 2,3,10,10 );
		List<Interval> chunks = IntervalChunks.generateGrid( total, array(5, 3) );
		assertEquals(8, chunks.size());
		assertIntervalEquals( Intervals.createMinSize( 2, 3, 5, 3 ), chunks.get( 0 ) );
		assertIntervalEquals( Intervals.createMinSize( 7, 3, 5, 3 ), chunks.get( 1 ) );
		assertIntervalEquals( Intervals.createMinSize( 7, 12, 5, 1 ), chunks.get( 7 ) );
	}

	@Test
	public void testChunkInterval() {
		// Test to chunk one dimensional interval of length 21 into 2 chunks.
		Interval complete = Intervals.createMinSize( 5, 21 );
		List< Interval > chunks = IntervalChunks.chunkInterval( complete, 2 );
		assertIntervalEquals( Intervals.createMinSize( 5, 10 ), chunks.get( 0 ) );
		assertIntervalEquals( Intervals.createMinSize( 15, 10 ), chunks.get( 1 ) );
		assertIntervalEquals( Intervals.createMinSize( 25, 1 ), chunks.get( 2 ) );
	}

	private void assertArrayEquals( long[] expected, long[] actual ) {
		if( !Arrays.equals(expected, actual))
			fail( "Arrays are different:\n"
					+ "Expected :" + Arrays.toString( expected ) + "\n"
					+ "Actual   :" + Arrays.toString( actual ) );
	}

	private long[] array( long... v )
	{
		return v;
	}

}
