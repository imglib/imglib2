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

package net.imglib2;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import net.imglib2.RealCursor;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.RealPointSampleList;
import net.imglib2.type.numeric.real.DoubleType;

import org.junit.Before;
import org.junit.Test;

/**
 * TODO
 * 
 */
public class RealPointSampleListTest
{
	final static private int n = 10;

	final static private int m = 10000;

	final static private Random rnd = new Random( 123456 );

	final static private RealPointSampleList< DoubleType > realPointSampleList = new RealPointSampleList< DoubleType >( n );

	final static private ArrayList< RealPoint > realPointList = new ArrayList< RealPoint >();

	final static private ArrayList< DoubleType > sampleList = new ArrayList< DoubleType >();

	final static private boolean positionEquals(
			final RealLocalizable a,
			final RealLocalizable b )
	{
		final int na = a.numDimensions();
		if ( na != b.numDimensions() )
			return false;
		for ( int d = 0; d < na; ++d )
		{
			if ( a.getDoublePosition( d ) != b.getDoublePosition( d ) )
				return false;
		}
		return true;
	}

	@Before
	public void init()
	{
		for ( int i = 0; i < m; ++i )
		{
			final double[] position = new double[ n ];
			for ( int d = 0; d < n; ++d )
				position[ d ] = rnd.nextDouble();

			final RealPoint realPoint = new RealPoint( position );
			final DoubleType sample = new DoubleType( rnd.nextDouble() );

			realPointList.add( realPoint );
			sampleList.add( sample );
			realPointSampleList.add( realPoint, sample );
		}
	}

	@Test
	public void testIteration()
	{
		final Iterator< DoubleType > sampleIterator = sampleList.iterator();
		for ( final DoubleType t : realPointSampleList )
			assertTrue( "Samples differ ", t == sampleIterator.next() );
	}

	@Test
	public void testPosition()
	{
		final Iterator< RealPoint > realPointIterator = realPointList.iterator();
		final RealCursor< DoubleType > realPointSampleCursor = realPointSampleList.cursor();

		while ( realPointSampleCursor.hasNext() )
		{
			realPointSampleCursor.fwd();
			assertTrue( "Positions differ ", positionEquals( realPointIterator.next(), realPointSampleCursor ) );
		}
	}

	@Test
	public void testCopy()
	{
		final ArrayList< RealCursor< DoubleType > > copies = new ArrayList< RealCursor< DoubleType > >();
		final RealCursor< DoubleType > cursor = realPointSampleList.cursor();
		while ( cursor.hasNext() )
		{
			cursor.fwd();
			copies.add( cursor.copyCursor() );
		}

		cursor.reset();
		final Iterator< RealCursor< DoubleType > > copyIterator = copies.iterator();
		while ( cursor.hasNext() )
		{
			cursor.fwd();
			final RealCursor< DoubleType > copy = copyIterator.next();
			assertTrue( "Copy failed at sample ", copy.get() == cursor.get() );
			assertTrue( "Copy failed at position ", positionEquals( copy, cursor ) );
		}

	}
}
