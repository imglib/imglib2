/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2016 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
package net.imglib2.img.array;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.loops.LoopBuilder;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.view.Views;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

/**
 * Performance benchmark to demonstrate {@link LoopBuilder} performance.
 *
 * @author Matthias Arzt
 */
@State( Scope.Benchmark )
public class ArrayRandomAccessBenchmark
{

	Img< DoubleType > in = ArrayImgs.doubles( 300, 300 );

	Img< DoubleType > out = ArrayImgs.doubles( 300, 300 );

	@Benchmark
	public void copyImage()
	{
		final Cursor< DoubleType > cursor = in.cursor();
		final RandomAccess< DoubleType > ra = out.randomAccess();

		while ( cursor.hasNext() ) {
			cursor.fwd();
			ra.setPosition( cursor );
			ra.get().set(cursor.get());
		}
	}

	@Benchmark
	public void copyLocalizingCursor()
	{
		final Cursor< DoubleType > cursor = in.localizingCursor();
		final RandomAccess< DoubleType > ra = out.randomAccess();

		while ( cursor.hasNext() ) {
			cursor.fwd();
			ra.setPosition( cursor );
			ra.get().set(cursor.get());
		}
	}

	@Benchmark
	public void copyFlatIterable()
	{
		final Cursor< DoubleType > a = Views.flatIterable( in ).cursor();
		final Cursor< DoubleType > b = Views.flatIterable( out ).cursor();
		while ( a.hasNext() )
			b.next().set( a.next() );
	}

	@Benchmark
	public void copyLoopBuilder()
	{
		LoopBuilder.setImages( in, out ).forEachPixel( (i, o) -> o.set(i) );
	}

	public static void main( final String... args ) throws RunnerException
	{
		final Options opt = new OptionsBuilder()
				.include( ArrayRandomAccessBenchmark.class.getSimpleName() )
				.forks( 0 )
				.warmupIterations( 15 )
				.measurementIterations( 8 )
				.warmupTime( TimeValue.milliseconds( 100 ) )
				.measurementTime( TimeValue.milliseconds( 100 ) )
				.build();
		new Runner( opt ).run();
	}
}
