/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2025 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package net.imglib2.img.cell;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.imglib2.img.basictypeaccess.array.FloatArray;

/**
 * TODO
 *
 */
public class CellTest
{
	int[][] dim = {
			{ 10, 12 },
			{ 200, 30, 2, 384 },
			{ 12, 3, 4, 1, 9 }
	};

	long[][] offset = {
			{ 0, 0 },
			{ 0, 912389123123l, 1231238214214367l, 2 },
			{ 321, 3, 1, 0, 0 }
	};

	int[] expectedLength = {
			120,
			4608000,
			1296
	};

	@Test
	public void testLocalIndexCalculation()
	{
		final long[] min = new long[] { 0, 9876543210l, 222 };
		final Cell< FloatArray > cell = new Cell<>( new int[] {20, 8, 10}, min, new FloatArray( 1 ) );
		final long[][] position = { {3, 4, 5}, {12, 0, 3}, {3, 2, 0} };
		final int[] expectedIndex = { 883, 492, 43 };
		for ( int i = 0; i < position.length; ++i )
		{
			for ( int d =0; d <min.length; ++d)
				position[ i ][ d ] += min[ d ];
			assertTrue( cell.globalPositionToIndex( position[ i ] ) == expectedIndex[ i ] );
		}
	}

	@Test
	public void testGlobalPositionCalculation()
	{
		final Cell< FloatArray > cell = new Cell<>( new int[] {20, 8, 10}, new long[] { 0, 9876543210l, 222 }, new FloatArray( 1 ) );
		final int[] index = { 883, 492, 43 };
		final long[][] expectedPosition = { { 3, 9876543214l, 227 }, { 12, 9876543210l, 225 }, { 3, 9876543212l, 222 } };
		for ( int i = 0; i < index.length; ++i )
		{
			final long[] position = new long[ 3 ];
			cell.indexToGlobalPosition( index[ i ], position );
			assertArrayEquals( expectedPosition[ i ], position );
			for ( int d = 0; d < position.length; ++d )
				assertTrue( cell.indexToGlobalPosition( index[ i ], d ) == expectedPosition[ i ][ d ] );
		}
	}
}
