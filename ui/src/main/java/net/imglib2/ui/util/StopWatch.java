package net.imglib2.ui.util;

public class StopWatch
{
	private long time;

	private long total;

	private long started;

	private boolean running;

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

	public synchronized void start()
	{
		if ( running )
			stop();
		started = safeNanos();
		running = true;
	}

	public synchronized void stop()
	{
		if ( running )
			total += safeNanos() - started;
		running = false;
	}

	public synchronized long nanoTime()
	{
		if ( running )
			return total + safeNanos() - started;
		else
			return total;
	}
}
