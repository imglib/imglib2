/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2018 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.LongConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.imglib2.Cursor;
import net.imglib2.Dimensions;
import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.IterableInterval;
import net.imglib2.Positionable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.AbstractArrayCursor;
import net.imglib2.img.cell.CellCursor;
import net.imglib2.img.planar.PlanarCursor;
import net.imglib2.parallel.TaskExecutor;
import net.imglib2.parallel.Parallelization;
import net.imglib2.parallel.TaskExecutors;
import net.imglib2.util.Intervals;
import net.imglib2.view.Views;
import net.imglib2.view.iteration.SlicingCursor;

/**
 * {@link LoopBuilder} provides an easy way to write fast loops on
 * {@link RandomAccessibleInterval}s. For example, this is a loop that
 * calculates the sum of two images:
 * <p>
 * <pre>
 * {@code
 * RandomAccessibleInterval<DoubleType> imageA = ...
 * RandomAccessibleInterval<DoubleType> imageB = ...
 * RandomAccessibleInterval<DoubleType> sum = ...
 *
 * LoopBuilder.setImages(imageA, imageB, sum).forEachPixel(
 *     (a, b, s) -> {
 *          s.setReal(a.getRealDouble() + b.getRealDouble());
 *     }
 * );
 * }
 * </pre>
 * <p>
 * The {@link RandomAccessibleInterval}s {@code imageA}, {@code imageB} and
 * {@code sum} must have equal dimensions, but the bounds of there
 * {@link Intervals} can differ.
 *
 * @author Matthias Arzt
 */
public class LoopBuilder< T >
{

	// fields

	private final Dimensions dimensions;

	private final RandomAccessibleInterval< ? >[] images;

	private TaskExecutor taskExecutor = TaskExecutors.singleThreaded();

	private boolean useFlatIterationOrder = false;

	// public methods

	/**
	 * @see LoopBuilder
	 */
	public static < A > LoopBuilder< Consumer< A > > setImages( final RandomAccessibleInterval< A > a )
	{
		return new LoopBuilder<>( a );
	}

	/**
	 * @see LoopBuilder
	 */
	public static < A, B > LoopBuilder< BiConsumer< A, B > > setImages( final RandomAccessibleInterval< A > a, final RandomAccessibleInterval< B > b )
	{
		return new LoopBuilder<>( a, b );
	}

	/**
	 * @see LoopBuilder
	 */
	public static < A, B, C > LoopBuilder< TriConsumer< A, B, C > > setImages( final RandomAccessibleInterval< A > a, final RandomAccessibleInterval< B > b, final RandomAccessibleInterval< C > c )
	{
		return new LoopBuilder<>( a, b, c );
	}

	/**
	 * @see LoopBuilder
	 */
	public static < A, B, C, D > LoopBuilder< FourConsumer< A, B, C, D > > setImages( final RandomAccessibleInterval< A > a, final RandomAccessibleInterval< B > b, final RandomAccessibleInterval< C > c, final RandomAccessibleInterval< D > d )
	{
		return new LoopBuilder<>( a, b, c, d );
	}

	/**
	 * @see LoopBuilder
	 */
	public static < A, B, C, D, E > LoopBuilder< FiveConsumer< A, B, C, D, E > > setImages( final RandomAccessibleInterval< A > a, final RandomAccessibleInterval< B > b, final RandomAccessibleInterval< C > c, final RandomAccessibleInterval< D > d, final RandomAccessibleInterval< E > e )
	{
		return new LoopBuilder<>( a, b, c, d, e );
	}

	/**
	 * @see LoopBuilder
	 */
	public static < A, B, C, D, E, F > LoopBuilder< SixConsumer< A, B, C, D, E, F > > setImages( final RandomAccessibleInterval< A > a, final RandomAccessibleInterval< B > b, final RandomAccessibleInterval< C > c, final RandomAccessibleInterval< D > d, final RandomAccessibleInterval< E > e, final RandomAccessibleInterval< F > f )
	{
		return new LoopBuilder<>( a, b, c, d, e, f );
	}

	/**
	 * @see LoopBuilder
	 */
	public void forEachPixel( final T action )
	{
		Objects.requireNonNull( action );
		forEachChunk( chunk -> {
			chunk.forEachPixel( action );
			return null;
		} );
	}

