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

import java.util.Random;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
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
public class BinaryMaskRegionOfInterestTest
{
	interface MaskValue
	{
		public boolean v( Localizable l );
	}

	static class RandomValue implements MaskValue
	{
		Random r = new Random( 1492 );

		@Override
		public boolean v( final Localizable l )
		{
			return r.nextBoolean();
		}
	}

	static class ROIValue implements MaskValue
	{
		final RealRandomAccess< BitType > ra;

		ROIValue( final RegionOfInterest roi )
		{
			this.ra = roi.realRandomAccess();
		}

		@Override
		public boolean v( final Localizable l )
		{
			ra.setPosition( l );
			return ra.get().get();
		}
	}

	static Img< BitType > getMask( final long[] dim, final MaskValue mv )
	{
		final Img< BitType > img = new ArrayImgFactory< BitType >().create( dim, new BitType() );
		final Cursor< BitType > c = img.localizingCursor();
		while ( c.hasNext() )
		{
			final BitType t = c.next();
			t.set( mv.v( c ) );
		}
		return img;
	}

	@Test
	public void testConstructor()
	{
		final BinaryMaskRegionOfInterest< BitType, Img< BitType >> x = new BinaryMaskRegionOfInterest< BitType, Img< BitType >>(
				getMask( new long[] { 23, 32 }, new RandomValue() ) );
		assertEquals( 2, x.numDimensions() );
	}

	@Test
	public void testNumDimensions()
	{
		for ( final long[] dims : new long[][] { { 23, 32 }, { 2, 3, 4 }, { 43, 12, 3, 6 } } )
		{
			final BinaryMaskRegionOfInterest< BitType, Img< BitType >> x = new BinaryMaskRegionOfInterest< BitType, Img< BitType >>(
					getMask( dims, new RandomValue() ) );
			assertEquals( dims.length, x.numDimensions() );
		}
	}

	@Test
	public void testRealMin()
	{
		final RegionOfInterest roi = new RectangleRegionOfInterest( new double[] { 1.1, 2.2, 3.3 }, new double[] { 4.4, 5.5, 9.9 } );
		final BinaryMaskRegionOfInterest< BitType, Img< BitType >> x = new BinaryMaskRegionOfInterest< BitType, Img< BitType >>(
				getMask( new long[] { 15, 15, 15 }, new ROIValue( roi ) ) );
		assertEquals( x.realMin( 0 ), 2, 0 );
		assertEquals( x.realMin( 1 ), 3, 0 );
		assertEquals( x.realMin( 2 ), 4, 0 );
	}

	@Test
	public void testRealMax()
	{
		final RegionOfInterest roi = new RectangleRegionOfInterest( new double[] { 1.1, 2.2, 3.3 }, new double[] { 4.4, 5.5, 9.9 } );
		final BinaryMaskRegionOfInterest< BitType, Img< BitType >> x = new BinaryMaskRegionOfInterest< BitType, Img< BitType >>(
				getMask( new long[] { 15, 15, 15 }, new ROIValue( roi ) ) );
		assertEquals( x.realMax( 0 ), 5, 0 );
		assertEquals( x.realMax( 1 ), 7, 0 );
		assertEquals( x.realMax( 2 ), 13, 0 );
	}

	@Test
	public void testIsMember()
	{
		final long[] dims = { 10, 11, 12 };
		final Img< BitType > mask = getMask( dims, new RandomValue() );
		final BinaryMaskRegionOfInterest< BitType, Img< BitType >> x = new BinaryMaskRegionOfInterest< BitType, Img< BitType >>( mask );
		final Random r = new Random( 1086 );
		final RandomAccess< BitType > raMask = mask.randomAccess();
		final RealRandomAccess< BitType > raROI = x.realRandomAccess();
		for ( int iteration = 0; iteration < 100; iteration++ )
		{
			final long[] position = { r.nextInt( ( int ) dims[ 0 ] ), r.nextInt( ( int ) dims[ 1 ] ), r.nextInt( ( int ) dims[ 2 ] ) };
			raMask.setPosition( position );
			final boolean value = raMask.get().get();
			raROI.setPosition( position );
			assertEquals( value, raROI.get().get() );
		}
	}

	@Test
	public void testMin()
	{
		final long[] dims = { 15, 16, 17 };
		final Img< BitType > mask = getMask( dims, new RandomValue() );
		final RegionOfInterest roi = new RectangleRegionOfInterest( new double[] { 1.1, 2.2, 3.3 }, new double[] { 4.4, 5.5, 9.9 } );
		final BinaryMaskRegionOfInterest< BitType, Img< BitType >> x = new BinaryMaskRegionOfInterest< BitType, Img< BitType >>(
				getMask( dims, new ROIValue( roi ) ) );
		final IterableInterval< ? extends BitType > ii = x.getIterableIntervalOverROI( mask );
		assertEquals( ii.min( 0 ), 2 );
		assertEquals( ii.min( 1 ), 3 );
		assertEquals( ii.min( 2 ), 4 );
	}

