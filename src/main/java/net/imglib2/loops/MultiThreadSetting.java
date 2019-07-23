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
package net.imglib2.loops;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.function.Consumer;

/**
 * NOTE: Currently only to be used in LoopBuilder, and therefor package-private.
 * <p>
 * This class might be used as input parameter to an algorithm
 * to specify how the multi-threading is performed.
 * <p>
 * The caller of the algorithm, can use
 * {@link MultiThreadSetting} to specify if the algorithm
 * runs single-threaded or multi-threaded. How many threads
 * to use, and which {@link ExecutorService} or other method
 * to use for multi-threading.
 * <p>
 * The algorithm, should use the methods of this interface
 * to implement the multi-threading.
 */
interface MultiThreadSetting
{

	/**
	 * Setting for single-threaded execution. No Multi-Threading
	 * is used.
	 */
	MultiThreadSetting SINGLE = new MultiThreadSetting()
	{
		@Override
		public int suggestNumberOfTasks()
		{
			return 1;
		}

		@Override
		public < T > void forEach( Collection< T > values, Consumer< T > action )
		{
			values.forEach( action );
		}
	};

	/**
	 * Setting for multi-threaded execution. {@link ForkJoinPool}
	 * is used for execution.
	 */
	MultiThreadSetting MULTI = new MultiThreadSetting()
	{
		@Override
		public int suggestNumberOfTasks()
		{
			int parallelism = getParallelism();
			return ( parallelism == 1 ) ? 1 : parallelism * 4;
		}

		private int getParallelism()
		{
			if ( ForkJoinTask.inForkJoinPool() )
				return ForkJoinTask.getPool().getParallelism();
			return ForkJoinPool.commonPool().getParallelism();
		}

		@Override
		public < T > void forEach( Collection< T > values, Consumer< T > action )
		{
			values.parallelStream().forEach( action );
		}
	};

	/**
	 * An algorithm that can be multi-threaded will usually split
	 * it's calculations into sub-tasks / chunks. This
	 * method returns a number, that is believed to be the optimal
	 * number of tasks.
	 * <p>
	 * Usually equal to the number of cpu cores available, or 1
	 * if no multi threading is to be used.
	 * <p>
	 * The return values must be greater than or equal to 1.
	 */
	int suggestNumberOfTasks();

	/**
	 * Executes the given action for each value in the collection.
	 * <p>
	 * An example, that prints "A", "B", "C" in potentially multiple threads:
	 * <pre>
	 * {@code
	 *
	 * List<String> parameters = Array.asList("A", "B", "C");
	 * multiThreadSetting.forEach(
	 *     parameters,
	 *     string -> {
	 *         System.out.println(string)
	 *     }
	 * )
	 * }
	 * </pre>
	 */
	< T > void forEach( Collection< T > values, Consumer< T > action );

}
