/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2022 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

import java.util.ArrayList;
import java.util.Collections;

/**
 * Tools for setting up basic benchmarks.
 *
 * Call {@link #benchmark(int, Runnable)} with the number of iterations and a
 * {@link Runnable} to benchmark to obtain a list of run-times in milliseconds.
 *
 * @author Tobias Pietzsch
 */
public class BenchmarkHelper
{
	/**
	 * Run a benchmark numRuns times and record the milliseconds taken for each
	 * run.
	 *
	 * @param numRuns
	 *            how many times to run the benchmark.
	 * @param benchmark
	 *            the benchmark.
	 * @return run-times for each run (in milliseconds).
	 */
	public static ArrayList< Long > benchmark( final int numRuns, final Runnable benchmark )
	{
		final ArrayList< Long > times = new ArrayList< Long >( numRuns );
		for ( int i = 0; i < numRuns; ++i )
		{
			final long startTime = System.currentTimeMillis();
			benchmark.run();
			final long endTime = System.currentTimeMillis();
			times.add( endTime - startTime );
		}
		return times;
	}

	/**
	 * Run a benchmark numRuns times and print the results to {@link System#out}
	 * .
	 *
	 * @param numRuns
	 *            how many times to run the benchmark.
	 * @param printIndividualTimes
	 *            whether to print the time for every individual run or just the
	 *            median.
	 * @param b
	 *            the benchmark.
	 */
	public static void benchmarkAndPrint( final int numRuns, final boolean printIndividualTimes, final Runnable b )
	{
		final ArrayList< Long > times = new ArrayList< Long >( 100 );
		for ( int i = 0; i < numRuns; ++i )
		{
			final long startTime = System.currentTimeMillis();
			b.run();
			final long endTime = System.currentTimeMillis();
			times.add( endTime - startTime );
		}
		if ( printIndividualTimes )
		{
			for ( int i = 0; i < numRuns; ++i )
				System.out.println( "run " + i + ": " + times.get( i ) + " ms" );
			System.out.println();
		}
		Collections.sort( times );
		System.out.println( "median: " + median( times ) + " ms" );
		System.out.println( "best: " + times.get( 0 ) + " ms" );
		System.out.println();
	}

	/**
	 * Compute median of a sorted list of {@link Long}s.
	 *
	 * @param values
	 *            sorted list of values.
	 * @return median of the values.
	 */
	private static long median( final ArrayList< Long > values )
	{
		if ( values.size() % 2 == 1 )
			return values.get( ( values.size() + 1 ) / 2 - 1 );
		final long lower = values.get( values.size() / 2 - 1 );
		final long upper = values.get( values.size() / 2 );

		return ( lower + upper ) / 2;
	}
}
