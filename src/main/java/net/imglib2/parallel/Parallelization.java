/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2019 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

/**
 * This class allows to configure an algorithm for parallelization.
 * <p>
 * The algorithm needs to use the {@link TaskExecutor} returned by
 * {@link Parallelization#getTaskExecutor()} to implement the parallelization.
 * Alternatively it can use {@link Parallelization#getExecutorService()}.
 * But {@link TaskExecutor} is simpler and better suited for image precessing algorithms.
 * <p>
 * The algorithm can be executed singleThreaded, multiThreaded or by
 * using a specified {@link ExecutorService} or {@link TaskExecutor}:
 * <pre>
 *     {@code
 *
 *     // Single-threaded call
 *     Parallelization.runSingleThreaded( () -> myAlgorithm( image ) );
 *
 *     // Multi-threaded call
 *     Parallelization.runMultiThreaded( () -> myAlgorithm( image ) );
 *
 *     // ExecutorService
 *     Parallelization.withExecutor( executorService, () -> myAlgorithm( image ) );
 *
 *     // Multi-threaded is the default.
 *     //     A normal function call, that's not somehow wrapped by
 *     //     Parallelization.runSingleThreaded( ... ) runs multi-threaded.
 *     myAlgorithm( image );
 *
 *     // Example Algorithm, that fills an image with ones.
 *     public void myAlgorithm( RandomAccessibleInterval< IntType > image )
 *     {
 *         TaskExecutor taskExecutor = Parallelization.getTaskExecutor();
 *         int numTasks = taskExecutor.suggestNumberOfTasks();
 *         List< Interval > chunks = IntervalChunks.chunkInterval( image, numTasks );
 *
 *         // The TaskExecutor executes the forEach method in multiple threads, if requested.
 *         taskExecutor.forEach( chunks, chunk -> {
 *             for ( IntType pixel : Views.interval( image, chunk ) )
 *                 pixel.setOne();
 *         } );
 *     }
 *     }
 * </pre>
 */
public final class Parallelization
{

	private Parallelization()
	{
		// prevent from instantiation
	}

	private static final ThreadLocal< TaskExecutor > executor = ThreadLocal.withInitial( () -> TaskExecutors.multiThreaded() );

	// Methods to support the implementation of an multi-threaded algorithm

	/**
	 * Returns the {@link TaskExecutor} that was set for this thread.
	 */
	public static TaskExecutor getTaskExecutor()
	{
		return executor.get();
	}

	public static ExecutorService getExecutorService()
	{
		return getTaskExecutor().getExecutorService();
	}

	// Method to call a multi-threaded algorithm

	/**
	 * To run an algorithm single-threaded use:
	 * <p>
	 * {@code Parallelization.runSingleThreaded( () -> myAlgorithm( input ) );}
	 */
	public static void runSingleThreaded( Runnable action )
	{
		runWithExecutor( TaskExecutors.singleThreaded(), action );
	}

	/**
	 * To run an algorithm single-threaded use:
	 * <p>
	 * {@code output = Parallelization.runSingleThreaded( () -> myAlgorithm( input ) );}
	 */
	public static < T > T runSingleThreaded( Callable< T > action )
	{
		return runWithExecutor( TaskExecutors.singleThreaded(), action );
	}

	/**
	 * To run an algorithm multi-threaded use:
	 * <p>
	 * {@code Parallelization.runMultiThreaded( () -> myAlgorithm( input ) );}
	 */
	public static void runMultiThreaded( Runnable action )
	{
		runWithExecutor( TaskExecutors.multiThreaded(), action );
	}

	/**
	 * To run an algorithm multi-threaded use:
	 * <p>
	 * {@code output = Parallelization.runMultiThreaded( () -> myAlgorithm( input ) );}
	 */
	public static < T > T runMultiThreaded( Callable< T > action )
	{
		return runWithExecutor( TaskExecutors.multiThreaded(), action );
	}

	/**
	 * To run an algorithm a given number of threads use:
	 * <p>
	 * {@code Parallelization.runWithNumThreads( numThreads, () -> myAlgorithm( input ) );}
	 */
	public static void runWithNumThreads( int numThreads, Runnable action )
	{
		try (TaskExecutor taskExecutor = TaskExecutors.numThreads( numThreads ))
		{
			runWithExecutor( taskExecutor, action );
		}
	}

	/**
	 * To run an algorithm a given number of threads use:
	 * <p>
	 * {@code output = Parallelization.runWithNumThreads( numThreads, () -> myAlgorithm( input ) );}
	 */
	public static < R > R runWithNumThreads( int numThreads, Callable< R > action )
	{
		try (TaskExecutor taskExecutor = TaskExecutors.numThreads( numThreads ))
		{
			return runWithExecutor( taskExecutor, action );
		}
	}

	/**
	 * Executes the given {@link Runnable} with the given {@link ExecutorService},
	 * and waits for the execution to finish.
	 */
	public static void runWithExecutor( ExecutorService executorService, Runnable action )
	{
		runWithExecutor( TaskExecutors.forExecutorService( executorService ), action );
	}

	/**
	 * Executes the given {@link Callable} with the given {@link ExecutorService},
	 * waits for the execution to finish and returns the result.
	 */
	public static < R > R runWithExecutor( ExecutorService executorService, Callable< R > action )
	{
		return runWithExecutor( TaskExecutors.forExecutorService( executorService ), action );
	}

	/**
	 * Executes the given {@link Runnable} with the given {@link TaskExecutor},
	 * and waits for the execution to finish.
	 */
	public static void runWithExecutor( TaskExecutor taskExecutor, Runnable action )
	{
		try (Frame frame = setExecutorRequiresReset( taskExecutor ))
		{
			action.run();
		}
	}

	/**
	 * Executes the given {@link Callable} with the given {@link TaskExecutor},
	 * waits for the execution to finish and returns the result.
	 */
	public static < T > T runWithExecutor( TaskExecutor taskExecutor, Callable< T > action )
	{
		try (Frame frame = setExecutorRequiresReset( taskExecutor ))
		{
			return action.call();
		}
		catch ( Exception e )
		{
			throw new RuntimeException( e );
		}
	}

	/**
	 * This method can be used to execute an algorithm with a given {@link TaskExecutor}.
	 * But it's easier to use {@link #runWithExecutor}.
	 * <p>
	 * This method sets the {@link TaskExecutor} for the current thread.
	 * This can be used to execute a certain part of your code with the given
	 * {@link TaskExecutor}. It's mandatory to call the {@code close()} of the
	 * return {@link Frame frame} afterwards. This could be done using an
	 * try-with-resources statement.
	 * <pre>
	 * {@code
	 *
	 * try ( Parallelization.Frame frame = Parallelization.setExecutorRequiresReset( taskExecutor ) )
	 * {
	 *     myAlgorithm(input);
	 * }
	 * }
	 * </pre>
	 * Or by explicitly calling {@code frame.close()} in the finally block.
	 * <pre>
	 * {@code
	 *
	 * Parallelization.Frame frame = Parallelization.setExecutorRequiresReset( taskExecutor );
	 * try
	 * {
	 *     myAlgorithm(input);
	 * }
	 * finally
	 * {
	 *     frame.close();
	 * }
	 * }
	 * </pre>
	 */
	public static Frame setExecutorRequiresReset( TaskExecutor taskExecutor )
	{
		final TaskExecutor old = executor.get();
		executor.set( taskExecutor );
		return () -> executor.set( old );
	}

	public interface Frame extends AutoCloseable
	{
		@Override
		void close(); // NB: Throws no exceptions
	}
}
