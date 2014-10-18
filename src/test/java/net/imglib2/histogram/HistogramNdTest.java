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

package net.imglib2.histogram;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.UnsignedByteType;

import org.junit.Test;

/**
 * Test code for HistogramNd.
 * 
 * @author Barry DeZonia
 */
public class HistogramNdTest
{

	@Test
	public void testUnconstrainedNoTails()
	{

		final List< UnsignedByteType > data1 = getData1();
		final List< UnsignedByteType > data2 = getData2();
		final List< Iterable< UnsignedByteType >> data =
				new ArrayList< Iterable< UnsignedByteType >>();
		data.add( data1 );
		data.add( data2 );

		final long[] minVals = new long[] { 0, 0 };
		final long[] numBins = new long[] { 256, 256 };
		final boolean[] tailBins = new boolean[] { false, false };

		final HistogramNd< UnsignedByteType > hist =
				Integer1dBinMapper.histogramNd( minVals, numBins, tailBins );

		hist.countData( data );

		assertEquals( 256 * 256, hist.getBinCount() );
		assertEquals( 11, hist.totalCount() );
		assertEquals( 0, hist.lowerTailCount() );
		assertEquals( 0, hist.lowerTailCount( 0 ) );
		assertEquals( 0, hist.lowerTailCount( 1 ) );
		assertEquals( 0, hist.upperTailCount() );
		assertEquals( 0, hist.upperTailCount( 0 ) );
		assertEquals( 0, hist.upperTailCount( 1 ) );
		assertEquals( 0, hist.ignoredCount() );

		final List< UnsignedByteType > list = new ArrayList< UnsignedByteType >();
		list.add( null );
		list.add( null );

		list.set( 0, new UnsignedByteType( 5 ) );
		list.set( 1, new UnsignedByteType( 4 ) );
		assertEquals( 1, hist.frequency( list ) );

		list.set( 0, new UnsignedByteType( 3 ) );
		list.set( 1, new UnsignedByteType( 4 ) );
		assertEquals( 1, hist.frequency( list ) );

		list.set( 0, new UnsignedByteType( 5 ) );
		list.set( 1, new UnsignedByteType( 7 ) );
		assertEquals( 1, hist.frequency( list ) );

		list.set( 0, new UnsignedByteType( 9 ) );
		list.set( 1, new UnsignedByteType( 7 ) );
		assertEquals( 1, hist.frequency( list ) );

		list.set( 0, new UnsignedByteType( 10 ) );
		list.set( 1, new UnsignedByteType( 1 ) );
		assertEquals( 1, hist.frequency( list ) );

		list.set( 0, new UnsignedByteType( 7 ) );
		list.set( 1, new UnsignedByteType( 1 ) );
		assertEquals( 1, hist.frequency( list ) );

		list.set( 0, new UnsignedByteType( 10 ) );
		list.set( 1, new UnsignedByteType( 9 ) );
		assertEquals( 2, hist.frequency( list ) );

		list.set( 0, new UnsignedByteType( 9 ) );
		list.set( 1, new UnsignedByteType( 12 ) );
		assertEquals( 2, hist.frequency( list ) );

		list.set( 0, new UnsignedByteType( 5 ) );
		list.set( 1, new UnsignedByteType( 0 ) );
		assertEquals( 1, hist.frequency( list ) );
	}

