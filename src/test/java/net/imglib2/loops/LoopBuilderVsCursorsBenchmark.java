/*
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

package net.imglib2.loops;

import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converters;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.img.planar.PlanarImgs;
import net.imglib2.test.RandomImgs;
import net.imglib2.type.numeric.real.DoubleType;
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
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.LongStream;

/**
 * This benchmark compares the performance of {@link LoopBuilder}
 * against {@link Views#flatIterable(RandomAccessibleInterval)},
 * when performing a pixel copy operation.
 */
@Warmup( iterations = 4, time = 100, timeUnit = TimeUnit.MILLISECONDS )
@Fork( 1 )
@Measurement( iterations = 8, time = 100, timeUnit = TimeUnit.MILLISECONDS )
@BenchmarkMode( { Mode.AverageTime } )
@State( Scope.Benchmark )
@OutputTimeUnit( TimeUnit.MILLISECONDS )
public class LoopBuilderVsCursorsBenchmark
{

	private static final Map< String, Supplier< RandomAccessibleInterval< DoubleType > > >
			IMG_FACTORIES = initImageFactories();

	/**
	 * This {@link Map} contains methods to create different types of
	 * {@link RandomAccessibleInterval}s. Each type might behave differently
	 * in terms of performance.
	 */
	private static Map< String, Supplier< RandomAccessibleInterval< DoubleType > > > initImageFactories()
	{
		final long[] DIMS = { 200, 200, 100 };
		final HashMap< String, Supplier< RandomAccessibleInterval< DoubleType > > > map = new HashMap<>();
		map.put( "ArrayImg", () -> ArrayImgs.doubles( DIMS ) );
		map.put( "ArrayImg_Continuous_Subset", () -> Views.interval( ArrayImgs.doubles( 200, 200, 300 ), Intervals.createMinSize( 0, 0, 99, 200, 200, 100 ) ) );
		map.put( "ArrayImg_Subset", () -> Views.interval( ArrayImgs.doubles( 300, 300, 100 ), Intervals.createMinSize( 50, 50, 0, 200, 200, 100 ) ) );
		map.put( "ArrayImg_HyperSlice", () -> Views.hyperSlice( ArrayImgs.doubles( 200, 200, 100, 10 ), 3, 0 ) );
		map.put( "ArrayImg_Translated_Zero", () -> Views.translate( Views.translate( ArrayImgs.doubles( DIMS ), 10, 10, 10 ), -10, -10, -10 ) );
		map.put( "PlanarImg", () -> PlanarImgs.doubles( DIMS ) );
		map.put( "PlanarImg_Continuous_Subset", () -> Views.interval( PlanarImgs.doubles( 200, 200, 300 ), Intervals.createMinSize( 0, 0, 99, 200, 200, 100 ) ) );
		map.put( "PlanarImg_Subset", () -> Views.interval( PlanarImgs.doubles( 300, 300, 100 ), Intervals.createMinSize( 50, 50, 0, 200, 200, 100 ) ) );
		map.put( "PlanarImg_HyperSlice", () -> Views.hyperSlice( PlanarImgs.doubles( 200, 200, 100, 10 ), 3, 0 ) );
		map.put( "PlanarImg_Translated_Zero", () -> Views.hyperSlice( PlanarImgs.doubles( 200, 200, 100, 10 ), 3, 0 ) );
		map.put( "CellImg", () -> new CellImgFactory<>( new DoubleType(), 64, 64, 64 ).create( DIMS ) );
		map.put( "Translated", () -> Views.translate( ArrayImgs.doubles( DIMS ), 10, 10, 10 ) );
		map.put( "Cropped", () -> {
			final long[] largerDims = LongStream.of( DIMS ).map( x -> x + 20 ).toArray();
			final Img< DoubleType > largerImage = ArrayImgs.doubles( largerDims );
			return Views.interval( largerImage, new FinalInterval( DIMS ) );
		} );
		map.put( "Rotated", () -> Views.rotate( ArrayImgs.doubles( DIMS ), 0, 1 ) );
		map.put( "Converted", () -> Converters.convertRAI( ArrayImgs.doubles( DIMS ), ( i, o ) -> o.set( i ), new DoubleType() ) );
		return map;
	}

	@Param( "ArrayImg" )
	private String inputImage;

