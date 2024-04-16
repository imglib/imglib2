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

package net.imglib2;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

/**
 * Tests {@link RealInterval}.
 *
 * @author Matthias Arzt
 */
public class RealIntervalTest
{

	private final RealInterval interval = new RealInterval()
	{

		@Override
		public double realMin( int d )
		{
			return 1.5 + d;
		}

		@Override
		public double realMax( int d )
		{
			return 7.3 + d;
		}

		@Override
		public int numDimensions()
		{
			return 2;
		}
	};

	/**
	 * Tests {@link Interval#realMin(double[])}.
	 */
	@Test
	public void testRealMinWithDoubles()
	{
		double[] result = new double[ 2 ];
		interval.realMin( result );
		assertArrayEquals( new double[] { 1.5, 2.5 }, result, 0.0 );
	}

	/**
	 * Tests {@link Interval#realMax(double[])}.
	 */
	@Test
	public void testRealMaxWithDoubles()
	{
		double[] result = new double[ 2 ];
		interval.realMax( result );
		assertArrayEquals( new double[] { 7.3, 8.3 }, result, 0.0 );
	}

	/**
	 * Tests {@link Interval#realMin(RealPositionable)}.
	 */
	@Test
	public void testRealMinWithPositionable()
	{
		double[] result = new double[ 2 ];
		interval.realMin( RealPoint.wrap( result ) );
		assertArrayEquals( new double[] { 1.5, 2.5 }, result, 0.0 );
	}

	/**
	 * Tests {@link Interval#realMax(double[])}.
	 */
	@Test
	public void testRealMaxWithPositionable()
	{
		double[] result = new double[ 2 ];
		interval.realMax( RealPoint.wrap( result ) );
		assertArrayEquals( new double[] { 7.3, 8.3 }, result, 0.0 );
	}
}
