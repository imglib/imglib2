/*-
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2023 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package net.imglib2.converter;

import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.img.planar.PlanarImgs;
import net.imglib2.loops.LoopBuilder;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
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
 * Measure the performance reduction when
 * {@link Converters#convert(RandomAccessibleInterval, Converter, Type)}
 * is used with different implementations of {@link RandomAccessibleInterval}
 * and {@link Converter}.
 */
@State( Scope.Benchmark )
@Warmup( iterations = 5, time = 100, timeUnit = TimeUnit.MILLISECONDS )
@Measurement( iterations = 5, time = 100, timeUnit = TimeUnit.MILLISECONDS )
@BenchmarkMode( Mode.AverageTime )
@OutputTimeUnit( TimeUnit.MILLISECONDS )
@Fork( 1 )
public class ConvertersBenchmark
{

	private final RandomAccessibleInterval<ARGBType> colors = ArrayImgs.argbs( 1000, 1000 );

	private final RandomAccessibleInterval<ARGBType> colorsPlanarImg = PlanarImgs.argbs( 1000, 1000 );

	private final RandomAccessibleInterval<ARGBType> colorsCellImg = new CellImgFactory<>( new ARGBType() ).create( 1000, 1000 );

	private final RandomAccessibleInterval<UnsignedByteType> red = Converters.convert( colors, ( i, o ) -> o.set( ARGBType.red( i.get() ) ), new UnsignedByteType() );

	private final RandomAccessibleInterval<UnsignedByteType> green = Converters.convert( colors, ( i, o ) -> o.set( ARGBType.green( i.get() ) ), new UnsignedByteType() );

	private final RandomAccessibleInterval<UnsignedByteType> blue = Converters.convert( colors, ( i, o ) -> o.set( ARGBType.blue( i.get() ) ), new UnsignedByteType() );

	private final RandomAccessibleInterval<UnsignedByteType> redPlanarImg = Converters.convert( colorsPlanarImg, ( i, o ) -> o.set( ARGBType.red( i.get() ) ), new UnsignedByteType() );

	private final RandomAccessibleInterval<UnsignedByteType> redCellImg = Converters.convert( colorsCellImg, ( i, o ) -> o.set( ARGBType.red( i.get() ) ), new UnsignedByteType() );

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
				s += sum2( red );
				s += sum2( green );
				s += sum2( blue );
				s += sum2( redPlanarImg );
				s += sum2( redCellImg );
			}
		}
		blackhole.consume( s );
	}

	@Benchmark
	public double benchmarkSum()
	{
		return sum( red );
	}

	@Benchmark
	public double benchmarkLoopBuilder()
	{
		final double[] sum = new double[ 1 ];
		LoopBuilder.setImages( red ).forEachPixel( pixel -> sum[ 0 ] += pixel.getRealDouble() );
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
		final Options options = new OptionsBuilder().include( ConvertersBenchmark.class.getName() ).build();
		new Runner( options ).run();
	}
}
