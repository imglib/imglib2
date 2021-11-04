/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2021 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package net.imglib2.loops;

import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.loops.IntervalChunks;
import net.imglib2.loops.LoopBuilder;
import net.imglib2.parallel.Parallelization;
import net.imglib2.parallel.TaskExecutor;
import net.imglib2.parallel.TaskExecutors;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Similar to {@link LoopBuilder}, but expects the first image to be an {@link IterableInterval}
 * and the other images to be {@link RandomAccessible}.
 * <p>
 * Please note: It is usually preferable to use LoopBuilder as it often has a better performance,
 * and using {@link net.imglib2.RandomAccessibleInterval RandomAccessibleInterval} for all images is simpler.
 * <p>
 * Here is an usage example, that calculates the sum of two images:
 *
 * <pre>
 * {@code
 * IterableInterval<DoubleType> sum = ...
 * RandomAccessible<DoubleType> imageA = ...
 * RandomAccessible<DoubleType> imageB = ...
 *
 * IterableLoopBuilder.setImages(sum, imageA, imageB).forEachPixel(
 *     (s, a, b) -> {
 *          s.setReal(a.getRealDouble() + b.getRealDouble());
 *     }
 * );
 * }
 * </pre>
 *
 * @see LoopBuilder
 */
public class IterableLoopBuilder< T >
{

	private TaskExecutor taskExecutor = TaskExecutors.singleThreaded();

	private final IterableInterval< ? > firstImage;

	private final List< RandomAccessible< ? > > otherImages;

	private IterableLoopBuilder( IterableInterval< ? > firstImage, RandomAccessible< ? > ... otherImages )
	{
		this.firstImage = firstImage;
		this.otherImages = Arrays.asList( otherImages );
	}

	/**
	 * Set the image to loop over.
	 */
	public static < A > IterableLoopBuilder< Consumer< A > > setImages( IterableInterval< A > a )
	{
		return new IterableLoopBuilder<>( a );
	}

	/**
	 * Sets the images to loop over.
	 */
	public static < A, B > IterableLoopBuilder< BiConsumer< A, B > > setImages( IterableInterval< A > a, RandomAccessible< B > b )
	{
		return new IterableLoopBuilder<>( a, b );
	}

	/**
	 * Sets the images to loop over.
	 */
	public static < A, B, C > IterableLoopBuilder< LoopBuilder.TriConsumer< A, B, C > > setImages( IterableInterval< A > a, RandomAccessible< B > b, RandomAccessible< C > c )
	{
		return new IterableLoopBuilder<>( a, b, c );
	}

	/**
	 * Executes the given action pixel wise for the given images.
	 */
	public void forEachPixel( T action )
	{
		forEachChunk( chunk -> {
			chunk.forEachPixel( action );
			return null;
		} );
	}

	/**
	 * @see LoopBuilder#forEachChunk
	 */
	public < R > List< R > forEachChunk( Function< LoopBuilder.Chunk< T >, R > chunkAction )
	{
		List< Interval > intervals = IntervalChunks.chunkInterval( new FinalInterval( firstImage.size() ), taskExecutor.suggestNumberOfTasks() );
		List< Chunk< T > > chunks = ListUtils.map( interval -> new Chunk< T >( firstImage, otherImages, interval ), intervals );
		return taskExecutor.forEachApply( chunks, chunkAction );
	}

	/**
	 * This will cause the loop to be executed in a multi threaded fashion.
	 * Details can be set using the {@link Parallelization} framework.
	 */
	public IterableLoopBuilder< T > multithreaded()
	{
		return multithreaded( Parallelization.getTaskExecutor() );
	}

	/**
	 * This will cause the loop to be executed in a multi threaded fashion.
	 * The give {@link TaskExecutor} will be used for multi threading.
	 */
	public IterableLoopBuilder< T > multithreaded( TaskExecutor taskExecutor )
	{
		this.taskExecutor = taskExecutor;
		return this;
	}

	private static class Chunk< T > implements LoopBuilder.Chunk< T > {

		private final IterableInterval< ? > firstImage;

		private final List< RandomAccessible< ? > > otherImages;

		private final Interval interval;

		private Chunk( IterableInterval< ? > firstImage, List< RandomAccessible< ? > > otherImages, Interval interval )
		{
			this.firstImage = firstImage;
			this.otherImages = otherImages;
			this.interval = interval;
		}

		@Override
		public void forEachPixel( T action )
		{
			Cursor< ? > cursor = firstImage.localizingCursor();
			List< RandomAccess< ? > > randomAccesses = otherImages.stream().map( RandomAccessible::randomAccess ).collect( Collectors.toList());
			cursor.jumpFwd( interval.min( 0 ) );
			long size = interval.dimension( 0 );
			FastCursorRandomAccessLoops.loop( action, size, cursor, randomAccesses );
		}
	}

}
