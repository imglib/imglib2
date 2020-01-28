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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * The {@link ForkJoinExecutorService} is an {@link ExecutorService},
 * for efficient nested parallelization.
 * <p>
 * The {@link ForkJoinPool} is an ExecutorService that provides
 * an entry point to a technique called work-steeling.
 * Work-steeling allows good performance for nested parallelization.
 * But calling {@link ForkJoinPool#submit} or {@link ForkJoinPool#invokeAll}
 * alone, won't result in any work-steeling and performance boost.
 * It's necessary to use {@link ForkJoinTask}s and their methods
 * {@link ForkJoinTask#fork fork} or {@link ForkJoinTask#invokeAll
 * invokeAll} to benefit from work-steeling.
 * <p>
 * ForkJoinExecutorService is an ExecutorService that internally
 * calls ForkJoinTask.fork() and ForkJoinTask.invokeAll(...) and
 * therefore directly achieves good performance by work-steeling.
 * <p>
 * ForkJoinExecutorService is not a fully functional ExecutorService.
 * Methods like {@link #shutdownNow()}, {@link #awaitTermination(long, TimeUnit)}
 * and {@link #invokeAll(Collection, long, TimeUnit)} are not implemented.
 */
public class ForkJoinExecutorService extends AbstractExecutorService
{

	public int getParallelism()
	{
		return getPool().getParallelism();
	}

	@Override
	public void shutdown()
	{
	}

	@Override
	public List< Runnable > shutdownNow()
	{
		throw new UnsupportedOperationException( "ForkJoinExecutorService, shutdownNow is not implemented." );
	}

	@Override
	public boolean isShutdown()
	{
		return false;
	}

	@Override
	public boolean isTerminated()
	{
		return false;
	}

	@Override
	public boolean awaitTermination( long l, TimeUnit timeUnit ) throws
			InterruptedException
	{
		// NB: it's possible to implement this method. One might use a set of weak references to collect all tasks submitted.
		// And this method call ForkJoinTask.get( long, timeUnit), to get the timing correct.
		// But doing so introduces reduced performance, as the set of tasks needs to be managed.
		// It's simpler to not use await termination at all.
		// Alternative is to collect the futures and call get on them.
		throw new UnsupportedOperationException( "ForkJoinExecutorService, awaitTermination is not implemented." );
	}

	@Override
	public < T > List< Future< T > > invokeAll( Collection< ? extends Callable< T > > collection ) throws
			InterruptedException
	{
		// TODO: Revisit if we ever drop support for Java 8.
		//  For Java 11, the code below could be replaced by
		//    return getPool().invokeAll( collection );
		//  For Java 8, this throws
		//    RejectedExecutionException: Thread limit exceeded replacing blocked worker
		//  Also revisit the submit/execute methods below.
		//  See https://github.com/imglib/imglib2/pull/269#discussion_r326855353
		List< ForkJoinTask< T > > futures = new ArrayList<>( collection.size() );
		for ( Callable< T > callable : collection )
			futures.add( ForkJoinTask.adapt( callable ) );
		ForkJoinTask.invokeAll( futures );
		return Collections.unmodifiableList( futures );
	}

	@Override
	public < T > List< Future< T > > invokeAll( Collection< ? extends Callable< T > > collection, long l, TimeUnit timeUnit ) throws
			InterruptedException
	{
		throw new UnsupportedOperationException( "ForkJoinExecutorService, invokeAll with timeout is not implemented." );
	}

	@Override
	public Future< ? > submit( Runnable runnable )
	{
		return ForkJoinTask.adapt( runnable ).fork();
	}

	@Override
	public < T > Future< T > submit( Runnable runnable, T t )
	{
		return ForkJoinTask.adapt( runnable, t ).fork();
	}

	@Override
	public < T > Future< T > submit( Callable< T > callable )
	{
		return ForkJoinTask.adapt( callable ).fork();
	}

	@Override
	public void execute( Runnable runnable )
	{
		ForkJoinTask.adapt( runnable ).fork();
	}

	private ForkJoinPool getPool()
	{
		ForkJoinPool pool = ForkJoinTask.getPool();
		return pool != null ? pool : ForkJoinPool.commonPool();
	}
}
