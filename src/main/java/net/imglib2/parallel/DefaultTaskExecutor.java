/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2022 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A {@link TaskExecutor} that wraps around a given {@link ExecutorService}.
 */
public class DefaultTaskExecutor implements TaskExecutor
{

	private final ExecutorService executorService;

	public DefaultTaskExecutor( final ExecutorService executorService )
	{
		this.executorService = executorService;
	}

	@Override
	public ExecutorService getExecutorService()
	{
		return executorService;
	}

	@Override
	public int getParallelism()
	{
		if ( executorService instanceof ForkJoinPool )
			return ( ( ForkJoinPool ) executorService ).getParallelism();
		else if ( executorService instanceof ThreadPoolExecutor )
			return Math.max( 1, ( ( ThreadPoolExecutor ) executorService ).getCorePoolSize() );
		else if ( executorService instanceof ForkJoinExecutorService )
			return ( ( ForkJoinExecutorService ) executorService ).getParallelism();
		else if ( executorService instanceof SequentialExecutorService )
			return ( ( SequentialExecutorService ) executorService ).getParallelism();
		return Runtime.getRuntime().availableProcessors();
	}

	@Override
	public void runAll( final List< Runnable > tasks )
	{
		final List< Callable< Object > > callables = new ArrayList<>( tasks.size() );
		// use for-loop because stream with collect(Collectors.toList) is slow.
		for ( Runnable task : tasks )
			callables.add( Executors.callable( task ) );
		invokeAllIgnoreResults( callables );
	}

	@Override
	public int suggestNumberOfTasks()
	{
		int parallelism = getParallelism();
		return ( parallelism == 1 ) ? 1 : ( int ) Math.min( ( long ) parallelism * 4L, ( long ) Integer.MAX_VALUE );
	}

	@Override
	public < T > void forEach( final List< ? extends T > parameters, final Consumer< ? super T > task )
	{
		final List< Callable< Object > > callables = new ArrayList<>( parameters.size() );
		// use for-loop because stream with collect(Collectors.toList) is slow.
		for ( T parameter : parameters )
			callables.add( () -> {
				task.accept( parameter );
				return null;
			} );
		invokeAllIgnoreResults( callables );
	}

	@Override
	public < T, R > List< R > forEachApply( List< ? extends T > parameters, Function< ? super T, ? extends R > task )
	{
		final List< Callable< R > > callables = new ArrayList<>( parameters.size() );
		// use for-loop because stream with collect(Collectors.toList) is slow.
		for ( T parameter : parameters )
			callables.add( () -> task.apply( parameter ) );
		try
		{
			final List< Future< R > > futures = executorService.invokeAll( callables );
			final List< R > results = new ArrayList<>( futures.size() );
			for ( Future< R > future : futures )
				results.add( future.get() );
			return results;
		}
		catch ( InterruptedException | ExecutionException e )
		{
			throw unwrapExecutionException( e );
		}
	}

	private void invokeAllIgnoreResults( final List< Callable< Object > > callables )
	{
		try
		{
			final List< Future< Object > > futures = executorService.invokeAll( callables );
			for ( Future< Object > future : futures )
				future.get();
		}
		catch ( InterruptedException | ExecutionException e )
		{
			throw unwrapExecutionException( e );
		}
	}

	/**
	 * {@link ExecutorService} wrap all exceptions thrown by any task into a
	 * {@link ExecutionException}, this makes the stack traces rather hard to
	 * read. This method unwraps the {@link ExecutionException} and thereby
	 * reveals the original exception, and ensures it's complete stack trace.
	 */
	private RuntimeException unwrapExecutionException( Throwable e )
	{
		if ( e instanceof ExecutionException )
		{
			final Throwable cause = e.getCause();
			cause.setStackTrace( concatenate( cause.getStackTrace(), e.getStackTrace() ) );
			e = cause;
		}
		if ( e instanceof RuntimeException )
			throw ( RuntimeException ) e;
		else
			return new RuntimeException( e );
	}

	private < T > T[] concatenate( final T[] a, final T[] b )
	{
		int aLen = a.length;
		int bLen = b.length;
		@SuppressWarnings( "unchecked" )
		T[] c = ( T[] ) Array.newInstance( a.getClass().getComponentType(), aLen + bLen );
		System.arraycopy( a, 0, c, 0, aLen );
		System.arraycopy( b, 0, c, aLen, bLen );
		return c;
	}

	@Override
	public void close()
	{
		executorService.shutdown();
	}
}
