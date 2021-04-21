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
package net.imglib2.img;

import java.util.ArrayList;
import java.util.Collections;

import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.planar.PlanarImg;
import net.imglib2.img.planar.PlanarImgs;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.util.BenchmarkHelper;
import net.imglib2.view.Views;

/*
 * Benchmarked 20.06.2013  Thinkpad i7-2620M CPU @ 2.7 GHz
 * 
 * 	Planar Image: the optimized cursor is slower when executed after Array cursors, but faster by  ~factor 100 if executed
 * 				  on its own. This may be due to JIT confusion.
 *  Array Image: the optimized cursor is faster by ~factor 100
 */
public class IterableSubIntervalBenchmark
{

	/**
	 * Walk through an Interval on an Img using a LocalizingCursor, localizing
	 * on every step.
	 * 
	 * @param img
	 * @param interval
	 */
	protected static void localizingWalkThrough( final Img< IntType > img, final Interval interval )
	{
		final Cursor< IntType > c = Views.interval( img, interval ).localizingCursor();

		final long[] pos = new long[ interval.numDimensions() ];

		int i = 0;
		while ( c.hasNext() )
		{
			c.fwd();
			i += c.get().get();
			c.localize( pos );
		}
		j = ( int ) pos[ 0 ] + i;
	}

	/**
	 * Walk through an Interval on an Img using a Cursor
	 * 
	 * @param img
	 * @param interval
	 */
	protected static void walkThrough( final Img< IntType > img, final Interval interval )
	{
		final Cursor< IntType > c = Views.interval( img, interval ).cursor();
		int i = 0;
		while ( c.hasNext() )
		{
			c.fwd();
			i += c.get().get();
		}
		j = i;
	}

	public static int j;

	public static void main( final String[] args )
	{
		final int numRuns = 20;
		final boolean printIndividualTimes = false;

		final long[] dimensions = new long[] { 5000, 5000, 2, 2 };

		// doesn't fit the interval, will force unoptimized cursor
		final long[] dimensionsUnoptimized = new long[] { 5001, 5000, 2, 2 };

		// fits the interval, should be optimized
		final Interval interval = new FinalInterval( new long[] { 0, 0, 1, 1 }, new long[] { 4999, 4999, 1, 1 } );

		// create and fill images
		final ArrayImg< IntType, ? > arrayImg = ArrayImgs.ints( dimensions ); // fits
																				// the
																				// interval
		// doesn't fit the interval
		final ArrayImg< IntType, ? > arrayImgUnOp = ArrayImgs.ints( dimensionsUnoptimized );

		// fits the interval
		final PlanarImg< IntType, ? > planarImg = PlanarImgs.ints( dimensions );

		// doesn't fit the interval
		final PlanarImg< IntType, ? > planarImgUnOp = PlanarImgs.ints( dimensionsUnoptimized );

		testArrayImg( numRuns, printIndividualTimes, interval, arrayImg, arrayImgUnOp );
		testPlanarImg( numRuns, printIndividualTimes, interval, planarImg, planarImgUnOp );
		testArrayImg( numRuns, printIndividualTimes, interval, arrayImg, arrayImgUnOp );
		testPlanarImg( numRuns, printIndividualTimes, interval, planarImg, planarImgUnOp );
		testArrayImg( numRuns, printIndividualTimes, interval, arrayImg, arrayImgUnOp );
		testPlanarImg( numRuns, printIndividualTimes, interval, planarImg, planarImgUnOp );
	}

	/*
	 * the 2nd img is unoptimized with respect to the provided interval i.e.
	 * while optimized cursors can be used for the first image given interval
	 * this is not possible for the 2nd one.
	 */
	protected static void testArrayImg( final int numRuns, final boolean printIndividualTimes, final Interval interval, final ArrayImg< IntType, ? > arrayImg, final ArrayImg< IntType, ? > arrayImgUnOp )
	{

		// BLOCK 1

		System.out.println( "normal cursor | array img" );
		System.out.println( "walk through a subinterval" );
		benchmarkAndCompare( numRuns, printIndividualTimes, new Runnable()
		{
			@Override
			public void run()
			{
				walkThrough( arrayImgUnOp, interval );
			}
		}, new Runnable()
		{
			@Override
			public void run()
			{
				walkThrough( arrayImg, interval );
			}
		} );

		// BLOCK 2

		System.out.println( "localizing cursor | array img" );
		System.out.println( "walk through a subinterval" );
		benchmarkAndCompare( numRuns, printIndividualTimes, new Runnable()
		{
			@Override
			public void run()
			{
				localizingWalkThrough( arrayImgUnOp, interval );
			}
		}, new Runnable()
		{
			@Override
			public void run()
			{
				localizingWalkThrough( arrayImg, interval );
			}
		} );
	}

