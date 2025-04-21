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

package net.imglib2.test;

import net.imglib2.Interval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.LongType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.util.Intervals;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ImgLib2AssertTest
{
	@Test
	public void testIntervalToString()
	{
		Interval interval = Intervals.createMinMax( 1, 2, 3, 4 );
		String restult = ImgLib2Assert.intervalToString( interval );
		assertEquals( "{min=[1, 2], max=[3, 4]}", restult );
	}

	@Test
	public void testAssertIntervalsEquals()
	{
		Interval a = Intervals.createMinMax( 1, 2, 3, 4 );
		Interval b = Intervals.createMinMax( 1, 2, 3, 4 );
		ImgLib2Assert.assertIntervalEquals( a, b );
	}

	@Test( expected = AssertionError.class )
	public void testAssertIntervalsEquals_Fail()
	{
		Interval a = Intervals.createMinMax( 1, 2, 3, 4 );
		Interval b = Intervals.createMinMax( 1, 3, 3, 4 );
		ImgLib2Assert.assertIntervalEquals( a, b );
	}

	// test assertImagesEquals with Predicate

	@Test
	public void testAssertImageEqualsWithPredicate()
	{
		ImgLib2Assert.assertImageEquals( img( 3 ), img( 5 ), ( a, e ) -> (a.getInteger() == 3) && (e.getInteger() == 5) );
	}

	@Test( expected = AssertionError.class )
	public void testAssertImageEqualsWithPredicate_Fail()
	{
		ImgLib2Assert.assertImageEquals( img( 0 ), img( 0 ), ( a, e ) -> false );
	}

	@Test( expected = AssertionError.class )
	public void testAssertImageEqualsWithPredicate_FailForInterval()
	{
		ImgLib2Assert.assertImageEquals( img( 1 ), ArrayImgs.ints( 1, 1 ), ( a, e ) -> true );
	}

	// test assertImagesEquals

	@Test
	public void testAssertImageEquals()
	{
		ImgLib2Assert.assertImageEquals( img( 1 ), img( 1 ) );
	}

	@Test( expected = AssertionError.class )
	public void testAssertImageEquals_Fail()
	{
		ImgLib2Assert.assertImageEquals( img( 1 ), img( 2 ) );
	}

	// test method assertImageEqualsIntegerType

	@Test
	public void testAssertImageEqualsIntegerType()
	{
		ImgLib2Assert.assertImageEqualsIntegerType( img( 42 ), img( 42L ) );
	}

	@Test( expected = AssertionError.class )
	public void testAssertImageEqualsIntegerType_Fail()
	{
		ImgLib2Assert.assertImageEqualsIntegerType( img( Integer.MAX_VALUE ), img( Long.MAX_VALUE ) );
	}

	// test method assertImageEqualsRealType

	@Test
	public void testAssertImageEqualsRealType()
	{
		ImgLib2Assert.assertImageEqualsRealType( img( 42.0 ), img( 42.5 ), 0.5 );
	}

	@Test( expected = AssertionError.class )
	public void testAssertImageEqualsRealType_Fail()
	{
		ImgLib2Assert.assertImageEqualsRealType( img( 42.0 ), img( 42.5 ), 0.1 );
	}

	// Helper methods

	private Img< IntType > img( int pixel )
	{
		return ArrayImgs.ints( new int[] { pixel }, 1 );
	}

	private Img< LongType > img( long pixel )
	{
		return ArrayImgs.longs( new long[] { pixel }, 1 );
	}

	private Img< DoubleType > img( double pixel )
	{
		return ArrayImgs.doubles( new double[] { pixel }, 1 );
	}

}
