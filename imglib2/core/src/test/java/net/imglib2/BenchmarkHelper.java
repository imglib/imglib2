/**
 * Copyright (c) 2009--2012, ImgLib2 developers
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  Neither the name of the Fiji project nor
 * the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * @author Tobias Pietzsch
 */
package net.imglib2;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 *
 */
public class BenchmarkHelper
{
	public static Long median( final ArrayList<Long> values )
	{
		Collections.sort(values);

		if (values.size() % 2 == 1)
			return values.get((values.size() + 1) / 2 - 1);
		else {
			final long lower = values.get(values.size() / 2 - 1);
			final long upper = values.get(values.size() / 2);

			return (lower + upper) / 2;
		}
	}

	public interface Benchmark
	{
		public void run();
	}

	public static void benchmark( final Benchmark b )
	{
		benchmark( 20, true, b );
	}

	public static void benchmark( final int numRuns, final boolean printIndividualTimes, final Benchmark b )
	{
		final ArrayList<Long> times = new ArrayList<Long>( 100 );
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
		System.out.println( "median: " + median( times ) + " ms" );
		System.out.println();
	}
}
