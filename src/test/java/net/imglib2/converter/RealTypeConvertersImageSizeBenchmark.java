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

package net.imglib2.converter;

import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.parallel.Parallelization;
import net.imglib2.test.RandomImgs;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.results.BenchmarkResult;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.Collection;
import java.util.HashMap;

/**
 * Compare execution time between single threaded and multi threaded
 * execution of {@link RealTypeConverters#copyFromTo(RandomAccessible, RandomAccessibleInterval)}.
 */
@State(Scope.Benchmark)
public class RealTypeConvertersImageSizeBenchmark {

	@Param({ "ImageSize" })
	public String imageSize;

	private Img< FloatType > in;

	private Img< FloatType > out;

	@Setup(Level.Trial)
	public void setup()
	{
		int size = Integer.valueOf(imageSize);
		in = RandomImgs.seed(42).nextImage(new FloatType(), 100, size / 100);
		out = ArrayImgs.floats(100, size / 100);
	}

	@Benchmark
	public void singleThreaded()
	{
		Parallelization.runSingleThreaded(
				() -> RealTypeConverters.copyFromTo(in, out)
		);
	}

	@Benchmark
	public void multiThreaded()
	{
		Parallelization.runMultiThreaded(
				() -> RealTypeConverters.copyFromTo(in, out)
		);
	}

	public static void main(final String... args) throws RunnerException
	{
		String[] imageSizes = { "100", "1000", "10000", "100000", "1000000", "10000000", "100000000" };
		String simpleName =
				RealTypeConvertersImageSizeBenchmark.class.getName();
		final Options opt = new OptionsBuilder().include(simpleName).forks(1)
				.warmupIterations(5).measurementIterations(10)
				.warmupTime(TimeValue.milliseconds(500))
				.measurementTime(TimeValue.milliseconds(500))
				.param("imageSize", imageSizes)
				.build();
		Collection< RunResult > results = new Runner(opt).run();
		HashMap< Pair< String, String >, Double > map = asMap(results);
		for (String imageSize : imageSizes) {
			double s = map.get(new ValuePair<>(simpleName + ".singleThreaded",
					imageSize));
			double m = map.get(new ValuePair<>(simpleName + ".multiThreaded",
					imageSize));
			System.out.println(
					"Multi-threading speedup: " + m / s + " for image size: " +
							imageSize);
		}
	}

	public static HashMap< Pair< String, String >, Double > asMap(
			Collection< RunResult > results)
	{
		HashMap< Pair< String, String >, Double > map = new HashMap<>();
		for (RunResult result : results) {
			BenchmarkResult aggregatedResult = result.getAggregatedResult();
			BenchmarkParams params = aggregatedResult.getParams();
			double score = aggregatedResult.getPrimaryResult().getScore();
			String benchmark = params.getBenchmark();
			String size = params.getParam("imageSize");
			map.put(new ValuePair<>(benchmark, size), score);
		}
		return map;
	}

}
