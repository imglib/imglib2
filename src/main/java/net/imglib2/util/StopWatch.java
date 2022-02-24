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
package net.imglib2.util;

import java.text.DecimalFormat;

/**
 * Utility class to measure time differences in nano-seconds, based on
 * {@link System#nanoTime()}. It compensates glitches in
 * {@link System#nanoTime()}, such that the stop time can never be earlier than
 * the start time. (For example, see
 * <a href="http://stackoverflow.com/a/8854104">stackoverflow</a>)
 *
 * @author Tobias Pietzsch
 */
public class StopWatch
{
	private long time;

	private long total;

	private long started;

	private boolean running;

	/**
	 * Use {@link #createStopped()} or {@link #createAndStart()} to create a StopWatch.
	 * This constructor will also create a StopWatch, just like {@link #createStopped()},
	 * but the more expressively named factory methods are preferred.
	 */
	private StopWatch()
	{
		time = System.nanoTime();
		total = 0;
		started = 0;
		running = false;
	}

	/**
	 * Construct new {@link StopWatch}. It is not running initially. Call
	 * {@link #start()} to start timing.
	 */
	public static StopWatch createStopped()
	{
		return new StopWatch();
	}

	/**
	 * Construct and start a new {@link StopWatch}.
	 */
	public static StopWatch createAndStart()
	{
		StopWatch sw = new StopWatch();
		sw.start();
		return sw;
	}

	private long safeNanos()
	{
		final long t = System.nanoTime();
		if ( t > time )
			time = t;
		return time;
	}

	/**
	 * Start the clock.
	 */
	public synchronized void start()
	{
		if ( running )
			stop();
		started = safeNanos();
		running = true;
	}

	/**
	 * Stop the clock.
	 */
	public synchronized void stop()
	{
		if ( running )
			total += safeNanos() - started;
		running = false;
	}

	/**
	 * Get the total time the clock was running, in nano-seconds. Note that the
	 * clock can be started and stopped multiple times, accumulating the time
	 * intervals it was running in between.
	 *
	 * @return the total time the clock was running, in nano-seconds.
	 */
	public synchronized long nanoTime()
	{
		if ( running )
			return total + safeNanos() - started;
		else
			return total;
	}

	/**
	 * Get the total time the clock was running in seconds. Note that the
	 * clock can be started and stopped multiple times, accumulating the time
	 * intervals it was running in between.
	 */
	public double seconds()
	{
		return nanoTime() * 1e-9;
	}

	/**
	 * Get the total time the clock was running as string. Note that the
	 * clock can be started and stopped multiple times, accumulating the time
	 * intervals it was running in between.
	 */
	@Override
	public String toString()
	{
		return secondsToString( seconds() );
	}

	private static DecimalFormat format = new DecimalFormat( "#0.000" );

	public static String secondsToString( double seconds )
	{
		double abs = Math.abs( seconds );
		if ( abs < 1e-6 )
			return format.format( seconds * 1e9 ) + " ns";
		if ( abs < 1e-3 )
			return format.format( seconds * 1e6 ) + " \u00b5s";
		if ( abs < 1 )
			return format.format( seconds * 1e3 ) + " ms";
		return format.format( seconds ) + " s";
	}
}
