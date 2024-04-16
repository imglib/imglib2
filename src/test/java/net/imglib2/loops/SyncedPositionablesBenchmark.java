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
