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

package net.imglib2.img.array;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.integer.Unsigned2BitType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.ImgTestHelper;
import net.imglib2.util.Util;

import org.junit.Test;

/**
 * Unit tests for {@link ArrayImg}.
 *
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 * @author Curtis Rueden
 * @author Matthias Arzt
 * @author Philipp Hanslovsky
 */
public class ArrayImgTest
{
	@Test
	public void testArrayImg()
	{
		final long[][] dim = ImgTestHelper.dims();
		for ( int i = 0; i < dim.length; ++i )
		{
			assertTrue( "ArrayImg failed for: dim=" + Util.printCoordinates( dim[ i ] ),
					ImgTestHelper.testImg( dim[ i ], new ArrayImgFactory<>( new FloatType() ), new ArrayImgFactory<>( new FloatType() ) ) );
		}
	}

	@Test
	public void testSizeLimit()
	{
		assumeTrue( "Don't run the test, because there is less than 1 GB of memory.",
				Runtime.getRuntime().maxMemory() > Integer.MAX_VALUE );
		// NB: For some pixel types, the maximum image size for an ArrayImg is Integer.MAX_VALUE.
		// This test ensures that an ArrayImg of size Integer.MAX_VALUE works properly.
		// An ArrayImg should never be bigger that Integer.MAX_VALUE because
		// ArrayImg.jumpFwd(...) and other methods might fail.
		long size = Integer.MAX_VALUE;
		Img< Unsigned2BitType > image = new ArrayImgFactory<>( new Unsigned2BitType() ).create( size );
		// Set the lat pixel using a randomAccess
		image.randomAccess().setPositionAndGet( size - 1 ).set( 2 );
		// Try to read the last pixel using a cursor
		Cursor< Unsigned2BitType > cursor = image.cursor();
		cursor.jumpFwd( size );
		Unsigned2BitType lastPixel = cursor.get();
		assertEquals( 2, lastPixel.get() );
		assertFalse( cursor.hasNext() );
		cursor.jumpFwd( -1 );
	}

	@Test
	public void testArrayImgInvalidDimensions()
	{
		ImgTestHelper.assertInvalidDims( new ArrayImgFactory<>( new FloatType() ) );
	}
}
