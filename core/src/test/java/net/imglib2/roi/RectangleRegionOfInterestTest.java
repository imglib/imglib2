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

import java.util.Random;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RealPoint;
import net.imglib2.RealRandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;

import org.junit.Test;

/**
 * 
 * @author leek
 */
public class RectangleRegionOfInterestTest
{

	/**
	 * Test method for
	 * {@link net.imglib2.roi.RectangleRegionOfInterest#contains(double[])}.
	 */
	@Test
	public void testIsMember()
	{
		final RectangleRegionOfInterest r = new RectangleRegionOfInterest(
				new double[] { 1.3, 10.5, 2.6 },
				new double[] { 5.6, 2.1, 3.3 } );
		assertTrue( r.contains( new double[] { 1.3, 10.5, 2.6 } ) );
		assertFalse( r.contains( new double[] { 1.3 + 5.6, 10.5 + 2.1, 2.6 + 3.3 } ) );
		assertTrue( r.contains( new double[] { 1.5, 10.8, 2.7 } ) );
	}

	/**
	 * Test method for
	 * {@link net.imglib2.roi.RectangleRegionOfInterest#RectangleRegionOfInterest(double[], double[])}
	 * .
	 */
	@Test
	public void testRectangleRegionOfInterest()
	{
		final RectangleRegionOfInterest r = new RectangleRegionOfInterest(
				new double[] { 1.3, 10.5 },
				new double[] { 5.6, 2.1 } );
		assertEquals( r.numDimensions(), 2 );
	}

	/**
	 * Test method for
	 * {@link net.imglib2.roi.RectangleRegionOfInterest#getOrigin(net.imglib2.RealPositionable)}
	 * .
	 */
	@Test
	public void testGetOriginRealPositionable()
	{
		final RectangleRegionOfInterest r = new RectangleRegionOfInterest(
				new double[] { 1.3, 10.5 },
				new double[] { 5.6, 2.1 } );
		final RealPoint pt = new RealPoint( 2 );
		r.getOrigin( pt );
		assertEquals( pt.getDoublePosition( 0 ), 1.3, 0 );
		assertEquals( pt.getDoublePosition( 1 ), 10.5, 0 );
	}

	/**
	 * Test method for
	 * {@link net.imglib2.roi.RectangleRegionOfInterest#getOrigin(double[])}.
	 */
	@Test
	public void testGetOriginArrayDouble()
	{
		final RectangleRegionOfInterest r = new RectangleRegionOfInterest(
				new double[] { 1.3, 10.5 },
				new double[] { 5.6, 2.1 } );
		final double[] result = new double[ 2 ];
		r.getOrigin( result );
		assertEquals( result[ 0 ], 1.3, 0 );
		assertEquals( result[ 1 ], 10.5, 0 );
	}

	/**
	 * Test method for
	 * {@link net.imglib2.roi.RectangleRegionOfInterest#getOrigin(int)}.
	 */
	@Test
	public void testGetOriginInt()
	{
		final RectangleRegionOfInterest r = new RectangleRegionOfInterest(
				new double[] { 1.3, 10.5 },
				new double[] { 5.6, 2.1 } );
		assertEquals( r.getOrigin( 0 ), 1.3, 0 );
		assertEquals( r.getOrigin( 1 ), 10.5, 0 );
	}

