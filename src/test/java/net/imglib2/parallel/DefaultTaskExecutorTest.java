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

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests {@link DefaultTaskExecutor}.
 */
public class DefaultTaskExecutorTest
{

	private final DefaultTaskExecutor sequential = new DefaultTaskExecutor( new SequentialExecutorService() );

	private final DefaultTaskExecutor twoThreads = new DefaultTaskExecutor( new ForkJoinPool( 2 ) );

	@Test
	public void testGetParallelism()
	{
		testGetParallelism( 1, new SequentialExecutorService() );
		testGetParallelism( 2, Executors.newFixedThreadPool( 2 ) );
		testGetParallelism( 3, new ForkJoinPool( 3 ) );
		testGetParallelism( 1, Executors.newCachedThreadPool() );
		testGetParallelism( ForkJoinPool.commonPool().getParallelism(), new ForkJoinExecutorService() );
	}

	private void testGetParallelism( int expectedParallelism, ExecutorService executorService )
	{
		TaskExecutor taskExecutor = new DefaultTaskExecutor( executorService );
		assertEquals( expectedParallelism, taskExecutor.getParallelism() );
	}

	@Test
	public void testSuggestNumberOfTasks()
	{
		assertEquals( 1, sequential.suggestNumberOfTasks() );
		assertEquals( 8, twoThreads.suggestNumberOfTasks() );
	}

	@Test
	public void testForEach()
	{
		AtomicInteger sum = new AtomicInteger();
		List< Integer > parameters = Arrays.asList( 1, 2, 3 );
		twoThreads.forEach( parameters, sum::addAndGet );
		assertEquals( 6, sum.get() );
	}

	@Test
	public void testForEachApply()
	{
		List< Integer > parameters = Arrays.asList( 1, 2, 3 );
		List< Integer > squared = twoThreads.forEachApply( parameters, i -> i * i );
		assertEquals( Arrays.asList( 1, 4, 9 ), squared );
	}

	@Test
	public void testRunAll()
	{
		AtomicInteger sum = new AtomicInteger();
		List< Runnable > tasks = Arrays.asList( () -> sum.addAndGet( 1 ),
				() -> sum.addAndGet( 2 ), () -> sum.addAndGet( 3 ) );
		twoThreads.runAll( tasks );
		assertEquals( 6, sum.get() );
	}


	@Test
	public void testExceptionHandling() {
		try
		{
			twoThreads.runAll( Collections.singletonList( () -> throwDummyException() ) );
			fail( "DefaultTaskExecutor.runAll() failed to rethrow the DummyException." );
		}
		catch ( DummyException e ) // expected exception
		{
			assertStackTraceContainsMethod( e, "testExceptionHandling" );
			assertStackTraceContainsMethod( e, "runAll" );
			assertStackTraceContainsMethod( e, "throwDummyException" );
		}
	}

	public void assertStackTraceContainsMethod( Exception e, String methodName )
	{
		StackTraceElement[] stack = e.getStackTrace();
		assertTrue( Stream.of( stack ).anyMatch( stackTraceElement -> stackTraceElement.getMethodName().equals( methodName ) ) );
	}

	public void throwDummyException()
	{
		throw new DummyException();
	}

	private static class DummyException extends RuntimeException
	{

	}
}
