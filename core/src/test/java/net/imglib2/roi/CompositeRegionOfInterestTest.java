/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
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

package net.imglib2.roi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import net.imglib2.RealRandomAccess;
import net.imglib2.type.logic.BitType;

import org.junit.Test;

/**
 * 
 * @author Lee Kamentsky
 */
public class CompositeRegionOfInterestTest
{

	/**
	 * Test method for
	 * {@link net.imglib2.roi.CompositeRegionOfInterest#CompositeRegionOfInterest(int)}
	 * .
	 */
	@Test
	public void testCompositeRegionOfInterestInt()
	{
		@SuppressWarnings( "deprecation" )
		final CompositeRegionOfInterest c = new CompositeRegionOfInterest( 2 );
		assertEquals( 2, c.numDimensions() );
	}

	/**
	 * Test method for
	 * {@link net.imglib2.roi.CompositeRegionOfInterest#CompositeRegionOfInterest(net.imglib2.roi.RegionOfInterest)}
	 * .
	 */
	@Test
	public void testCompositeRegionOfInterestRegionOfInterest()
	{
		@SuppressWarnings( "deprecation" )
		final CompositeRegionOfInterest c = new CompositeRegionOfInterest( new RectangleRegionOfInterest( new double[] { 1, 2 }, new double[] { 3, 4 } ) );
		assertEquals( 2, c.numDimensions() );
	}

	/**
	 * Test method for
	 * {@link net.imglib2.roi.CompositeRegionOfInterest#CompositeRegionOfInterest(java.util.Collection)}
	 * .
	 */
	@Test
	public void testCompositeRegionOfInterestCollectionOfRegionOfInterest()
	{
		final ArrayList< RegionOfInterest > list = new ArrayList< RegionOfInterest >();
		list.add( new RectangleRegionOfInterest( new double[] { 1, 2 }, new double[] { 3, 4 } ) );
		list.add( new RectangleRegionOfInterest( new double[] { 5, 6 }, new double[] { 3, 4 } ) );
		@SuppressWarnings( "deprecation" )
		final CompositeRegionOfInterest c = new CompositeRegionOfInterest( list );
		assertEquals( 2, c.numDimensions() );
	}

	private void assertInside( @SuppressWarnings( "deprecation" ) final CompositeRegionOfInterest c, final double[] position )
	{
		final RealRandomAccess< BitType > ra = c.realRandomAccess();
		ra.setPosition( position );
		assertTrue( ra.get().get() );
	}

	private void assertInside( @SuppressWarnings( "deprecation" ) final CompositeRegionOfInterest c, final double x, final double y )
	{
		assertInside( c, new double[] { x, y } );
	}

	private void assertOutside( @SuppressWarnings( "deprecation" ) final CompositeRegionOfInterest c, final double[] position )
	{
		final RealRandomAccess< BitType > ra = c.realRandomAccess();
		ra.setPosition( position );
		assertFalse( ra.get().get() );
	}

	private void assertOutside( @SuppressWarnings( "deprecation" ) final CompositeRegionOfInterest c, final double x, final double y )
	{
		assertOutside( c, new double[] { x, y } );
	}

	/**
	 * Test method for
	 * {@link net.imglib2.roi.CompositeRegionOfInterest#or(net.imglib2.roi.RegionOfInterest)}
	 * .
	 */
	@SuppressWarnings( "deprecation" )
	@Test
	public void testOr()
	{
		final CompositeRegionOfInterest c = new CompositeRegionOfInterest( new RectangleRegionOfInterest( new double[] { 1, 2 }, new double[] { 3, 4 } ) );
		c.or( new RectangleRegionOfInterest( new double[] { 3, 5 }, new double[] { 3, 4 } ) );
		assertInside( c, 2, 3 );
		assertInside( c, 5, 8 );
		assertInside( c, 3.5, 5.5 );
		assertOutside( c, 0, 0 );
	}

