package net.imglib2.img.array;

import java.util.concurrent.TimeUnit;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.img.Img;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.ByteType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.ShortType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedIntType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;
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
@Warmup( iterations = 10, time = 100, timeUnit = TimeUnit.MILLISECONDS )
@Measurement( iterations = 10, time = 100, timeUnit = TimeUnit.MILLISECONDS )
@BenchmarkMode( Mode.AverageTime )
@OutputTimeUnit( TimeUnit.MILLISECONDS )
@Fork( 1 )
public class ArrayRandomAccessBenchmark
{
	@Param( value = { "false", "true" } )
	private boolean slowdown;

	@Setup
	public void setup()
	{
		if ( slowdown )
		{
			spoil( new FloatType() );
			spoil( new DoubleType() );
			spoil( new UnsignedShortType() );
			spoil( new UnsignedByteType() );
			spoil( new ShortType() );
			spoil( new ByteType() );
			spoil( new UnsignedIntType() );
			spoil( new IntType() );
		}
	}

	public < T extends NativeType< T > & RealType< T > > double spoil( final T type )
	{
		final Img< T > img = new ArrayImgFactory<>( type ).create( 1000, 1000 );
		return doSum1( img );
	}

	private final Img< IntType > img = new ArrayImgFactory<>( new IntType() ).create( 1000, 1000 );

	@Benchmark
	public Object sum()
	{
		return doSum( img );
	}

	public < T extends RealType< T > > double doSum( RandomAccessible< T > img )
	{
		double sum = 0;
		RandomAccess< T > ra = img.randomAccess();
		ra.setPosition( 0, 1 );
		for ( int y = 0; y < 1000; y++ )
		{
			ra.setPosition( 0, 0 );
			for ( int x = 0; x < 1000; x++ )
			{
				sum += ra.get().getRealDouble();
				ra.fwd( 0 );
			}
			ra.fwd( 1 );
		}
		return sum + ra.getIntPosition( 0 );
	}

	public < T extends RealType< T > > double doSum1( RandomAccessible< T > img )
	{
		double sum = 0;
		RandomAccess< T > ra = img.randomAccess();
		ra.setPosition( 0, 1 );
		for ( int y = 0; y < 1000; y++ )
		{
			ra.setPosition( 0, 0 );
			for ( int x = 0; x < 1000; x++ )
			{
				sum += ra.get().getRealDouble();
				ra.fwd( 0 );
			}
			ra.fwd( 1 );
		}
		return sum + ra.getIntPosition( 0 );
	}

	public static void main( String... args ) throws RunnerException
	{
		Options options = new OptionsBuilder()
				.include( ArrayRandomAccessBenchmark.class.getSimpleName() )
				.build();
		new Runner( options ).run();
	}
}
