/*-
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

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * {@link TaskExecutor} is recommended to be used in image
 * processing algorithms instead of {@link ExecutorService}.
 * It's simpler to use, and allows single threaded execution.
 * <p>
 * <pre>
 * {@code
 *
 * // Example of a multi threaded method that fills an image with ones.
 * public void fillWithOnes( RandomAccessibleInterval< IntType > image, TaskExecutor taskExecutor )
 * {
 *     int numTasks = taskExecutor.suggestNumberOfTasks();
 *     List< RandomAccessibleInterval< IntType > > chunks = splitImageIntoChunks( image, numTasks );
 *
 *     // The TaskExecutor executes the forEach method in multi threads, if requested.
 *     taskExecutor.forEach( chunks, chunk -> {
 *         for ( IntType pixel : Views.iterable( chunk ) )
 *             pixel.setOne();
 *     } );
 * }
 *
 * // The method can be run multi threaded or single threaded
 * fillWithOnes( image, TaskExecutors.singleThreaded() );
 * fillWithOnes( image, TaskExecutors.multiThreaded() );
 * }
 * </pre>
 */
public interface TaskExecutor extends AutoCloseable
{

	/**
	 * Get the number of threads that are used for execution.
	 */
	int getParallelism();

	/**
	 * If there is a big task, that could be split into sub tasks
	 * for parallelization. This method gives you a reasonable number
	 * of sub tasks.
	 * <p>
	 * A single threaded {@link TaskExecutor} it would return 1.
	 * A multi threaded {@link TaskExecutor} usually return 4 times the
	 * number of threads.
	 */
	int suggestNumberOfTasks();

	/**
	 * This method will execute the given list of tasks. A single
	 * threaded {@link TaskExecutor} will execute the task one after an
	 * other. A multi threaded {@link TaskExecutor} will distribute the
	 * task to the threads. And wait until every task completed.
	 */
	void runAll( List< Runnable > tasks );

	/**
	 * Like {@link #runAll(List)} but - instead of a list of tasks - it takes
	 * a list of parameters an a function that is called for each of
	 * the parameters.
	 */
	< T > void forEach( List< ? extends T > parameters, Consumer< ? super T > task );

	/**
	 * List {@link #forEach(List, Consumer)} but collects the results.
	 */
	< T, R > List< R > forEachApply( List< ? extends T > parameters, Function< ? super T, ? extends R > task );

	/**
	 * Get the underlying {@link ExecutorService}, this is not always a
	 * fully functional {@link ExecutorService}. Especially the methods
	 * {@link ExecutorService#shutdown()} and
	 * {@link ExecutorService#awaitTermination(long, TimeUnit)} must not be
	 * used.
	 */
	ExecutorService getExecutorService();

	@Override
	void close();
}
