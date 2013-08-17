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
