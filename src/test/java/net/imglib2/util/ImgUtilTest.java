/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2016 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package net.imglib2.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.LongType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;

import org.junit.Test;

/**
 * TODO
 * 
 */
public class ImgUtilTest
{

	@Test
	public void testCopyDoubleArrayIntIntArrayImgOfT()
	{
		final double[] input = { 0, 1, 2, 3, 4, 5, 6, 7, 8 };
		final int[] offsets = { 0, 0, 8 };
		final int[][] strides = { { 1, 3 }, { 3, 1 }, { -1, -3 } };
		final double[][][] expected = {
				{
				{ 0, 1, 2 },
				{ 3, 4, 5 },
				{ 6, 7, 8 }
		}, {
		{ 0, 3, 6 },
		{ 1, 4, 7 },
		{ 2, 5, 8 }
		}, {
		{ 8, 7, 6 },
		{ 5, 4, 3 },
		{ 2, 1, 0 }
		} };
		for ( int i = 0; i < offsets.length; i++ )
		{
			final Img< DoubleType > img = new ArrayImgFactory< DoubleType >().create( new long[] { 3, 3 }, new DoubleType() );
			ImgUtil.copy( input, offsets[ i ], strides[ i ], img );
			final RandomAccess< DoubleType > ra = img.randomAccess();
			final long[] location = new long[ 2 ];
			for ( int x = 0; x < 3; x++ )
			{
				location[ 0 ] = x;
				for ( int y = 0; y < 3; y++ )
				{
					location[ 1 ] = y;
					ra.setPosition( location );
					assertEquals( expected[ i ][ y ][ x ], ra.get().get(), 0 );
				}
			}
		}
	}

	@Test
	public void testCopyFloatArrayIntIntArrayImgOfT()
	{
		final float[] input = { 0, 1, 2, 3, 4, 5, 6, 7, 8 };
		final int[] offsets = { 0, 0, 8 };
		final int[][] strides = { { 1, 3 }, { 3, 1 }, { -1, -3 } };
		final float[][][] expected = {
				{
				{ 0, 1, 2 },
				{ 3, 4, 5 },
				{ 6, 7, 8 }
		}, {
		{ 0, 3, 6 },
		{ 1, 4, 7 },
		{ 2, 5, 8 }
		}, {
		{ 8, 7, 6 },
		{ 5, 4, 3 },
		{ 2, 1, 0 }
		} };
		for ( int i = 0; i < offsets.length; i++ )
		{
			final Img< FloatType > img = new ArrayImgFactory< FloatType >().create( new long[] { 3, 3 }, new FloatType() );
			ImgUtil.copy( input, offsets[ i ], strides[ i ], img );
			final RandomAccess< FloatType > ra = img.randomAccess();
			final long[] location = new long[ 2 ];
			for ( int x = 0; x < 3; x++ )
			{
				location[ 0 ] = x;
				for ( int y = 0; y < 3; y++ )
				{
					location[ 1 ] = y;
					ra.setPosition( location );
					assertEquals( expected[ i ][ y ][ x ], ra.get().get(), 0 );
				}
			}
		}
	}

	@Test
	public void testCopyLongArrayIntIntArrayImgOfT()
	{
		final long[] input = { 0, 1, 2, 3, 4, 5, 6, 7, 8 };
		final int[] offsets = { 0, 0, 8 };
		final int[][] strides = { { 1, 3 }, { 3, 1 }, { -1, -3 } };
		final long[][][] expected = {
				{
				{ 0, 1, 2 },
				{ 3, 4, 5 },
				{ 6, 7, 8 }
		}, {
		{ 0, 3, 6 },
		{ 1, 4, 7 },
		{ 2, 5, 8 }
		}, {
		{ 8, 7, 6 },
		{ 5, 4, 3 },
		{ 2, 1, 0 }
		} };
		for ( int i = 0; i < offsets.length; i++ )
		{
			final Img< LongType > img = new ArrayImgFactory< LongType >().create( new long[] { 3, 3 }, new LongType() );
			ImgUtil.copy( input, offsets[ i ], strides[ i ], img );
			final RandomAccess< LongType > ra = img.randomAccess();
			final long[] location = new long[ 2 ];
			for ( int x = 0; x < 3; x++ )
			{
				location[ 0 ] = x;
				for ( int y = 0; y < 3; y++ )
				{
					location[ 1 ] = y;
					ra.setPosition( location );
					assertEquals( expected[ i ][ y ][ x ], ra.get().get(), 0 );
				}
			}
		}
	}

	@Test
	public void testCopyIntArrayIntIntArrayImgOfT()
	{
		final int[] input = { 0, 1, 2, 3, 4, 5, 6, 7, 8 };
		final int[] offsets = { 0, 0, 8 };
		final int[][] strides = { { 1, 3 }, { 3, 1 }, { -1, -3 } };
		final int[][][] expected = {
				{
				{ 0, 1, 2 },
				{ 3, 4, 5 },
				{ 6, 7, 8 }
		}, {
		{ 0, 3, 6 },
		{ 1, 4, 7 },
		{ 2, 5, 8 }
		}, {
		{ 8, 7, 6 },
		{ 5, 4, 3 },
		{ 2, 1, 0 }
		} };
		for ( int i = 0; i < offsets.length; i++ )
		{
			final Img< IntType > img = new ArrayImgFactory< IntType >().create( new long[] { 3, 3 }, new IntType() );
			ImgUtil.copy( input, offsets[ i ], strides[ i ], img );
			final RandomAccess< IntType > ra = img.randomAccess();
			final long[] location = new long[ 2 ];
			for ( int x = 0; x < 3; x++ )
			{
				location[ 0 ] = x;
				for ( int y = 0; y < 3; y++ )
				{
					location[ 1 ] = y;
					ra.setPosition( location );
					assertEquals( expected[ i ][ y ][ x ], ra.get().get(), 0 );
				}
			}
		}
	}