	/**
	 * Test method for
	 * {@link net.imglib2.roi.AbstractIterableRegionOfInterest#getIterableIntervalOverROI(net.imglib2.RandomAccessible)}
	 * .
	 */
	@Test
	public void testGetIterableIntervalOverROI()
	{
		final int width = 27;
		final int height = 16;
		final int depth = 17;
		final Img< IntType > img = new ArrayImgFactory< IntType >().create( new long[] { width, height, depth }, new IntType() );
		final RandomAccess< IntType > a = img.randomAccess();
		for ( int i = 0; i < width; i++ )
		{
			a.setPosition( i, 0 );
			for ( int j = 0; j < height; j++ )
			{
				a.setPosition( j, 1 );
				for ( int k = 0; k < depth; k++ )
				{
					a.setPosition( k, 2 );
					a.get().set( i + j * width + k * width * height );
				}
			}
		}
		final double dimensions[][][] = {
				{ { 1.0, 2.0, 3.0 }, { 5.0, 6.0, 7.0 } },
				{ { 1.5, 2.5, 3.5 }, { 5.0, 6.0, 7.0 } },
				{ { 1.5, 2.5, 3.5 }, { 5.5, 6.5, 7.5 } },
				{ { 1.5, 2.5, 3.5 }, { 5.6, 6.6, 7.6 } } };

		for ( final double[][] dd : dimensions )
		{
			final RectangleRegionOfInterest r = new RectangleRegionOfInterest( dd[ 0 ], dd[ 1 ] );
			final RealRandomAccess< BitType > ra = r.realRandomAccess();
			final boolean mask[][][] = new boolean[ width ][ height ][ depth ];
			for ( int i = 0; i < width; i++ )
			{
				ra.setPosition( i, 0 );
				for ( int j = 0; j < height; j++ )
				{
					ra.setPosition( j, 1 );
					for ( int k = 0; k < depth; k++ )
					{
						ra.setPosition( k, 2 );
						if ( ra.get().get() )
							mask[ i ][ j ][ k ] = true;
					}
				}
			}
			final Cursor< IntType > c = r.getIterableIntervalOverROI( img ).localizingCursor();
			while ( c.hasNext() )
			{
				final int value = c.next().get();
				final int x = value % width;
				final int y = ( ( value - x ) / width ) % height;
				final int z = ( value - x - y * width ) / ( width * height );
				assertTrue( mask[ x ][ y ][ z ] );
				mask[ x ][ y ][ z ] = false;
			}
			for ( int i = 0; i < width; i++ )
			{
				for ( int j = 0; j < height; j++ )
				{
					for ( int k = 0; k < depth; k++ )
					{
						assertFalse( mask[ i ][ j ][ k ] );
					}
				}
			}
		}
	}

	/**
	 * Test method for
	 * {@link net.imglib2.roi.AbstractIterableRegionOfInterest#max(int)}.
	 */
	@Test
	public void testMaxInt()
	{
		final RectangleRegionOfInterest r = new RectangleRegionOfInterest(
				new double[] { 1.3, 10.5, 2.6 },
				new double[] { 5.8, 2.1, 3.4 } );
		assertEquals( r.max( 0 ), 7 );
		assertEquals( r.max( 1 ), 12 );
		assertEquals( r.max( 2 ), 5 );
	}

	/**
	 * Test method for
	 * {@link net.imglib2.roi.AbstractIterableRegionOfInterest#min(int)}.
	 */
	@Test
	public void testMinInt()
	{
		final RectangleRegionOfInterest r = new RectangleRegionOfInterest(
				new double[] { 1.3, 10.5, 2.6 },
				new double[] { 5.8, 2.1, 3.3 } );
		assertEquals( r.min( 0 ), 2 );
		assertEquals( r.min( 1 ), 11 );
		assertEquals( r.min( 2 ), 3 );
	}

	/**
	 * Test method for
	 * {@link net.imglib2.roi.AbstractRegionOfInterest#realMin(int)}.
	 */
	@Test
	public void testRealMinInt()
	{
		final RectangleRegionOfInterest r = new RectangleRegionOfInterest(
				new double[] { 1.3, 10.5, 2.6 },
				new double[] { 5.8, 2.1, 3.3 } );
		assertEquals( r.realMin( 0 ), 1.3, 0 );
		assertEquals( r.realMin( 1 ), 10.5, 0 );
		assertEquals( r.realMin( 2 ), 2.6, 0 );
	}

	/**
	 * Test method for
	 * {@link net.imglib2.roi.AbstractRegionOfInterest#realMax(int)}.
	 */
	@Test
	public void testRealMaxInt()
	{
		final RectangleRegionOfInterest r = new RectangleRegionOfInterest(
				new double[] { 1.3, 10.5, 2.6 },
				new double[] { 5.8, 2.1, 3.3 } );
		assertEquals( r.realMax( 0 ), 1.3 + 5.8, 0 );
		assertEquals( r.realMax( 1 ), 10.5 + 2.1, 0 );
		assertEquals( r.realMax( 2 ), 2.6 + 3.3, 0 );
	}

