/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2018 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
import net.imglib2.type.numeric.integer.IntType;

import org.junit.Test;

/**
 * Tests the Integer1dBinMapper class.
 * 
 * @author Barry DeZonia
 */
public class Integer1dBinMapperTest
{

	@Test
	public void testNoTail()
	{
		long binPos;
		final IntType tmp = new IntType();
		final Integer1dBinMapper< IntType > binMapper =
				new Integer1dBinMapper< IntType >( 0, 100, false );
		assertEquals( 100, binMapper.getBinCount() );
		for ( int i = 0; i <= 99; i++ )
		{
			tmp.setInteger( i );
			binPos = binMapper.map( tmp );
			assertEquals( i, binPos );
			binMapper.getLowerBound( binPos, tmp );
			assertEquals( i, tmp.getIntegerLong() );
			binMapper.getUpperBound( binPos, tmp );
			assertEquals( i, tmp.getIntegerLong() );
			binMapper.getCenterValue( binPos, tmp );
			assertEquals( i, tmp.getIntegerLong() );
		}
		tmp.setReal( -1 );
		assertEquals( Long.MIN_VALUE, binMapper.map( tmp ) );
		tmp.setReal( 100 );
		assertEquals( Long.MAX_VALUE, binMapper.map( tmp ) );
	}

	@Test
	public void testTail()
	{
		long binPos;
		final IntType tmp = new IntType();
		final Integer1dBinMapper< IntType > binMapper =
				new Integer1dBinMapper< IntType >( 0, 100, true );
		assertEquals( 100, binMapper.getBinCount() );
		// test the interior areas
		for ( int i = 0; i < 98; i++ )
		{
			tmp.setInteger( i );
			binPos = binMapper.map( tmp );
			assertEquals( i + 1, binPos );
			binMapper.getLowerBound( binPos, tmp );
			assertEquals( i, tmp.getIntegerLong() );
			binMapper.getUpperBound( binPos, tmp );
			assertEquals( i, tmp.getIntegerLong() );
			binMapper.getCenterValue( binPos, tmp );
			assertEquals( i, tmp.getIntegerLong() );
		}

		// test the lower tail
		tmp.setInteger( -1 );
		binPos = binMapper.map( tmp );
		assertEquals( 0, binPos );

		// test the upper tail
		tmp.setInteger( 100 );
		binPos = binMapper.map( tmp );
		assertEquals( 99, binPos );
	}

}