	@Test
	public void testCopyImgOfTDoubleArrayIntIntArray()
	{
		final double[][] input = { { 0, 1, 2 }, { 3, 4, 5 }, { 6, 7, 8 } };
		final int[] offsets = { 0, 0, 8 };
		final int[][] strides = { { 1, 3 }, { 3, 1 }, { -1, -3 } };
		final double[][] expected = {
				{ 0, 1, 2, 3, 4, 5, 6, 7, 8 },
				{ 0, 3, 6, 1, 4, 7, 2, 5, 8 },
				{ 8, 7, 6, 5, 4, 3, 2, 1, 0 } };
		final double[] output = new double[ 9 ];
		final Img< DoubleType > img = new ArrayImgFactory< DoubleType >().create( new long[] { 3, 3 }, new DoubleType() );
		final RandomAccess< DoubleType > ra = img.randomAccess();
		final long[] location = new long[ 2 ];
		for ( int x = 0; x < 3; x++ )
		{
			location[ 0 ] = x;
			for ( int y = 0; y < 3; y++ )
			{
				location[ 1 ] = y;
				ra.setPosition( location );
				ra.get().set( input[ y ][ x ] );
			}
		}
		for ( int i = 0; i < offsets.length; i++ )
		{
			ImgUtil.copy( img, output, offsets[ i ], strides[ i ] );
			assertArrayEquals( expected[ i ], output, 0 );
		}
	}

	@Test
	public void testCopyImgOfTFloatArrayIntIntArray()
	{
		final float[][] input = { { 0, 1, 2 }, { 3, 4, 5 }, { 6, 7, 8 } };
		final int[] offsets = { 0, 0, 8 };
		final int[][] strides = { { 1, 3 }, { 3, 1 }, { -1, -3 } };
		final float[][] expected = {
				{ 0, 1, 2, 3, 4, 5, 6, 7, 8 },
				{ 0, 3, 6, 1, 4, 7, 2, 5, 8 },
				{ 8, 7, 6, 5, 4, 3, 2, 1, 0 } };
		final float[] output = new float[ 9 ];
		final Img< FloatType > img = new ArrayImgFactory< FloatType >().create( new long[] { 3, 3 }, new FloatType() );
		final RandomAccess< FloatType > ra = img.randomAccess();
		final long[] location = new long[ 2 ];
		for ( int x = 0; x < 3; x++ )
		{
			location[ 0 ] = x;
			for ( int y = 0; y < 3; y++ )
			{
				location[ 1 ] = y;
				ra.setPosition( location );
				ra.get().set( input[ y ][ x ] );
			}
		}
		for ( int i = 0; i < offsets.length; i++ )
		{
			ImgUtil.copy( img, output, offsets[ i ], strides[ i ] );
			assertArrayEquals( expected[ i ], output, 0 );
		}
	}

	@Test
	public void testCopyImgOfTLongArrayIntIntArray()
	{
		final long[][] input = { { 0, 1, 2 }, { 3, 4, 5 }, { 6, 7, 8 } };
		final int[] offsets = { 0, 0, 8 };
		final int[][] strides = { { 1, 3 }, { 3, 1 }, { -1, -3 } };
		final long[][] expected = {
				{ 0, 1, 2, 3, 4, 5, 6, 7, 8 },
				{ 0, 3, 6, 1, 4, 7, 2, 5, 8 },
				{ 8, 7, 6, 5, 4, 3, 2, 1, 0 } };
		final long[] output = new long[ 9 ];
		final Img< LongType > img = new ArrayImgFactory< LongType >().create( new long[] { 3, 3 }, new LongType() );
		final RandomAccess< LongType > ra = img.randomAccess();
		final long[] location = new long[ 2 ];
		for ( int x = 0; x < 3; x++ )
		{
			location[ 0 ] = x;
			for ( int y = 0; y < 3; y++ )
			{
				location[ 1 ] = y;
				ra.setPosition( location );
				ra.get().set( input[ y ][ x ] );
			}
		}
		for ( int i = 0; i < offsets.length; i++ )
		{
			ImgUtil.copy( img, output, offsets[ i ], strides[ i ] );
			assertArrayEquals( expected[ i ], output );
		}
	}

	@Test
	public void testCopyImgOfTIntArrayIntIntArray()
	{
		final int[][] input = { { 0, 1, 2 }, { 3, 4, 5 }, { 6, 7, 8 } };
		final int[] offsets = { 0, 0, 8 };
		final int[][] strides = { { 1, 3 }, { 3, 1 }, { -1, -3 } };
		final int[][] expected = {
				{ 0, 1, 2, 3, 4, 5, 6, 7, 8 },
				{ 0, 3, 6, 1, 4, 7, 2, 5, 8 },
				{ 8, 7, 6, 5, 4, 3, 2, 1, 0 } };
		final int[] output = new int[ 9 ];
		final Img< LongType > img = new ArrayImgFactory< LongType >().create( new long[] { 3, 3 }, new LongType() );
		final RandomAccess< LongType > ra = img.randomAccess();
		final long[] location = new long[ 2 ];
		for ( int x = 0; x < 3; x++ )
		{
			location[ 0 ] = x;
			for ( int y = 0; y < 3; y++ )
			{
				location[ 1 ] = y;
				ra.setPosition( location );
				ra.get().set( input[ y ][ x ] );
			}
		}
		for ( int i = 0; i < offsets.length; i++ )
		{
			ImgUtil.copy( img, output, offsets[ i ], strides[ i ] );
			assertArrayEquals( expected[ i ], output );
		}
	}

}
