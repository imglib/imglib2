/*-
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2025 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
package net.imglib2.view;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.iterator.LocalizingIntervalIterator;
import net.imglib2.loops.LoopBuilder;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.util.IntervalIndexer;
import net.imglib2.util.Intervals;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * Measure the cost of looping over a reshaped view ({@link Views#reshape}),
 * relative to looping over the {@link RandomAccessibleInterval} it wraps. The
 * operation performed at each element is a plain increment, so that what is
 * measured is the loop and the view, not the operation.
 * <p>
 * Two axes are swept. {@code imgType} selects the source image
 * ({@code ArrayImg} or {@code CellImg}), and {@code viewShape} selects what is
 * put on top of it:
 * <ul>
 * <li>{@code none} &mdash; the image itself, no view. This is the baseline.</li>
 * <li>{@code flat1d}, {@code merge2d}, {@code identity3d}, {@code split4d}
 * &mdash; reshapes to 1, 2, 3 and 4 dimensions. {@code flat1d} merges all
 * axes, {@code merge2d} merges the first two, {@code split4d} splits the last
 * axis. {@code identity3d} is removed by {@code TransformBuilder}.</li>
 * <li>{@code singleton4d} &mdash; inserts a size-1 axis, which
 * {@link Views#reshape} expresses as a {@code MixedTransform}.</li>
 * <li>{@code translate3d} &mdash; not a reshape, but {@code Views.translate},
 * for comparison with another {@code MixedTransform} view.</li>
 * <li>{@code noneCrop}, {@code merge2dCrop}, {@code general3dCrop} &mdash;
 * one element is cropped off each side of every axis, such that the elements
 * are not a contiguous run of the source. Cursors and {@code LoopBuilder} then
 * move a {@code ReshapeRandomAccess} incrementally. {@code noneCrop} crops the
 * image itself, and is the baseline for the others. {@code general3dCrop} crops
 * {@code general3d} (not benchmarked separately), a reshape to
 * {@code (2 * d0, d1 / 2, d2)}, whose first group has two axes on both sides
 * (merges two source axes then splits into two target axes).</li>
 * </ul>
 * <p>
 * Note when reading the numbers: reshape preserves flat iteration order, so
 * walking a reshaped view in flat order walks the source in flat order too.
 * Memory locality is therefore just as good for the reshaped rows as for the
 * baseline, and any slowdown they show is arithmetic and indirection rather
 * than cache behaviour.
 */
@State( Scope.Benchmark )
@Warmup( iterations = 5, time = 100, timeUnit = TimeUnit.MILLISECONDS )
@Measurement( iterations = 5, time = 200, timeUnit = TimeUnit.MILLISECONDS )
@BenchmarkMode( Mode.AverageTime )
@OutputTimeUnit( TimeUnit.MILLISECONDS )
@Fork( 1 )
public class ReshapeViewBenchmark
{
	/** 3,600,000 elements, ~14 MB as {@code int} -- comfortably out of cache. */
	static final long[] DIMS = new long[] { 300, 300, 40 };

	/** Deliberately ragged against {@link #DIMS}. */
	static final int[] CELL_DIMS = new int[] { 64, 64, 8 };

	/** {@code split4d} splits the last axis into {@code dim / SPLIT} and {@code SPLIT}. */
	private static final long SPLIT = 5;

	static final String[] IMG_TYPES = { "ArrayImg", "CellImg" };

	private static final String[] VIEW_SHAPES = { "none", "flat1d", "merge2d", "identity3d", "split4d", "singleton4d", "translate3d", "noneCrop", "merge2dCrop", "general3dCrop" };

	@Param( { "ArrayImg", "CellImg" } )
	String imgType;

	@Param( { "none", "flat1d", "merge2d", "identity3d", "split4d", "singleton4d", "translate3d", "noneCrop", "merge2dCrop", "general3dCrop" } )
	String viewShape;

	private RandomAccessibleInterval< IntType > subject;

	@Setup
	public void setup()
	{
		subject = createSubject( imgType, viewShape, DIMS, CELL_DIMS, SPLIT );
	}

	@Benchmark
	public void flatIterableCursor()
	{
		flatIterableCursorLoop( subject );
	}

	@Benchmark
	public void rawCursor()
	{
		rawCursorLoop( subject );
	}

