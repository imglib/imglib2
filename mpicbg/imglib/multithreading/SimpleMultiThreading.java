package mpicbg.imglib.multithreading;

/**
 * <p>Title: MultiThreading </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * <p>License: GPL
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * @author Dave Hale, Stephan Preibisch
 * @version 1.0
 */

public class SimpleMultiThreading
{
	/*
	final int start = 0;
	final int end = 10;

	final AtomicInteger ai = new AtomicInteger(start);

	Thread[] threads = newThreads();
	for (int ithread = 0; ithread < threads.length; ++ithread)
	{
		threads[ithread] = new Thread(new Runnable()
		{
			public void run()
			{
				// do something....
				// for example:
				for (int i3 = ai.getAndIncrement(); i3 < end; i3 = ai.getAndIncrement())
				{
				}
			}
		});
	}
	startAndJoin(threads);
	*/

	public static void startTask(Runnable run)
	{
		Thread[] threads = newThreads();

		for (int ithread = 0; ithread < threads.length; ++ithread)
			threads[ithread] = new Thread(run);

		startAndJoin(threads);
	}

	public static void startTask(Runnable run, int numThreads)
	{
		Thread[] threads = newThreads(numThreads);

		for (int ithread = 0; ithread < threads.length; ++ithread)
			threads[ithread] = new Thread(run);

		startAndJoin(threads);
	}

	public static Thread[] newThreads()
	{
	  int nthread = Runtime.getRuntime().availableProcessors();
	  return new Thread[nthread];
	}

	public static Thread[] newThreads(int numThreads)
	{
	  return new Thread[numThreads];
	}

	public static void startAndJoin(Thread[] threads)
	{
		for (int ithread = 0; ithread < threads.length; ++ithread)
		{
			threads[ithread].setPriority(Thread.NORM_PRIORITY);
			threads[ithread].start();
		}

		try
		{
			for (int ithread = 0; ithread < threads.length; ++ithread)
				threads[ithread].join();
		} catch (InterruptedException ie)
		{
			throw new RuntimeException(ie);
		}
	}

	public static void start(Thread[] threads)
	{
		for (int ithread = 0; ithread < threads.length; ++ithread)
		{
			threads[ithread].setPriority(Thread.MIN_PRIORITY);
			threads[ithread].start();
		}
	}
	
	public static void threadHaltUnClean()
	{
		int i = 0;
		
		while ( i == 0 ) {}
	}
	
	public static void threadWait( final long milliseconds )
	{
		try
		{
			Thread.sleep( milliseconds );
		}
		catch (final InterruptedException e)
		{
			System.err.println("MultiThreading.threadWait(): Thread woken up: " + e );
		}		
	}
}
