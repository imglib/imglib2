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

package net.imglib2.img.transform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.Point;
import net.imglib2.RandomAccess;
import net.imglib2.RealPoint;
import net.imglib2.img.Img;
import net.imglib2.img.NativeImg;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.basictypeaccess.array.IntArray;
import net.imglib2.type.numeric.integer.IntType;

import org.junit.Test;

/**
 * 
 * @author leek
 */
public class ImgTranslationAdapterTest
{

	private Img< IntType > makeImage( final int[][] imgArray )
	{
		final NativeImg< IntType, IntArray > img = new ArrayImgFactory< IntType >().createIntInstance(
				new long[] { imgArray[ 0 ].length, imgArray.length }, 1 );
		final IntType t = new IntType( img );
		img.setLinkedType( t );
		final RandomAccess< IntType > ra = img.randomAccess();
		for ( int i = 0; i < imgArray.length; i++ )
		{
			ra.setPosition( i, 1 );
			for ( int j = 0; j < imgArray[ i ].length; j++ )
			{
				ra.setPosition( j, 0 );
				ra.get().set( imgArray[ i ][ j ] );
			}
		}
		return img;
	}

	/**
	 * Test method for
	 * {@link net.imglib2.img.transform.ImgTranslationAdapter#ImgTranslationAdapter(net.imglib2.img.Img)}
	 * .
	 */
	@Test
	public void testImgTranslationAdapterI()
	{
		final ImgTranslationAdapter< IntType, Img< IntType >> t = new ImgTranslationAdapter< IntType, Img< IntType >>( makeImage( new int[][] { { 1 } } ) );
		assertEquals( t.getLongPosition( 0 ), 0 );
		assertEquals( t.getLongPosition( 1 ), 0 );
	}

	/**
	 * Test method for
	 * {@link net.imglib2.img.transform.ImgTranslationAdapter#ImgTranslationAdapter(net.imglib2.img.Img, long[])}
	 * .
	 */
	@Test
	public void testImgTranslationAdapterILongArray()
	{
		final ImgTranslationAdapter< IntType, Img< IntType >> t = new ImgTranslationAdapter< IntType, Img< IntType >>(
				makeImage( new int[][] { { 1 } } ), new long[] { 2, 3 } );
		assertEquals( t.getLongPosition( 0 ), 2 );
		assertEquals( t.getLongPosition( 1 ), 3 );
	}

	/**
	 * Test method for
	 * {@link net.imglib2.img.transform.ImgTranslationAdapter#ImgTranslationAdapter(net.imglib2.img.Img, net.imglib2.Localizable)}
	 * .
	 */
	@Test
	public void testImgTranslationAdapterILocalizable()
	{
		final ImgTranslationAdapter< IntType, Img< IntType >> t = new ImgTranslationAdapter< IntType, Img< IntType >>(
				makeImage( new int[][] { { 1 } } ), new Point( new long[] { 2, 3 } ) );
		assertEquals( t.getLongPosition( 0 ), 2 );
		assertEquals( t.getLongPosition( 1 ), 3 );
	}

	/**
	 * Test method for
	 * {@link net.imglib2.img.transform.ImgTranslationAdapter#randomAccess()}.
	 */
	@Test
	public void testRandomAccess()
	{
		final ImgTranslationAdapter< IntType, Img< IntType >> t = new ImgTranslationAdapter< IntType, Img< IntType >>(
				makeImage( new int[][] { { 1, 2 }, { 3, 4 } } ), new long[] { 2, 3 } );
		final RandomAccess< IntType > ra = t.randomAccess();
		ra.setPosition( new long[] { 2, 3 } );
		assertEquals( ra.get().get(), 1 );
		ra.setPosition( new long[] { 3, 3 } );
		assertEquals( ra.get().get(), 2 );
		ra.setPosition( new long[] { 2, 4 } );
		assertEquals( ra.get().get(), 3 );
		ra.setPosition( new long[] { 3, 4 } );
		assertEquals( ra.get().get(), 4 );
	}

