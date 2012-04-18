/**
 * Copyright (c) 2009--2012, ImgLib2 developers
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  Neither the name of the imglib project nor
 * the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
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
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
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
	 * Test method for {@link net.imglib2.util.RealSum#RealSum(int)}.
	 */
	@Test
	public void testRealSumInt()
	{
		final RealSum sum = new RealSum( 10 );
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
	 * Test method for {@link net.imglib2.util.RealSum#getSum()}.
	 */
	@Test
	public void testDoubleSum()
	{
		double sum = 0;
		for ( int i = 0; i < stream.length; ++i )
			sum += stream[ i ];
		Assert.assertEquals( sum, referenceSum.doubleValue(), 0.01 );
	}

	/**
	 * Test method for {@link net.imglib2.util.RealSum#add(double)}.
	 */
	@Test
	public void testAdd()
	{
		for ( int t = 0; t < 20; ++t )
		{
			final RealSum sum = new RealSum();
			for ( int i = 0; i < stream.length; ++i )
				sum.add( 1 );
			Assert.assertEquals( sum.getSum(), stream.length, 0.0001 );
		}
	}
	
	/**
	 * Test method for {@link net.imglib2.util.RealSum#add(double)}.
	 */
	@Test
	public void testDoubleAdd()
	{
		for ( int t = 0; t < 20; ++t )
		{
			double sum = 0;
			for ( int i = 0; i < stream.length; ++i )
				sum += 1;
			Assert.assertEquals( sum, stream.length, 0.0001 );
		}
	}
}
