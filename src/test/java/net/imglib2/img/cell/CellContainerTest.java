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

package net.imglib2.img.cell;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.real.FloatType;

import org.junit.Before;
import org.junit.Test;

/**
 * TODO
 * 
 */
public class CellContainerTest
{
	int[] cellDimensions;

	long[] dimensions;

	Img< FloatType > img;

	@Before
	public void setUp()
	{
		cellDimensions = new int[] { 8, 16, 5, 2 };
		dimensions = new long[] { 20, 37, 12, 33 };
		img = new CellImgFactory< FloatType >( cellDimensions ).create( dimensions, new FloatType() );
	}

	@Test
	public void equalIterationOrder()
	{
		final Img< FloatType > img2 = new CellImgFactory< FloatType >( cellDimensions ).create( dimensions, new FloatType() );
		assertTrue( img2.iterationOrder().equals( img.iterationOrder() ) );
		assertTrue( img.iterationOrder().equals( img2.iterationOrder() ) );

		final Img< FloatType > img3 = new CellImgFactory< FloatType >( 9 ).create( dimensions, new FloatType() );
		assertFalse( img3.iterationOrder().equals( img.iterationOrder() ) );
		assertFalse( img.iterationOrder().equals( img3.iterationOrder() ) );

		final Img< FloatType > img4 = new ArrayImgFactory< FloatType >().create( dimensions, new FloatType() );
		assertFalse( img4.iterationOrder().equals( img.iterationOrder() ) );
		assertFalse( img.iterationOrder().equals( img4.iterationOrder() ) );
	}
}
