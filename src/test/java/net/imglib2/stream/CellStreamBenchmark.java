/*-
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
package net.imglib2.stream;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Spliterator;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import net.imglib2.img.Img;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.loops.LoopBuilder;
import net.imglib2.type.numeric.integer.IntType;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@State( Scope.Benchmark )
@Warmup( iterations = 5, time = 100, timeUnit = TimeUnit.MILLISECONDS )
@Measurement( iterations = 20, time = 300, timeUnit = TimeUnit.MILLISECONDS )
@BenchmarkMode( Mode.AverageTime )
@OutputTimeUnit( TimeUnit.MILLISECONDS )
@Fork( 1 )
public class CellStreamBenchmark
{
	private final Img< IntType > img;

	public CellStreamBenchmark()
	{
		final long[] dimensions = { 200, 200, 100 };
		img = new CellImgFactory<>( new IntType(), 64 ).create( dimensions );
		Random random = new Random( 1L );
		img.forEach( t -> t.set( random.nextInt( 256 ) ) );
	}

	@Benchmark
	public long benchmarkForLoop()
	{
		long count = 0;
		for ( IntType t : img )
		{
			if ( t.get() > 127 )
				++count;
		}
		return count;
	}

	@Benchmark
	public long benchmarkStream()
	{
//		return img.stream().mapToInt( IntType::get ).filter( value -> value > 127 ).count();
		return img.stream().filter( t -> t.get() > 127 ).count();
	}

	@Benchmark
	public long benchmarkParallelStream()
	{
//		return img.parallelStream().mapToInt( IntType::get ).filter( value -> value > 127 ).count();
		return img.parallelStream().filter( t -> t.get() > 127 ).count();
	}

	static class Count implements Consumer< IntType >
	{
		private long count;

		@Override
		public void accept( final IntType t )
		{
			if ( t.get() > 127 )
				++count;
		}

		public long get()
		{
			return count;
		}
	}

	@Benchmark
	public long benchmarkSpliterator()
	{
		final Count count = new Count();
		final Spliterator< IntType > spl = img.spliterator();
		spl.forEachRemaining( count );
		return count.get();
	}

	@Benchmark
	public long benchmarkIterator()
	{
		final Count count = new Count();
		final Iterator< IntType > it = img.iterator();
		while( it.hasNext() )
		{
			count.accept( it.next() );
		}
		return count.get();
	}

	@Benchmark
	public long benchmarkIterator2()
	{
		final Count count = new Count();
		final Iterator< IntType > it = img.iterator();
		countIt( it, count );
		return count.get();
	}

	static void countIt( Iterator< IntType > it, Count count )
	{
		while( it.hasNext() )
		{
			count.accept( it.next() );
		}
	}

	@Benchmark
	public long benchmarkLoopBuilder()
	{
		final List< Long > longs = LoopBuilder.setImages( img ).multiThreaded().forEachChunk( consumerChunk -> {
			final long[] count = { 0 };
			consumerChunk.forEachPixel( t -> {
				if ( t.get() > 127 )
					++count[ 0 ];
			} );
			return count[ 0 ];
		} );
		return longs.stream().mapToLong( Long::longValue ).sum();
	}

	public static void main( String... args ) throws RunnerException
	{
		Options options = new OptionsBuilder().include( CellStreamBenchmark.class.getSimpleName() ).build();
		new Runner( options ).run();
	}
}