	/**
	 * Test method for
	 * {@link net.imglib2.img.transform.ImgTranslationAdapter#randomAccess(net.imglib2.Interval)}
	 * .
	 */
	@Test
	public void testRandomAccessInterval()
	{
		final ImgTranslationAdapter< IntType, Img< IntType >> t = new ImgTranslationAdapter< IntType, Img< IntType >>(
				makeImage( new int[][] { { 1, 2 }, { 3, 4 } } ), new long[] { 2, 3 } );
		final RandomAccess< IntType > ra = t.randomAccess( new FinalInterval( new long[] { 2, 3 }, new long[] { 4, 5 } ) );
		ra.setPosition( new long[] { 2, 3 } );
		assertEquals( ra.get().get(), 1 );
		ra.setPosition( new long[] { 3, 3 } );
		assertEquals( ra.get().get(), 2 );
		ra.setPosition( new long[] { 2, 4 } );
		assertEquals( ra.get().get(), 3 );
		ra.setPosition( new long[] { 3, 4 } );
		assertEquals( ra.get().get(), 4 );
	}

	/**
	 * Test method for
	 * {@link net.imglib2.img.transform.ImgTranslationAdapter#min(int)}.
	 */
	@Test
	public void testMinInt()
	{
		final ImgTranslationAdapter< IntType, Img< IntType >> t = new ImgTranslationAdapter< IntType, Img< IntType >>(
				makeImage( new int[][] { { 1, 2 }, { 3, 4 } } ), new long[] { 2, 3 } );
		assertEquals( t.min( 0 ), 2 );
		assertEquals( t.min( 1 ), 3 );
	}

	/**
	 * Test method for
	 * {@link net.imglib2.img.transform.ImgTranslationAdapter#min(long[])}.
	 */
	@Test
	public void testMinLongArray()
	{
		final ImgTranslationAdapter< IntType, Img< IntType >> t = new ImgTranslationAdapter< IntType, Img< IntType >>(
				makeImage( new int[][] { { 1, 2 }, { 3, 4 } } ), new long[] { 2, 3 } );
		final long[] min = new long[ 2 ];
		t.min( min );
		assertEquals( min[ 0 ], 2 );
		assertEquals( min[ 1 ], 3 );
	}

	/**
	 * Test method for
	 * {@link net.imglib2.img.transform.ImgTranslationAdapter#min(net.imglib2.Positionable)}
	 * .
	 */
	@Test
	public void testMinPositionable()
	{
		final ImgTranslationAdapter< IntType, Img< IntType >> t = new ImgTranslationAdapter< IntType, Img< IntType >>(
				makeImage( new int[][] { { 1, 2 }, { 3, 4 } } ), new long[] { 2, 3 } );
		final Point min = new Point( 2 );
		t.min( min );
		assertEquals( min.getIntPosition( 0 ), 2 );
		assertEquals( min.getIntPosition( 1 ), 3 );
	}

	/**
	 * Test method for
	 * {@link net.imglib2.img.transform.ImgTranslationAdapter#max(int)}.
	 */
	@Test
	public void testMaxInt()
	{
		final ImgTranslationAdapter< IntType, Img< IntType >> t = new ImgTranslationAdapter< IntType, Img< IntType >>(
				makeImage( new int[][] { { 1, 2 }, { 3, 4 } } ), new long[] { 2, 3 } );
		assertEquals( t.max( 0 ), 3 );
		assertEquals( t.max( 1 ), 4 );
	}

	/**
	 * Test method for
	 * {@link net.imglib2.img.transform.ImgTranslationAdapter#max(long[])}.
	 */
	@Test
	public void testMaxLongArray()
	{
		final ImgTranslationAdapter< IntType, Img< IntType >> t = new ImgTranslationAdapter< IntType, Img< IntType >>(
				makeImage( new int[][] { { 1, 2 }, { 3, 4 } } ), new long[] { 2, 3 } );
		final long[] max = new long[ 2 ];
		t.max( max );
		assertEquals( max[ 0 ], 3 );
		assertEquals( max[ 1 ], 4 );
	}

	/**
	 * Test method for
	 * {@link net.imglib2.img.transform.ImgTranslationAdapter#max(net.imglib2.Positionable)}
	 * .
	 */
	@Test
	public void testMaxPositionable()
	{
		final ImgTranslationAdapter< IntType, Img< IntType >> t = new ImgTranslationAdapter< IntType, Img< IntType >>(
				makeImage( new int[][] { { 1, 2 }, { 3, 4 } } ), new long[] { 2, 3 } );
		final Point max = new Point( 2 );
		t.max( max );
		assertEquals( max.getIntPosition( 0 ), 3 );
		assertEquals( max.getIntPosition( 1 ), 4 );
	}