	/*
	 * the 2nd img is unoptimized with respect to the provided interval i.e.
	 * while optimized cursors can be used for the first image given inter this
	 * is not possible for the 2nd one.
	 */
	protected static void testPlanarImg( final int numRuns, final boolean printIndividualTimes, final Interval interval, final PlanarImg< IntType, ? > planarImg, final PlanarImg< IntType, ? > planarImgUnOp )
	{

		// BLOCK 1

		System.out.println( "normal cursor | planar img" );
		System.out.println( "walk through a subinterval" );
		benchmarkAndCompare( numRuns, printIndividualTimes, new Runnable()
		{
			@Override
			public void run()
			{
				walkThrough( planarImgUnOp, interval );
			}
		}, new Runnable()
		{
			@Override
			public void run()
			{
				walkThrough( planarImg, interval );
			}
		} );

		// BLOCK 2

		System.out.println( "localizing cursor | planar img" );
		System.out.println( "walk through a subinterval" );
		benchmarkAndCompare( numRuns, printIndividualTimes, new Runnable()
		{
			@Override
			public void run()
			{
				localizingWalkThrough( planarImgUnOp, interval );
			}
		}, new Runnable()
		{
			@Override
			public void run()
			{
				localizingWalkThrough( planarImg, interval );
			}
		} );
	}

	// HELPER

	public static void benchmarkAndCompare( final int numRuns, final boolean individualRuns, final Runnable norm, final Runnable opt )
	{
		final ArrayList< Long > valuesNorm = BenchmarkHelper.benchmark( numRuns, norm );
		final ArrayList< Long > valuesOpt = BenchmarkHelper.benchmark( numRuns, opt );

		final long[] statsNorm = computeStats( valuesNorm );
		final long[] statsOpt = computeStats( valuesOpt );
		final long[] statsDiff = new long[] { ( statsNorm[ 0 ] - statsOpt[ 0 ] ), ( statsNorm[ 1 ] - statsOpt[ 1 ] ), ( statsNorm[ 2 ] - statsOpt[ 2 ] ) };
		// print
		System.out.println( "\t| Unoptimized \t| Optimized \t| Speedup Time \t| Speedup % \t|" );
		System.out.println( "Median\t|\t" + statsNorm[ 0 ] + "\t|\t" + statsOpt[ 0 ] + "\t| " + statsDiff[ 0 ] + "ms   \t| " + ( ( int ) ( 1000.0 / statsNorm[ 0 ] * statsDiff[ 0 ] ) / 10.0 ) + "%   \t|" );
		System.out.println( "Best\t|\t" + statsNorm[ 1 ] + "\t|\t" + statsOpt[ 1 ] + "\t| " + statsDiff[ 1 ] + "ms   \t| " + ( ( int ) ( 1000.0 / statsNorm[ 1 ] * statsDiff[ 1 ] ) / 10.0 ) + "%   \t|" );
		System.out.println( "Worst\t|\t" + statsNorm[ 2 ] + "\t|\t" + statsOpt[ 2 ] + "\t| " + statsDiff[ 2 ] + "ms   \t| " + ( ( int ) ( 1000.0 / statsNorm[ 2 ] * statsDiff[ 2 ] ) / 10.0 ) + "%   \t|" );
		System.out.println();
	}

	public static long[] computeStats( final ArrayList< Long > values )
	{
		Collections.sort( values );

		long median = 0;

		if ( values.size() % 2 == 1 )
		{
			median = values.get( ( values.size() + 1 ) / 2 - 1 );
		}
		else
		{
			final long lower = values.get( values.size() / 2 - 1 );
			final long upper = values.get( values.size() / 2 );

			median = ( lower + upper ) / 2;
		}

		return new long[] { median, values.get( 0 ), values.get( values.size() - 1 ) };
	}

}
