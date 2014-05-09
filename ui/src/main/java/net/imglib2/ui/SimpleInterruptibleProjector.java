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
package net.imglib2.ui;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converter;
import net.imglib2.ui.util.StopWatch;

/**
 * An {@link InterruptibleProjector}, that renders a target 2D
 * {@link RandomAccessibleInterval} by copying values from a source
 * {@link RandomAccessible}. The source can have more dimensions than the
 * target. Target coordinate <em>(x,y)</em> is copied from source coordinate
 * <em>(x,y,0,...,0)</em>.
 * <p>
 * A specified number of threads is used for rendering.
 * 
 * @param <A>
 *            pixel type of the source {@link RandomAccessible}.
 * @param <B>
 *            pixel type of the target {@link RandomAccessibleInterval}.
 * 
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 * @author Stephan Saalfeld
 */
public class SimpleInterruptibleProjector< A, B > extends AbstractInterruptibleProjector< A, B >
{
	final protected RandomAccessible< A > source;

	/**
	 * Number of threads to use for rendering
	 */
	final protected int numThreads;

	/**
	 * Time needed for rendering the last frame, in nano-seconds.
	 */
	protected long lastFrameRenderNanoTime;

	/**
	 * Create new projector with the given source and a converter from source to
	 * target pixel type.
	 * 
	 * @param source
	 *            source pixels.
	 * @param converter
	 *            converts from the source pixel type to the target pixel type.
	 * @param target
	 *            the target interval that this projector maps to
	 * @param numThreads
	 *            how many threads to use for rendering.
	 */
	public SimpleInterruptibleProjector(
			final RandomAccessible< A > source,
			final Converter< ? super A, B > converter,
			final RandomAccessibleInterval< B > target,
			final int numThreads )
	{
		super( source.numDimensions(), converter, target );
		this.source = source;
		this.numThreads = numThreads;
		lastFrameRenderNanoTime = -1;
	}

	/**
	 * Render the 2D target image by copying values from the source. Source can
	 * have more dimensions than the target. Target coordinate <em>(x,y)</em> is
	 * copied from source coordinate <em>(x,y,0,...,0)</em>.
	 * 
	 * @param target
	 * @param numThreads
	 *            how many threads to use for rendering.
	 * @return true if rendering was completed (all target pixels written).
	 *         false if rendering was interrupted.
	 */
	@Override
	public boolean map()
	{
		interrupted.set( false );

		final StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		min[ 0 ] = target.min( 0 );
		min[ 1 ] = target.min( 1 );
		max[ 0 ] = target.max( 0 );
		max[ 1 ] = target.max( 1 );

		final long cr = -target.dimension( 0 );

		final int width = ( int ) target.dimension( 0 );
		final int height = ( int ) target.dimension( 1 );

		final ExecutorService ex = Executors.newFixedThreadPool( numThreads );
		final int numTasks;
		if ( numThreads > 1 )
		{
			numTasks = Math.min( numThreads * 10, height );
		}
		else
			numTasks = 1;
		final double taskHeight = ( double ) height / numTasks;
		for ( int taskNum = 0; taskNum < numTasks; ++taskNum )
		{
			final long myMinY = min[ 1 ] + ( int ) ( taskNum * taskHeight );
			final long myHeight = ( ( taskNum == numTasks - 1 ) ? height : ( int ) ( ( taskNum + 1 ) * taskHeight ) ) - myMinY - min[ 1 ];

			final Runnable r = new Runnable()
			{
				@Override
				public void run()
				{
					if ( interrupted.get() )
						return;

					final RandomAccess< A > sourceRandomAccess = source.randomAccess( SimpleInterruptibleProjector.this );
					final RandomAccess< B > targetRandomAccess = target.randomAccess( target );

					sourceRandomAccess.setPosition( min );
					sourceRandomAccess.setPosition( myMinY, 1 );
					targetRandomAccess.setPosition( min[ 0 ], 0 );
					targetRandomAccess.setPosition( myMinY, 1 );
					for ( int y = 0; y < myHeight; ++y )
					{
						if ( interrupted.get() )
							return;
						for ( int x = 0; x < width; ++x )
						{
							converter.convert( sourceRandomAccess.get(), targetRandomAccess.get() );
							sourceRandomAccess.fwd( 0 );
							targetRandomAccess.fwd( 0 );
						}
						sourceRandomAccess.move( cr, 0 );
						targetRandomAccess.move( cr, 0 );
						sourceRandomAccess.fwd( 1 );
						targetRandomAccess.fwd( 1 );
					}
				}
			};
			ex.execute( r );
		}
		ex.shutdown();
		try
		{
			ex.awaitTermination( 1, TimeUnit.HOURS );
		}
		catch ( final InterruptedException e )
		{
			e.printStackTrace();
		}

		lastFrameRenderNanoTime = stopWatch.nanoTime();

		return !interrupted.get();
	}

	protected AtomicBoolean interrupted = new AtomicBoolean();

	@Override
	public void cancel()
	{
		interrupted.set( true );
	}

	@Override
	public long getLastFrameRenderNanoTime()
	{
		return lastFrameRenderNanoTime;
	}
}
