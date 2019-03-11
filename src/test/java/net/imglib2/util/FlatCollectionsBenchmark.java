package net.imglib2.util;

import net.imglib2.img.Img;
import net.imglib2.test.RandomImgs;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.IntType;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.Collection;

/**
 * Benchmark for {@link FlatCollections}.
 * <p>
 * The Benchmark demonstrates the performance reduction
 * when wrapping an ImgLib2 image as Java {@link Collection}.
 * The low performance is probably caused by the boxing of
 * primitive types in Java.
 */
@State( Scope.Benchmark )
public class FlatCollectionsBenchmark
{
	Img< IntType > image = RandomImgs.seed( 42 ).nextImage( new IntType(), 100, 100, 100);

	Collection< Integer > list = FlatCollections.integerCollection( image );

	@Benchmark
	public double benchmarkSumList() {
		double sum = 0;
		for( Integer pixel : list )
			sum += pixel;
		return sum;
	}

	@Benchmark
	public double benchmarkSumImg() {
		double sum = 0;
		for( RealType<?> pixel : image )
			sum += pixel.getRealDouble();
		return sum;
	}

	public static void main( final String... args ) throws RunnerException
	{
		final Options opt = new OptionsBuilder()
				.include( FlatCollectionsBenchmark.class.getSimpleName() )
				.forks( 0 )
				.warmupIterations( 4 )
				.measurementIterations( 8 )
				.warmupTime( TimeValue.milliseconds( 100 ) )
				.measurementTime( TimeValue.milliseconds( 100 ) )
				.build();
		new Runner( opt ).run();
	}
}
