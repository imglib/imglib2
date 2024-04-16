/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2024 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;

import org.junit.Test;

/**
 * Tests the Real1dBinMapper class.
 * 
 * @author Barry DeZonia
 */
public class Real1dBinMapperTest
{

	// DONE

	@Test
	public void testIntNoTails()
	{
		long binPos;
		final IntType tmp = new IntType();
		final Real1dBinMapper< IntType > binMapper =
				new Real1dBinMapper< IntType >( 0.0, 100.0, 100, false );
		assertEquals( 100, binMapper.getBinCount() );
		for ( double i = 0; i <= 100; i += 0.125 )
		{
			tmp.setReal( i );
			binPos = binMapper.map( tmp );
			double expectedBin = Math.round( i );
			if ( i >= 99.5 )
				expectedBin--;
			assertEquals( expectedBin, binPos, 0 );
			binMapper.getLowerBound( binPos, tmp );
			assertEquals( expectedBin, tmp.getRealDouble(), 0.0 );
			binMapper.getUpperBound( binPos, tmp );
			assertEquals( expectedBin + 1, tmp.getRealDouble(), 0.0 );
			// Note - one would hope this would calc easily but due to rounding
			// errors
			// one cannot always easily tell what the bin center is when using
			// an
			// integral type with a Real1dBinMapper. One should really use an
			// Integer1dBinMapper in these cases. Disabling test.
			// binMapper.getCenterValues(binPos, tmpList);
			// assertEquals(expectedBin + 1, tmp.getRealDouble(), 0.0);
		}
		tmp.setReal( -1 );
		assertEquals( Long.MIN_VALUE, binMapper.map( tmp ) );
		tmp.setReal( 101 );
		assertEquals( Long.MAX_VALUE, binMapper.map( tmp ) );
	}

	// DONE

	@Test
	public void testIntTails()
	{
		long binPos;
		final IntType tmp = new IntType();
		final Real1dBinMapper< IntType > binMapper =
				new Real1dBinMapper< IntType >( 0.0, 100.0, 102, true );
		assertEquals( 102, binMapper.getBinCount() );
		for ( double i = 0; i <= 100; i += 0.125 )
		{
			tmp.setReal( i );
			binPos = binMapper.map( tmp );
			double expectedBin = Math.round( i ) + 1;
			if ( i >= 99.5 )
				expectedBin--;
			assertEquals( expectedBin, binPos, 0 );
			binMapper.getLowerBound( binPos, tmp );
			assertEquals( expectedBin - 1, tmp.getRealDouble(), 0.0 );
			binMapper.getUpperBound( binPos, tmp );
			assertEquals( expectedBin, tmp.getRealDouble(), 0.0 );
			// Note - one would hope this would calc easily but due to rounding
			// errors
			// one cannot always easily tell what the bin center is when using
			// an
			// integral type with a Real1dBinMapper. One should really use an
			// Integer1dBinMapper in these cases. Disabling test.
			// binMapper.getCenterValues(binPos, tmpList);
			// assertEquals(expectedBin + 1, tmp.getRealDouble(), 0.0);
		}
		tmp.setReal( -1 );
		assertEquals( 0, binMapper.map( tmp ) );
		tmp.setReal( 101 );
		assertEquals( 101, binMapper.map( tmp ) );
	}

	// DONE

	@Test
	public void testFloatNoTails()
	{
		long binPos;
		final FloatType tmp = new FloatType();
		final Real1dBinMapper< FloatType > binMapper =
				new Real1dBinMapper< FloatType >( 0.0, 100.0, 100, false );
		assertEquals( 100, binMapper.getBinCount() );
		for ( double i = 0; i <= 100; i += 0.125 )
		{
			tmp.setReal( i );
			binPos = binMapper.map( tmp );
			double expectedBin = Math.floor( i );
			if ( i == 100.0 )
				expectedBin--;
			assertEquals( expectedBin, binPos, 0 );
			binMapper.getLowerBound( binPos, tmp );
			assertEquals( expectedBin, tmp.getRealDouble(), 0.0 );
			binMapper.getUpperBound( binPos, tmp );
			assertEquals( expectedBin + 1, tmp.getRealDouble(), 0.0 );
			binMapper.getCenterValue( binPos, tmp );
			assertEquals( expectedBin + 0.5, tmp.getRealDouble(), 0.0 );
		}
		tmp.setReal( -0.0001 );
		assertEquals( Long.MIN_VALUE, binMapper.map( tmp ) );
		tmp.setReal( 100.0001 );
		assertEquals( Long.MAX_VALUE, binMapper.map( tmp ) );
	}

	// DONE

	@Test
	public void testFloatTails()
	{
		long binPos;
		final FloatType tmp = new FloatType();
		final Real1dBinMapper< FloatType > binMapper =
				new Real1dBinMapper< FloatType >( 0.0, 100.0, 102, true );
		assertEquals( 102, binMapper.getBinCount() );
		for ( double i = 0; i <= 100; i += 0.125 )
		{
			tmp.setReal( i );
			binPos = binMapper.map( tmp );
			double expectedBin = Math.floor( i ) + 1;
			if ( i == 100.0 )
				expectedBin--;
			assertEquals( expectedBin, binPos, 0 );
			binMapper.getLowerBound( binPos, tmp );
			assertEquals( expectedBin - 1, tmp.getRealDouble(), 0.0 );
			binMapper.getUpperBound( binPos, tmp );
			assertEquals( expectedBin, tmp.getRealDouble(), 0.0 );
			binMapper.getCenterValue( binPos, tmp );
			assertEquals( expectedBin - 0.5, tmp.getRealDouble(), 0.0 );
		}
		tmp.setReal( -0.0001 );
		assertEquals( 0, binMapper.map( tmp ) );
		tmp.setReal( 100.0001 );
		assertEquals( 101, binMapper.map( tmp ) );
	}

