/*
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

package net.imglib2.blocks;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.img.planar.PlanarImgFactory;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.util.Intervals;
import net.imglib2.view.Views;

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
 * Benchmark copying blocks of 32768 elements from reshaped views of a {@code
 * 300x300x40} image with {@link PrimitiveBlocks}, compared to the {@link
 * FallbackPrimitiveBlocks} (which uses {@code LoopBuilder}).
 * <p>
 * An {@code ArrayImg} is reshaped directly (without {@link ReshapeCopier}).
 * For the other image types, {@code aligned} blocks map to boxes in the
 * image, while {@code straddling} blocks don't, and require gathering.
 */
@State( Scope.Benchmark )
@Warmup( iterations = 5, time = 100, timeUnit = TimeUnit.MILLISECONDS )
@Measurement( iterations = 5, time = 200, timeUnit = TimeUnit.MILLISECONDS )
@BenchmarkMode( Mode.AverageTime )
@OutputTimeUnit( TimeUnit.MICROSECONDS )
@Fork( 1 )
public class ReshapeBlocksBenchmark
{
	private static final long[] DIMS = { 300, 300, 40 };

	private static final int[] CELL_DIMS = { 64, 64, 8 };

	private static final String[] IMG_TYPES = { "ArrayImg", "PlanarImg", "CellImg" };

	private static final String[] SHAPES = { "none", "flat1d", "merge2d", "split4d" };

	private static final String[] BLOCKS = { "aligned", "straddling" };

	@Param( { "ArrayImg", "PlanarImg", "CellImg" } )
	String imgType;

	@Param( { "none", "flat1d", "merge2d", "split4d" } )
	String shape;

	@Param( { "aligned", "straddling" } )
	String block;

	@Param( { "view", "fallback" } )
	String impl;

	private PrimitiveBlocks< IntType > blocks;

	private Interval interval;

	private int[] dest;

	@Setup
	public void setup()
	{
		final RandomAccessibleInterval< IntType > view = createView( imgType, shape );
		blocks = createBlocks( view, impl );
		interval = createInterval( shape, block );
		dest = new int[ ( int ) Intervals.numElements( interval ) ];
	}

	@Benchmark
	public void copy()
	{
		blocks.copy( interval, dest );
	}

	static RandomAccessibleInterval< IntType > createView( final String imgType, final String shape )
	{
		final Img< IntType > img;
		switch ( imgType )
		{
		case "ArrayImg":
			img = new ArrayImgFactory<>( new IntType() ).create( DIMS );
			break;
		case "PlanarImg":
			img = new PlanarImgFactory<>( new IntType() ).create( DIMS );
			break;
		case "CellImg":
			img = new CellImgFactory<>( new IntType(), CELL_DIMS ).create( DIMS );
			break;
		default:
			throw new IllegalArgumentException( imgType );
		}
		int i = 0;
		for ( final IntType t : Views.flatIterable( img ) )
			t.set( i++ );
		switch ( shape )
		{
		case "none":
			return img;
		case "flat1d":
			return Views.reshape( img, 3600000 );
		case "merge2d":
			return Views.reshape( img, 90000, 40 );
		case "split4d":
			return Views.reshape( img, 300, 300, 8, 5 );
		default:
			throw new IllegalArgumentException( shape );
		}
	}

	static PrimitiveBlocks< IntType > createBlocks( final RandomAccessibleInterval< IntType > view, final String impl )
	{
		switch ( impl )
		{
		case "view":
			return PrimitiveBlocks.of( view, PrimitiveBlocks.OnFallback.FAIL );
		case "fallback":
			return new FallbackPrimitiveBlocks<>( view, new IntType() );
		default:
			throw new IllegalArgumentException( impl );
		}
	}

	/**
	 * A block of 32768 elements.
	 */
	static Interval createInterval( final String shape, final String block )
	{
		final boolean aligned = "aligned".equals( block );
		switch ( shape )
		{
		case "none":
			return FinalInterval.createMinSize( 64, 100, aligned ? 8 : 12, 64, 64, 8 );
		case "flat1d":
			// aligned: starts at a plane, straddling: crosses a plane boundary
			return FinalInterval.createMinSize( aligned ? 900000 : 900000 - 16384, 32768 );
		case "merge2d":
			// aligned: starts at a row, straddling: doesn't
			return FinalInterval.createMinSize( aligned ? 30000 : 30150, 8, 4096, 8 );
		case "split4d":
			// aligned: complete rows of the split axis, straddling: partial rows
			return aligned
					? FinalInterval.createMinSize( 64, 100, 0, 2, 64, 64, 8, 1 )
					: FinalInterval.createMinSize( 64, 100, 2, 1, 64, 64, 4, 2 );
		default:
			throw new IllegalArgumentException( shape );
		}
	}

	/**
	 * Check that "view" and "fallback" copy the same values.
	 */
	static void selfCheck()
	{
		for ( final String imgType : IMG_TYPES )
			for ( final String shape : SHAPES )
				for ( final String block : BLOCKS )
				{
					final RandomAccessibleInterval< IntType > view = createView( imgType, shape );
					final Interval interval = createInterval( shape, block );
					final int[] expected = new int[ ( int ) Intervals.numElements( interval ) ];
					final int[] actual = new int[ expected.length ];
					createBlocks( view, "fallback" ).copy( interval, expected );
					createBlocks( view, "view" ).copy( interval, actual );
					if ( !Arrays.equals( expected, actual ) )
						throw new AssertionError( imgType + " / " + shape + " / " + block );
				}
		System.out.println( "selfCheck: OK" );
	}

	public static void main( String... args ) throws RunnerException
	{
		selfCheck();
		final Options options = new OptionsBuilder().include( ReshapeBlocksBenchmark.class.getSimpleName() + "\\." ).build();
		new Runner( options ).run();
	}
}
