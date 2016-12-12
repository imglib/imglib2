/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2016 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package net.imglib2.cache;

/**
 * A {@link Thread} that tries to cleanup a {@link CacheReferenceQueue}
 * regularly.
 *
 * @author Stephan Saalfeld
 */
public class CacheReferenceQueueCleanupThread extends Thread
{
	final static public int DEFAULT_WAIT_TIME = 2000;
	final static public int DEFAULT_MAX_NUM_ENTRIES = Integer.MAX_VALUE;

	final protected CacheReferenceQueue< ?, ? > referenceQueue;

	/**
	 * wait time in milliseconds between two runs
	 */
	final int waitTime;

	/**
	 * max number of entries to clean up in each cycle
	 */
	final int maxNumEntries;

	public CacheReferenceQueueCleanupThread( CacheReferenceQueue< ?, ? > referenceQueue, final int waitTime, final int maxNumEntries )
	{
		this.referenceQueue = referenceQueue;
		this.waitTime = waitTime;
		this.maxNumEntries = maxNumEntries;
	}

	public CacheReferenceQueueCleanupThread( CacheReferenceQueue< ?, ? > referenceQueue, final int waitTime )
	{
		this( referenceQueue, waitTime, DEFAULT_MAX_NUM_ENTRIES );
	}


	public CacheReferenceQueueCleanupThread( CacheReferenceQueue< ?, ? > referenceQueue )
	{
		this( referenceQueue, DEFAULT_WAIT_TIME );
	}

	@Override
	public void run()
	{
		try
		{
			while ( !isInterrupted() )
			{
				referenceQueue.cleanUp( maxNumEntries );
				synchronized ( this )
				{
					wait( waitTime );
				}
			}
		}
		catch ( final InterruptedException e )
		{
			System.out.println( "CacheReferenceQueueCleanupThread " + this + " iterrupted!" );
		}
	}
}
