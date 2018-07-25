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

package net.imglib2.util;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import net.imglib2.FinalInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.img.list.ListImgFactory;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.logic.BoolType;

import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.DoubleType;
import org.junit.Test;

import java.util.function.BiPredicate;

public class UtilTest
{

	@Test
	public void testPrintCoordinatesEmpty()
	{
	    final double[] doubleCoordinates = {};
	    final int[] intCoordinates = {};
	    final long[] longCoordinates = {};
	    final float[] floatCoordinates = {};
	    final boolean[] booleanCoordinates = {};
	    final RealLocalizable rl = new RealPoint();
	    String expected  = "(Array empty)";
	    String rlExpected = "(RealLocalizable empty)";

	    assertEquals(expected, Util.printCoordinates(doubleCoordinates));
	    assertEquals(expected, Util.printCoordinates(intCoordinates));
	    assertEquals(expected, Util.printCoordinates(longCoordinates));
	    assertEquals(expected, Util.printCoordinates(floatCoordinates));
	    assertEquals(expected, Util.printCoordinates(booleanCoordinates));
	    assertEquals(rlExpected, Util.printCoordinates(rl));
	}

	@Test
	public void testPrintCoordinatesNull()
	{
	    final double[] nullDouble = null;
	    final int[] nullInt = null;
	    final long[] nullLong = null;
	    final float[] nullFloat = null;
	    final boolean[] nullBoolean = null;
	    final RealLocalizable nullRl = null;
	    String expected  = "(Array empty)";
	    String rlExpected = "(RealLocalizable empty)";

	    assertEquals(expected, Util.printCoordinates(nullDouble));
	    assertEquals(expected, Util.printCoordinates(nullInt));
	    assertEquals(expected, Util.printCoordinates(nullLong));
	    assertEquals(expected, Util.printCoordinates(nullFloat));
	    assertEquals(expected, Util.printCoordinates(nullBoolean));
	    assertEquals(rlExpected, Util.printCoordinates(nullRl));

	}

	@Test
	public void testPrintCoordinatesOneElem()
	{
	    final double[] oneElemDouble = {1};
	    final int[] oneElemInt = {1};
	    final long[] oneElemLong = {1};
	    final boolean[] oneElemBoolean = {true};
	    final RealLocalizable oneElemRl = new RealPoint(oneElemDouble);
	    String expected  = "(1)";
	    String expectedDouble  = "(1.0)";

	    assertEquals(expectedDouble, Util.printCoordinates(oneElemDouble));
	    assertEquals(expected, Util.printCoordinates(oneElemInt));
	    assertEquals(expected, Util.printCoordinates(oneElemLong));
	    assertEquals(expected, Util.printCoordinates(oneElemBoolean));
	    assertEquals(expectedDouble, Util.printCoordinates(oneElemRl));

	}

	@Test
	public void testPrintCoordinatesManyElems()
	{
	    final double[] doubleData = {1,5,7};
	    final int[] intData = {1,5,7};
	    final long[] longData = {1,5,7};
	    final boolean[] booleanData = {true, false, true};
	    final RealLocalizable rlData = new RealPoint(doubleData);
	    String expected  = "(1, 5, 7)";
	    String expectedDouble  = "(1.0, 5.0, 7.0)";
	    String expectedBoolean  = "(1, 0, 1)";

	    assertEquals(expectedDouble, Util.printCoordinates(doubleData));
	    assertEquals(expected, Util.printCoordinates(intData));
	    assertEquals(expected, Util.printCoordinates(longData));
	    assertEquals(expectedBoolean, Util.printCoordinates(booleanData));
	    assertEquals(expectedDouble, Util.printCoordinates(rlData));

	}

	@Test
	public void testGetSuitableImgFactory() {
		final ImgFactory< BitType > smallBitFactory = Util.getSuitableImgFactory( new FinalInterval( 10, 10 ), new BitType() );
		assertTrue( smallBitFactory instanceof ArrayImgFactory );

		final ImgFactory< BitType > largeBitFactory = Util.getSuitableImgFactory( new FinalInterval( 1_000_000_000, 1_000_000_000 ), new BitType() );
		assertTrue( largeBitFactory instanceof CellImgFactory );

		final ImgFactory< BoolType > boolFactory = Util.getSuitableImgFactory( new FinalInterval( 10, 10 ), new BoolType() );
		assertTrue( boolFactory instanceof ListImgFactory );
	}

	@Test
	public void testImagesEqual() {
		assertTrue( Util.imagesEqual( intsImage(1,2,3), intsImage(1,2,3) ) );
		assertFalse( Util.imagesEqual( intsImage(1), intsImage(1,2,3) ) );
		assertFalse( Util.imagesEqual( intsImage(1,2,3), intsImage(1,4,3) ) );
		assertTrue( Util.imagesEqual( doublesImage(1,2,3), doublesImage(1,2,3) ) );
		assertFalse( Util.imagesEqual( doublesImage(1,2,3), doublesImage(1,4,3) ) );
	}

	@Test
	public void testImagesEqualWithPredicate() {
		BiPredicate< RealType<?>, RealType<?> > predicate = ( a, b ) -> a.getRealDouble() == b.getRealDouble();
		assertTrue( Util.imagesEqual( intsImage(1,2,3), doublesImage(1,2,3), predicate ) );
		assertFalse( Util.imagesEqual( intsImage(1), doublesImage(1,2,3), predicate ) );
		assertFalse( Util.imagesEqual( intsImage(1,2,3), doublesImage(1,4,3), predicate ) );
	}

	private Img< IntType > intsImage( int... pixels )
	{
		return ArrayImgs.ints( pixels, pixels.length );
	}

	private Img<DoubleType > doublesImage( double... pixels )
	{
		return ArrayImgs.doubles( pixels, pixels.length );
	}
}
