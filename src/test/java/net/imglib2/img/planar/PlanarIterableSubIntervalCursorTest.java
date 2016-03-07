/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2015 Tobias Pietzsch, Stephan Preibisch, Barry DeZonia,
 * Stephan Saalfeld, Curtis Rueden, Albert Cardona, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Jonathan Hale, Lee Kamentsky, Larry Lindsey, Mark
 * Hiner, Michael Zinsmaier, Martin Horn, Grant Harris, Aivar Grislis, John
 * Bogovic, Steffen Jaensch, Stefan Helfrich, Jan Funke, Nick Perry, Mark Longair,
 * Melissa Linkert and Dimiter Prodanov.
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
package net.imglib2.img.planar;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.img.AbstractSubIntervalIterableCursorTest;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.util.IntervalIndexer;
import net.imglib2.view.Views;

import org.junit.Before;
import org.junit.Test;

public class PlanarIterableSubIntervalCursorTest extends AbstractSubIntervalIterableCursorTest< PlanarImg< IntType, ? > >
{

	/** Interval for a single plane in img **/
	protected Interval intervalSinglePlaneShifted;

	protected Interval intervalSinglePlaneFull;

	protected Interval intervalFastPart;

	int numValues;

	private FinalInterval intervalLine;

	@Before
	public void createSourceData()
	{
		dimensions = new long[] { 23, 31, 11, 7, 3 };

		intervalLine = new FinalInterval( new long[] { 0, 12, 3, 5, 1 }, new long[] { dimensions[ 0 ] - 1, 13, 3, 5, 1 } );

		intervalShifted = new FinalInterval( new long[] { 0, 0, 3, 5, 1 }, new long[] { dimensions[ 0 ] - 1, dimensions[ 1 ] - 1, 4, 5, 1 } );

		intervalFast = new FinalInterval( new long[] { dimensions[ 0 ], dimensions[ 1 ], 5, 1, 1 } );

		intervalFastPart = new FinalInterval( new long[] { dimensions[ 0 ], 2, 3, 1, 1 } );

		intervalSinglePlaneShifted = new FinalInterval( new long[] { 0, 0, 3, 5, 1 }, new long[] { dimensions[ 0 ] - 1, dimensions[ 1 ] - 1, 3, 5, 1 } );

		intervalSinglePlaneFull = new FinalInterval( new long[] { 0, 0, 1, 1, 1 }, new long[] { dimensions[ 0 ] - 1, dimensions[ 1 ] - 1, 1, 1, 1 } );

		// create random data for all dims and fill the planar img
		numValues = 1;
		for ( int d = 0; d < dimensions.length; ++d )
			numValues *= dimensions[ d ];

		intData = new int[ numValues ];
		Random random = new Random( 0 );
		for ( int i = 0; i < numValues; ++i )
		{
			intData[ i ] = random.nextInt();
		}

		img = ( PlanarImg< IntType, ? > ) new PlanarImgFactory< IntType >().create( dimensions, new IntType() );

		long[] pos = new long[ dimensions.length ];
		RandomAccess< IntType > a = img.randomAccess();

		for ( int i = 0; i < numValues; ++i )
		{
			IntervalIndexer.indexToPosition( i, dimensions, pos );
			a.setPosition( pos );
			a.get().set( intData[ i ] );
		}
	}

	/**
	 * Test whether the correct cursors are created.
	 */
	@Test
	public void testOptimizable()
	{

		// Testing Cursor
		assertTrue( ( Views.interval( img, intervalLine ).cursor() instanceof PlanarSubsetCursor ) );

		// Testing Localzing Cursor
		assertTrue( ( Views.interval( img, intervalLine ).localizingCursor() instanceof PlanarSubsetLocalizingCursor ) );

		// Testing Localizing Cursor
		assertTrue( ( Views.interval( img, intervalShifted ).localizingCursor() instanceof PlanarSubsetLocalizingCursor ) );

		// Testing Cursor
		assertTrue( ( Views.interval( img, intervalSinglePlaneShifted ).cursor() instanceof PlanarPlaneSubsetCursor ) );

		// Testing Localizing Cursor
		assertTrue( ( Views.interval( img, intervalSinglePlaneShifted ).localizingCursor() instanceof PlanarPlaneSubsetLocalizingCursor ) );

		// Testing Cursor
		assertTrue( ( Views.interval( img, intervalFast ).cursor() instanceof PlanarSubsetCursor ) );

		// Testing Localizing Cursor
		assertTrue( ( Views.interval( img, intervalFast ).localizingCursor() instanceof PlanarSubsetLocalizingCursor ) );

		// Testing Cursor
		assertTrue( ( Views.interval( img, intervalSinglePlaneFull ).cursor() instanceof PlanarPlaneSubsetCursor ) );

		// Testing Localizing Cursor
		assertTrue( ( Views.interval( img, intervalSinglePlaneFull ).localizingCursor() instanceof PlanarPlaneSubsetLocalizingCursor ) );

		// Following, test that optimized cursor is not created when not
		// optimizeable
		// Testing Cursor
		assertFalse( ( Views.interval( img, intervalFastPart ).cursor() instanceof PlanarSubsetCursor ) );

		// Testing Localizing Cursor
		assertFalse( ( Views.interval( img, intervalFastPart ).localizingCursor() instanceof PlanarSubsetLocalizingCursor ) );

		// Testing Cursor
		assertFalse( ( Views.interval( img, intervalFastPart ).cursor() instanceof PlanarPlaneSubsetCursor ) );

		// Testing Localizing Cursor
		assertFalse( ( Views.interval( img, intervalFastPart ).localizingCursor() instanceof PlanarPlaneSubsetLocalizingCursor ) );
	}

	@Test
	public void testIterationSinglePlane()
	{
		Cursor< IntType > cursor = Views.interval( img, intervalSinglePlaneFull ).cursor();

		testCursorIteration( cursor, intervalSinglePlaneFull );
	}

	@Test
	public void testIterationIntervalLine()
	{
		Cursor< IntType > cursor = Views.interval( img, intervalLine ).cursor();

		testCursorIteration( cursor, intervalLine );
	}

	@Test
	public void testIterationSinglePlaneShifted()
	{
		Cursor< IntType > cursor = Views.interval( img, intervalSinglePlaneShifted ).cursor();

		testCursorIteration( cursor, intervalSinglePlaneShifted );
	}

	@Test
	public void testJumpFwdSinglePlane()
	{
		Cursor< IntType > cursor = Views.interval( img, intervalSinglePlaneFull ).localizingCursor();

		testCursorJumpFwd( cursor, intervalSinglePlaneFull );
	}

	// Localizing cursor

	@Test
	public void testLocalizingtIterationSinglePlaneShifted()
	{
		Cursor< IntType > cursor = Views.interval( img, intervalSinglePlaneShifted ).localizingCursor();

		testCursorIteration( cursor, intervalSinglePlaneShifted );
	}

	@Test
	public void testLocalizingJumpFwdSinglePlane()
	{
		Cursor< IntType > cursor = Views.interval( img, intervalSinglePlaneFull ).localizingCursor();

		testCursorJumpFwd( cursor, intervalSinglePlaneFull );
	}

	@Test
	public void testLocalizingIterationSinglePlane()
	{
		Cursor< IntType > cursor = Views.interval( img, intervalSinglePlaneFull ).localizingCursor();

		testCursorIteration( cursor, intervalSinglePlaneFull );
	}
}