	@Test
	public void testConstrainedWithTails()
	{
		final List< UnsignedByteType > data1 = getData1();
		final List< UnsignedByteType > data2 = getData2();
		final List< Iterable< UnsignedByteType >> data =
				new ArrayList< Iterable< UnsignedByteType >>();
		data.add( data1 );
		data.add( data2 );

		final List< UnsignedByteType > list = new ArrayList< UnsignedByteType >();
		list.add( null );
		list.add( null );

		final long[] minVals = new long[] { 4, 4 };
		final long[] numBins = new long[] { 8, 8 };
		final boolean[] tailBins = new boolean[] { true, true };

		final HistogramNd< UnsignedByteType > hist =
				Integer1dBinMapper.histogramNd( minVals, numBins, tailBins );

		hist.countData( data );

		assertEquals( 8 * 8, hist.getBinCount() );
		assertEquals( 11, hist.distributionCount() );
		assertEquals( 4, hist.lowerTailCount() );
		assertEquals( 1, hist.lowerTailCount( 0 ) );
		assertEquals( 3, hist.lowerTailCount( 1 ) );
		assertEquals( 5, hist.upperTailCount() );
		assertEquals( 3, hist.upperTailCount( 0 ) );
		assertEquals( 2, hist.upperTailCount( 1 ) );
		assertEquals( 0, hist.ignoredCount() );

		list.set( 0, new UnsignedByteType( 5 ) );
		list.set( 1, new UnsignedByteType( 4 ) );
		assertEquals( 1, hist.frequency( list ) );

		list.set( 0, new UnsignedByteType( 3 ) );
		list.set( 1, new UnsignedByteType( 4 ) );
		assertEquals( 1, hist.frequency( list ) );

		list.set( 0, new UnsignedByteType( 5 ) );
		list.set( 1, new UnsignedByteType( 7 ) );
		assertEquals( 1, hist.frequency( list ) );

		list.set( 0, new UnsignedByteType( 9 ) );
		list.set( 1, new UnsignedByteType( 7 ) );
		assertEquals( 1, hist.frequency( list ) );

		list.set( 0, new UnsignedByteType( 10 ) );
		list.set( 1, new UnsignedByteType( 1 ) );
		assertEquals( 1, hist.frequency( list ) );

		list.set( 0, new UnsignedByteType( 7 ) );
		list.set( 1, new UnsignedByteType( 1 ) );
		assertEquals( 1, hist.frequency( list ) );

		list.set( 0, new UnsignedByteType( 10 ) );
		list.set( 1, new UnsignedByteType( 9 ) );
		assertEquals( 2, hist.frequency( list ) );

		list.set( 0, new UnsignedByteType( 9 ) );
		list.set( 1, new UnsignedByteType( 12 ) );
		assertEquals( 2, hist.frequency( list ) );

		list.set( 0, new UnsignedByteType( 5 ) );
		list.set( 1, new UnsignedByteType( 0 ) );
		assertEquals( 1, hist.frequency( list ) );
	}

	@Test
	public void testConstrainedWithNoTails()
	{

		final List< UnsignedByteType > data1 = getData1();
		final List< UnsignedByteType > data2 = getData2();
		final List< Iterable< UnsignedByteType >> data =
				new ArrayList< Iterable< UnsignedByteType >>();
		data.add( data1 );
		data.add( data2 );

		final List< UnsignedByteType > list = new ArrayList< UnsignedByteType >();
		list.add( null );
		list.add( null );

		final long[] minVals = new long[] { 5, 5 };
		final long[] numBins = new long[] { 5, 5 };
		final boolean[] tailBins = new boolean[] { false, false };

		final HistogramNd< UnsignedByteType > hist =
				Integer1dBinMapper.histogramNd( minVals, numBins, tailBins );

		hist.countData( data );

		assertEquals( 5 * 5, hist.getBinCount() );
		assertEquals( 2, hist.distributionCount() );
		assertEquals( 0, hist.lowerTailCount() );
		assertEquals( 0, hist.lowerTailCount( 0 ) );
		assertEquals( 0, hist.lowerTailCount( 1 ) );
		assertEquals( 0, hist.upperTailCount() );
		assertEquals( 0, hist.upperTailCount( 0 ) );
		assertEquals( 0, hist.upperTailCount( 1 ) );
		assertEquals( 9, hist.ignoredCount() );

		list.set( 0, new UnsignedByteType( 5 ) );
		list.set( 1, new UnsignedByteType( 4 ) );
		assertEquals( 0, hist.frequency( list ) );

		list.set( 0, new UnsignedByteType( 3 ) );
		list.set( 1, new UnsignedByteType( 4 ) );
		assertEquals( 0, hist.frequency( list ) );

		list.set( 0, new UnsignedByteType( 5 ) );
		list.set( 1, new UnsignedByteType( 7 ) );
		assertEquals( 1, hist.frequency( list ) );

		list.set( 0, new UnsignedByteType( 9 ) );
		list.set( 1, new UnsignedByteType( 7 ) );
		assertEquals( 1, hist.frequency( list ) );

		list.set( 0, new UnsignedByteType( 10 ) );
		list.set( 1, new UnsignedByteType( 1 ) );
		assertEquals( 0, hist.frequency( list ) );

		list.set( 0, new UnsignedByteType( 7 ) );
		list.set( 1, new UnsignedByteType( 1 ) );
		assertEquals( 0, hist.frequency( list ) );

		list.set( 0, new UnsignedByteType( 10 ) );
		list.set( 1, new UnsignedByteType( 9 ) );
		assertEquals( 0, hist.frequency( list ) );

		list.set( 0, new UnsignedByteType( 9 ) );
		list.set( 1, new UnsignedByteType( 12 ) );
		assertEquals( 0, hist.frequency( list ) );

		list.set( 0, new UnsignedByteType( 5 ) );
		list.set( 1, new UnsignedByteType( 0 ) );
		assertEquals( 0, hist.frequency( list ) );
	}

