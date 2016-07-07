/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2016 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package tests;

import static org.junit.Assert.assertTrue;
import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.real.FloatType;

import org.junit.Test;

/**
 * Basic tests
 * 
 * If these tests fail, the world is about to end.
 * 
 * 
 * @author Johannes Schindelin
 */
public class BasicTests extends JUnitTestBase
{
	/**
	 * A very simple test image: 3x3x3, with the pixel (1, 0, 2) set to 1,
	 * otherwise 0
	 */
	protected Img< FloatType > singlePixel = makeSinglePixel3D( 3, 1, 0, 2 );

	protected float[] singlePixelSignature = { 0.037037037f, 1.0f, 0.0f, 2.0f, 0.18885258f, 0.0f, 0.0f, 0.0f };

	/**
	 * The second test image
	 */
	protected Img< FloatType > testImage = makeTestImage3D( 3 );

	protected float[] testImageSignature = { 11.0f, 1.1818181f, 1.2424242f, 1.3636364f, 6.6666665f, 0.7959956f, 0.7796777f, 0.77138925f };

	/**
	 * Test the value of the single "bright" pixel
	 */
	@Test
	public void testOnePixel()
	{
		assertTrue( get3D( singlePixel, 1, 0, 2 ) == 1 );
	}

	/**
	 * Test the value of a "dark" pixel
	 */
	@Test
	public void testAnotherPixel()
	{
		assertTrue( get3D( singlePixel, 2, 0, 1 ) == 0 );
	}

	/**
	 * Verify that the pixels were stored correctly
	 */
	@Test
	public void testDefinition()
	{
		assertTrue( match( testImage, new TestGenerator( 3 ) ) );
	}

	/**
	 * Verify the known (and hand-generated) image signatures
	 */
	@Test
	public void testSignature()
	{
		assertTrue( matchSignature( singlePixel, singlePixelSignature ) );
		assertTrue( matchSignature( testImage, testImageSignature ) );
	}

	/**
	 * Ensure that all pixels are iterated over
	 */
	@Test
	public void testCursorCoverage()
	{
		final Cursor< FloatType > cursor = testImage.localizingCursor();
		int count = 0;
		while ( cursor.hasNext() )
		{
			cursor.fwd();
			count++;
		}
		assertTrue( count == 27 );

		final Cursor< FloatType > localizingCursor = testImage.localizingCursor();
		count = 0;
		while ( localizingCursor.hasNext() )
		{
			localizingCursor.fwd();
			count++;
		}
		assertTrue( count == 27 );
	}
}
