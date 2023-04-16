package net.imglib2.blocks;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import net.imglib2.converter.Converter;
import net.imglib2.img.basictypeaccess.array.FloatArray;
import net.imglib2.img.basictypeaccess.array.ShortArray;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.FloatType;
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
@Measurement( iterations = 10, time = 100, timeUnit = TimeUnit.MILLISECONDS )
@BenchmarkMode( Mode.AverageTime )
@OutputTimeUnit( TimeUnit.MICROSECONDS )
@Fork( 1 )
public class ConvertBenchmark
{
	private static final int LENGTH = 64 * 64 * 64;

	private final short[] uint16src;
	private final float[] src;
	private final float[] dest;

	public ConvertBenchmark()
	{
		src = new float[ LENGTH ];
		dest = new float[ LENGTH ];
		uint16src = new short[ LENGTH ];
	}

	@Benchmark
	public void benchmarkConvert()
	{
		convert( uint16src, dest, LENGTH );
	}

	@Benchmark
	public void benchmarkConvert2()
	{
		final Converter< UnsignedShortType, FloatType > converter = ( in, out ) -> out.setReal( in.getRealFloat() );
		convert2( uint16src, dest, LENGTH, converter );
	}

	@Benchmark
	public void benchmarkConvert3()
	{
		final Supplier< Converter< UnsignedShortType, FloatType > > converterSupplier = () -> ( in, out ) -> out.setReal( in.getRealFloat() );
		final Convert convert = Convert.create( new UnsignedShortType(), new FloatType(), converterSupplier );
		convert3( uint16src, dest, LENGTH, convert );
	}

	static void copy1( float[] src, float[] dest, int src_offset, int dest_offset, int length )
	{
		for ( int i = 0; i < length; i++ )
			dest[ i + dest_offset ] = src[ i + src_offset ];
	}

	static void copy2( float[] src, float[] dest, int length )
	{
		for ( int i = 0; i < length; i++ )
			dest[ i ] = src[ i ];
	}

	static void convert( short[] src, float[] dest, int length )
	{
		for ( int i = 0; i < length; i++ )
			dest[ i ] = src[ i ] & 0xffff;
	}

	static void convert2( short[] src, float[] dest, int length, final Converter< UnsignedShortType, FloatType > converter )
	{
		final UnsignedShortType in = new UnsignedShortType( new ShortArray( src ) );
		final FloatType out = new FloatType( new FloatArray( dest ) );
		for ( int i = 0; i < length; i++ )
		{
			in.index().set( i );
			out.index().set( i );
			converter.convert( in, out );
		}
	}

	static void convert3( short[] src, float[] dest, int length, Convert convert )
	{
		convert.convert( src, dest, length );
	}

	public static void main( String... args ) throws RunnerException
	{
		Options options = new OptionsBuilder().include( ConvertBenchmark.class.getSimpleName() ).build();
		new Runner( options ).run();
//		new ConvertBenchmark().benchmarkConvert3();
	}
}
