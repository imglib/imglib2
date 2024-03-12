/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2023 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

import java.math.BigDecimal;
import java.util.Random;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * 
 * @author Stephan Saalfeld
 */
public class RealSumTest
{
	final static protected double[] stream = new double[ 1000000 ];

	static protected BigDecimal referenceSum = new BigDecimal( 0.0 );

	final static Random rnd = new Random( 12345 );

	@BeforeClass
	public static void init()
	{
		for ( int i = 0; i < stream.length; ++i )
		{
			stream[ i ] = rnd.nextDouble() * 1000000;
			referenceSum = referenceSum.add( new BigDecimal( stream[ i ] ) );
		}
	}

	/**
	 * Test method for {@link net.imglib2.util.RealSum#RealSum()}.
	 */
	@Test
	public void testRealSum()
	{
		final RealSum sum = new RealSum();
		Assert.assertEquals( sum.getSum(), 0.0, 0.001 );
	}

	/**
	 * Test method for {@link net.imglib2.util.RealSum#getSum()}.
	 */
	@Test
	public void testGetSum()
	{
		final RealSum sum = new RealSum();
		for ( int i = 0; i < stream.length; ++i )
			sum.add( stream[ i ] );
		Assert.assertEquals( sum.getSum(), referenceSum.doubleValue(), 0.0001 );
	}

	/**
	 * Test method for {@link net.imglib2.util.RealSum#add(double)}.
	 */
	@Test
	public void testAdd()
	{
		final RealSum sum = new RealSum();
		for ( int i = 0; i < stream.length; ++i )
			sum.add( 1 );
		Assert.assertEquals( sum.getSum(), stream.length, 0.0001 );
	}

	/**
	 * Test method for {@link net.imglib2.util.RealSum#add(double)} on a hard
	 * example that fails for naive double summation.
	 */
	@Test
	public void testHardExample() {
		final double[] values = new double[]{1.0, 1.0e100, 1.0, -1.0e100};
		final RealSum sum = new RealSum();
		for (double value : values) {
			sum.add(value);
		}
		Assert.assertEquals(2.0, sum.getSum(), 0.0001);
	}
}
