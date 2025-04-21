/*-
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
package net.imglib2.util;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StopWatchTest
{
	@Ignore // NB: This is not a test. But it's a short demo how StopWatch could be used during debugging.
	@Test
	public void demo() throws InterruptedException
	{
		StopWatch sw = StopWatch.createAndStart();
		Thread.sleep( 42 );
		System.out.println( sw );
	}

	@Ignore // NB: Time measurement depends on side effects. The test might sometimes fail. Better ignore to avoid confusion.
	@Test
	public void testCreateAndStart() throws InterruptedException
	{
		StopWatch sw = StopWatch.createAndStart();
		Thread.sleep( 42 );
		long nanoTime = sw.nanoTime();
		assertEquals( 42e6, nanoTime, 1e6);
	}

	@Ignore // NB: Time measurement depends on side effects. The test might sometimes fail. Better ignore it to avoid confusion.
	@Test
	public void testStartStop() throws InterruptedException
	{
		StopWatch sw = StopWatch.createStopped();
		Thread.sleep( 10 );
		sw.start();
		Thread.sleep( 10 );
		sw.stop();
		Thread.sleep( 10 );
		long nanoTime = sw.nanoTime();
		assertEquals( 10e6, nanoTime, 1e6);
	}

	@Ignore // NB: Time measurement depends on side effects. The test might sometimes fail. Better ignore it to avoid confusion.
	@Test
	public void testSeconds() throws InterruptedException
	{
		StopWatch sw = StopWatch.createAndStart();
		Thread.sleep( 42 );
		double time = sw.seconds();
		assertEquals( 0.042, time, 0.010);
	}

	// TODO: Fix or remove this test. It may fail depending on locale, e.g., secondsToString(42) will give "42,000 s" for de.
	public void testSecondsToString() {
		assertEquals("42.000 s", StopWatch.secondsToString(42));
		assertEquals("42.000 ms", StopWatch.secondsToString(42e-3));
		assertEquals("42.000 \u00b5s", StopWatch.secondsToString(42e-6));
		assertEquals("42.000 ns", StopWatch.secondsToString(42e-9));
		assertEquals("-42.000 ms", StopWatch.secondsToString(-42e-3));
		assertEquals("42.000 s", StopWatch.secondsToString(42.00000000001));
	}
}
