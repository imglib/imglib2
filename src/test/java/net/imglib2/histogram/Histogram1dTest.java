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

package net.imglib2.histogram;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import net.imglib2.type.numeric.integer.UnsignedByteType;

import org.junit.Test;

/**
 * Test code for Histogram1d.
 * 
 * @author Barry DeZonia
 */
public class Histogram1dTest
{

	@Test
	public void test()
	{

		final List< UnsignedByteType > data = getData1();

		BinMapper1d< UnsignedByteType > binMapper =
				new Integer1dBinMapper< UnsignedByteType >( 0, 256, false );

		Histogram1d< UnsignedByteType > hist =
				new Histogram1d< UnsignedByteType >( data, binMapper );

		assertEquals( 5, hist.firstDataValue().get() );

		assertEquals( 256, hist.getBinCount() );
		assertEquals( 11, hist.totalCount() );
		assertEquals( 1, hist.frequency( new UnsignedByteType( 3 ) ) );
		assertEquals( 3, hist.frequency( new UnsignedByteType( 5 ) ) );
		assertEquals( 1, hist.frequency( new UnsignedByteType( 7 ) ) );
		assertEquals( 3, hist.frequency( new UnsignedByteType( 9 ) ) );
		assertEquals( 3, hist.frequency( new UnsignedByteType( 10 ) ) );
		assertEquals( 0, hist.lowerTailCount() );
		assertEquals( 0, hist.upperTailCount() );

		binMapper = new Integer1dBinMapper< UnsignedByteType >( 4, 8, true );

		hist = new Histogram1d< UnsignedByteType >( data, binMapper );

		assertEquals( 8, hist.getBinCount() );
		assertEquals( 11, hist.distributionCount() );
		assertEquals( 1, hist.frequency( new UnsignedByteType( 3 ) ) );
		assertEquals( 3, hist.frequency( new UnsignedByteType( 5 ) ) );
		assertEquals( 1, hist.frequency( new UnsignedByteType( 7 ) ) );
		assertEquals( 3, hist.frequency( new UnsignedByteType( 9 ) ) );
		assertEquals( 3, hist.frequency( new UnsignedByteType( 10 ) ) );
		assertEquals( 1, hist.lowerTailCount() );
		assertEquals( 3, hist.upperTailCount() );

		binMapper = new Integer1dBinMapper< UnsignedByteType >( 5, 5, false );

		hist = new Histogram1d< UnsignedByteType >( data, binMapper );

		assertEquals( 5, hist.getBinCount() );
		assertEquals( 7, hist.distributionCount() );
		assertEquals( 0, hist.frequency( new UnsignedByteType( 3 ) ) );
		assertEquals( 3, hist.frequency( new UnsignedByteType( 5 ) ) );
		assertEquals( 1, hist.frequency( new UnsignedByteType( 7 ) ) );
		assertEquals( 3, hist.frequency( new UnsignedByteType( 9 ) ) );
		assertEquals( 0, hist.frequency( new UnsignedByteType( 10 ) ) );
		assertEquals( 0, hist.lowerTailCount() );
		assertEquals( 0, hist.upperTailCount() );
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
}
