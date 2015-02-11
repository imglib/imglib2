/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2015 Tobias Pietzsch, Stephan Preibisch, Barry DeZonia,
 * Stephan Saalfeld, Curtis Rueden, Albert Cardona, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Jonathan Hale, Lee Kamentsky, Larry Lindsey, Mark
 * Hiner, Michael Zinsmaier, Martin Horn, Grant Harris, Aivar Grislis, John
 * Bogovic, Steffen Jaensch, Stefan Helfrich, Jan Funke, Nick Perry, Mark Longair,
 * Melissa Linkert and Dimiter Prodanov.
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

import java.util.Random;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.util.IntervalIndexer;

import org.junit.Before;
import org.junit.Test;

/**
 * TODO
 * 
 */
public class CopyTest
{
	long[] dimensions;

	int numValues;

	int[] intData;

	long intDataSum;

	CellImg< IntType, ?, ? > intImg;

	@Before
	public void createSourceData()
	{
		dimensions = new long[] { 48, 17, 102 };

		numValues = 1;
		for ( int d = 0; d < dimensions.length; ++d )
			numValues *= dimensions[ d ];

		intData = new int[ numValues ];
		intDataSum = 0;
		final Random random = new Random( 0 );
		for ( int i = 0; i < numValues; ++i )
		{
			intData[ i ] = random.nextInt();
			intDataSum += intData[ i ];
		}

		intImg = new CellImgFactory< IntType >( 10 ).create( dimensions, new IntType() );

		final long[] pos = new long[ dimensions.length ];
		final RandomAccess< IntType > a = intImg.randomAccess();

		for ( int i = 0; i < numValues; ++i )
		{
			IntervalIndexer.indexToPosition( i, dimensions, pos );
			a.setPosition( pos );
			a.get().set( intData[ i ] );
		}
	}

	void copyWithSourceIteration( final Img< IntType > srcImg, final Img< IntType > dstImg )
	{
		final long[] pos = new long[ dimensions.length ];
		final Cursor< IntType > src = srcImg.localizingCursor();
		final RandomAccess< IntType > dst = dstImg.randomAccess();
		while ( src.hasNext() )
		{
			src.fwd();
			src.localize( pos );
			dst.setPosition( pos );
			dst.get().set( src.get() );
		}
	}

	void copyWithDestIteration( final Img< IntType > srcImg, final Img< IntType > dstImg )
	{
		final long[] pos = new long[ dstImg.numDimensions() ];
		final Cursor< IntType > dst = dstImg.localizingCursor();
		final RandomAccess< IntType > src = srcImg.randomAccess();
		while ( dst.hasNext() )
		{
			dst.fwd();
			dst.localize( pos );
			src.setPosition( pos );
			dst.get().set( src.get() );
		}
	}

	public void copyWithIterationBoth( final Img< IntType > srcImg, final Img< IntType > dstImg )
	{
		final Cursor< IntType > src = srcImg.cursor();
		final Cursor< IntType > dst = dstImg.cursor();
		while ( src.hasNext() )
		{
			dst.next().set( src.next().get() );
		}
	}

	int[] getImgAsInts( final Img< IntType > img )
	{
		final RandomAccess< IntType > a = img.randomAccess();
		final int N = ( int ) img.size();
		final int[] data = new int[ N ];
		final long[] dim = new long[ img.numDimensions() ];
		final long[] pos = new long[ img.numDimensions() ];
		img.dimensions( dim );
		for ( int i = 0; i < N; ++i )
		{
			IntervalIndexer.indexToPosition( i, dim, pos );
			a.setPosition( pos );
			data[ i ] = a.get().get();
		}
		return data;
	}

	@Test
	public void testCopyToArrayContainerWithSourceIteration()
	{
		final ArrayImg< IntType, ? > array = new ArrayImgFactory< IntType >().create( dimensions, new IntType() );
		copyWithSourceIteration( intImg, array );
		assertArrayEquals( intData, getImgAsInts( array ) );
	}

	@Test
	public void testCopyToArrayContainerWithDestIteration()
	{
		final ArrayImg< IntType, ? > array = new ArrayImgFactory< IntType >().create( dimensions, new IntType() );
		copyWithDestIteration( intImg, array );
		assertArrayEquals( intData, getImgAsInts( array ) );
	}

	@Test
	public void testCopyToCellContainerWithSourceIteration()
	{
		final CellImg< IntType, ?, ? > cellImg = new CellImgFactory< IntType >( new int[] { 2, 7, 4 } ).create( dimensions, new IntType() );
		copyWithSourceIteration( intImg, cellImg );
		assertArrayEquals( intData, getImgAsInts( cellImg ) );
	}

	@Test
	public void testCopyToCellContainerWithDestIteration()
	{
		final CellImg< IntType, ?, ? > cellImg = new CellImgFactory< IntType >( new int[] { 2, 7, 4 } ).create( dimensions, new IntType() );
		copyWithDestIteration( intImg, cellImg );
		assertArrayEquals( intData, getImgAsInts( cellImg ) );
	}

	@Test
	public void testCopyArrayToArrayWithIterationBoth()
	{
		final ArrayImg< IntType, ? > array2 = new ArrayImgFactory< IntType >().create( dimensions, new IntType() );
		final ArrayImg< IntType, ? > array = new ArrayImgFactory< IntType >().create( dimensions, new IntType() );
		copyWithDestIteration( intImg, array2 );
		copyWithIterationBoth( array2, array );
		assertArrayEquals( intData, getImgAsInts( array ) );
	}
}