	/**
	 * Test method for
	 * {@link net.imglib2.roi.AbstractRegionOfInterest#numDimensions()}.
	 */
	@Test
	public void testNumDimensions()
	{
		final RectangleRegionOfInterest r = new RectangleRegionOfInterest(
				new double[] { 1.3, 10.5, 2.6 },
				new double[] { 5.8, 2.1, 3.3 } );
		assertEquals( r.numDimensions(), 3 );
	}

	/**
	 * Test method for
	 * {@link net.imglib2.roi.AbstractRegionOfInterest#realRandomAccess()}.
	 */
	@Test
	public void testRealRandomAccess()
	{
		final double dimensions[][][] = {
				{ { 1.0, 2.0, 3.0 }, { 5.0, 6.0, 7.0 } },
				{ { 1.5, 2.5, 3.5 }, { 5.0, 6.0, 7.0 } },
				{ { 1.5, 2.5, 3.5 }, { 5.5, 6.5, 7.5 } },
				{ { 1.5, 2.5, 3.5 }, { 5.6, 6.6, 7.6 } } };

		final Random random = new Random( 566 );
		for ( final double[][] dd : dimensions )
		{
			final RectangleRegionOfInterest r = new RectangleRegionOfInterest( dd[ 0 ], dd[ 1 ] );
			final RealRandomAccess< BitType > ra = r.realRandomAccess();
			for ( int iteration = 0; iteration < 100; iteration++ )
			{
				final double[] location = {
						random.nextFloat() * 10,
						random.nextFloat() * 10,
						random.nextFloat() * 10 };
				boolean is_inside = true;
				for ( int i = 0; i < 3; i++ )
				{
					if ( location[ i ] < dd[ 0 ][ i ] || location[ i ] >= dd[ 0 ][ i ] + dd[ 1 ][ i ] )
						is_inside = false;
				}
				ra.setPosition( location );
				assertEquals( ra.get().get(), is_inside );
				final long[] llocation = {
						Math.abs( random.nextInt() % 10 ),
						Math.abs( random.nextInt() % 10 ),
						Math.abs( random.nextInt() % 10 ) };
				is_inside = true;
				for ( int i = 0; i < 3; i++ )
				{
					if ( llocation[ i ] < dd[ 0 ][ i ] || llocation[ i ] >= dd[ 0 ][ i ] + dd[ 1 ][ i ] )
						is_inside = false;
				}
				ra.setPosition( llocation );
				assertEquals( ra.get().get(), is_inside );
			}
		}
	}

	@Test
	public void testSetOriginRealLocalizable()
	{
		final RectangleRegionOfInterest r = new RectangleRegionOfInterest(
				new double[] { 1.3, 10.5, 2.6 },
				new double[] { 5.8, 2.1, 3.3 } );
		final RealRandomAccess< BitType > ra = r.realRandomAccess();
		ra.setPosition( new double[] { .4, .6, .8 } );
		assertFalse( ra.get().get() );
		ra.setPosition( new double[] { 6.1, 11.0, 4.0 } );
		assertTrue( ra.get().get() );
		r.setOrigin( new RealPoint( new double[] { .2, .4, .6 } ) );
		ra.setPosition( new double[] { .4, .6, .8 } );
		assertTrue( ra.get().get() );
		ra.setPosition( new double[] { 6.1, 5.0, 4.0 } );
		assertFalse( ra.get().get() );
	}

	@Test
	public void testSetOriginDoubleArray()
	{
		final RectangleRegionOfInterest r = new RectangleRegionOfInterest(
				new double[] { 1.3, 10.5, 2.6 },
				new double[] { 5.8, 2.1, 3.3 } );
		final RealRandomAccess< BitType > ra = r.realRandomAccess();
		ra.setPosition( new double[] { .4, .6, .8 } );
		assertFalse( ra.get().get() );
		ra.setPosition( new double[] { 6.1, 11.0, 4.0 } );
		assertTrue( ra.get().get() );
		r.setOrigin( new double[] { .2, .4, .6 } );
		ra.setPosition( new double[] { .4, .6, .8 } );
		assertTrue( ra.get().get() );
		ra.setPosition( new double[] { 6.1, 5.0, 4.0 } );
		assertFalse( ra.get().get() );
	}

