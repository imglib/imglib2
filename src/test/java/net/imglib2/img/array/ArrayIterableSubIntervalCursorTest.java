/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2020 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
package net.imglib2.img.array;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.img.AbstractSubIntervalIterableCursorTest;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.util.IntervalIndexer;
import net.imglib2.view.Views;

import org.junit.Before;
import org.junit.Test;

/**
 * ArrayIterableSubIntervalCursorTest
 * 
 * TODO Javadoc
 * 
 */
public class ArrayIterableSubIntervalCursorTest extends AbstractSubIntervalIterableCursorTest< ArrayImg< IntType, ? >>
{
	int numValues;

	/*
	 * Interval to ensure optimized cursors are not created when not possible.
	 */
	Interval intervalFastPart;

	long intDataSum;

	@Before
	public void createSourceData()
	{
		dimensions = new long[] { 23, 31, 11, 7, 3 };

		intervalFast = new FinalInterval( new long[] { dimensions[ 0 ], dimensions[ 1 ], 5, 1, 1 } );

		intervalFastPart = new FinalInterval( new long[] { dimensions[ 0 ], 2, 3, 1, 1 } );

		intervalShifted = new FinalInterval( new long[] { 0, 0, 3, 5, 1 }, new long[] { dimensions[ 0 ] - 1, dimensions[ 1 ] - 1, 4, 5, 1 } );

		numValues = 1;
		for ( int d = 0; d < dimensions.length; ++d )
			numValues *= dimensions[ d ];

		intData = new int[ numValues ];
		intDataSum = 0;
		Random random = new Random( 0 );
		for ( int i = 0; i < numValues; ++i )
		{
			intData[ i ] = random.nextInt();
			intDataSum += intData[ i ];
		}

		img = new ArrayImgFactory<>( new IntType() ).create( dimensions );

		long[] pos = new long[ dimensions.length ];
		RandomAccess< IntType > a = img.randomAccess();

		for ( int i = 0; i < numValues; ++i )
		{
			IntervalIndexer.indexToPosition( i, dimensions, pos );
			a.setPosition( pos );
			a.get().set( intData[ i ] );
		}
	}

	@Test
	public void testOptimizable()
	{

		// Testing Cursor
		assertTrue( ( Views.interval( img, intervalFast ).cursor() instanceof ArraySubIntervalCursor ) );

		// Testing Localizing Cursor
		assertTrue( ( Views.interval( img, intervalFast ).localizingCursor() instanceof AbstractArrayLocalizingCursor ) );

		// Testing Cursor
		assertFalse( ( Views.interval( img, intervalFastPart ).cursor() instanceof ArraySubIntervalCursor ) );

		// Testing Localizing Cursor
		assertFalse( ( Views.interval( img, intervalFastPart ).localizingCursor() instanceof AbstractArrayLocalizingCursor ) );

		// Testing Cursor
		assertTrue( ( Views.interval( img, intervalShifted ).cursor() instanceof ArraySubIntervalCursor ) );

		// Testing Localizing Cursor
		assertTrue( ( Views.interval( img, intervalShifted ).localizingCursor() instanceof AbstractArrayLocalizingCursor ) );
	}

}
