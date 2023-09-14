/*
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
package net.imglib2.loops;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.util.Intervals;
import net.imglib2.view.Views;

import java.util.concurrent.TimeUnit;

/**
 * Performance benchmark to demonstrate {@link LoopBuilder} performance.
 *
 * @author Matthias Arzt
 */
@State( Scope.Benchmark )
@Fork( 1 )
@Warmup( iterations = 4, time = 100, timeUnit = TimeUnit.MILLISECONDS )
@Measurement( iterations = 8, time = 100, timeUnit = TimeUnit.MILLISECONDS )
public class LoopPerformanceBenchmark
{
	private final long[] dim = { 1000, 1000 };

	private final RandomAccessibleInterval< DoubleType > in = ArrayImgs.doubles( dim );

	private final RandomAccessibleInterval< DoubleType > out = ArrayImgs.doubles( dim );

	private final RandomAccessibleInterval< DoubleType > backIn = Views.interval( Views.extendBorder( in ), Intervals.translate( out, 1, 0 ) );

	private final RandomAccessibleInterval< DoubleType > frontIn = Views.interval( Views.extendBorder( in ), Intervals.translate( out, -1, 0 ) );

	@Benchmark
	public void gradient_niceAndSlow()
	{
		final Cursor< DoubleType > front = Views.flatIterable( backIn ).cursor();
		final Cursor< DoubleType > back = Views.flatIterable( frontIn ).cursor();

		for ( final DoubleType t : Views.flatIterable( out ) )
		{
			t.set( front.next() );
			t.sub( back.next() );
			t.mul( 0.5 );
		}
	}

	@Benchmark
	public void gradient_better()
	{
		final RandomAccess< DoubleType > result = out.randomAccess();
		final RandomAccess< DoubleType > back = Views.extendBorder( in ).randomAccess();
		final RandomAccess< DoubleType > front = Views.extendBorder( in ).randomAccess();

		back.setPosition( out.minAsLongArray() );
		front.setPosition( out.minAsLongArray() );
		back.bck( 0 );
		front.fwd( 0 );

		LoopUtils.createIntervalLoop( SyncedPositionables.create( result, back, front ), out, () -> {
			result.get().set( front.get() );
			result.get().sub( back.get() );
			result.get().mul( 0.5 );
		} ).run();
	}

	@Benchmark
	public void gradient_LoopBuilder()
	{

		LoopBuilder.setImages( out, frontIn, backIn ).forEachPixel( ( result, back, front ) -> {
			result.set( front );
			result.sub( back );
			result.mul( 0.5 );
		} );
	}

	@Benchmark
	public void gradient_LoopBuilder_MultiThreaded()
	{
		LoopBuilder.setImages( out, frontIn, backIn ).multiThreaded().forEachPixel( ( result, back, front ) -> {
			result.set( front );
			result.sub( back );
			result.mul( 0.5 );
		} );
	}

	@Benchmark
	public void copy_pairedView()
	{
		Views.interval( Views.pair( in, out ), out ).forEach( p -> p.getA().set( p.getB() ) );
	}

	@Benchmark
	public void copy_loopBuilder()
	{
		LoopBuilder.setImages( in, out ).forEachPixel( ( in, out ) -> out.set( in ) );
	}

	@Benchmark
	public void copy_flatIterable()
	{
		Cursor< DoubleType > i = Views.flatIterable( in ).cursor();
		Cursor< DoubleType > o = Views.flatIterable( out ).cursor();
		while ( o.hasNext() )
			o.next().set( i.next() );
	}

	public static void main( final String... args ) throws RunnerException
	{
		final Options opt = new OptionsBuilder()
				.include( LoopPerformanceBenchmark.class.getSimpleName() )
				.build();
		new Runner( opt ).run();
	}
}