	@Test
	public void testRgbHist()
	{
		final ArrayImgFactory< ARGBType > factory = new ArrayImgFactory< ARGBType >();
		final Img< ARGBType > img = factory.create( new long[] { 100, 200 }, new ARGBType() );
		for ( final ARGBType v : img )
		{
			v.set( ( int ) ( Math.random() * Integer.MAX_VALUE ) );
		}
		final RgbIterator data = new RgbIterator( img );
		final double[] minVals = new double[] { 0, 0, 0 };
		final double[] maxVals = new double[] { 255, 255, 255 };
		final long[] numBins = new long[] { 16, 16, 16 }; // 16^3 uses less mem
															// than 256^3
		final boolean[] tailBins = new boolean[] { false, false, false };
		final HistogramNd< IntType > hist =
				Real1dBinMapper.histogramNd( minVals, maxVals, numBins, tailBins );
		hist.countData( data );
		assertNotNull( hist );
		assertEquals( 20000, hist.distributionCount() );
	}

	private List< UnsignedByteType > getData1()
	{
		final List< UnsignedByteType > data = new ArrayList< UnsignedByteType >();
		data.add( new UnsignedByteType( 5 ) );
		data.add( new UnsignedByteType( 3 ) );
		data.add( new UnsignedByteType( 5 ) );
		data.add( new UnsignedByteType( 9 ) );
		data.add( new UnsignedByteType( 10 ) );
		data.add( new UnsignedByteType( 7 ) );
		data.add( new UnsignedByteType( 10 ) );
		data.add( new UnsignedByteType( 10 ) );
		data.add( new UnsignedByteType( 9 ) );
		data.add( new UnsignedByteType( 9 ) );
		data.add( new UnsignedByteType( 5 ) );
		return data;
	}

	private List< UnsignedByteType > getData2()
	{
		final List< UnsignedByteType > data = new ArrayList< UnsignedByteType >();
		data.add( new UnsignedByteType( 4 ) );
		data.add( new UnsignedByteType( 4 ) );
		data.add( new UnsignedByteType( 7 ) );
		data.add( new UnsignedByteType( 7 ) );
		data.add( new UnsignedByteType( 1 ) );
		data.add( new UnsignedByteType( 1 ) );
		data.add( new UnsignedByteType( 9 ) );
		data.add( new UnsignedByteType( 9 ) );
		data.add( new UnsignedByteType( 12 ) );
		data.add( new UnsignedByteType( 12 ) );
		data.add( new UnsignedByteType( 0 ) );
		return data;
	}

	private class RgbIterator implements Iterable< List< IntType >>
	{

		private final Img< ARGBType > img;

		public RgbIterator( final Img< ARGBType > img )
		{
			this.img = img;
		}

		@Override
		public Iterator< List< IntType >> iterator()
		{
			return new Iterator< List< IntType >>()
			{

				private Cursor< ARGBType > cursor;

				private List< IntType > tuple;

				@Override
				public boolean hasNext()
				{
					if ( cursor == null )
					{
						cursor = img.cursor();
						tuple = new ArrayList< IntType >();
						tuple.add( new IntType() );
						tuple.add( new IntType() );
						tuple.add( new IntType() );
					}
					return cursor.hasNext();
				}

				@Override
				public List< IntType > next()
				{
					final ARGBType argbValue = cursor.next();
					tuple.get( 0 ).set( ( argbValue.get() >> 16 ) & 0xff );
					tuple.get( 1 ).set( ( argbValue.get() >> 8 ) & 0xff );
					tuple.get( 2 ).set( ( argbValue.get() >> 0 ) & 0xff );
					return tuple;
				}

				@Override
				public void remove()
				{
					// do nothing
				}

			};
		}

	}
}
