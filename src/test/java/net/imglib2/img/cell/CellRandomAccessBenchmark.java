/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2021 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

import java.util.Random;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.util.BenchmarkHelper;
import net.imglib2.util.IntervalIndexer;

/**
 * TODO
 *
 */
public class CellRandomAccessBenchmark
{
	long[] dimensions;

	int numValues;

	int[] intData;

	long intDataSum;

	CellImg< IntType, ? > intImg;

	CellImg< IntType, ? > intImgCopy;

	public void createSourceData()
	{
		dimensions = new long[] { 480, 480, 102 };

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

		intImg = new CellImgFactory<>( new IntType(), 40 ).create( dimensions );
	}

	/**
	 * Fill intImg (a CellContainer with 40x40x40 cells) with data using flat
	 * array iteration order.
	 */
	public void fillImage()
	{
		final int[] pos = new int[ dimensions.length ];
		final RandomAccess< IntType > a = intImg.randomAccess();

		final int[] idim = new int[ dimensions.length ];
		for ( int d = 0; d < dimensions.length; ++d )
			idim[ d ] = ( int ) dimensions[ d ];

		for ( int i = 0; i < numValues; ++i )
		{
			IntervalIndexer.indexToPosition( i, idim, pos );
			a.setPosition( pos );
			a.get().set( intData[ i ] );
		}
	}

	public void copyWithSourceIteration( final Img< IntType > srcImg, final Img< IntType > dstImg )
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

	public static void main( final String[] args )
	{
		final CellRandomAccessBenchmark randomAccessBenchmark = new CellRandomAccessBenchmark();
		randomAccessBenchmark.createSourceData();

		System.out.println( "benchmarking fill" );
		BenchmarkHelper.benchmarkAndPrint( 50, true, new Runnable()
		{
			@Override
			public void run()
			{
				randomAccessBenchmark.fillImage();
			}
		} );
		randomAccessBenchmark.intData = null;

		randomAccessBenchmark.intImgCopy = new CellImgFactory<>( new IntType(), 32 ).create( randomAccessBenchmark.dimensions );
		System.out.println( "benchmarking copy to smaller" );
		BenchmarkHelper.benchmarkAndPrint( 20, false, new Runnable()
		{
			@Override
			public void run()
			{
				randomAccessBenchmark.copyWithSourceIteration( randomAccessBenchmark.intImg, randomAccessBenchmark.intImgCopy );
			}
		} );
		randomAccessBenchmark.intImgCopy = null;

		randomAccessBenchmark.intImgCopy = new CellImgFactory<>( new IntType(), 50 ).create( randomAccessBenchmark.dimensions );
		System.out.println( "benchmarking copy to larger" );
		BenchmarkHelper.benchmarkAndPrint( 20, false, new Runnable()
		{
			@Override
			public void run()
			{
				randomAccessBenchmark.copyWithSourceIteration( randomAccessBenchmark.intImg, randomAccessBenchmark.intImgCopy );
			}
		} );
		randomAccessBenchmark.intImgCopy = null;

		randomAccessBenchmark.intImgCopy = new CellImgFactory<>( new IntType(), 32, 64, 16 ).create( randomAccessBenchmark.dimensions );
		System.out.println( "benchmarking copy to mixed" );
		BenchmarkHelper.benchmarkAndPrint( 20, false, new Runnable()
		{
			@Override
			public void run()
			{
				randomAccessBenchmark.copyWithSourceIteration( randomAccessBenchmark.intImg, randomAccessBenchmark.intImgCopy );
			}
		} );
		randomAccessBenchmark.intImgCopy = null;
	}
}
