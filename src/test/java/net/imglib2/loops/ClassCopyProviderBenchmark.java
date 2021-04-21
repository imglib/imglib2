/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2021 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import net.imglib2.type.numeric.integer.IntType;

/**
 * How to speed up a method like "forEach", by using {@link ClassCopyProvider}.
 * <p>
 * {@link ClassCopyProviderBenchmark} demonstrates how to solve the problem
 * described <a href=
 * "https://github.com/tpietzsch/none/">https://github.com/tpietzsch/none/</a>
 * by using {@link ClassCopyProvider}.
 * <p>
 * There is a significant speedup using Java 8. ClassCopyProvider might be
 * removed in the future, if there is no speedup with more recent Java versions.
 *
 * @author Matthias Arzt
 */
@State( Scope.Benchmark )
public class ClassCopyProviderBenchmark
{

	public final int[] values = new int[ 100000 ];

	@Benchmark
	public void sumMinMax()
	{
		calculateSumMinAndMax( this::normalForEach );
	}

	@Benchmark
	public void sumMinMax_using_ClassCopyProvider()
	{
		calculateSumMinAndMax( this::fasterForEach );
	}

	private void calculateSumMinAndMax( final Consumer< IntConsumer > loop )
	{
		for ( int i = 0; i < 10; i++ )
		{
			final IntType sum = new IntType( 0 );
			loop.accept( x -> sum.set( sum.get() + x ) );
			final IntType min = new IntType( Integer.MAX_VALUE );
			loop.accept( x -> min.set( Math.min( min.get(), x ) ) );
			final IntType max = new IntType( Integer.MIN_VALUE );
			loop.accept( x -> max.set( Math.max( max.get(), x ) ) );
		}
	}

	private void normalForEach( final IntConsumer action )
	{
		for ( final int value : values )
			action.accept( value );
	}

	private void fasterForEach( final IntConsumer action )
	{
		// NB: For every different "action.getClass()", "loopFactory" will make
		// a copy of the class "Loop" and it's byte code
		// and return an instance of this copied class.
		final Object key = action.getClass();
		final LoopInterface loop = loopFactory.newInstanceForKey( key );
		loop.accept( values, action );
	}

	private static final ClassCopyProvider< LoopInterface > loopFactory = new ClassCopyProvider<>( Loop.class, LoopInterface.class );

	public static class Loop implements LoopInterface
	{

		@Override
		public void accept( final int[] values, final IntConsumer action )
		{
			for ( final int value : values )
				action.accept( value );
		}
	}

	public interface LoopInterface extends BiConsumer< int[], IntConsumer >
	{}

	public static void main( final String... args ) throws RunnerException
	{
		final Options opt = new OptionsBuilder()
				.include( ClassCopyProviderBenchmark.class.getSimpleName() )
				.forks( 0 )
				.warmupIterations( 4 )
				.measurementIterations( 8 )
				.warmupTime( TimeValue.milliseconds( 100 ) )
				.measurementTime( TimeValue.milliseconds( 100 ) )
				.build();
		new Runner( opt ).run();
	}
}
