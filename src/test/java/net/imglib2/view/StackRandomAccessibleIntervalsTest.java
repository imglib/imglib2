/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2022 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package net.imglib2.view;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.integer.UnsignedByteType;

import org.junit.Before;
import org.junit.Test;

/**
 * Simple test to test Views.stack(...)
 * 
 * @author Christian Dietz
 *
 */
public class StackRandomAccessibleIntervalsTest
{

	private ArrayImg< UnsignedByteType, ? > img;

	@Before
	public void setUp()
	{
		final long[] dimension = new long[] { 10, 10, 10 };
		img = new ArrayImgFactory<>( new UnsignedByteType() ).create( dimension );

		final Cursor< UnsignedByteType > inCursor = img.localizingCursor();

		while ( inCursor.hasNext() )
		{
			// set with plane position
			inCursor.next().set( inCursor.getIntPosition( 2 ) );
		}

	}

	@Test
	public void testStacking()
	{
		// lets create a stack with every second plane of the input image,
		// works!
		final List< RandomAccessibleInterval< UnsignedByteType > > intervals =
				new ArrayList< RandomAccessibleInterval< UnsignedByteType > >();
		for ( int d = 0; d < img.dimension( 2 ); d++ )
		{
			if ( d % 2 == 0 )
				intervals.add(
						Views.dropSingletonDimensions( Views.interval( img, new FinalInterval( new long[] { img.min( 0 ), img.min( 1 ), d },
								new long[] { img.max( 0 ), img.max( 1 ), d } ) ) ) );
		}

		// stack it!
		final RandomAccessibleInterval< UnsignedByteType > stack = Views.stack( intervals );

		assertTrue( stack.numDimensions() == 3 );
		assertTrue( intervals.size() == stack.dimension( 2 ) );

		final Cursor< UnsignedByteType > stackC = Views.iterable( stack ).cursor();
		while ( stackC.hasNext() )
		{
			assertTrue( stackC.next().get() % 2 == 0 );
		}
	}
}
