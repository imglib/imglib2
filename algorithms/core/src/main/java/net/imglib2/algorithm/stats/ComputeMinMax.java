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

package net.imglib2.algorithm.stats;

import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.Algorithm;
import net.imglib2.algorithm.Benchmark;
import net.imglib2.algorithm.MultiThreaded;
import net.imglib2.multithreading.Chunk;
import net.imglib2.multithreading.SimpleMultiThreading;
import net.imglib2.type.Type;
import net.imglib2.util.Util;
import net.imglib2.view.Views;

/**
 * TODO
 * 
 * @author Stephan Preibisch
 */
public class ComputeMinMax< T extends Type< T > & Comparable< T >> implements Algorithm, MultiThreaded, Benchmark
{
	/**
	 * Computes minimal and maximal value in a given interval
	 * 
	 * @param interval
	 * @param min
	 * @param max
	 */
	final public static < T extends Comparable< T > & Type< T > > void computeMinMax( final RandomAccessibleInterval< T > interval, final T min, final T max )
	{
		final ComputeMinMax< T > c = new ComputeMinMax< T >( Views.iterable( interval ), min, max );
		c.process();

		min.set( c.getMin() );
		max.set( c.getMax() );
	}

	final IterableInterval< T > image;

	final T min, max;

	String errorMessage = "";

	int numThreads;

	long processingTime;

	public ComputeMinMax( final IterableInterval< T > interval, final T min, final T max )
	{
		setNumThreads();

		this.image = interval;

		this.min = min;
		this.max = max;
	}

	public T getMin()
	{
		return min;
	}

	public T getMax()
	{
		return max;
	}

	@Override
	public boolean process()
	{
		final long startTime = System.currentTimeMillis();

		final long imageSize = image.size();

		final AtomicInteger ai = new AtomicInteger( 0 );
		final Thread[] threads = SimpleMultiThreading.newThreads( getNumThreads() );

		final Vector< Chunk > threadChunks = SimpleMultiThreading.divideIntoChunks( imageSize, numThreads );
		final Vector< T > minValues = new Vector< T >();
		final Vector< T > maxValues = new Vector< T >();

		for ( int ithread = 0; ithread < threads.length; ++ithread )
		{
			minValues.add( image.firstElement().createVariable() );
			maxValues.add( image.firstElement().createVariable() );

			threads[ ithread ] = new Thread( new Runnable()
			{
				@Override
				public void run()
				{
					// Thread ID
					final int myNumber = ai.getAndIncrement();

					// get chunk of pixels to process
					final Chunk myChunk = threadChunks.get( myNumber );

					// compute min and max
					compute( myChunk.getStartPosition(), myChunk.getLoopSize(), minValues.get( myNumber ), maxValues.get( myNumber ) );

				}
			} );
		}

		SimpleMultiThreading.startAndJoin( threads );

		// compute overall min and max
		min.set( minValues.get( 0 ) );
		max.set( maxValues.get( 0 ) );

		for ( int i = 0; i < threads.length; ++i )
		{
			T value = minValues.get( i );
			if ( Util.min( min, value ) == value )
				min.set( value );

			value = maxValues.get( i );
			if ( Util.max( max, value ) == value )
				max.set( value );
		}

		processingTime = System.currentTimeMillis() - startTime;

		return true;
	}

	protected void compute( final long startPos, final long loopSize, final T min, final T max )
	{
		final Cursor< T > cursor = image.cursor();

		// init min and max
		cursor.fwd();

		min.set( cursor.get() );
		max.set( cursor.get() );

		cursor.reset();

		// move to the starting position of the current thread
		cursor.jumpFwd( startPos );

		// do as many pixels as wanted by this thread
		for ( long j = 0; j < loopSize; ++j )
		{
			cursor.fwd();

			final T value = cursor.get();

			if ( Util.min( min, value ) == value )
				min.set( value );

			if ( Util.max( max, value ) == value )
				max.set( value );
		}
	}

	@Override
	public boolean checkInput()
	{
		if ( errorMessage.length() > 0 )
		{
			return false;
		}
		else if ( image == null )
		{
			errorMessage = "ScaleSpace: [Image<A> img] is null.";
			return false;
		}
		else
			return true;
	}

	@Override
	public long getProcessingTime()
	{
		return processingTime;
	}

	@Override
	public void setNumThreads()
	{
		this.numThreads = Runtime.getRuntime().availableProcessors();
	}

	@Override
	public void setNumThreads( final int numThreads )
	{
		this.numThreads = numThreads;
	}

	@Override
	public int getNumThreads()
	{
		return numThreads;
	}

	@Override
	public String getErrorMessage()
	{
		return errorMessage;
	}
}
