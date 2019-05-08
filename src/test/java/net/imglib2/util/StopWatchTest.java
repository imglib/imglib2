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

	@Test
	public void testSecondsToString() {
		assertEquals("42.000 s", StopWatch.secondsToString(42));
		assertEquals("42.000 ms", StopWatch.secondsToString(42e-3));
		assertEquals("42.000 \u00b5s", StopWatch.secondsToString(42e-6));
		assertEquals("42.000 ns", StopWatch.secondsToString(42e-9));
		assertEquals("-42.000 ms", StopWatch.secondsToString(-42e-3));
		assertEquals("42.000 s", StopWatch.secondsToString(42.00000000001));
	}
}
