/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
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
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.img.basictypeaccess.array.CharArray;
import net.imglib2.img.basictypeaccess.array.DoubleArray;
import net.imglib2.img.basictypeaccess.array.FloatArray;
import net.imglib2.img.basictypeaccess.array.IntArray;
import net.imglib2.img.basictypeaccess.array.ShortArray;
import net.imglib2.util.Fraction;

import org.junit.Test;

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

	public < A extends ArrayDataAccess< A > > void testConstruction( final A creator )
	{
		for ( int i = 0; i < dim.length; ++i ) {
			final AbstractCell< A > cell = new DefaultCell< A >( creator, dim[ i ], offset[ i ], new Fraction( 2, 1 ) );
			assertTrue( creator.getClass().isInstance( cell.getData() ) );
			assertTrue( cell.size() == expectedLength[ i ] );
		}
	}

	@Test
	public void testByteConstruction()
	{
		testConstruction( new ByteArray( 1 ) );
	}

	@Test
	public void testCharConstruction()
	{
		testConstruction( new CharArray( 1 ) );
	}

	@Test
	public void testShortConstruction()
	{
		testConstruction( new ShortArray( 1 ) );
	}

	@Test
	public void testIntConstruction()
	{
		testConstruction( new IntArray( 1 ) );
	}

	@Test
	public void testFloatConstruction()
	{
		testConstruction( new FloatArray( 1 ) );
	}

	@Test
	public void testDoubleConstruction()
	{
		testConstruction( new DoubleArray( 1 ) );
	}

	@Test
	public void testLocalIndexCalculation()
	{
		final AbstractCell< FloatArray > cell = new DefaultCell< FloatArray >( new FloatArray( 1 ), new int[] {20, 8, 10}, new long[] { 0, 9876543210l, 222 } , new Fraction( 2, 1 ) );
		final long[][] position = { {3, 4, 5}, {12, 0, 3}, {3, 2, 0} };
		final int[] expectedIndex = { 883, 492, 43 };
		for ( int i = 0; i < position.length; ++i )
		{
			assertTrue( cell.localPositionToIndex( position[ i ] ) == expectedIndex[ i ] );
		}
	}

	@Test
	public void testGlobalPositionCalculation()
	{
		final AbstractCell< FloatArray > cell = new DefaultCell< FloatArray >( new FloatArray( 1 ), new int[] {20, 8, 10}, new long[] { 0, 9876543210l, 222 } , new Fraction( 2, 1 ) );
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
