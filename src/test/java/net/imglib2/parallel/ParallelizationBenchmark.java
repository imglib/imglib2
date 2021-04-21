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
package net.imglib2.parallel;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.test.RandomImgs;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.view.Views;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

/**
 * Demonstrates how to use {@link Parallelization} to execute a algorithm
 * single threaded / multi threaded ....
 * And shows the execution time.
 */
@Fork( 1 )
@Warmup( iterations = 5, time = 200, timeUnit = TimeUnit.MILLISECONDS )
@Measurement( iterations = 5, time = 200, timeUnit = TimeUnit.MILLISECONDS )
@State( Scope.Benchmark )
@BenchmarkMode( { Mode.AverageTime } )
public class ParallelizationBenchmark
{

	private final RandomAccessibleInterval< IntType > image = RandomImgs.seed( 42 ).nextImage( new IntType(), 100, 500, 500 );

	/**
	 * If the image is one dimensional: run simple calculate sum.
	 * If the image is at least 2D: cut the image into slices.
	 * Use {@link Parallelization#forEach} to call
	 * {@link #calculateSum} for each slice, and sum up the results.
	 */
	private static long calculateSum( RandomAccessibleInterval< IntType > image )
	{
		if ( image.numDimensions() <= 1 )
			return simpleCalculateSum( image );
		List< RandomAccessibleInterval< IntType > > slices = slices( image );
		AtomicLong result = new AtomicLong();
		Parallelization.getTaskExecutor().forEach( slices, slice -> result.addAndGet( calculateSum( slice ) ) );
		return result.get();
	}

	private static long simpleCalculateSum( RandomAccessibleInterval< IntType > image )
	{
		long result = 0;
		for ( IntType pixel : Views.iterable( image ) )
			result += pixel.getInteger();
		return result;
	}

	private static List< RandomAccessibleInterval< IntType > > slices( RandomAccessibleInterval< IntType > image )
	{
		int lastDim = image.numDimensions() - 1;
		final long min = image.min( lastDim );
		final long max = image.max( lastDim );
		return LongStream.rangeClosed( min, max )
				.mapToObj( position -> Views.hyperSlice( image, lastDim, position ) )
				.collect( Collectors.toList() );
	}

	@Benchmark
	public Long fixedThreadPool()
	{
		final ExecutorService executor = Executors.newFixedThreadPool( Runtime.getRuntime().availableProcessors() );
		Long sum = Parallelization.runWithExecutor( executor,
				() -> calculateSum( image )
		);
		executor.shutdown();
		return sum;
	}

	@Benchmark
	public Long twoThreadsForkJoinPool()
	{
		ForkJoinPool executor = new ForkJoinPool( 2 );
		Long sum = Parallelization.runWithExecutor( executor, () -> calculateSum( image ) );
		executor.shutdown();
		return sum;
	}

	@Benchmark
	public Long multiThreaded()
	{
		return Parallelization.runMultiThreaded( () -> calculateSum( image ) );
	}

	@Benchmark
	public Long singleThreaded()
	{
		return Parallelization.runSingleThreaded( () -> calculateSum( image ) );
	}

	@Benchmark
	public Long defaultBehavior()
	{
		return calculateSum( image );
	}

	@Benchmark
	public Long singleThreadedBaseline()
	{
		return calculateSum( image );
	}

	public static void main( String... args ) throws RunnerException
	{
		Options options = new OptionsBuilder()
				.include( ParallelizationBenchmark.class.getName() )
				.build();
		new Runner( options ).run();
	}
}