	// DONE

	@Test
	public void testBinBoundariesTails()
	{
		long pos;
		final FloatType tmp = new FloatType();
		Real1dBinMapper< FloatType > binMapper;

		pos = 0;
		binMapper = new Real1dBinMapper< FloatType >( 0.0, 4.0, 4, true );
		binMapper.getLowerBound( pos, tmp );
		assertEquals( Double.NEGATIVE_INFINITY, tmp.getRealDouble(), 0 );
		assertTrue( binMapper.includesLowerBound( pos ) );
		binMapper.getUpperBound( pos, tmp );
		assertEquals( 0, tmp.getRealDouble(), 0 );
		assertFalse( binMapper.includesUpperBound( pos ) );

		pos = 1;
		binMapper.getLowerBound( pos, tmp );
		assertEquals( 0, tmp.getRealDouble(), 0 );
		assertTrue( binMapper.includesLowerBound( pos ) );
		binMapper.getUpperBound( pos, tmp );
		assertEquals( 2, tmp.getRealDouble(), 0 );
		assertFalse( binMapper.includesUpperBound( pos ) );

		pos = 2;
		binMapper.getLowerBound( pos, tmp );
		assertEquals( 2, tmp.getRealDouble(), 0 );
		assertTrue( binMapper.includesLowerBound( pos ) );
		binMapper.getUpperBound( pos, tmp );
		assertEquals( 4, tmp.getRealDouble(), 0 );
		assertTrue( binMapper.includesUpperBound( pos ) );

		pos = 3;
		binMapper.getLowerBound( pos, tmp );
		assertEquals( 4, tmp.getRealDouble(), 0 );
		assertFalse( binMapper.includesLowerBound( pos ) );
		binMapper.getUpperBound( pos, tmp );
		assertEquals( Double.POSITIVE_INFINITY, tmp.getRealDouble(), 0 );
		assertTrue( binMapper.includesUpperBound( pos ) );

		tmp.setReal( -0.001 );
		pos = binMapper.map( tmp );
		assertEquals( 0, pos );

		tmp.setReal( 4.001 );
		pos = binMapper.map( tmp );
		assertEquals( 3, pos );
	}

	// DONE

	@Test
	public void testBinBoundariesNoTails()
	{
		long pos;
		final FloatType tmp = new FloatType();
		Real1dBinMapper< FloatType > binMapper;

		binMapper = new Real1dBinMapper< FloatType >( 0.0, 4.0, 4, false );
		pos = 0;
		binMapper.getLowerBound( pos, tmp );
		assertEquals( 0, tmp.getRealDouble(), 0 );
		assertTrue( binMapper.includesLowerBound( pos ) );
		binMapper.getUpperBound( pos, tmp );
		assertEquals( 1, tmp.getRealDouble(), 0 );
		assertFalse( binMapper.includesUpperBound( pos ) );

		pos = 1;
		binMapper.getLowerBound( pos, tmp );
		assertEquals( 1, tmp.getRealDouble(), 0 );
		assertTrue( binMapper.includesLowerBound( pos ) );
		binMapper.getUpperBound( pos, tmp );
		assertEquals( 2, tmp.getRealDouble(), 0 );
		assertFalse( binMapper.includesUpperBound( pos ) );

		pos = 2;
		binMapper.getLowerBound( pos, tmp );
		assertEquals( 2, tmp.getRealDouble(), 0 );
		assertTrue( binMapper.includesLowerBound( pos ) );
		binMapper.getUpperBound( pos, tmp );
		assertEquals( 3, tmp.getRealDouble(), 0 );
		assertFalse( binMapper.includesUpperBound( pos ) );

		pos = 3;
		binMapper.getLowerBound( pos, tmp );
		assertEquals( 3, tmp.getRealDouble(), 0 );
		assertTrue( binMapper.includesLowerBound( pos ) );
		binMapper.getUpperBound( pos, tmp );
		assertEquals( 4, tmp.getRealDouble(), 0 );
		assertTrue( binMapper.includesUpperBound( pos ) );

		tmp.setReal( -0.001 );
		assertEquals( Long.MIN_VALUE, binMapper.map( tmp ) );

		tmp.setReal( 4.001 );
		assertEquals( Long.MAX_VALUE, binMapper.map( tmp ) );
	}

	@Test
	public void testEmptyMapper()
	{
		long pos;
		final FloatType tmp = new FloatType();
		Real1dBinMapper< FloatType > binMapper;

		binMapper = new Real1dBinMapper< FloatType >( 0.0, 0.0, 4, false );
		assertNotNull( binMapper );
		tmp.set( 0 );
		pos = binMapper.map( tmp );
		assertEquals( 0, pos );
	}
}
