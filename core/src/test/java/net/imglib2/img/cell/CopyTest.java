/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
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
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
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
		Random random = new Random( 0 );
		for ( int i = 0; i < numValues; ++i )
		{
			intData[ i ] = random.nextInt();
			intDataSum += intData[ i ];
		}
		
		intImg = new CellImgFactory< IntType >( 10 ).create( dimensions, new IntType() );

		long[] pos = new long[ dimensions.length ];
		RandomAccess< IntType > a = intImg.randomAccess();

		for ( int i = 0; i < numValues; ++i )
		{
			IntervalIndexer.indexToPosition( i, dimensions, pos );
			a.setPosition( pos );
			a.get().set( intData[ i ] );
		}
	}

	void copyWithSourceIteration(Img< IntType > srcImg, Img< IntType > dstImg)
	{
		long[] pos = new long[ dimensions.length ];
		Cursor< IntType > src = srcImg.localizingCursor();
		RandomAccess< IntType > dst = dstImg.randomAccess();
		while( src.hasNext() ) {
			src.fwd();
			src.localize( pos );
			dst.setPosition( pos );
			dst.get().set( src.get() );
		}
	}

	void copyWithDestIteration(Img< IntType > srcImg, Img< IntType > dstImg)
	{
		long[] pos = new long[ dstImg.numDimensions() ];
		Cursor< IntType > dst = dstImg.localizingCursor();
		RandomAccess< IntType > src = srcImg.randomAccess();
		while( dst.hasNext() ) {
			dst.fwd();
			dst.localize( pos );
			src.setPosition( pos );
			dst.get().set( src.get() );
		}
	}

	public void copyWithIterationBoth(Img< IntType > srcImg, Img< IntType > dstImg)
	{
		Cursor< IntType > src = srcImg.cursor();
		Cursor< IntType > dst = dstImg.cursor();
		while( src.hasNext() ) {
			dst.next().set( src.next().get() );
		}
	}

	int[] getImgAsInts( Img< IntType > img )
	{
		RandomAccess< IntType > a = img.randomAccess();
		final int N = ( int ) img.size();
		int[] data = new int[ N ];
		final long[] dim = new long[ img.numDimensions() ];
		final long[] pos = new long[ img.numDimensions() ];
		img.dimensions( dim );
		for ( int i = 0; i < N; ++i ) {
			IntervalIndexer.indexToPosition( i, dim, pos );
			a.setPosition( pos );
			data[ i ] = a.get().get(); 
		}
		return data;
	}

	@Test
	public void testCopyToArrayContainerWithSourceIteration()
	{
		ArrayImg< IntType, ? > array = new ArrayImgFactory< IntType >().create( dimensions, new IntType() );
		copyWithSourceIteration( intImg, array );
		assertArrayEquals( intData, getImgAsInts( array ) );
	}

	@Test
	public void testCopyToArrayContainerWithDestIteration()
	{
		ArrayImg< IntType, ? > array = new ArrayImgFactory< IntType >().create( dimensions, new IntType() );
		copyWithDestIteration( intImg, array );
		assertArrayEquals( intData, getImgAsInts( array ) );
	}

	@Test
	public void testCopyToCellContainerWithSourceIteration()
	{
		CellImg< IntType, ?, ? > cellImg = new CellImgFactory< IntType >( new int[] {2, 7, 4} ).create( dimensions, new IntType() );
		copyWithSourceIteration( intImg, cellImg );
		assertArrayEquals( intData, getImgAsInts( cellImg ) );
	}

	@Test
	public void testCopyToCellContainerWithDestIteration()
	{
		CellImg< IntType, ?, ? > cellImg = new CellImgFactory< IntType >( new int[] {2, 7, 4} ).create( dimensions, new IntType() );
		copyWithDestIteration( intImg, cellImg );
		assertArrayEquals( intData, getImgAsInts( cellImg ) );
	}

	@Test
	public void testCopyArrayToArrayWithIterationBoth()
	{
		ArrayImg< IntType, ? > array2 = new ArrayImgFactory< IntType >().create( dimensions, new IntType() );
		ArrayImg< IntType, ? > array = new ArrayImgFactory< IntType >().create( dimensions, new IntType() );
		copyWithDestIteration( intImg, array2 );
		copyWithIterationBoth( array2, array );
		assertArrayEquals( intData, getImgAsInts( array ) );
	}
}
