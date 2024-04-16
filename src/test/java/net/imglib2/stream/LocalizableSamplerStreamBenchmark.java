/*-
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2024 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
package net.imglib2.stream;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import net.imglib2.Cursor;
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
public class LocalizableSamplerStreamBenchmark
{
	private Img< IntType > img;

	@Param( value = { "ArrayImg", "PlanarImg", "CellImg" } )
	private String imgType;

	@Setup
	public void setup()
	{
		final long[] dimensions = { 2000, 200, 10 };

		if ( imgType.equals( "ArrayImg" ) )
			img = ArrayImgs.ints( dimensions );
		else if ( imgType.equals( "PlanarImg" ) )
			img = PlanarImgs.ints( dimensions );
		else if ( imgType.equals( "CellImg" ) )
			img = new CellImgFactory<>( new IntType(), 64 ).create( dimensions );
		else
			throw new IllegalArgumentException();

		Random random = new Random( 1L );
		img.forEach( t -> t.set( random.nextInt( 1000 ) ) );
	}

	@Benchmark
	public long benchmarkStream()
	{
		long sum = Streams.localizable( img )
				.mapToLong( s -> s.get().get()
						+ s.getIntPosition( 0 )
						+ s.getIntPosition( 1 )
						+ s.getIntPosition( 2 )
				).sum();
		return sum;
	}

	@Benchmark
	public long benchmarkLocalizingStream()
	{
		long sum = Streams.localizing( img )
				.mapToLong( s -> s.get().get()
						+ s.getIntPosition( 0 )
						+ s.getIntPosition( 1 )
						+ s.getIntPosition( 2 )
				).sum();
		return sum;
	}

	@Benchmark
	public long benchmarkParallelStream()
	{
		long sum = Streams.localizable( img )
				.parallel()
				.mapToLong( s -> s.get().get()
						+ s.getIntPosition( 0 )
						+ s.getIntPosition( 1 )
						+ s.getIntPosition( 2 )
				).sum();
		return sum;
	}

	@Benchmark
	public long benchmarkLocalizingParallelStream()
	{
		long sum = Streams.localizing( img )
				.parallel()
				.mapToLong( s -> s.get().get()
						+ s.getIntPosition( 0 )
						+ s.getIntPosition( 1 )
						+ s.getIntPosition( 2 )
				).sum();
		return sum;
	}

	static class Sum implements Consumer< Cursor< IntType > >
	{
		private long sum;

		@Override
		public void accept( final Cursor< IntType > s )
		{
			sum += s.get().get()
					+ s.getIntPosition( 0 )
					+ s.getIntPosition( 1 )
					+ s.getIntPosition( 2 );
		}

		public long get()
		{
			return sum;
		}
	}

	@Benchmark
	public long benchmarkCursor()
	{
		final Sum sum = new Sum();
		final Cursor< IntType > it = img.cursor();
		while ( it.hasNext() )
		{
			it.fwd();
			sum.accept( it );
		}
		return sum.get();
	}

	@Benchmark
	public long benchmarkLocalizingCursor()
	{
		final Sum sum = new Sum();
		final Cursor< IntType > it = img.localizingCursor();
		while ( it.hasNext() )
		{
			it.fwd();
			sum.accept( it );
		}
		return sum.get();
	}

	public static void main( String... args ) throws RunnerException
	{
		Options options = new OptionsBuilder().include( LocalizableSamplerStreamBenchmark.class.getSimpleName() ).build();
		new Runner( options ).run();
	}
}