	/**
	 * Test method for
	 * {@link net.imglib2.img.transform.ImgTranslationAdapter#dimensions(long[])}
	 * .
	 */
	@Test
	public void testDimensions()
	{
		final ImgTranslationAdapter< IntType, Img< IntType >> t = new ImgTranslationAdapter< IntType, Img< IntType >>(
				makeImage( new int[][] { { 1, 2 }, { 3, 4 }, { 5, 6 } } ), new long[] { 2, 3 } );
		final long[] dimensions = new long[ 2 ];
		t.dimensions( dimensions );
		assertEquals( dimensions[ 0 ], 2 );
		assertEquals( dimensions[ 1 ], 3 );
	}

	/**
	 * Test method for
	 * {@link net.imglib2.img.transform.ImgTranslationAdapter#dimension(int)}.
	 */
	@Test
	public void testDimension()
	{
		final ImgTranslationAdapter< IntType, Img< IntType >> t = new ImgTranslationAdapter< IntType, Img< IntType >>(
				makeImage( new int[][] { { 1, 2 }, { 3, 4 }, { 5, 6 } } ), new long[] { 2, 3 } );
		assertEquals( t.dimension( 0 ), 2 );
		assertEquals( t.dimension( 1 ), 3 );
	}

	/**
	 * Test method for
	 * {@link net.imglib2.img.transform.ImgTranslationAdapter#realMin(int)}.
	 */
	@Test
	public void testRealMinInt()
	{
		final ImgTranslationAdapter< IntType, Img< IntType >> t = new ImgTranslationAdapter< IntType, Img< IntType >>(
				makeImage( new int[][] { { 1, 2 }, { 3, 4 }, { 5, 6 } } ), new long[] { 2, 3 } );
		assertEquals( t.realMin( 0 ), 2.0, 0 );
		assertEquals( t.realMin( 1 ), 3.0, 0 );
	}

	/**
	 * Test method for
	 * {@link net.imglib2.img.transform.ImgTranslationAdapter#realMin(double[])}
	 * .
	 */
	@Test
	public void testRealMinDoubleArray()
	{
		final ImgTranslationAdapter< IntType, Img< IntType >> t = new ImgTranslationAdapter< IntType, Img< IntType >>(
				makeImage( new int[][] { { 1, 2 }, { 3, 4 }, { 5, 6 } } ), new long[] { 2, 3 } );
		final double[] min = new double[ 2 ];
		t.realMin( min );
		assertEquals( min[ 0 ], 2.0, 0 );
		assertEquals( min[ 1 ], 3.0, 0 );
	}

	/**
	 * Test method for
	 * {@link net.imglib2.img.transform.ImgTranslationAdapter#realMin(net.imglib2.RealPositionable)}
	 * .
	 */
	@Test
	public void testRealMinRealPositionable()
	{
		final ImgTranslationAdapter< IntType, Img< IntType >> t = new ImgTranslationAdapter< IntType, Img< IntType >>(
				makeImage( new int[][] { { 1, 2 }, { 3, 4 }, { 5, 6 } } ), new long[] { 2, 3 } );
		final RealPoint min = new RealPoint( 2 );
		t.realMin( min );
		assertEquals( min.getDoublePosition( 0 ), 2.0, 0 );
		assertEquals( min.getDoublePosition( 1 ), 3.0, 0 );
	}

	/**
	 * Test method for
	 * {@link net.imglib2.img.transform.ImgTranslationAdapter#realMax(int)}.
	 */
	@Test
	public void testRealMaxInt()
	{
		final ImgTranslationAdapter< IntType, Img< IntType >> t = new ImgTranslationAdapter< IntType, Img< IntType >>(
				makeImage( new int[][] { { 1, 2 }, { 3, 4 } } ), new long[] { 2, 3 } );
		assertEquals( t.realMax( 0 ), 3.0, 0 );
		assertEquals( t.realMax( 1 ), 4.0, 0 );
	}

	/**
	 * Test method for
	 * {@link net.imglib2.img.transform.ImgTranslationAdapter#realMax(double[])}
	 * .
	 */
	@Test
	public void testRealMaxDoubleArray()
	{
		final ImgTranslationAdapter< IntType, Img< IntType >> t = new ImgTranslationAdapter< IntType, Img< IntType >>(
				makeImage( new int[][] { { 1, 2 }, { 3, 4 } } ), new long[] { 2, 3 } );
		final double[] max = new double[ 2 ];
		t.realMax( max );
		assertEquals( max[ 0 ], 3.0, 0 );
		assertEquals( max[ 1 ], 4.0, 0 );
	}

