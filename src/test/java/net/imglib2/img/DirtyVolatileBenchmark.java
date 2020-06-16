package net.imglib2.img;

import java.util.concurrent.TimeUnit;
import net.imglib2.img.basictypeaccess.array.DirtyByteArray;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

@State( Scope.Thread )
@Fork( 1 )
public class DirtyVolatileBenchmark
{
	public DirtyByteArray bytes;

	@Setup
	public void allocate()
	{
		bytes = new DirtyByteArray( 268435456 );
	}

	@Benchmark
	@BenchmarkMode( Mode.AverageTime )
	@OutputTimeUnit( TimeUnit.MILLISECONDS )
	public void touchAll()
	{
		final int len = bytes.getArrayLength();
		for ( int i = 0; i < len; i++ )
			bytes.setValue( i, ( byte ) 2 );
	}

	public static void main( final String... args ) throws RunnerException
	{
		final Options opt = new OptionsBuilder()
				.include( DirtyVolatileBenchmark.class.getSimpleName() )
				.warmupIterations( 4 )
				.measurementIterations( 8 )
				.warmupTime( TimeValue.milliseconds( 200 ) )
				.measurementTime( TimeValue.milliseconds( 200 ) )
				.build();
		new Runner( opt ).run();
	}
}