	/**
	 * Test method for
	 * {@link net.imglib2.roi.CompositeRegionOfInterest#remove(net.imglib2.roi.RegionOfInterest)}
	 * .
	 */
	@SuppressWarnings( "deprecation" )
	@Test
	public void testRemove()
	{
		final RectangleRegionOfInterest[] rois = new RectangleRegionOfInterest[] {
				new RectangleRegionOfInterest( new double[] { 1, 2 }, new double[] { 3, 4 } ),
				new RectangleRegionOfInterest( new double[] { 3, 5 }, new double[] { 3, 4 } )
		};
		final double[][] inside = { { 2, 4 }, { 5, 8 } };
		for ( int i = 0; i < 2; i++ )
		{
			final CompositeRegionOfInterest c = new CompositeRegionOfInterest( rois[ 0 ] );
			c.or( rois[ 1 ] );
			c.remove( rois[ i ] );
			for ( int j = 0; j < 2; j++ )
			{
				if ( i == j )
				{
					assertOutside( c, inside[ j ] );
				}
				else
				{
					assertInside( c, inside[ j ] );
				}
			}
		}
	}

	/**
	 * Test method for
	 * {@link net.imglib2.roi.CompositeRegionOfInterest#and(net.imglib2.roi.RegionOfInterest)}
	 * .
	 */
	@SuppressWarnings( "deprecation" )
	@Test
	public void testAnd()
	{
		final CompositeRegionOfInterest c = new CompositeRegionOfInterest( new RectangleRegionOfInterest( new double[] { 1, 2 }, new double[] { 3, 4 } ) );
		c.and( new RectangleRegionOfInterest( new double[] { 3, 5 }, new double[] { 3, 4 } ) );
		assertOutside( c, 2, 3 );
		assertOutside( c, 5, 8 );
		assertInside( c, 3.5, 5.5 );
		assertOutside( c, 0, 0 );
	}

	/**
	 * Test method for
	 * {@link net.imglib2.roi.CompositeRegionOfInterest#xor(net.imglib2.roi.RegionOfInterest)}
	 * .
	 */
	@SuppressWarnings( "deprecation" )
	@Test
	public void testXor()
	{
		final CompositeRegionOfInterest c = new CompositeRegionOfInterest( new RectangleRegionOfInterest( new double[] { 1, 2 }, new double[] { 3, 4 } ) );
		c.xor( new RectangleRegionOfInterest( new double[] { 3, 5 }, new double[] { 3, 4 } ) );
		assertInside( c, 2, 3 );
		assertInside( c, 5, 8 );
		assertOutside( c, 3.5, 5.5 );
		assertOutside( c, 0, 0 );
	}

	/**
	 * Test method for
	 * {@link net.imglib2.roi.CompositeRegionOfInterest#not(net.imglib2.roi.RegionOfInterest)}
	 * .
	 */
	@SuppressWarnings( "deprecation" )
	@Test
	public void testNot()
	{
		final CompositeRegionOfInterest c = new CompositeRegionOfInterest( new RectangleRegionOfInterest( new double[] { 1, 2 }, new double[] { 3, 4 } ) );
		c.not( new RectangleRegionOfInterest( new double[] { 3, 5 }, new double[] { 3, 4 } ) );
		assertInside( c, 2, 3 );
		assertOutside( c, 5, 8 );
		assertOutside( c, 3.5, 5.5 );
		assertOutside( c, 0, 0 );
	}

	/*
	 * Regression test of trak # 704
	 */
	@SuppressWarnings( "deprecation" )
	@Test
	public void testRealMin()
	{
		final CompositeRegionOfInterest c = new CompositeRegionOfInterest( new RectangleRegionOfInterest( new double[] { 1, 2 }, new double[] { 3, 4 } ) );
		c.or( new RectangleRegionOfInterest( new double[] { 3, 5 }, new double[] { 3, 4 } ) );
		assertEquals( 1, c.realMin( 0 ), 0 );
		assertEquals( 2, c.realMin( 1 ), 0 );
	}

	/*
	 * Regression test of trak # 704
	 */
	@SuppressWarnings( "deprecation" )
	@Test
	public void testRealMax()
	{
		final CompositeRegionOfInterest c = new CompositeRegionOfInterest( new RectangleRegionOfInterest( new double[] { 1, 2 }, new double[] { 3, 4 } ) );
		c.or( new RectangleRegionOfInterest( new double[] { 3, 5 }, new double[] { 3, 4 } ) );
		assertEquals( 6, c.realMax( 0 ), 0 );
		assertEquals( 9, c.realMax( 1 ), 0 );
	}
}
