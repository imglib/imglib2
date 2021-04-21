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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A {@link TaskExecutor} for single threaded execution.
 */
class SequentialTaskExecutor implements TaskExecutor
{
	private static final SequentialTaskExecutor INSTANCE = new SequentialTaskExecutor();

	private final ExecutorService executorService = new SequentialExecutorService();

	private SequentialTaskExecutor()
	{
		// Only one instance of the sequential task executor is needed.
	}

	public static TaskExecutor getInstance()
	{
		return INSTANCE;
	}

	@Override
	public ExecutorService getExecutorService()
	{
		return executorService;
	}

	@Override
	public int suggestNumberOfTasks()
	{
		return 1;
	}

	@Override
	public int getParallelism()
	{
		return 1;
	}

	@Override
	public void runAll( List< Runnable > tasks )
	{
		for ( Runnable task : tasks )
			task.run();
	}

	@Override
	public < T > void forEach( List< ? extends T > parameters, Consumer< ? super T > task )
	{
		for( T value : parameters )
			task.accept( value );
	}

	@Override
	public < T, R > List< R > forEachApply( List< ? extends T > parameters, Function< ? super T, ? extends R > task )
	{
		final List< R > results = new ArrayList<>( parameters.size() );
		for ( final T value : parameters )
		{
			R result = task.apply( value );
			results.add( result );
		}
		return results;
	}

	@Override
	public void close()
	{
		// no resources that need to be cleaned up
	}
}