	@Test
	public void testMax()
	{
		final long[] dims = { 15, 16, 17 };
		final Img< BitType > mask = getMask( dims, new RandomValue() );
		final RegionOfInterest roi = new RectangleRegionOfInterest( new double[] { 1.1, 2.2, 3.3 }, new double[] { 4.4, 5.5, 9.9 } );
		final BinaryMaskRegionOfInterest< BitType, Img< BitType >> x = new BinaryMaskRegionOfInterest< BitType, Img< BitType >>(
				getMask( dims, new ROIValue( roi ) ) );
		final IterableInterval< ? extends BitType > ii = x.getIterableIntervalOverROI( mask );
		assertEquals( ii.max( 0 ), 5 );
		assertEquals( ii.max( 1 ), 7 );
		assertEquals( ii.max( 2 ), 13 );
	}

	/*
	 * Regression test of #704
	 */
	@Test
	public void testMaxLongArray()
	{
		final long[] dims = { 15, 16, 17 };
		final Img< BitType > mask = getMask( dims, new RandomValue() );
		final RegionOfInterest roi = new RectangleRegionOfInterest( new double[] { 1.1, 2.2, 3.3 }, new double[] { 4.4, 5.5, 9.9 } );
		final BinaryMaskRegionOfInterest< BitType, Img< BitType >> x = new BinaryMaskRegionOfInterest< BitType, Img< BitType >>(
				getMask( dims, new ROIValue( roi ) ) );
		final IterableInterval< ? extends BitType > ii = x.getIterableIntervalOverROI( mask );
		final long[] result = new long[ 3 ];
		ii.max( result );
		assertEquals( result[ 0 ], 5 );
		assertEquals( result[ 1 ], 7 );
		assertEquals( result[ 2 ], 13 );

	}

	@Test
	public void testFirstElement()
	{
		final long[] dims = { 10, 11, 12 };
		final Img< IntType > img = new ArrayImgFactory< IntType >().create( dims, new IntType() );
		final RandomAccess< IntType > raImg = img.randomAccess();
		raImg.setPosition( new long[] { 2, 3, 4 } );
		raImg.get().set( 1234 );
		final RegionOfInterest roi = new RectangleRegionOfInterest( new double[] { 1.1, 2.2, 3.3 }, new double[] { 4.4, 5.5, 9.9 } );
		final BinaryMaskRegionOfInterest< BitType, Img< BitType >> x = new BinaryMaskRegionOfInterest< BitType, Img< BitType >>(
				getMask( dims, new ROIValue( roi ) ) );
		final IterableInterval< ? extends IntType > ii = x.getIterableIntervalOverROI( img );
		final IntType first = ii.firstElement();
		assertEquals( 1234, first.get() );
	}

	@Test
	public void testCursor()
	{
		final long[] dims = { 10, 11, 12 };
		final Img< BitType > mask = getMask( dims, new RandomValue() );
		final BinaryMaskRegionOfInterest< BitType, Img< BitType >> x = new BinaryMaskRegionOfInterest< BitType, Img< BitType >>( mask );
		final Img< IntType > img = new ArrayImgFactory< IntType >().create( dims, new IntType() );
		final Cursor< BitType > c1 = mask.localizingCursor();
		final RandomAccess< IntType > ra = img.randomAccess();
		int index = 1;
		while ( c1.hasNext() )
		{
			if ( c1.next().get() )
			{
				ra.setPosition( c1 );
				ra.get().set( index++ );
			}
		}
		for ( int kase = 0; kase < 2; kase++ )
		{
			int index2 = 1;
			Cursor< IntType > c2;
			switch ( kase )
			{
			case 0:
				c2 = x.getIterableIntervalOverROI( img ).localizingCursor();
				break;
			default:
				c2 = x.getIterableIntervalOverROI( img ).cursor();
				break;
			}
			while ( c2.hasNext() )
			{
				assertEquals( c2.next().get(), index2++ );
			}
			assertEquals( index2, index );
		}
	}

	@Test
	public void testSize()
	{
		final long[] dims = { 10, 11, 12 };
		final Img< BitType > mask = getMask( dims, new RandomValue() );
		final BinaryMaskRegionOfInterest< BitType, Img< BitType >> x = new BinaryMaskRegionOfInterest< BitType, Img< BitType >>( mask );
		final Img< IntType > img = new ArrayImgFactory< IntType >().create( dims, new IntType() );
		int index = 0;
		for ( final BitType t : mask )
		{
			if ( t.get() )
				index++;
		}
		assertEquals( index, x.getIterableIntervalOverROI( img ).size() );
	}
}
