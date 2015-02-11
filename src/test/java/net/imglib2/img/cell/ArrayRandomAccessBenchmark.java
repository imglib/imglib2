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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.util.IntervalIndexer;

/**
 * TODO
 * 
 */
public class ArrayRandomAccessBenchmark
{
	long[] dimensions;

	int numValues;

	int[] intData;

	long intDataSum;

	ArrayImg< IntType, ? > intImg;

	ArrayImg< IntType, ? > intImgCopy;

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

		intImg = new ArrayImgFactory< IntType >().create( dimensions, new IntType() );
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
		final int[] pos = new int[ dimensions.length ];
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

	public void copyWithIterationBoth( final Img< IntType > srcImg, final Img< IntType > dstImg )
	{
		final Cursor< IntType > src = srcImg.cursor();
		final Cursor< IntType > dst = dstImg.cursor();
		while ( src.hasNext() )
		{
			dst.next().set( src.next().get() );
		}
	}

	public static Long median( final ArrayList< Long > values )
	{
		Collections.sort( values );

		if ( values.size() % 2 == 1 )
			return values.get( ( values.size() + 1 ) / 2 - 1 );
		final long lower = values.get( values.size() / 2 - 1 );
		final long upper = values.get( values.size() / 2 );

		return ( lower + upper ) / 2;
	}

	public interface Benchmark
	{
		public void run();
	}

	public static void benchmark( final Benchmark b )
	{
		final ArrayList< Long > times = new ArrayList< Long >( 100 );
		final int numRuns = 20;
		for ( int i = 0; i < numRuns; ++i )
		{
			final long startTime = System.currentTimeMillis();
			b.run();
			final long endTime = System.currentTimeMillis();
			times.add( endTime - startTime );
		}
		for ( int i = 0; i < numRuns; ++i )
		{
			System.out.println( "run " + i + ": " + times.get( i ) + " ms" );
		}
		System.out.println();
		System.out.println( "median: " + median( times ) + " ms" );
		System.out.println();
	}

	public static void main( final String[] args )
	{
		final ArrayRandomAccessBenchmark randomAccessBenchmark = new ArrayRandomAccessBenchmark();
		randomAccessBenchmark.createSourceData();

		System.out.println( "benchmarking fill" );
		benchmark( new Benchmark()
		{
			@Override
			public void run()
			{
				randomAccessBenchmark.fillImage();
			}
		} );
		randomAccessBenchmark.intData = null;

		randomAccessBenchmark.intImgCopy = new ArrayImgFactory< IntType >().create( randomAccessBenchmark.dimensions, new IntType() );
		System.out.println( "benchmarking copy to array" );
		benchmark( new Benchmark()
		{
			@Override
			public void run()
			{
				randomAccessBenchmark.copyWithSourceIteration( randomAccessBenchmark.intImg, randomAccessBenchmark.intImgCopy );
			}
		} );

		System.out.println( "benchmarking copy from array" );
		benchmark( new Benchmark()
		{
			@Override
			public void run()
			{
				randomAccessBenchmark.copyWithSourceIteration( randomAccessBenchmark.intImgCopy, randomAccessBenchmark.intImg );
			}
		} );

		System.out.println( "benchmarking copy array to array using iteration" );
		benchmark( new Benchmark()
		{
			@Override
			public void run()
			{
				randomAccessBenchmark.copyWithIterationBoth( randomAccessBenchmark.intImg, randomAccessBenchmark.intImgCopy );
			}
		} );
		randomAccessBenchmark.intImgCopy = null;
	}
}