	@Benchmark
	public void loopBuilder()
	{
		loopBuilderLoop( subject );
	}

	@Benchmark
	public void randomAccessWalk()
	{
		randomAccessLoop( subject );
	}

	/*
	 * The loops. Each is a static method so that the benchmark methods and
	 * selfCheck() run exactly the same code.
	 *
	 * No Blackhole is needed: every loop stores into an array that stays
	 * reachable from the subject, which the JIT cannot eliminate. The stored
	 * values do drift upwards across invocations and eventually wrap at 2^31,
	 * which is harmless, and saves a per-invocation reset.
	 */

	/** The canonical "iterate an arbitrary RAI" idiom, and what {@code ReshapeViewTest} uses. */
	static void flatIterableCursorLoop( final RandomAccessibleInterval< IntType > rai )
	{
		final Cursor< IntType > c = Views.flatIterable( rai ).cursor();
		while ( c.hasNext() )
			c.next().inc();
	}

	/**
	 * What a caller naively gets. For a reshaped view this goes through
	 * {@code IntervalView.cursor()} to {@code IterableTransformBuilder}, which
	 * does not guarantee flat order; comparing against
	 * {@link #flatIterableCursorLoop} shows whether that helps or hurts.
	 */
	static void rawCursorLoop( final RandomAccessibleInterval< IntType > rai )
	{
		final Cursor< IntType > c = Views.iterable( rai ).cursor();
		while ( c.hasNext() )
			c.next().inc();
	}

	static void loopBuilderLoop( final RandomAccessibleInterval< IntType > rai )
	{
		LoopBuilder.setImages( rai ).forEachPixel( t -> t.inc() );
	}

	/** Drives a {@link RandomAccess} over every position, in flat order. */
	static void randomAccessLoop( final RandomAccessibleInterval< IntType > rai )
	{
		final LocalizingIntervalIterator it = new LocalizingIntervalIterator( rai );
		final RandomAccess< IntType > ra = rai.randomAccess( rai );
		while ( it.hasNext() )
		{
			it.fwd();
			ra.setPosition( it );
			ra.get().inc();
		}
	}

	static void runLoop( final String loop, final RandomAccessibleInterval< IntType > rai )
	{
		switch ( loop )
		{
		case "flatIterableCursor":
			flatIterableCursorLoop( rai );
			break;
		case "rawCursor":
			rawCursorLoop( rai );
			break;
		case "loopBuilder":
			loopBuilderLoop( rai );
			break;
		case "randomAccessWalk":
			randomAccessLoop( rai );
			break;
		default:
			throw new IllegalArgumentException( "unknown loop: " + loop );
		}
	}

	/*
	 * Subject construction.
	 */

	static Img< IntType > createImg( final String imgType, final long[] dims, final int[] cellDims )
	{
		switch ( imgType )
		{
		case "ArrayImg":
			return new ArrayImgFactory<>( new IntType() ).create( dims );
		case "CellImg":
			return new CellImgFactory<>( new IntType(), cellDims ).create( dims );
		default:
			throw new IllegalArgumentException( "unknown imgType: " + imgType );
		}
	}

	static long[] viewDims( final String viewShape, final long[] dims, final long split )
	{
		final int n = dims.length;
		switch ( viewShape )
		{
		case "none":
			return dims.clone();
		case "general3d":
			if ( dims[ 1 ] % 2 != 0 )
				throw new IllegalArgumentException( "dimension 1 must be even" );
			final long[] g = dims.clone();
			g[ 0 ] *= 2;
			g[ 1 ] /= 2;
			return g;
		case "flat1d":
			return new long[] { Intervals.numElements( dims ) };
		case "merge2d":
			final long[] m = new long[ n - 1 ];
			m[ 0 ] = dims[ 0 ] * dims[ 1 ];
			System.arraycopy( dims, 2, m, 1, n - 2 );
			return m;
		case "identity3d":
			return dims.clone();
		case "split4d":
			final long[] d = new long[ n + 1 ];
			System.arraycopy( dims, 0, d, 0, n - 1 );
			d[ n - 1 ] = dims[ n - 1 ] / split;
			d[ n ] = split;
			return d;
		case "singleton4d":
			// a size-1 axis inserted before the last: expressible as a MixedTransform
			final long[] e = new long[ n + 1 ];
			System.arraycopy( dims, 0, e, 0, n - 1 );
			e[ n - 1 ] = 1;
			e[ n ] = dims[ n - 1 ];
			return e;
		default:
			throw new IllegalArgumentException( "unknown viewShape: " + viewShape );
		}
	}