	/**
	 * Test method for
	 * {@link net.imglib2.img.transform.ImgTranslationAdapter#realMax(net.imglib2.RealPositionable)}
	 * .
	 */
	@Test
	public void testRealMaxRealPositionable()
	{
		final ImgTranslationAdapter< IntType, Img< IntType >> t = new ImgTranslationAdapter< IntType, Img< IntType >>(
				makeImage( new int[][] { { 1, 2 }, { 3, 4 } } ), new long[] { 2, 3 } );
		final RealPoint max = new RealPoint( 2 );
		t.realMax( max );
		assertEquals( max.getDoublePosition( 0 ), 3.0, 0 );
		assertEquals( max.getDoublePosition( 1 ), 4.0, 0 );
	}

	/**
	 * Test method for
	 * {@link net.imglib2.img.transform.ImgTranslationAdapter#cursor()}.
	 */
	@Test
	public void testCursor()
	{
		final ImgTranslationAdapter< IntType, Img< IntType >> t = new ImgTranslationAdapter< IntType, Img< IntType >>(
				makeImage( new int[][] { { 1, 2 }, { 3, 4 } } ), new long[] { 2, 3 } );
		t.cursor();
	}

	/**
	 * Test method for
	 * {@link net.imglib2.img.transform.ImgTranslationAdapter#localizingCursor()}
	 * .
	 */
	@Test
	public void testLocalizingCursor()
	{
		final ImgTranslationAdapter< IntType, Img< IntType >> t = new ImgTranslationAdapter< IntType, Img< IntType >>(
				makeImage( new int[][] { { 1, 2 }, { 3, 4 } } ), new long[] { 2, 3 } );
		t.localizingCursor();
	}

	/**
	 * Test method for
	 * {@link net.imglib2.img.transform.ImgTranslationAdapter#size()}.
	 */
	@Test
	public void testSize()
	{
		final ImgTranslationAdapter< IntType, Img< IntType >> t = new ImgTranslationAdapter< IntType, Img< IntType >>(
				makeImage( new int[][] { { 1, 2 }, { 3, 4 } } ), new long[] { 2, 3 } );
		assertEquals( t.size(), 4 );
	}

	/**
	 * Test method for
	 * {@link net.imglib2.img.transform.ImgTranslationAdapter#firstElement()}.
	 */
	@Test
	public void testFirstElement()
	{
		final ImgTranslationAdapter< IntType, Img< IntType >> t = new ImgTranslationAdapter< IntType, Img< IntType >>(
				makeImage( new int[][] { { 5, 2 }, { 3, 4 } } ), new long[] { 2, 3 } );
		final IntType first = t.firstElement();
		assertEquals( first.get(), 5 );
	}

	/**
	 * Test method for
	 * {@link net.imglib2.img.transform.ImgTranslationAdapter#iterator()}.
	 */
	@Test
	public void testIterator()
	{
		final ImgTranslationAdapter< IntType, Img< IntType >> t = new ImgTranslationAdapter< IntType, Img< IntType >>(
				makeImage( new int[][] { { 1, 2 }, { 3, 4 } } ), new long[] { 2, 3 } );
		int count = 0;
		for ( final IntType i : t )
		{
			assertEquals( i.get(), count + 1 );
			count++;
		}
		assertEquals( count, 4 );
	}

	/**
	 * Test method for
	 * {@link net.imglib2.img.transform.ImgTranslationAdapter#factory()}.
	 */
	@Test
	public void testFactory()
	{
		final ImgTranslationAdapter< IntType, Img< IntType >> t = new ImgTranslationAdapter< IntType, Img< IntType >>(
				makeImage( new int[][] { { 1, 2 }, { 3, 4 } } ), new long[] { 2, 3 } );
		t.factory();
	}

	/******************************************************
	 * Cursor tests
	 ******************************************************/
	/**
	 * Test method for {@link net.imglib2.Cursor#copyCursor()}.
	 */
	@Test
	public void testCopyCursor()
	{
		final ImgTranslationAdapter< IntType, Img< IntType >> t = new ImgTranslationAdapter< IntType, Img< IntType >>(
				makeImage( new int[][] { { 1, 2 }, { 3, 4 } } ), new long[] { 2, 5 } );
		final Cursor< IntType > c1 = t.cursor();
		final Cursor< IntType > c2 = c1.copyCursor();
		c1.next();
		c2.next();
		c2.next();
		assertEquals( c2.get().get(), 2 );
		assertEquals( c2.getLongPosition( 0 ), 3 );
		assertEquals( c2.getLongPosition( 1 ), 5 );
	}

