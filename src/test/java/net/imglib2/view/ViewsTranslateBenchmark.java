package net.imglib2.view;

import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.img.planar.PlanarImgFactory;
import net.imglib2.loops.LoopBuilder;
import net.imglib2.type.numeric.RealType;
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
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * Measure the performance reduction when Views.translate(...) is used on
 * different classes of {@link RandomAccessibleInterval}.
 */
@State( Scope.Benchmark )
@Warmup( iterations = 5, time = 100, timeUnit = TimeUnit.MILLISECONDS )
@Measurement( iterations = 5, time = 100, timeUnit = TimeUnit.MILLISECONDS )
@BenchmarkMode( Mode.AverageTime )
@OutputTimeUnit( TimeUnit.MILLISECONDS )
@Fork( 1 )
public class ViewsTranslateBenchmark
{

	private final RandomAccessibleInterval<IntType> arrayImg = createImg( new ArrayImgFactory<>( new IntType() ) );

	private final RandomAccessibleInterval<IntType> planarImg = createImg( new PlanarImgFactory<>( new IntType() ) );

	private final RandomAccessibleInterval<IntType> cellImg = createImg( new CellImgFactory<>( new IntType(), 100, 100 ) );

	private RandomAccessibleInterval<IntType> createImg( final ImgFactory<IntType> factory )
	{
		return Views.translate( factory.create( 1000, 1000 ), 40, 40 );
	}

	@Param( value = { "false", "true" } )
	boolean slowdown;

	@Setup
	public void slowdown( final Blackhole blackhole )
	{
		double s = 0;
		if ( slowdown )
		{
			for ( int i = 0; i < 10; i++ )
			{
				s += sum2( arrayImg );
				s += sum2( planarImg );
				s += sum2( cellImg );
			}
		}
		blackhole.consume( s );
	}

	@Benchmark
	public double benchmarkSum()
	{
		return sum( arrayImg );
	}

	@Benchmark
	public double benchmarkLoopBuilder()
	{
		final double[] sum = new double[ 1 ];
		LoopBuilder.setImages( arrayImg ).forEachPixel( pixel -> sum[ 0 ] += pixel.getRealDouble() );
		return sum[ 0 ];
	}

	public static double sum( final RandomAccessibleInterval<? extends RealType<?>> img )
	{
		double sum = 0;
		final RandomAccess<? extends RealType<?>> ra = img.randomAccess();
		ra.setPosition( img.min( 1 ), 1 );
		for ( int y = 0; y < img.dimension( 1 ); y++ )
		{
			ra.setPosition( img.min( 0 ), 0 );
			for ( int x = 0; x < img.dimension( 0 ); x++ )
			{
				sum += ra.get().getRealDouble();
				ra.fwd( 0 );
			}
			ra.fwd( 1 );
		}
		return sum;
	}

	public static double sum2( final RandomAccessibleInterval<? extends RealType<?>> img )
	{
		double sum = 0;
		final RandomAccess<? extends RealType<?>> ra = img.randomAccess();
		ra.setPosition( img.min( 1 ), 1 );
		for ( int y = 0; y < img.dimension( 1 ); y++ )
		{
			ra.setPosition( img.min( 0 ), 0 );
			for ( int x = 0; x < img.dimension( 0 ); x++ )
			{
				sum += ra.get().getRealDouble() * 2;
				ra.fwd( 0 );
			}
			ra.fwd( 1 );
		}
		return sum;
	}

	public static void main( final String... args ) throws RunnerException
	{
		final Options options = new OptionsBuilder().include( ViewsTranslateBenchmark.class.getSimpleName() ).build();
		new Runner( options ).run();
	}
}
