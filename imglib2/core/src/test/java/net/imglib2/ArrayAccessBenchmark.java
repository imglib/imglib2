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
import java.util.Random;

/**
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 *
 */
public class ArrayAccessBenchmark
{
	public static class TestLong implements Runnable
	{
		final byte[] data;

		public TestLong( final int size )
		{
			final Random random = new Random( 1273 );
			data = new byte[ size ];
			for ( int i = 0; i < size; ++i )
				data[ i ] = ( byte ) random.nextInt( 256 );
			sum = 0;
		}

		byte get( final long i )
		{
			return data[ ( int ) i ];
		}

		int sum;

		@Override
		public void run()
		{
			final long s = data.length;
			for ( long i = 0; i < s; ++i )
				sum += get( i );
		}
	}

	public static class TestInt implements Runnable
	{
		final byte[] data;

		public TestInt( final int size )
		{
			final Random random = new Random( 1273 );
			data = new byte[ size ];
			for ( int i = 0; i < size; ++i )
				data[ i ] = ( byte ) random.nextInt( 256 );
			sum = 0;
		}

		byte get( final int i )
		{
			return data[ i ];
		}

		int sum;

		@Override
		public void run()
		{
			final int s = data.length;
			for ( int i = 0; i < s; ++i )
				sum += get( i );
		}
	}

	public static void main( final String args[] )
	{
		final int numRuns = 10;
		final int dataSize = Integer.MAX_VALUE;

//		final Runnable test = new TestInt( dataSize );
		final Runnable test = new TestLong( dataSize );

		final ArrayList< Long > times = BenchmarkHelper.benchmark( numRuns, test );

		final boolean printIndividualTimes = true;
		if ( printIndividualTimes )
		{
			for ( int i = 0; i < numRuns; ++i )
				System.out.println( "run " + i + ": " + times.get( i ) + " ms" );
			System.out.println();
		}
		System.out.println( "median: " + BenchmarkHelper.median( times ) + " ms" );
		System.out.println();
	}
}
