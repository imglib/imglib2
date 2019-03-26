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
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class MultiThreadSettings
{

	/**
	 * Returns a {@link MultiThreadSetting}, that will execute a
	 * algorithm in a single thread without multi-threading.
	 */
	public static MultiThreadSetting single()
	{
		return SINGLE_THREAD_SETTINGS;
	}

	/**
	 * Returns a {@link MultiThreadSetting}, that will execute a
	 * algorithm with multi-threading.
	 */
	public static MultiThreadSetting multi()
	{
		return MULTI_THREAD_SETTING;
	}

	/**
	 * Returns a {@link MultiThreadSetting}, that will execute a
	 * algorithm using the given {@link ExecutorService}.
	 */
	public static MultiThreadSetting multi( int numberOfTasks, ExecutorService executor )
	{
		return new CustomMultiThreading( numberOfTasks, executor );
	}

	private static final MultiThreadSetting SINGLE_THREAD_SETTINGS = new SingleThreading();

	private static class SingleThreading implements MultiThreadSetting
	{

		@Override
		public boolean useMultiThreading()
		{
			return false;
		}

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
	}

	private static final MultiThreadSetting MULTI_THREAD_SETTING = new StreamMultiThreading();

	private static class StreamMultiThreading implements MultiThreadSetting
	{

		@Override
		public boolean useMultiThreading()
		{
			return true;
		}

		@Override
		public int suggestNumberOfTasks()
		{
			return Runtime.getRuntime().availableProcessors() * 4;
		}

		@Override
		public < T > void forEach( Collection< T > values, Consumer< T > action )
		{
			values.parallelStream().forEach( action );
		}
	}

	private static class CustomMultiThreading implements MultiThreadSetting
	{

		private final int numberOfTasks;

		private final ExecutorService executor;

		private CustomMultiThreading( int numberOfTasks, ExecutorService executor )
		{
			this.numberOfTasks = numberOfTasks;
			this.executor = executor;
		}

		@Override
		public boolean useMultiThreading()
		{
			return true;
		}

		@Override
		public int suggestNumberOfTasks()
		{
			return numberOfTasks;
		}

		@Override
		public < T > void forEach( Collection< T > values, Consumer< T > action )
		{
			try
			{
				Collection< Callable< Object > > callables = wrapAsCallables( values, action );
				final List< Future< Object > > futures = executor.invokeAll( callables );
				waitForAllTasksToComplete( futures );
			}
			catch ( InterruptedException e )
			{
				throw new RuntimeException( e );
			}
		}

		private < T > Collection< Callable< Object > > wrapAsCallables( Collection< T > values, Consumer< T > action )
		{
			return values.stream()
					.map( value -> ( Callable< Object > ) () -> {
						action.accept( value );
						return null;
					} )
					.collect( Collectors.toList() );
		}

		private void waitForAllTasksToComplete( List< Future< Object > > futures ) throws InterruptedException
		{
			ExecutionException exception = null;
			for ( Future< Object > future : futures )
				try
				{
					future.get();
				}
				catch ( ExecutionException e )
				{
					if ( exception == null )
						exception = e;
				}
			if ( exception != null )
				throw new RuntimeException( exception );
		}
	}
}
