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

package net.imglib2.util;

import net.imglib2.Dimensions;
import net.imglib2.FinalDimensions;
import net.imglib2.FinalRealInterval;
import net.imglib2.Interval;
import net.imglib2.RealInterval;
import net.imglib2.test.ImgLib2Assert;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IntervalsTest
{
	@Test
	public void testTranslateDim()
	{
		final Interval input = Intervals.createMinMax( 1, 2, 3, 5, 7, 9 );
		final Interval result = Intervals.translate( input, 10, 0 );
		final Interval expected = Intervals.createMinMax( 11, 2, 3, 15, 7, 9 );
		ImgLib2Assert.assertIntervalEquals( expected, result );
	}

	@Test
	public void testTranslateUsesCorrectOverload()
	{
		final Interval input = Intervals.createMinMax( 1, 2, 5, 7 );
		// when the number of axes equals 2, then both translate() methods are
		// applicable.
		// Which one is used?
		final Interval result = Intervals.translate( input, 10, ( long ) 1 );
		final Interval expected = Intervals.createMinMax( 11, 3, 15, 8 );
		ImgLib2Assert.assertIntervalEquals( expected, result );
	}

	@Test
	public void testTranslateDimNegative()
	{
		final Interval input = Intervals.createMinMax( 1, 2, 3, 5, 7, 9 );
		final Interval result = Intervals.translate( input, -10, 2 );
		final Interval expected = Intervals.createMinMax( 1, 2, -7, 5, 7, -1 );
		ImgLib2Assert.assertIntervalEquals( expected, result );
	}

	@Test
	public void testTranslate()
	{
		final Interval input = Intervals.createMinMax( 1, 2, 3, 5, 7, 9 );
		final Interval result = Intervals.translate( input, 10, 9, 8 );
		final Interval expected = Intervals.createMinMax( 11, 11, 11, 15, 16, 17 );
		ImgLib2Assert.assertIntervalEquals( expected, result );
	}

	@Test
	public void testTranslateNegative()
	{
		final Interval input = Intervals.createMinMax( 1, 2, 3, 5, 7, 9 );
		final Interval result = Intervals.translate( input, -10, -11, -12 );
		final Interval expected = Intervals.createMinMax( -9, -9, -9, -5, -4, -3 );
		ImgLib2Assert.assertIntervalEquals( expected, result );
	}

	@Test
	public void testRotate()
	{
		final Interval input = Intervals.createMinMax( 1, 2, 3, 5, 7, 9 );
		final Interval result = Intervals.rotate( input, 1, 0 );
		final Interval expected = Intervals.createMinMax( 2, -5, 3, 7, -1, 9 );
		ImgLib2Assert.assertIntervalEquals( expected, result );
	}

	@Test
	public void testAddDimension()
	{
		final Interval input = Intervals.createMinMax( 1, 2, 3, 5, 7, 9 );
		final Interval result = Intervals.addDimension( input, 4, 11 );
		final Interval expected = Intervals.createMinMax( 1, 2, 3, 4, 5, 7, 9, 11 );
		ImgLib2Assert.assertIntervalEquals( expected, result );
	}

	@Test
	public void testInvertAxis()
	{
		final Interval input = Intervals.createMinMax( 1, 2, 3, 5, 7, 9 );
		final Interval result = Intervals.invertAxis( input, 1 );
		final Interval expected = Intervals.createMinMax( 1, -7, 3, 5, -2, 9 );
		ImgLib2Assert.assertIntervalEquals( expected, result );
	}

	@Test
	public void testMoveAxis()
	{
		final Interval input = Intervals.createMinMax( 1, 2, 3, 5, 7, 9 );
		final Interval result = Intervals.moveAxis( input, 0, 2 );
		final Interval expected = Intervals.createMinMax( 2, 3, 1, 7, 9, 5 );
		ImgLib2Assert.assertIntervalEquals( expected, result );
	}

	@Test
	public void testPermuteAxis()
	{
		final Interval input = Intervals.createMinMax( 1, 2, 3, 5, 7, 9 );
		final Interval result = Intervals.permuteAxes( input, 0, 2 );
		final Interval expected = Intervals.createMinMax( 3, 2, 1, 9, 7, 5 );
		ImgLib2Assert.assertIntervalEquals( expected, result );
	}

	@Test
	public void testHyperSlice()
	{
		final Interval input = Intervals.createMinMax( 1, 2, 3, 5, 7, 9 );
		final Interval result = Intervals.hyperSlice( input, 2 );
		final Interval expected = Intervals.createMinMax( 1, 2, 5, 7 );
		ImgLib2Assert.assertIntervalEquals( expected, result );
	}

	@Test
	public void testZeroMin() {
		final Interval input = Intervals.createMinSize( 1, 2, 3, 5, 6, 7 );
		final Interval result = Intervals.zeroMin( input );
		final Interval expected = Intervals.createMinSize( 0, 0, 0, 5, 6, 7 );
		ImgLib2Assert.assertIntervalEquals( expected, result );
	}

	@Test
	public void testScale() {
		final RealInterval input = FinalRealInterval.createMinMax( 1, 2, 6.5, 7 );
		final RealInterval result = Intervals.scale( input, 3.0 );
		final RealInterval expected = FinalRealInterval.createMinMax( 3, 6, 19.5, 21 );
		ImgLib2Assert.assertIntervalEquals( expected, result, 0.0 );
	}

	@Test
	public void testEquals() {
		final Interval interval = Intervals.createMinMax( 1, 2, 3, 4 );
		final Interval sameInterval = Intervals.createMinMax( 1, 2, 3, 4 );
		final Interval differentInterval = Intervals.createMinMax( 1, 2, 3, 0 );
		assertTrue( Intervals.equals( interval, sameInterval ) );
		assertFalse( Intervals.equals( interval, differentInterval ) );
	}

	@Test
	public void testEqualsForRealIntervals() {
		final RealInterval interval = FinalRealInterval.createMinMax( 1, 2, 3, 4 );
		final RealInterval similarInterval = FinalRealInterval.createMinMax( 1.1, 1.9, 3.0, 4.1 );
		final RealInterval differentInterval = FinalRealInterval.createMinMax( 1, 2, 3, 5 );
		assertTrue( Intervals.equals( interval, similarInterval, 0.2 ) );
		assertFalse( Intervals.equals( interval, differentInterval, 0.2 ) );
		assertFalse( Intervals.equals( interval, similarInterval, 0.0 ) );
	}

	@Test
	public void testEqualDimensions() {
		final Dimensions dimensions = new FinalDimensions( 1, 2 );
		final Dimensions sameDimensions = new FinalDimensions( 1, 2 );
		final Dimensions differentDimensions = new FinalDimensions( 1, 3 );
		assertTrue( Intervals.equalDimensions( dimensions, sameDimensions ) );
		assertFalse( Intervals.equalDimensions( dimensions, differentDimensions ) );
	}
}
