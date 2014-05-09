/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
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
package net.imglib2.ui.util;

/**
 * Utility class to measure time differences in nano-seconds, based on
 * {@link System#nanoTime()}. It compensates glitches in
 * {@link System#nanoTime()}, such that the stop time can never be earlier than
 * the start time.
 * 
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class StopWatch
{
	private long time;

	private long total;

	private long started;

	private boolean running;

	/**
	 * Construct new {@link StopWatch}. It is not running initially. Call
	 * {@link #start()} to start timing.
	 */
	public StopWatch()
	{
		time = System.nanoTime();
		total = 0;
		started = 0;
		running = false;
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
}