	/**
	 * This method is similar to  {@link #forEachPixel} but more flexible when multi threading is used.
	 * <p>
	 * The following example calculates the sum of the pixel values of an image.
	 * Multi threading is used to improve performance. The image is split into chunks.
	 * The chunks are processed in parallel by multiple threads. A variable
	 * of {@code IntType} is used to calculate the sum, but {@code IntType} is not thread safe.
	 * It's therefore necessary to have one sum variable per chunk. This can be realized as follows:
	 *
	 * <pre>
	 * {@code
	 *
	 * List<IntType> listOfSums = LoopBuilder.setImages( image ).multithreaded().forEachChunk(
	 *     chunk -> {
	 *         IntType sum = new IntType();
	 *         chunk.forEach( pixel -> sum.add( pixel ) ):
	 *         return sum;
	 *     }
	 * );
	 *
	 * IntType totalSum = new IntType();
	 * listOfSums.forEach( sum -> totalSum.add( sum );
	 * return totalSum;
	 * }
	 * </pre>
	 */
	public < R > List< R > forEachChunk( final Function< Chunk< T >, R > action )
	{
		Objects.requireNonNull( action );
		if ( Intervals.numElements( dimensions ) == 0 )
			return Collections.emptyList();
		List< IterableInterval< ? > > iterableIntervals = imagesAsIterableIntervals();
		if ( allCursorsAreFast( iterableIntervals ) )
			return runUsingCursors( iterableIntervals, action );
		else
			return runUsingRandomAccesses( action );
	}

	private boolean allCursorsAreFast( List< IterableInterval< ? > > iterableIntervals )
	{
		return iterableIntervals.stream().allMatch( this::cursorIsFast );
	}

	private boolean cursorIsFast( IterableInterval< ? > image )
	{
		Cursor< ? > cursor = image.cursor();
		return cursor instanceof AbstractArrayCursor ||
				cursor instanceof SlicingCursor ||
				cursor instanceof PlanarCursor ||
				cursor instanceof CellCursor;
	}

	/**
	 * By default {@link LoopBuilder} runs the loop without multi-threading.
	 * Calling this method allows {@link LoopBuilder} to use multi-threading for optimal performance.
	 * <p>
	 * Usually, if this method is used, {@link LoopBuilder} will indeed
	 * use multi-threading. But that's not always the case.
	 * The {@link Parallelization} class can still be used to explicitly run the
	 * code single-threaded.
	 * <p>
	 * Here is a small example for a copy method with enabled multi-threading.
	 * <pre>
	 * {@code
	 *
	 * public void copy( RandomAccessibleInterval<T> source, RandomAccessibleInterval<T> target)
	 * {
	 *     LoopBuilder.setImages( source, target ).multiThreaded().forEachPixel( ( s, t ) -> t.set( s ) );
	 * }
	 * }
	 * </pre>
	 * This method usually runs multi-threaded. Which means good performance.
	 * But sometimes, a user might want to run the code single-threaded.
	 * There's no need to write a second single-threaded version of our copy method.
	 * The {@link Parallelization} class allows the user to run the code single-threaded:
	 * <pre>
	 * {@code
	 *
	 * Parallelization.runSingleThreaded( () -> {
	 *     copy( source, target );
	 * } );
	 * }
	 * </pre>
	 * WARNING: You need to make sure that the action passed to {@link #forEachPixel} is thread safe.
	 *
	 * @see Parallelization
	 */
	public LoopBuilder< T > multiThreaded()
	{
		return multiThreaded( Parallelization.getTaskExecutor() );
	}

	/**
	 * By default {@link LoopBuilder} runs the loop without multi-threading.
	 * Calling this method causes LoopBuilder to use the given {@link TaskExecutor} for multi-threading.
	 * <p>
	 * WARNING: You need to make sure that your operation is thread safe.
	 */
	public LoopBuilder< T > multiThreaded( TaskExecutor taskExecutor )
	{
		this.taskExecutor = Objects.requireNonNull( taskExecutor );
		return this;
	}

	/**
	 * {@link LoopBuilder} might use any iteration order to execute
	 * the loop. Calling this method will cause {@link LoopBuilder}
	 * to use flat iteration order, when executing the loop.
	 * <p>
	 * WARNING: Don't use multi-threading if you want to have flat
	 * iteration order.
	 */
	public LoopBuilder< T > flatIterationOrder()
	{
		return this.flatIterationOrder( true );
	}

	/**
	 * If false, {@link LoopBuilder} might use any iteration order
	 * to execute the loop.
	 * <p>
	 * If true, {@link LoopBuilder} will use
	 * flat iteration order, and multi threading is disabled.
	 * <p>
	 * WARNING: Don't use multi-threading if you want to have flat
	 * iteration order.
	 *
	 * @see net.imglib2.FlatIterationOrder
	 */
	public LoopBuilder< T > flatIterationOrder( boolean value )
	{
		this.useFlatIterationOrder = value;
		return this;
	}

	public interface TriConsumer< A, B, C >
	{
		void accept( A a, B b, C c );
	}

	public interface FourConsumer< A, B, C, D >
	{
		void accept( A a, B b, C c, D d );
	}

	public interface FiveConsumer< A, B, C, D, E >
	{
		void accept( A a, B b, C c, D d, E e );
	}

	public interface SixConsumer< A, B, C, D, E, F >
	{
		void accept( A a, B b, C c, D d, E e, F f );
	}

