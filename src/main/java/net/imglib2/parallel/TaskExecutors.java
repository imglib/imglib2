/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2025 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ThreadFactory;
import java.util.function.Supplier;

/**
 * Utility class, with methods to create {@link TaskExecutor}s.
 */
public final class TaskExecutors
{

	private TaskExecutors()
	{
		// prevent from instantiation
	}

	/**
	 * {@link TaskExecutor} for single threaded execution.
	 */
	public static TaskExecutor singleThreaded()
	{
		return SequentialTaskExecutor.getInstance();
	}

	/**
	 * {@link TaskExecutor} for multi-threaded execution.
	 * {@link ForkJoinPool} is used.
	 */
	public static TaskExecutor multiThreaded()
	{
		return FORK_JOIN_TASK_EXECUTOR;
	}

	private static final TaskExecutor FORK_JOIN_TASK_EXECUTOR =
			new DefaultTaskExecutor( new ForkJoinExecutorService() );

	/**
	 * {@link TaskExecutor} that uses the given number or threads.
	 * The {@link TaskExecutor} needs to be closed by calling {@link TaskExecutor#close()}.
	 */
	public static TaskExecutor numThreads( int numThreads )
	{
		numThreads = Math.max( 1, numThreads );
		return forExecutorService( new ForkJoinPool( numThreads ) );
	}

	/**
	 * Creates a {@link TaskExecutor} that wraps around given {@link ExecutorService}.
	 */
	public static TaskExecutor forExecutorService( ExecutorService executorService )
	{
		return new DefaultTaskExecutor( executorService );
	}

	/**
	 * Creates a {@link TaskExecutor} that wraps around given {@link ExecutorService},
	 * and will return the given parallelism.
	 */
	public static TaskExecutor forExecutorServiceAndNumThreads( ExecutorService executorService, int numThreads )
	{
		return new DefaultTaskExecutor( executorService )
		{
			@Override
			public int getParallelism()
			{
				return numThreads;
			}
		};
	}

	/**
	 * Creates a {@link TaskExecutor} that wraps around given {@link ExecutorService},
	 * and will suggest the given number of tasks when asked.
	 */
	public static TaskExecutor forExecutorServiceAndNumTasks( ExecutorService executorService, int numTasks )
	{
		return new DefaultTaskExecutor( executorService )
		{
			@Override
			public int suggestNumberOfTasks()
			{
				return numTasks;
			}
		};
	}

	/**
	 * Returns a {@link TaskExecutor} that uses a fixed thread pool with the
	 * given number of threads.
	 */
	public static TaskExecutor fixedThreadPool( int numThreads )
	{
		ThreadFactory threadFactory = threadFactory( () -> singleThreaded() );
		return forExecutorService( Executors.newFixedThreadPool( numThreads, threadFactory ) );
	}

	/**
	 * Returns a {@link TaskExecutor} that uses a fixed thread pool with the
	 * given number of threads. But that's not the end of the story.
	 * <p>
	 * Each of the threads uses itself a fixed thread pool with the given
	 * number of sub threads.
	 * <p>
	 * This {@link TaskExecutor} is useful for nested parallelization,
	 * when detailed control for the level of parallelism is needed.
	 */
	public static TaskExecutor nestedFixedThreadPool( int numThreads, int numSubThreads )
	{
		ThreadFactory threadFactory = threadFactory( () -> fixedThreadPool( numSubThreads ) );
		return forExecutorService( Executors.newFixedThreadPool( numThreads, threadFactory ) );
	}

	/**
	 * Returns a {@link ThreadFactory}. Whenever this thread factory is used
	 * to create a thread, a {@link TaskExecutor} will is create and assigned
	 * to the thread. The {@link TaskExecutor} is created using the given supplier.
	 */
	public static ThreadFactory threadFactory( Supplier< TaskExecutor > taskExecutorFactory )
	{
		return applyTaskExecutorToThreadFactory( taskExecutorFactory, Executors.defaultThreadFactory() );
	}

	/**
	 * Returns a {@link ThreadFactory}. Whenever this thread factory is used
	 * to create a thread, a {@link TaskExecutor} will is create and assigned
	 * to the thread. The threads created, are using the given thread factory,
	 * and the {@link TaskExecutors} are create using the given supplier.
	 */
	public static ThreadFactory applyTaskExecutorToThreadFactory( Supplier< TaskExecutor > taskExecutorFactory, ThreadFactory threadFactory )
	{
		return runnable -> threadFactory.newThread( () -> {
			try (TaskExecutor taskExecutor = taskExecutorFactory.get())
			{
				Parallelization.runWithExecutor( taskExecutor, runnable );
			}
		} );
	}
}