	/**
	 * Test method for {@link net.imglib2.Localizable#localize(int[])}.
	 */
	@Test
	public void testLocalizeIntArray()
	{
		final Cursor< IntType > c = new ImgTranslationAdapter< IntType, Img< IntType >>(
				makeImage( new int[][] { { 1, 2 }, { 3, 4 } } ), new long[] { 2, 5 } ).cursor();
		final int[] position = new int[ 2 ];
		c.next();
		c.localize( position );
		assertEquals( position[ 0 ], 2 );
		assertEquals( position[ 1 ], 5 );
		c.next();
		c.localize( position );
		assertEquals( position[ 0 ], 3 );
		assertEquals( position[ 1 ], 5 );
		c.next();
		c.localize( position );
		assertEquals( position[ 0 ], 2 );
		assertEquals( position[ 1 ], 6 );
		c.next();
		c.localize( position );
		assertEquals( position[ 0 ], 3 );
		assertEquals( position[ 1 ], 6 );
	}

	/**
	 * Test method for {@link net.imglib2.Localizable#localize(long[])}.
	 */
	@Test
	public void testLocalizeLongArray()
	{
		final Cursor< IntType > c = new ImgTranslationAdapter< IntType, Img< IntType >>(
				makeImage( new int[][] { { 1, 2 }, { 3, 4 } } ), new long[] { 2, 5 } ).cursor();
		final long[] position = new long[ 2 ];
		c.next();
		c.localize( position );
		assertEquals( position[ 0 ], 2 );
		assertEquals( position[ 1 ], 5 );
	}

	/**
	 * Test method for {@link net.imglib2.Localizable#getIntPosition(int)}.
	 */
	@Test
	public void testGetIntPosition()
	{
		final Cursor< IntType > c = new ImgTranslationAdapter< IntType, Img< IntType >>(
				makeImage( new int[][] { { 1, 2 }, { 3, 4 } } ), new long[] { 2, 5 } ).cursor();
		c.next();
		assertEquals( c.getIntPosition( 0 ), 2 );
		assertEquals( c.getIntPosition( 1 ), 5 );
	}

	/**
	 * Test method for {@link net.imglib2.Localizable#getLongPosition(int)}.
	 */
	@Test
	public void testGetLongPosition()
	{
		final Cursor< IntType > c = new ImgTranslationAdapter< IntType, Img< IntType >>(
				makeImage( new int[][] { { 1, 2 }, { 3, 4 } } ), new long[] { 2, 5 } ).cursor();
		c.next();
		assertEquals( c.getLongPosition( 0 ), 2 );
		assertEquals( c.getLongPosition( 1 ), 5 );
	}

	/**
	 * Test method for {@link net.imglib2.RealLocalizable#localize(float[])}.
	 */
	@Test
	public void testLocalizeFloatArray()
	{
		final Cursor< IntType > c = new ImgTranslationAdapter< IntType, Img< IntType >>(
				makeImage( new int[][] { { 1, 2 }, { 3, 4 } } ), new long[] { 2, 5 } ).cursor();
		final float[] position = new float[ 2 ];
		c.next();
		c.localize( position );
		assertEquals( position[ 0 ], 2, 0 );
		assertEquals( position[ 1 ], 5, 0 );
	}

	/**
	 * Test method for {@link net.imglib2.RealLocalizable#localize(double[])}.
	 */
	@Test
	public void testLocalizeDoubleArray()
	{
		final Cursor< IntType > c = new ImgTranslationAdapter< IntType, Img< IntType >>(
				makeImage( new int[][] { { 1, 2 }, { 3, 4 } } ), new long[] { 2, 5 } ).cursor();
		final double[] position = new double[ 2 ];
		c.next();
		c.localize( position );
		assertEquals( position[ 0 ], 2, 0 );
		assertEquals( position[ 1 ], 5, 0 );
	}

