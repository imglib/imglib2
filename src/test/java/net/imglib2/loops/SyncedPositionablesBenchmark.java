package net.imglib2.loops;

import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.integer.IntType;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.List;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@State( Scope.Benchmark )
public class SyncedPositionablesBenchmark
{
	private long[] dims = { 100, 100, 100 };

	private List< Img< IntType > > array = sixTimes(() -> ArrayImgs.ints( dims ));

	private < T > List< T > sixTimes( Supplier< T > supplier )
	{
		return IntStream.range( 0, 6 ).mapToObj( ignore -> supplier.get() ).collect( Collectors.toList());
	}

	@Benchmark
	public void benchmark2() {
		LoopBuilder.setImages( array.get( 0 ), array.get( 1 ) )
				.forEachPixel( (a, r) -> r.setReal( a.getRealDouble() ) );
	}

	@Benchmark
	public void benchmark3() {
		LoopBuilder.setImages( array.get( 0 ), array.get( 1 ), array.get( 2 ) )
				.forEachPixel( (a, b, r) -> r.setReal( a.getRealDouble() + b.getRealDouble() ) );
	}

	@Benchmark
	public void benchmark4() {
		LoopBuilder.setImages( array.get( 0 ), array.get( 1 ), array.get( 2 ), array.get( 3 ) )
				.forEachPixel( (a, b, c, r) -> r.setReal( a.getRealDouble() + b.getRealDouble() + c.getRealDouble() ) );
	}

	@Benchmark
	public void benchmark5() {
		LoopBuilder.setImages( array.get( 0 ), array.get( 1 ), array.get( 2 ), array.get( 3 ), array.get( 4) )
				.forEachPixel( (a, b, c, d, r) -> r.setReal( a.getRealDouble() + b.getRealDouble() + c.getRealDouble() + d.getRealDouble() ) );
	}

	@Benchmark
	public void benchmark6() {
		LoopBuilder.setImages( array.get( 0 ), array.get( 1 ), array.get( 2 ), array.get( 3 ), array.get( 4 ), array.get( 5) )
				.forEachPixel( (a, b, c, d, e, r) -> r.setReal( a.getRealDouble() + b.getRealDouble() + c.getRealDouble() + d.getRealDouble() + e.getRealDouble() ) );
	}

	public static void main( final String... args ) throws RunnerException
	{
		final Options opt = new OptionsBuilder()
				.include( SyncedPositionables.class.getSimpleName() )
				.forks( 0 )
				.warmupIterations( 10 )
				.measurementIterations( 8 )
				.warmupTime( TimeValue.milliseconds( 100 ) )
				.measurementTime( TimeValue.milliseconds( 100 ) )
				.build();
		new Runner( opt ).run();
	}
}