	public interface Chunk< T >
	{
		void forEachPixel( T action );
	}

	// Helper methods

	private LoopBuilder( final RandomAccessibleInterval< ? >... images )
	{
		this.images = images;
		this.dimensions = new FinalInterval( images[ 0 ] );
		checkDimensions();
	}

	private void checkDimensions()
	{
		final long[] dims = Intervals.dimensionsAsLongArray( dimensions );
		final boolean equal = Stream.of( images ).allMatch( image -> Arrays.equals( dims, Intervals.dimensionsAsLongArray( image ) ) );
		if ( !equal )
		{
			StringJoiner joiner = new StringJoiner( ", " );
			for ( Interval interval : images )
				joiner.add( Arrays.toString( Intervals.dimensionsAsLongArray( interval ) ) );
			throw new IllegalArgumentException( "LoopBuilder, image dimensions do not match: " + joiner + "." );
		}
	}

	private < R > List< R > runUsingRandomAccesses( Function< Chunk< T >, R > chunkAction )
	{
		final int nTasks = taskExecutor.suggestNumberOfTasks();
		final Interval interval = new FinalInterval( dimensions );
		final List< Interval > chunks = IntervalChunks.chunkInterval( interval, nTasks );
		return taskExecutor.forEachApply( chunks, chunk -> runOnChunkUsingRandomAccesses( images, chunkAction, chunk ) );
	}

	static < T, R > R runOnChunkUsingRandomAccesses( RandomAccessibleInterval[] images, Function< Chunk< T >, R > chunkAction, Interval subInterval )
	{
		final List< RandomAccess< ? > > samplers = Stream.of( images ).map( LoopBuilder::initRandomAccess ).collect( Collectors.toList() );
		final Positionable synced = SyncedPositionables.create( samplers );
		if ( !Views.isZeroMin( subInterval ) )
			synced.move( Intervals.minAsLongArray( subInterval ) );
		return chunkAction.apply( pixelAction -> {
			final Runnable runnable = BindActionToSamplers.bindActionToSamplers( pixelAction, samplers );
			LoopUtils.createIntervalLoop( synced, subInterval, runnable ).run();
		} );
	}

	private static RandomAccess< ? > initRandomAccess( final RandomAccessibleInterval< ? > image )
	{
		final RandomAccess< ? > ra = image.randomAccess();
		ra.setPosition( Intervals.minAsLongArray( image ) );
		return ra;
	}

	private List< IterableInterval< ? > > imagesAsIterableIntervals()
	{
		return useFlatIterationOrder ?
				flatIterableIntervals() :
				equalIterationOrderIterableIntervals();
	}

	private < R > List< R > runUsingCursors( List< IterableInterval< ? > > iterableIntervals, Function< Chunk< T >, R > chunkAction )
	{
		int nTasks = taskExecutor.suggestNumberOfTasks();
		final FinalInterval indices = new FinalInterval( Intervals.numElements( images[ 0 ] ) );
		List< Interval > chunks = IntervalChunks.chunkInterval( indices, nTasks );
		return taskExecutor.forEachApply( chunks, chunk ->
				LoopBuilder.runOnChunkUsingCursors( iterableIntervals, chunkAction, chunk.min( 0 ), chunk.dimension( 0 ) ) );
	}

	static < T, R > R runOnChunkUsingCursors( List< IterableInterval< ? > > iterableIntervals, Function< Chunk< T >, R > chunkAction, long offset, long numElements )
	{
		final List< Cursor< ? > > cursors = iterableIntervals.stream().map( IterableInterval::cursor ).collect( Collectors.toList() );
		if ( offset != 0 )
			jumpFwd( cursors, offset );
		return chunkAction.apply( pixelAction -> {
			LongConsumer cursorLoop = FastCursorLoops.createLoop( pixelAction, cursors );
			cursorLoop.accept( numElements );
		} );
	}

	private static void jumpFwd( List< Cursor< ? > > cursors, long offset )
	{
		for ( Cursor< ? > cursor : cursors )
			cursor.jumpFwd( offset );
	}

	private List< IterableInterval< ? > > equalIterationOrderIterableIntervals()
	{
		List< IterableInterval< ? > > iterableIntervals = Stream.of( images ).map( Views::iterable ).collect( Collectors.toList() );
		List< Object > iterationOrders = iterableIntervals.stream().map( IterableInterval::iterationOrder ).collect( Collectors.toList() );
		if ( allEqual( iterationOrders ) )
			return iterableIntervals;
		return flatIterableIntervals();
	}

	private List< IterableInterval< ? > > flatIterableIntervals()
	{
		return Stream.of( images ).map( Views::flatIterable ).collect( Collectors.toList() );
	}

	private static boolean allEqual( List< Object > values )
	{
		Object first = values.get( 0 );
		return values.stream().allMatch( first::equals );
	}
}
