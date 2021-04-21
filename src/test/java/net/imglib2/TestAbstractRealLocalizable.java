/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2021 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

import org.junit.Test;

/**
 * 
 * @author Lee Kamentsky
 */
public class TestAbstractRealLocalizable
{

	static private class AbstractRealLocalizableImpl extends AbstractRealLocalizable
	{
		public AbstractRealLocalizableImpl( final int nDimensions )
		{
			super( nDimensions );
		}

		double getPosition( final int d )
		{
			return position[ d ];
		}
	}

	/**
	 * Test method for
	 * {@link net.imglib2.AbstractRealLocalizable#AbstractRealLocalizableSampler(int)}
	 * .
	 */
	@Test
	public void testAbstractRealLocalizableSampler()
	{
		final AbstractRealLocalizableImpl x = new AbstractRealLocalizableImpl( 2 );
		assertEquals( x.numDimensions(), 2 );
		for ( int i = 0; i < 2; i++ )
		{
			assertEquals( x.getPosition( i ), 0, 0 );
		}
	}

	/**
	 * Test method for
	 * {@link net.imglib2.AbstractRealLocalizable#localize(float[])}.
	 */
	@Test
	public void testLocalizeFloatArray()
	{
		final AbstractRealLocalizableImpl x = new AbstractRealLocalizableImpl( 2 );
		final float[] p = { 1.1F, 2.2F };
		x.localize( p );
		for ( int i = 0; i < 2; i++ )
		{
			assertEquals( ( float ) ( x.getPosition( i ) ), p[ i ], 0 );
		}
	}

	/**
	 * Test method for
	 * {@link net.imglib2.AbstractRealLocalizable#localize(double[])}.
	 */
	@Test
	public void testLocalizeDoubleArray()
	{
		final AbstractRealLocalizableImpl x = new AbstractRealLocalizableImpl( 2 );
		final double[] p = { 1.1, 2.2 };
		x.localize( p );
		for ( int i = 0; i < 2; i++ )
		{
			assertEquals( x.getPosition( i ), p[ i ], 0 );
		}
	}

	/**
	 * Test method for
	 * {@link net.imglib2.AbstractRealLocalizable#getFloatPosition(int)}.
	 */
	@Test
	public void testGetFloatPosition()
	{
		final AbstractRealLocalizableImpl x = new AbstractRealLocalizableImpl( 2 );
		final float[] p = { 1.1F, 2.2F };
		x.localize( p );
		for ( int i = 0; i < 2; i++ )
		{
			assertEquals( x.getFloatPosition( i ), p[ i ], 0 );
		}
	}

	/**
	 * Test method for
	 * {@link net.imglib2.AbstractRealLocalizable#getDoublePosition(int)}.
	 */
	@Test
	public void testGetDoublePosition()
	{
		final AbstractRealLocalizableImpl x = new AbstractRealLocalizableImpl( 2 );
		final double[] p = { 1.1, 2.2 };
		x.localize( p );
		for ( int i = 0; i < 2; i++ )
		{
			assertEquals( x.getDoublePosition( i ), p[ i ], 0 );
		}
	}

}
