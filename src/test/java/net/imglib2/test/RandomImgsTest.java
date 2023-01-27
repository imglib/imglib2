/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2022 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package net.imglib2.test;

import net.imglib2.Interval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converters;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.ByteType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.LongType;
import net.imglib2.type.numeric.integer.ShortType;
import net.imglib2.type.numeric.integer.Unsigned12BitType;
import net.imglib2.type.numeric.integer.Unsigned2BitType;
import net.imglib2.type.numeric.integer.Unsigned4BitType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedIntType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.integer.UnsignedVariableBitLengthType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Intervals;
import net.imglib2.util.Util;
import net.imglib2.view.Views;
import org.junit.Test;

import java.util.Random;
import java.util.function.DoubleBinaryOperator;

import static net.imglib2.test.ImgLib2Assert.assertImageEquals;
import static net.imglib2.test.ImgLib2Assert.assertIntervalEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RandomImgsTest
{
	@Test
	public void testRandomImage()
	{
		int expected = new Random( 42 ).nextInt();
		Img< IntType > image = RandomImgs.seed( 42 ).nextImage( new IntType(), 1 );
		assertImageEquals( ArrayImgs.ints( new int[] { expected }, 1 ), image );
	}

	@Test
	public void testRandomImageDimensions()
	{
		long[] dims = { 4, 6, 7 };
		Img< IntType > image = RandomImgs.seed( 42 ).nextImage( new IntType(), dims );
		assertArrayEquals( dims, Intervals.dimensionsAsLongArray( image ) );
	}

	@Test
	public void testRandomImageInterval()
	{
		Interval interval = Intervals.createMinSize( 2, 5, 7, 3, 4, 5 );
		RandomAccessibleInterval< IntType > image = RandomImgs.seed( 42 ).nextImage( new IntType(), interval );
		assertIntervalEquals( interval, image );
	}

	@Test
	public void testRandomize()
	{
		int expected = new Random( 42 ).nextInt();
		Img< IntType > image = RandomImgs.seed( 42 ).randomize( ArrayImgs.ints( new int[ 1 ], 1 ) );
		assertImageEquals( ArrayImgs.ints( new int[] { expected }, 1 ), image );
	}

	@Test
	public void testRandomImageIntergerTypes()
	{
		createAndTestIntegerTypeImage( new ByteType() );
		createAndTestIntegerTypeImage( new ShortType() );
		createAndTestIntegerTypeImage( new IntType() );
		createAndTestIntegerTypeImage( new LongType() );
		createAndTestIntegerTypeImage( new UnsignedByteType() );
		createAndTestIntegerTypeImage( new UnsignedShortType() );
		createAndTestIntegerTypeImage( new UnsignedIntType() );
		//testRealType( new UnsignedLongType() ); // NB: UnsignedLongType#getRealDouble needs to be fixed
		createAndTestIntegerTypeImage( new Unsigned2BitType() );
		createAndTestIntegerTypeImage( new Unsigned4BitType() );
		createAndTestIntegerTypeImage( new Unsigned12BitType() );
		createAndTestIntegerTypeImage( new UnsignedVariableBitLengthType( 5 ) );
	}

	@Test
	public void testARGBType()
	{
		Img< ARGBType > image = RandomImgs.seed( 42 ).nextImage( new ARGBType(), 10, 10 );
		testIsRandomImageIntegerType( Converters.argbChannel( image, 0 ) );
		testIsRandomImageIntegerType( Converters.argbChannel( image, 1 ) );
		testIsRandomImageIntegerType( Converters.argbChannel( image, 2 ) );
		testIsRandomImageIntegerType( Converters.argbChannel( image, 3 ) );
	}

	private < T extends IntegerType< T > & NativeType< T > > void createAndTestIntegerTypeImage( T type )
	{
		Img< T > image = RandomImgs.seed( 42 ).nextImage( type, 10, 10 );
		testIsRandomImageIntegerType( image );
	}

	private < T extends IntegerType< T > & NativeType< T > > void testIsRandomImageIntegerType( RandomAccessibleInterval< T > image )
	{
		T type = Util.getTypeFromInterval( image );
		double min = type.getMinValue();
		double max = type.getMaxValue();
		double actualMin = fold( Double.POSITIVE_INFINITY, Math::min, Views.iterable( image ) );
		double actualMax = fold( Double.NEGATIVE_INFINITY, Math::max, Views.iterable( image ) );
		// NB: Test, if the actual maximal value is close enough to the types getMaxValue().
		String msg1 = "Actual max is to low, type: " + type.getClass().getSimpleName() + " max: " + max + " actual max: " + actualMax;
		assertTrue( msg1, min * 0.2 + max * 0.8 < actualMax );
		// NB: Test, if the actual minimal value is close enough to the types getMinValue().
		String msg2 = "Actual min is to high, type: " + type.getClass().getSimpleName() + " min: " + min + " actual min: " + actualMin;
		assertTrue( msg2, min * 0.8 + max * 0.2 > actualMin );
	}

	@Test
	public void testRandomImageRealTypes()
	{
		createAndTestRealTypeImage( new FloatType() );
		createAndTestRealTypeImage( new DoubleType() );
	}

	private < T extends NativeType< T > & RealType< T > > void createAndTestRealTypeImage( T type )
	{
		Img< ? extends RealType< ? > > image = RandomImgs.seed( 42 ).nextImage( type, 10, 10 );
		testUniformDistribution( image );
	}

	private void testUniformDistribution( Img< ? extends RealType< ? > > image )
	{
		assertEquals( 0.5, mean( image ), 0.1 );
		assertEquals( 1.0 / 12.0, variance( image ), 0.1 );
	}

	private double mean( Img< ? extends RealType< ? > > image )
	{
		double sum = fold( 0, ( acc, value ) -> acc + value, image );
		return sum / Intervals.numElements( image );
	}

	private double variance( Img< ? extends RealType< ? > > image )
	{
		double mean = mean( image );
		double sum_squared = fold( 0, ( acc, value ) -> acc + Math.pow( value - mean, 2 ), image );
		return sum_squared / Intervals.numElements( image );
	}

	private double fold( final double neutral, final DoubleBinaryOperator operation, final Iterable< ? extends RealType< ? > > image )
	{
		double accumulator = neutral;
		for ( RealType< ? > values : image )
			accumulator = operation.applyAsDouble( accumulator, values.getRealDouble() );
		return accumulator;
	}
}
