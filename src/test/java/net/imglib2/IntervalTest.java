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

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Tests default methods of the interface {@link Interval}.
 *
 * @author Matthias Arzt
 */
public class IntervalTest
{

	private final Interval interval = new Interval()
	{

		@Override
		public long min( int d )
		{
			return 3;
		}

		@Override
		public long max( int d )
		{
			return 7;
		}

		@Override
		public int numDimensions()
		{
			return 1;
		}
	};

	/**
	 * Tests {@link Interval#min(long[])}.
	 */
	@Test
	public void testMinWithLongs()
	{
		long[] result = new long[ 1 ];
		interval.min( result );
		assertArrayEquals( new long[] { 3 }, result );
	}

	/**
	 * Tests {@link Interval#min(Positionable)}.
	 */
	@Test
	public void testMinWithPositionable()
	{
		Point result = new Point( 1 );
		interval.min( result );
		assertEquals( 3, result.getLongPosition( 0 ) );
	}

	/**
	 * Tests {@link Interval#max(long[])}.
	 */
	@Test
	public void testMaxWithLongs()
	{
		long[] result = new long[ 1 ];
		interval.max( result );
		assertArrayEquals( new long[] { 7 }, result );
	}

	/**
	 * Tests {@link Interval#max(Positionable)}.
	 */
	@Test
	public void testMaxWithPositionable()
	{
		Point result = new Point( 1 );
		interval.max( result );
		assertEquals( 7, result.getLongPosition( 0 ) );
	}

	/**
	 * Tests {@link Interval#realMin(int)}.
	 */
	@Test
	public void testRealMin()
	{
		assertEquals( 3.0, interval.realMin( 0 ), 0.0 );
	}

	/**
	 * Tests {@link Interval#realMax(int)}.
	 */
	@Test
	public void testRealMax()
	{
		assertEquals( 7.0, interval.realMax( 0 ), 0.0 );
	}

	/**
	 * Tests {@link Interval#dimension(int)}.
	 */
	@Test
	public void testDimension()
	{
		assertEquals( 5, interval.dimension( 0 ) );
	}
}