	@Param( "ArrayImg" )
	private String outputImage;

	private RandomAccessibleInterval< DoubleType > in;

	private RandomAccessibleInterval< DoubleType > out;

	/**
	 * If true, this will run the LoopBuilder and flat iterable copy,
	 * for many different images, before each benchmark. Which will
	 * result in slower probably more realistic execution time during
	 * measurements.
	 */
	private boolean slowDown = false;

	@Setup
	public void setup()
	{
		if(slowDown)
			slowDown();
		in = IMG_FACTORIES.get( inputImage ).get();
		out = IMG_FACTORIES.get( outputImage ).get();
		RandomImgs.seed( 0 ).randomize( in );
	}

	public static void slowDown()
	{
		System.out.print( "\nslow down..." );
		IMG_FACTORIES.forEach( ( title, factory ) -> {
			RandomAccessibleInterval< DoubleType > a = factory.get();
			RandomAccessibleInterval< DoubleType > b = factory.get();
			loopBuilder( a, b );
			flatCopy( a, b );
		} );
		System.out.println( "done" );
	}

	@Benchmark
	public Object flatCopy()
	{
		flatCopy( in, out );
		return out;
	}

	private static void flatCopy( RandomAccessibleInterval< DoubleType > in, RandomAccessibleInterval< DoubleType > out )
	{
		Cursor< DoubleType > inCursor = Views.flatIterable( in ).cursor();
		Cursor< DoubleType > outCursor = Views.flatIterable( out ).cursor();
		long i = Intervals.numElements( in );
		while ( --i >= 0 )
			outCursor.next().set( inCursor.next() );
	}

	@Benchmark
	public Object loopBuilder()
	{
		loopBuilder( in, out );
		return out;
	}

	static void loopBuilder( RandomAccessibleInterval< DoubleType > in, RandomAccessibleInterval< DoubleType > out )
	{
		LoopBuilder.setImages( in, out ).forEachPixel( ( i1, o ) -> o.set( i1 ) );
	}

	public static void main( final String... args ) throws RunnerException
	{
		final Options opt = new OptionsBuilder()
				.include( LoopBuilderVsCursorsBenchmark.class.getSimpleName() )
				.param( "inputImage", IMG_FACTORIES.keySet().toArray( new String[ 0 ] ) )
				.param( "outputImage", IMG_FACTORIES.keySet().toArray( new String[ 0 ] ) )
				.build();
		final Collection< RunResult > result = new Runner( opt ).run();
		printTable( result );
	}

	private static void printTable( Collection< RunResult > map )
	{
		System.out.println( "The table shows:" );
		System.out.println( " * Zero: LoopBuilder and flat iterable copy have the same performance." );
		System.out.println( " * Positive value: LoopBuilder is faster." );
		System.out.println( " * Negative value: Flat iterable copy is faster." );
		System.out.println( " * 17.0 means: LoopBuilder is 18 times as fast as flat copy." );
		DecimalFormat formatter = new DecimalFormat( "#0.00" );
		StringBuilder out = new StringBuilder();
		for ( String outputImg : IMG_FACTORIES.keySet() )
		{
			out.append( "\t" ).append( outputImg );
		}
		out.append( "\n" );
		for ( String inputImg : IMG_FACTORIES.keySet() )
		{
			out.append( inputImg );
			for ( String outputImg : IMG_FACTORIES.keySet() )
			{
				final double loopBuilderScore = getValue( map, "loopBuilder", inputImg, outputImg );
				final double flatCopyScore = getValue( map, "flatCopy", inputImg, outputImg );
				double speedUp = ( flatCopyScore - loopBuilderScore ) / Math.min( loopBuilderScore, flatCopyScore );
				out.append( "\t" ).append( formatter.format( speedUp ) );
			}
			out.append( "\n" );
		}
		System.out.println( out );
	}

	private static double getValue( Collection< RunResult > map, String label, String inputImg, String outputImg )
	{
		return map.stream()
				.filter( r -> r.getPrimaryResult().getLabel().equals( label ) )
				.filter( r -> r.getParams().getParam( "inputImage" ).equals( inputImg ) )
				.filter( r -> r.getParams().getParam( "outputImage" ).equals( outputImg ) )
				.map( r -> r.getPrimaryResult().getScore() )
				.findAny().get();
	}
}
