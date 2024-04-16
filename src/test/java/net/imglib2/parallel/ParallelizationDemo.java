/*
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
package net.imglib2.parallel;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This demo shows how to use {@link Parallelization} to write a
 * algorithm, that can be run single threaded or multi threaded,
 * or with a given number of threads.
 */
public class ParallelizationDemo
{
	public static void main( String... args )
	{
		System.out.println( "\nSingle Threaded:\n================" );
		Parallelization.runSingleThreaded( () -> exampleAlgorithm() );

		System.out.println( "\nMulti Threaded:\n===============" );
		Parallelization.runMultiThreaded( () -> exampleAlgorithm() );

		System.out.println( "\nTwo Threads:\n===============" );
		ExecutorService executor = Executors.newFixedThreadPool( 2 );
		Parallelization.runWithExecutor( executor, () -> exampleAlgorithm() );
		executor.shutdown();
	}

	private static void exampleAlgorithm()
	{
		TaskExecutor taskExecutor = Parallelization.getTaskExecutor();

		System.out.println( "Parallelism Level: " + taskExecutor.getParallelism() );

		List< Integer > list = Arrays.asList( 1, 2, 3, 4 );

		taskExecutor.forEach( list, index -> {
			System.out.println( "task " + index + " start");
			waitOneSecond();
			System.out.println( "task " + index + " finish");
		} );
	}

	private static void waitOneSecond()
	{
		try
		{
			Thread.sleep( 1000 );
		}
		catch ( InterruptedException e )
		{
			throw new RuntimeException( e );
		}
	}
}