	/**
	 * Test method for {@link net.imglib2.RealLocalizable#getFloatPosition(int)}
	 * .
	 */
	@Test
	public void testGetFloatPosition()
	{
		final Cursor< IntType > c = new ImgTranslationAdapter< IntType, Img< IntType >>(
				makeImage( new int[][] { { 1, 2 }, { 3, 4 } } ), new long[] { 2, 5 } ).cursor();
		c.next();
		assertEquals( c.getFloatPosition( 0 ), 2, 0 );
		assertEquals( c.getFloatPosition( 1 ), 5, 0 );
	}

	/**
	 * Test method for
	 * {@link net.imglib2.RealLocalizable#getDoublePosition(int)}.
	 */
	@Test
	public void testGetDoublePosition()
	{
		final Cursor< IntType > c = new ImgTranslationAdapter< IntType, Img< IntType >>(
				makeImage( new int[][] { { 1, 2 }, { 3, 4 } } ), new long[] { 2, 5 } ).cursor();
		c.next();
		assertEquals( c.getDoublePosition( 0 ), 2, 0 );
		assertEquals( c.getDoublePosition( 1 ), 5, 0 );
	}

	/**
	 * Test method for {@link net.imglib2.Sampler#get()}.
	 */
	@Test
	public void testGet()
	{
		final Cursor< IntType > c = new ImgTranslationAdapter< IntType, Img< IntType >>(
				makeImage( new int[][] { { 1, 2 }, { 3, 4 } } ), new long[] { 2, 5 } ).cursor();
		c.next();
		assertEquals( c.get().get(), 1 );
		c.next();
		assertEquals( c.get().get(), 2 );
		c.next();
		assertEquals( c.get().get(), 3 );
		c.next();
		assertEquals( c.get().get(), 4 );
	}

	/**
	 * Test method for {@link net.imglib2.Iterator#hasNext()}.
	 */
	@Test
	public void testHasNext()
	{
		final Cursor< IntType > c = new ImgTranslationAdapter< IntType, Img< IntType >>(
				makeImage( new int[][] { { 1, 2 }, { 3, 4 } } ), new long[] { 2, 5 } ).cursor();
		for ( int i = 0; i < 4; i++ )
		{
			assertTrue( c.hasNext() );
			c.next();
		}
		assertFalse( c.hasNext() );
	}

	/**
	 * Test method for {@link net.imglib2.Iterator#jumpFwd(long)}.
	 */
	@Test
	public void testJumpFwd()
	{
		final Cursor< IntType > c = new ImgTranslationAdapter< IntType, Img< IntType >>(
				makeImage( new int[][] { { 1, 2 }, { 3, 4 } } ), new long[] { 2, 5 } ).cursor();
		c.next();
		c.jumpFwd( 2 );
		assertTrue( c.hasNext() );
		assertEquals( c.getIntPosition( 0 ), 2 );
		assertEquals( c.getIntPosition( 1 ), 6 );
	}

	/**
	 * Test method for {@link net.imglib2.Iterator#fwd()}.
	 */
	@Test
	public void testFwd()
	{
		final Cursor< IntType > c = new ImgTranslationAdapter< IntType, Img< IntType >>(
				makeImage( new int[][] { { 1, 2 }, { 3, 4 } } ), new long[] { 2, 5 } ).cursor();
		c.next();
		c.fwd();
		assertTrue( c.hasNext() );
		assertEquals( c.getIntPosition( 0 ), 3 );
		assertEquals( c.getIntPosition( 1 ), 5 );
	}

	/**
	 * Test method for {@link net.imglib2.Iterator#reset()}.
	 */
	@Test
	public void testReset()
	{
		final Cursor< IntType > c = new ImgTranslationAdapter< IntType, Img< IntType >>(
				makeImage( new int[][] { { 1, 2 }, { 3, 4 } } ), new long[] { 2, 5 } ).cursor();
		c.next();
		c.jumpFwd( 3 );
		assertFalse( c.hasNext() );
		c.reset();
		assertTrue( c.hasNext() );
		c.next();
		assertEquals( c.getIntPosition( 0 ), 2 );
		assertEquals( c.getIntPosition( 1 ), 5 );
	}

	/**
	 * Test method for {@link net.imglib2.EuclideanSpace#numDimensions()}.
	 */
	@Test
	public void testNumDimensions()
	{
		final Cursor< IntType > c = new ImgTranslationAdapter< IntType, Img< IntType >>(
				makeImage( new int[][] { { 1, 2 }, { 3, 4 } } ), new long[] { 2, 5 } ).cursor();
		assertEquals( c.numDimensions(), 2 );
	}

}
