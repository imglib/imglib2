package net.imglib2.stream;

import java.util.Comparator;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import net.imglib2.Cursor;
import net.imglib2.LocalizableSampler;
import net.imglib2.RandomAccess;
import net.imglib2.Sampler;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.img.planar.PlanarImgs;
import net.imglib2.type.numeric.integer.IntType;
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
@Warmup( iterations = 5, time = 100, timeUnit = TimeUnit.MILLISECONDS )
@Measurement( iterations = 15, time = 100, timeUnit = TimeUnit.MILLISECONDS )
@BenchmarkMode( Mode.AverageTime )
@OutputTimeUnit( TimeUnit.MILLISECONDS )
@Fork( 1 )
public class WrapperBenchmark
{
	private Img< IntType > img;

	@Param( value = { "ArrayImg", "PlanarImg", "CellImg" } )
	private String imgType;

	@Setup
	public void setup()
	{
		long[] dimensions = { 100, 100, 100 };

		if ( imgType.equals( "ArrayImg" ) )
			img = ArrayImgs.ints( dimensions );
		else if ( imgType.equals( "PlanarImg" ) )
			img = PlanarImgs.ints( dimensions );
		else if ( imgType.equals( "CellImg" ) )
			img = new CellImgFactory<>( new IntType(), 64 ).create( dimensions );
		else
			throw new IllegalArgumentException();

		Random random = new Random();
		img.forEach( t -> t.set( random.nextInt( 1000 ) ) );
		img.getAt( 21, 24, 12 ).set( 1000 );
	}

	@Benchmark
	public int benchmarkStreamWithCopy()
	{
		final Optional< LocalizableSampler< IntType > > max =
				Streams.localizable( img )
						.parallel()
						.filter( c -> c.getIntPosition( 0 ) % 2 == 1 )
						.map( LocalizableSampler::copy )
						.max( Comparator.comparingInt( s -> s.get().get() ) );
		return max.get().get().get();
	}

	@Benchmark
	public int benchmarkStreamWithCustomMax()
	{
		final Optional< LocalizableSampler< IntType > > max =
				Streams.localizable_( img )
						.parallel()
						.filter( c -> c.getIntPosition( 0 ) % 2 == 1 )
						.max( Comparator.comparingInt( s -> s.get().get() ) );
		return max.get().get().get();
	}

	@Benchmark
	public int benchmarkCursor()
	{
		final Comparator< Sampler< IntType > > comparator = Comparator.comparingInt( s -> s.get().get() );
		final RandomAccess< IntType > max = img.randomAccess();
		final Cursor< IntType > c = img.cursor();
//		img.min( max ); // === max.setPosition( img.minAsLongArray() );
		while ( c.hasNext() )
		{
			c.fwd();
			if ( c.getIntPosition( 0 ) % 2 == 1 )
				if ( comparator.compare( c, max ) > 0 )
					max.setPosition( c );
		}
		return max.get().get();
	}

	@Benchmark
	public int benchmarkLocalizingCursor()
	{
		final Comparator< Sampler< IntType > > comparator = Comparator.comparingInt( s -> s.get().get() );
		final RandomAccess< IntType > max = img.randomAccess();
		final Cursor< IntType > c = img.localizingCursor();
//		img.min( max ); // === max.setPosition( img.minAsLongArray() );
		while ( c.hasNext() )
		{
			c.fwd();
			if ( c.getIntPosition( 0 ) % 2 == 1 )
				if ( comparator.compare( c, max ) > 0 )
					max.setPosition( c );
		}
		return max.get().get();
	}

	public static void main( String... args ) throws RunnerException
	{
		Options options = new OptionsBuilder().include( WrapperBenchmark.class.getSimpleName() ).build();
		new Runner( options ).run();
	}
}
