package net.imglib2.stream;

import java.util.Arrays;
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
import net.imglib2.util.IntervalIndexer;
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
@Measurement( iterations = 5, time = 100, timeUnit = TimeUnit.MILLISECONDS )
@BenchmarkMode( Mode.AverageTime )
@OutputTimeUnit( TimeUnit.NANOSECONDS )
@Fork( 1 )
public class IndexerBenchmark
{
	final int[] dimensions;

	final int[] steps;
	final int[] steps2;

	final int[] position;

	final int index = 324634;

	public IndexerBenchmark()
	{
//		dimensions = new int[] { 123, 123, 23, 15, 123 };
		dimensions = new int[] { 123 * 123, 23 * 15 * 123 };
		steps = new int[ dimensions.length ];
		steps2 = new int[ dimensions.length ];
		position = new int[ dimensions.length ];
		IntervalIndexer.createAllocationSteps( dimensions, steps );
		createAllocationSteps2( dimensions, steps2 );
	}

	public static void createAllocationSteps2( final int[] dimensions, final int[] steps )
	{
		steps[ 0 ] = dimensions[ 0 ];
		for ( int d = 1; d < dimensions.length; ++d )
			steps[ d ] = steps[ d - 1 ] * dimensions[ d ];
	}


	@Benchmark
	public void benchmarkIterative()
	{
		indexToPosition( index, dimensions, position );
	}

	@Benchmark
	public void benchmarkWithSteps()
	{
		indexToPosition( index, dimensions, steps, position );
	}

	@Benchmark
	public void benchmarkWithSteps2()
	{
		indexToPosition2( index, dimensions, steps, steps2, position );
	}

	void test()
	{
		for ( int d = 0; d < position.length; ++d )
		{
			int p = indexToPosition( index, dimensions, steps, d );
			int p2 = indexToPosition2( index, dimensions, steps, steps2, d );
			System.out.println( "p = " + p + ", " + p2 );
		}
	}

	static public void indexToPosition( int index, final int[] dimensions, final int[] position )
	{
		final int maxDim = dimensions.length - 1;
		for ( int d = 0; d < maxDim; ++d )
		{
			final int j = index / dimensions[ d ];
			position[ d ] = index - j * dimensions[ d ];
			index = j;
		}
		position[ maxDim ] = index;
	}

	static public void indexToPosition( int index, final int[] dimensions, final int[] steps, final int[] position )
	{
		for ( int d = 0; d < position.length; ++d )
			position[ d ] = indexToPosition( index, dimensions, steps, d );
	}

	static public void indexToPosition2( int index, final int[] dimensions, final int[] steps, final int[] steps2, final int[] position )
	{
		for ( int d = 0; d < position.length; ++d )
			position[ d ] = indexToPosition2( index, dimensions, steps, steps2, d );
	}

	static public int indexToPosition( final int index, final int[] dimensions, final int[] steps, final int dimension )
	{
		return ( index / steps[ dimension ] ) % dimensions[ dimension ];
	}

	static public int indexToPosition2( final int index, final int[] dimensions, final int[] steps, final int[] steps2, final int dimension )
	{
		return ( index / steps[ dimension ] ) - ( index / steps2[ dimension ] ) * dimensions[ dimension ];
	}


	public static void main( String... args ) throws RunnerException
	{
		Options options = new OptionsBuilder().include( IndexerBenchmark.class.getSimpleName() ).build();
		new Runner( options ).run();
//		new IndexerBenchmark().test();
	}
}