	static RandomAccessibleInterval< IntType > createSubject( final String imgType, final String viewShape,
			final long[] dims, final int[] cellDims, final long split )
	{
		return wrap( createImg( imgType, dims, cellDims ), viewShape, dims, split );
	}

	static RandomAccessibleInterval< IntType > wrap( final Img< IntType > img, final String viewShape, final long[] dims, final long split )
	{
		if ( isCrop( viewShape ) )
			return Views.interval( wrap( img, uncropped( viewShape ), dims, split ),
					cropInterval( viewDims( uncropped( viewShape ), dims, split ) ) );

		switch ( viewShape )
		{
		case "none":
			return img;
		case "translate3d":
			return Views.translate( img, 1, 2, 3 );
		default:
			return Views.reshape( img, viewDims( viewShape, dims, split ) );
		}
	}

	private static final String CROP_SUFFIX = "Crop";

	private static boolean isCrop( final String viewShape )
	{
		return viewShape.endsWith( CROP_SUFFIX );
	}

	private static String uncropped( final String viewShape )
	{
		return viewShape.substring( 0, viewShape.length() - CROP_SUFFIX.length() );
	}

	/** Crop one element off each side of every axis. */
	private static Interval cropInterval( final long[] viewDims )
	{
		return Intervals.expand( new FinalInterval( viewDims ), -1 );
	}

	/*
	 * Self check.
	 */

	/**
	 * A loop that silently visited nothing would report a spectacular and
	 * entirely fake speedup. Run every (imgType, viewShape, loop) combination
	 * once over a small image and check that each element of the view was
	 * incremented exactly once, and no other element of the image was touched.
	 */
	static void selfCheck()
	{
		final long[] dims = { 5, 8, 6 };
		final int[] cellDims = { 2, 3, 4 };
		for ( final String imgType : IMG_TYPES )
			for ( final String viewShape : VIEW_SHAPES )
				for ( final String loop : new String[] { "flatIterableCursor", "rawCursor", "loopBuilder", "randomAccessWalk" } )
				{
					final Img< IntType > img = createImg( imgType, dims, cellDims );
					final RandomAccessibleInterval< IntType > s = wrap( img, viewShape, dims, 2 );
					runLoop( loop, s );
					checkIncrements( img, expectedIncrements( viewShape, dims, 2 ), imgType + " / " + viewShape + " / " + loop );
				}
		System.out.println( "selfCheck: OK" );
	}

	/**
	 * For each element of the image (in flat order), how often it should be
	 * incremented. Computed from flat indices, without using any views.
	 */
	private static int[] expectedIncrements( final String viewShape, final long[] dims, final long split )
	{
		final int[] expected = new int[ ( int ) Intervals.numElements( dims ) ];
		if ( !isCrop( viewShape ) )
		{
			Arrays.fill( expected, 1 );
			return expected;
		}
		final long[] viewDims = viewDims( uncropped( viewShape ), dims, split );
		final LocalizingIntervalIterator it = new LocalizingIntervalIterator( cropInterval( viewDims ) );
		final long[] pos = new long[ viewDims.length ];
		while ( it.hasNext() )
		{
			it.fwd();
			it.localize( pos );
			expected[ ( int ) IntervalIndexer.positionToIndex( pos, viewDims ) ] = 1;
		}
		return expected;
	}

	private static void checkIncrements( final Img< IntType > img, final int[] expected, final String what )
	{
		final Cursor< IntType > c = Views.flatIterable( img ).cursor();
		for ( int i = 0; i < expected.length; ++i )
		{
			final int v = c.next().get();
			if ( v != expected[ i ] )
				throw new AssertionError( what + ": element " + i + " is " + v + ", expected " + expected[ i ] );
		}
	}

	public static void main( String... args ) throws RunnerException
	{
		selfCheck();
		final Options options = new OptionsBuilder()
				.include( ReshapeViewBenchmark.class.getSimpleName() ).build();
		new Runner( options ).run();
	}
}