	@Test
	public void testSetOriginDoubleInt()
	{
		final RectangleRegionOfInterest r = new RectangleRegionOfInterest(
				new double[] { 1.3, 10.5, 2.6 },
				new double[] { 5.8, 2.1, 3.3 } );
		final RealRandomAccess< BitType > ra = r.realRandomAccess();
		ra.setPosition( new double[] { .4, .6, .8 } );
		assertFalse( ra.get().get() );
		ra.setPosition( new double[] { 6.1, 11.0, 4.0 } );
		assertTrue( ra.get().get() );
		final double[] test = new double[] { .2, .4, .6 };
		for ( int i = 0; i < 3; i++ )
		{
			r.setOrigin( test[ i ], i );
		}
		ra.setPosition( new double[] { .4, .6, .8 } );
		assertTrue( ra.get().get() );
		ra.setPosition( new double[] { 6.1, 5.0, 4.0 } );
		assertFalse( ra.get().get() );
	}

	@Test
	public void testSetExtentDoubleArray()
	{
		final RectangleRegionOfInterest r = new RectangleRegionOfInterest(
				new double[] { 1.3, 10.5, 2.6 },
				new double[] { 5.8, 2.1, 3.3 } );
		final int width = 27;
		final int height = 16;
		final int depth = 17;
		final Img< IntType > img = new ArrayImgFactory< IntType >().create( new long[] { width, height, depth }, new IntType() );
		assertEquals( r.getIterableIntervalOverROI( img ).size(), 6 * 2 * 3 );
		final double[] test = new double[] { 10, 11, 12 };
		r.setExtent( test );
		assertEquals( r.getIterableIntervalOverROI( img ).size(), 10 * 11 * 12 );
		for ( int i = 0; i < 3; i++ )
			assertEquals( r.realMax( i ), test[ i ] + r.realMin( i ), 0.0 );
	}

	@Test
	public void testSetExtentDoubleInt()
	{
		final RectangleRegionOfInterest r = new RectangleRegionOfInterest(
				new double[] { 1.3, 10.5, 2.6 },
				new double[] { 5.8, 2.1, 3.3 } );
		final int width = 27;
		final int height = 16;
		final int depth = 17;
		final Img< IntType > img = new ArrayImgFactory< IntType >().create( new long[] { width, height, depth }, new IntType() );
		assertEquals( r.getIterableIntervalOverROI( img ).size(), 6 * 2 * 3 );
		final double[] test = new double[] { 10, 11, 12 };
		for ( int i = 0; i < 3; i++ )
		{
			r.setExtent( test[ i ], i );
		}
		assertEquals( r.getIterableIntervalOverROI( img ).size(), 10 * 11 * 12 );
		for ( int i = 0; i < 3; i++ )
			assertEquals( r.realMax( i ), test[ i ] + r.realMin( i ), 0.0 );
	}

	@Test
	public void testGetExtentDoubleArray()
	{
		final double[] extent = new double[] { 5.8, 2.1, 3.3 };
		final RectangleRegionOfInterest r = new RectangleRegionOfInterest(
				new double[] { 1.3, 10.5, 2.6 },
				extent );
		final double[] result = new double[ 3 ];
		r.getExtent( result );
		for ( int i = 0; i < 3; i++ )
		{
			assertEquals( extent[ i ], result[ i ], 0 );
		}
	}

	@Test
	public void testGetExtentInt()
	{
		final double[] extent = new double[] { 5.8, 2.1, 3.3 };
		final RectangleRegionOfInterest r = new RectangleRegionOfInterest(
				new double[] { 1.3, 10.5, 2.6 },
				extent );
		for ( int i = 0; i < 3; i++ )
		{
			assertEquals( extent[ i ], r.getExtent( i ), 0 );
		}
	}

	@Test
	public void testGetExtentRealPositionable()
	{
		final double[] extent = new double[] { 5.8, 2.1, 3.3 };
		final RectangleRegionOfInterest r = new RectangleRegionOfInterest(
				new double[] { 1.3, 10.5, 2.6 },
				extent );
		final RealPoint pt = new RealPoint( extent.length );
		r.getExtent( pt );
		for ( int i = 0; i < extent.length; i++ )
		{
			assertEquals( extent[ i ], pt.getDoublePosition( i ), 0 );
		}
	}
}
