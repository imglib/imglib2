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

package tests.labeling;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.imglib2.Cursor;
import net.imglib2.algorithm.labeling.AllConnectedComponents;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.labeling.LabelingType;
import net.imglib2.labeling.NativeImgLabeling;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;

import org.junit.Test;

/**
 * TODO
 * 
 * @author Lee Kamentsky
 */
public class AllConnectedComponentsTest
{
	private void test2D( final boolean[][] input, final int[][] expected, final long[][] structuringElement, final int start, final int background )
	{
		final long[] dimensions = new long[] { input.length, input[ 0 ].length };
		final ArrayImgFactory< BitType > imgFactory = new ArrayImgFactory< BitType >();
		final ArrayImgFactory< IntType > labelingFactory = new ArrayImgFactory< IntType >();
		final ArrayImg< BitType, ? > image = imgFactory.create( dimensions, new BitType() );
		final NativeImgLabeling< Integer, IntType > labeling = new NativeImgLabeling< Integer, IntType >( labelingFactory.create( dimensions, new IntType() ) );
		/*
		 * Fill the image.
		 */
		final Cursor< BitType > c = image.localizingCursor();
		final int[] position = new int[ 2 ];
		while ( c.hasNext() )
		{
			final BitType t = c.next();
			c.localize( position );
			t.set( input[ position[ 0 ] ][ position[ 1 ] ] );
		}
		/*
		 * Run the algorithm.
		 */
		final Iterator< Integer > names = AllConnectedComponents.getIntegerNames( start );
		if ( structuringElement == null )
			AllConnectedComponents.labelAllConnectedComponents( labeling, image, names );
		else
			AllConnectedComponents.labelAllConnectedComponents( labeling, image, names, structuringElement );

		/*
		 * Check the result
		 */
		final Cursor< LabelingType< Integer >> lc = labeling.localizingCursor();
		final HashMap< Integer, Integer > map = new HashMap< Integer, Integer >();
		while ( lc.hasNext() )
		{
			final LabelingType< Integer > lt = lc.next();
			lc.localize( position );
			final List< Integer > labels = lt.getLabeling();
			final int expectedValue = expected[ ( position[ 0 ] ) ][ ( position[ 1 ] ) ];
			if ( expectedValue == background )
				assertEquals( labels.size(), 0 );
			else
			{
				assertEquals( labels.size(), 1 );
				final Integer value = labels.get( 0 );
				if ( map.containsKey( value ) )
					assertEquals( expectedValue, map.get( value ).intValue() );
				else
					map.put( value, expectedValue );
			}
		}
	}

	@Test
	public void testEmpty()
	{
		final boolean[][] input = new boolean[ 3 ][ 3 ];
		final int[][] expected = new int[ 3 ][ 3 ];
		test2D( input, expected, null, 1, 0 );
	}

	@Test
	public void testOne()
	{
		final boolean[][] input = new boolean[][] { { false, false, false }, { false, true, false }, { false, false, false } };
		final int[][] expected = new int[][] { { 0, 0, 0 }, { 0, 1, 0 }, { 0, 0, 0 } };
		test2D( input, expected, null, 1, 0 );
	}

	@Test
	public void testOutOfBounds()
	{
		/*
		 * Make sure that the labeler can handle out of bounds conditions
		 */
		for ( final long[] offset : AllConnectedComponents.getStructuringElement( 2 ) )
		{
			final boolean[][] input = new boolean[ 3 ][ 3 ];
			final int[][] expected = new int[ 3 ][ 3 ];

			input[ ( int ) ( offset[ 0 ] ) + 1 ][ ( int ) ( offset[ 1 ] ) + 1 ] = true;
			expected[ ( int ) ( offset[ 0 ] ) + 1 ][ ( int ) ( offset[ 1 ] ) + 1 ] = 1;
			test2D( input, expected, null, 1, 0 );
		}
	}

	@Test
	public void testOneObject()
	{
		final boolean[][] input = new boolean[][] { { false, false, false, false, false }, { false, true, true, true, false }, { false, true, true, true, false }, { false, true, true, true, false }, { false, false, false, false, false } };
		final int[][] expected = new int[][] { { 0, 0, 0, 0, 0 }, { 0, 1, 1, 1, 0 }, { 0, 1, 1, 1, 0 }, { 0, 1, 1, 1, 0 }, { 0, 0, 0, 0, 0 } };
		test2D( input, expected, null, 1, 0 );
	}

	@Test
	public void testTwoObjects()
	{
		final boolean[][] input = new boolean[][] { { false, false, false, false, false }, { false, true, true, true, false }, { false, false, false, false, false }, { false, true, true, true, false }, { false, false, false, false, false } };
		final int[][] expected = new int[][] { { 0, 0, 0, 0, 0 }, { 0, 1, 1, 1, 0 }, { 0, 0, 0, 0, 0 }, { 0, 2, 2, 2, 0 }, { 0, 0, 0, 0, 0 } };
		test2D( input, expected, null, 1, 0 );
	}

	@Test
	public void testBigObject()
	{
		/*
		 * Internally, AllConnectedComponents has a custom stack that grows when
		 * it hits 100 elements. So we exercise that code.
		 */
		final boolean[][] input = new boolean[ 25 ][ 25 ];
		final int[][] expected = new int[ 25 ][ 25 ];
		for ( int i = 0; i < input.length; i++ )
		{
			Arrays.fill( input[ i ], true );
			Arrays.fill( expected[ i ], 1 );
		}
		test2D( input, expected, null, 1, 0 );
	}

	@Test
	public void testBigBigObject()
	{
		/*
		 * This is a regression test of a bug reported by Jean-Yves Tinevez. The
		 * code should fail on a 2-d array that needs to push more than 675 / 2
		 * elements.
		 */
		final boolean[][] input = new boolean[ 100 ][ 100 ];
		final int[][] expected = new int[ 100 ][ 100 ];
		for ( int i = 0; i < input.length; i++ )
		{
			Arrays.fill( input[ i ], true );
			Arrays.fill( expected[ i ], 1 );
		}
		test2D( input, expected, null, 1, 0 );
	}

	@Test
	public void testCustomStrel()
	{
		final boolean[][] input = new boolean[][] { { false, false, false, false, false }, { false, true, true, true, false }, { true, false, false, false, false }, { false, true, true, true, false }, { false, false, false, false, false } };
		final int[][] expected = new int[][] { { 0, 0, 0, 0, 0 }, { 0, 1, 1, 1, 0 }, { 3, 0, 0, 0, 0 }, { 0, 2, 2, 2, 0 }, { 0, 0, 0, 0, 0 } };
		final long[][] strel = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };
		test2D( input, expected, strel, 1, 0 );

	}
}
