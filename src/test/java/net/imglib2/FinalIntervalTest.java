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

package net.imglib2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests {@link FinalInterval}.
 * 
 * @author Curtis Rueden
 */
public class FinalIntervalTest
{

	/**
	 * Tests {@link FinalInterval#FinalInterval(Dimensions)}.
	 */
	@Test
	public void testCtorDimensions()
	{
		final FinalDimensions source = new FinalDimensions(23, 45, 34);
		final FinalInterval bounds = new FinalInterval( source );
		assertInterval( 0, 0, 0, 22, 44, 33, 23, 45, 34, bounds );
	}

	/**
	 * Tests {@link FinalInterval#FinalInterval(Interval)}.
	 */
	@Test
	public void testCtorInterval()
	{
		final FinalInterval source = FinalInterval.createMinMax( 3, 2, 1, 8, 6, 4 );
		final FinalInterval bounds = new FinalInterval( source );
		assertInterval( 3, 2, 1, 8, 6, 4, 6, 5, 4, bounds );
	}

	/**
	 * Tests {@link FinalInterval#FinalInterval(long[], long[])}.
	 */
	@Test
	public void testCtorMinMax()
	{
		final long[] min = { -5, -3, -4 };
		final long[] max = { -4, 5, 0 };
		final FinalInterval bounds = new FinalInterval( min, max );
		assertInterval( -5, -3, -4, -4, 5, 0, 2, 9, 5, bounds );
	}

	/**
	 * Tests {@link FinalInterval#FinalInterval(long...)}.
	 */
	@Test
	public void testCtorLongArray()
	{
		final FinalInterval bounds = new FinalInterval( 42, 60, 71 );
		assertInterval( 0, 0, 0, 41, 59, 70, 42, 60, 71, bounds );
	}

	/**
	 * Tests {@link FinalInterval#createMinSize(long...)}.
	 */
	@Test
	public void testCreateMinSizeOneArray()
	{
		final FinalInterval bounds = FinalInterval.createMinSize( 2, 4, 8, 16, 32, 64 );
		assertInterval( 2, 4, 8, 17, 35, 71, 16, 32, 64, bounds );
	}

	/**
	 * Tests {@link FinalInterval#createMinMax(long...)}.
	 */
	@Test
	public void testCreateMinMaxOneArray()
	{
		final FinalInterval bounds = FinalInterval.createMinMax( 2, 3, 5, 8, 13, 21 );
		assertInterval( 2, 3, 5, 8, 13, 21, 7, 11, 17, bounds );
	}

	/**
	 * Tests {@link FinalInterval#createMinSize(long[], long[])}.
	 */
	@Test
	public void testCreateMinSizeTwoArrays()
	{
		final long[] min = { 5, 3, 7 };
		final long[] size = { 13, 17, 11 };
		final FinalInterval bounds = FinalInterval.createMinSize( min, size );
		assertInterval( 5, 3, 7, 17, 19, 17, 13, 17, 11, bounds );
	}

	private void assertInterval( int min0, int min1, int min2, int max0, int max1, int max2, int dim0, int dim1, int dim2, Interval interval )
	{
		assertEquals( min0, interval.min( 0 ) );
		assertEquals( min1, interval.min( 1 ) );
		assertEquals( min2, interval.min( 2 ) );
		assertEquals( max0, interval.max( 0 ) );
		assertEquals( max1, interval.max( 1 ) );
		assertEquals( max2, interval.max( 2 ) );
		assertEquals( dim0, interval.dimension( 0 ) );
		assertEquals( dim1, interval.dimension( 1 ) );
		assertEquals( dim2, interval.dimension( 2 ) );
	}

	@Test
	public void testEquals()
	{
		final FinalInterval interval = FinalInterval.createMinMax( 1, 2, 3, 4 );
		final FinalInterval sameInterval = FinalInterval.createMinMax( 1, 2, 3, 4 );
		final FinalInterval differentInterval = FinalInterval.createMinMax( 1, 2, 3, 0 );
		assertTrue( interval.equals( interval ) );
		assertTrue( interval.equals( sameInterval ) );
		assertFalse( interval.equals( differentInterval ) );
		assertFalse( interval.equals( null ) );
	}

	@Test
	public void testHashCode()
	{
		final FinalInterval interval = FinalInterval.createMinMax( 1, 2, 3, 4 );
		final FinalInterval sameInterval = FinalInterval.createMinMax( 1, 2, 3, 4 );
		assertEquals( interval.hashCode(), interval.hashCode() );
		assertEquals( interval.hashCode(), sameInterval.hashCode() );
	}

	@Test
	public void testToString()
	{
		final FinalInterval interval = FinalInterval.createMinMax( 1, 2, 3, 4 );
		assertEquals( "FinalInterval [(1, 2) -- (3, 4) = 3x3]", interval.toString() );
	}
}
