package net.imglib2.blocks;

import java.util.concurrent.TimeUnit;
import net.imglib2.transform.integer.MixedTransform;
import net.imglib2.util.Intervals;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@State( Scope.Benchmark )
@Warmup( iterations = 5, time = 200, timeUnit = TimeUnit.MILLISECONDS )
@Measurement( iterations = 5, time = 200, timeUnit = TimeUnit.MILLISECONDS )
@BenchmarkMode( Mode.AverageTime )
@OutputTimeUnit( TimeUnit.MILLISECONDS )
@Fork( 1 )
public class PermuteInvertBenchmark
{
	@Param( value = { "0", "1", "2" } )
	private int scenario;

	private final int[][] destSizes = new int[][] {
			{ 64, 64 },
			{ 128, 128 },
			{ 256, 256 },
	};

	private int[] destSize;

	private byte[] src;

	private byte[] dest;

	private MixedTransform transform;

	private PermuteInvert permuteInvert;

	public PermuteInvertBenchmark()
	{
	}

	@Setup
	public void setup()
	{
		transform = new MixedTransform( 2, 2 );
		transform.setComponentMapping( new int[] { 1, 0 } );
		transform.setComponentInversion( new boolean[] { true, false } );

		destSize = destSizes[ scenario ];
		src = new byte[ ( int ) Intervals.numElements( destSize ) ];
		dest = new byte[ ( int ) Intervals.numElements( destSize ) ];

		final MemCopy memCopy = MemCopy.BYTE;
		permuteInvert = new PermuteInvert( memCopy, transform );
	}

	@Benchmark
	public void benchmark()
	{
		permuteInvert.permuteAndInvert( src, dest, destSize );
	}

	public static void main( String... args ) throws RunnerException
	{
		Options options = new OptionsBuilder().include( PermuteInvertBenchmark.class.getSimpleName() ).build();
		new Runner( options ).run();
	}
}
