/*-
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2024 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

import java.util.concurrent.TimeUnit;
import net.imglib2.RandomAccessible;
import net.imglib2.converter.Converters;
import net.imglib2.converter.RealDoubleConverter;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.cell.CellImg;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.loops.LoopBuilder;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.util.Intervals;
import net.imglib2.util.Util;
import net.imglib2.view.Views;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * Benchmark copying from a CellImg with various out-of-bounds extensions.
 */
@State( Scope.Benchmark )
@Warmup( iterations = 5, time = 100, timeUnit = TimeUnit.MILLISECONDS )
@Measurement( iterations = 5, time = 100, timeUnit = TimeUnit.MILLISECONDS )
@BenchmarkMode( Mode.AverageTime )
@OutputTimeUnit( TimeUnit.MILLISECONDS )
@Fork( 1 )
public class CopyBenchmarkViewPrimitiveBlocks
{
	private final int[] cellDimensions = { 64, 64, 64 };
	private final int[] srcDimensions = { 300, 300, 300 };
	private final int[] destDimensions = { 64, 64, 64 };
	private final int[] pos = { 64, 100, 100 };
	private final int[] oobPos = { -32, -32, -32 };

	private final RandomAccessible< DoubleType > srcView;

	private final RandomAccessible< DoubleType > srcViewPermuted;

	private final ArrayImg< DoubleType, ? > destArrayImg;

	private final double[] dest;

	public CopyBenchmarkViewPrimitiveBlocks()
	{
		final CellImgFactory< UnsignedByteType > cellImgFactory = new CellImgFactory<>( new UnsignedByteType(), cellDimensions );
		final CellImg< UnsignedByteType, ? > cellImg = cellImgFactory.create( srcDimensions );
		srcView = Converters.convert( Views.extendZero( cellImg ), new RealDoubleConverter<>(), new DoubleType() );
		srcViewPermuted = Converters.convert( Views.extendZero( Views.zeroMin( Views.permute( cellImg, 0, 1 ) ) ), new RealDoubleConverter<>(), new DoubleType() );
		destArrayImg = new ArrayImgFactory<>( new DoubleType() ).create( destDimensions );
		dest = new double[ ( int ) Intervals.numElements( destDimensions ) ];
	}


	@Param( { "true", "false" } )
	private boolean oob;

	@Param( { "true", "false" } )
	private boolean permute;

	@Benchmark
	public void benchmarkLoopBuilder()
	{
		final long[] min = Util.int2long( oob ? oobPos : pos );
		final long[] max = min.clone();
		for ( int d = 0; d < max.length; d++ )
			max[ d ] += destDimensions[ d ] - 1;
		LoopBuilder
				.setImages( Views.interval( permute ? srcViewPermuted : srcView, min, max), destArrayImg )
				.multiThreaded( false )
				.forEachPixel( (i,o) -> o.set( i.get() ) );
	}

	@Benchmark
	public void benchmarkPrimitiveBlocks()
	{
		final PrimitiveBlocks< DoubleType > blocks = PrimitiveBlocks.of( permute ? srcViewPermuted : srcView );
		blocks.copy( oob ? oobPos : pos, dest, destDimensions );
	}

	public static void main( String... args ) throws RunnerException
	{
		Options options = new OptionsBuilder().include( CopyBenchmarkViewPrimitiveBlocks.class.getSimpleName() + "\\." ).build();
		new Runner( options ).run();
	}
}
