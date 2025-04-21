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
/**
 *
 */
package net.imglib2.transform.integer.permutation;

import java.util.ArrayList;
import java.util.Collections;

import net.imglib2.Localizable;
import net.imglib2.Positionable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Philipp Hanslovsky
 *
 */
public class AbstractPermutationTransformTest
{

	/**
	 * Wrapper class for AbstractPermutatioTransform to test protected methods.
	 */
	public static class MockPermutation extends AbstractPermutationTransform
	{

		// test protected methods
		@Override
		public int apply( final int x )
		{
			return super.apply( x );
		}

		@Override
		public long applyChecked( final int x )
		{
			return super.applyChecked( x );
		}

		@Override
		public int applyInverse( final int y )
		{
			return super.applyInverse( y );
		}

		@Override
		public long applyInverseChecked( final int y )
		{
			return super.applyInverseChecked( y );
		}

		public MockPermutation( final int[] lut )
		{
			super( lut );
		}

		// ignore unimplemented methods
		@Override
		public void applyInverse( final long[] source, final long[] target )
		{

		}

		@Override
		public void applyInverse( final int[] source, final int[] target )
		{

		}

		@Override
		public void applyInverse( final Positionable source, final Localizable target )
		{

		}

		@Override
		public MockPermutation inverse()
		{
			return new MockPermutation( this.inverseLut );
		}

		@Override
		public int numSourceDimensions()
		{
			return 0;
		}

		@Override
		public int numTargetDimensions()
		{
			return 0;
		}

		@Override
		public void apply( final long[] source, final long[] target )
		{

		}

		@Override
		public void apply( final int[] source, final int[] target )
		{

		}

		@Override
		public void apply( final Localizable source, final Positionable target )
		{

		}

	}

	private final int size = 200;

	private final int[] lut = new int[ this.size ];

	private final int[] inv = new int[ this.size ];

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{

		final ArrayList< Integer > al = new ArrayList< Integer >();
		for ( int i = 0; i < this.size; ++i )
			al.add( i );

		Collections.shuffle( al );

		for ( int i = 0; i < this.size; ++i )
		{
			this.lut[ i ] = al.get( i );
			this.inv[ this.lut[ i ] ] = i;
		}

	}

	@Test
	public void test()
	{
		final MockPermutation transform = new MockPermutation( this.lut );
		final MockPermutation inverse = transform.inverse();
		int resInt;
		long resLong;
		for ( int i = 0; i < this.size; ++i )
		{
			resInt = transform.apply( i );
			resLong = transform.applyChecked( i );
			Assert.assertEquals( this.lut[ i ], resInt );
			Assert.assertEquals( this.lut[ i ], resLong );

			resInt = inverse.apply( transform.apply( i ) );
			resLong = transform.apply( inverse.apply( i ) );
			Assert.assertEquals( i, resInt );
			Assert.assertEquals( i, resLong );

			resInt = inverse.apply( i );
			resLong = inverse.applyChecked( i );
			Assert.assertEquals( this.inv[ i ], resInt );
			Assert.assertEquals( this.inv[ i ], resLong );
		}

		Assert.assertEquals( -Long.MAX_VALUE, transform.applyChecked( -1 ) );
		Assert.assertEquals( Long.MAX_VALUE, transform.applyChecked( this.size + 1 ) );

	}

}
