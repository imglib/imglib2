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

import org.junit.Test;

import net.imglib2.type.NativeType;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;

/**
 * TODO
 *
 */
public class CellContainerFactoryTest
{
	int[][] dim = {
			{ 10, 12 },
			{ 200, 30, 2, 2384 },
			{ 12, 3, 4, 1, 9 }
	};

	long[][] offset = {
			{ 0, 0 },
			{ 0, 912389123123l, 1231238214214367l, 2 },
			{ 321, 3, 1, 0, 0 }
	};

	int[] expectedLength = {
			120,
			28608000,
			1296
	};

	private < T extends NativeType< T > > void testDefaultCellSize( final T type )
	{
		final int defaultCellSize = 43;
		final int[] expectedCellDims = { 43, 43, 43, 43 };
		final CellImgFactory< T > factory = new CellImgFactory<>( type, defaultCellSize );
		final long[] dimension = { 100, 80, 4, 3 };
		final CellImg< T, ? > img = factory.create( dimension );
		final int[] cellDims = new int[ dimension.length ];
		img.getCellGrid().cellDimensions( cellDims );
		assertArrayEquals( expectedCellDims, cellDims );
	}

	@Test
	public void testBitDefaultCellSize()
	{
		testDefaultCellSize( new BitType() );
	}

	@Test
	public void testIntDefaultCellSize()
	{
		testDefaultCellSize( new IntType() );
	}

	@Test
	public void testFloatDefaultCellSize()
	{
		testDefaultCellSize( new FloatType() );
	}

	private < T extends NativeType< T > > void testDefaultCellDimensions( final T type )
	{
		final int[] defaultCellDims = { 6, 8, 5, 3 };
		final int[] expectedCellDims = defaultCellDims.clone();
		final CellImgFactory< T > factory = new CellImgFactory<>( type, defaultCellDims );
		final long[] dimension = { 100, 80, 4, 3 };
		final CellImg< T, ? > img = factory.create( dimension );
		final int[] cellDims = new int[ dimension.length ];
		img.getCellGrid().cellDimensions( cellDims );
		assertArrayEquals( expectedCellDims, cellDims );
	}

	@Test
	public void testBitDefaultCellDimensions()
	{
		testDefaultCellDimensions( new BitType() );
	}

	@Test
	public void testIntDefaultCellDimensions()
	{
		testDefaultCellDimensions( new IntType() );
	}

	@Test
	public void testFloatDefaultCellDimensions()
	{
		testDefaultCellDimensions( new FloatType() );
	}
}
