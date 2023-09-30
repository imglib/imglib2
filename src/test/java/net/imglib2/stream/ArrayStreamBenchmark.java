package net.imglib2.stream;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Spliterator;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.loops.LoopBuilder;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.util.Intervals;
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
public class ArrayStreamBenchmark
{
	private final int[] values;

	private final Img< IntType > img;

	public ArrayStreamBenchmark()
	{
		final long[] dimensions = { 2000, 2000 };
		values = new int[ ( int ) Intervals.numElements( dimensions ) ];
		img = ArrayImgs.ints( values, dimensions );
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
		return img.parallelStream().unordered().filter( t -> t.get() > 127 ).count();
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
	public long benchmarkForLoopArray()
	{
		long count = 0;
		for ( int value : values )
		{
			if ( value > 127 )
				++count;
		}
		return count;
	}

	@Benchmark
	public long benchmarkStreamArray()
	{
		return IntStream.of( values ).filter( value -> value > 127 ).count();
	}

	@Benchmark
	public long benchmarkParallelStreamArray()
	{
		return IntStream.of( values ).parallel().filter( value -> value > 127 ).count();
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
		Options options = new OptionsBuilder()
				.include( ArrayStreamBenchmark.class.getSimpleName() )
				.build();
		new Runner( options ).run();
	}
}
