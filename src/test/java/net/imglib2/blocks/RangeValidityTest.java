/*-
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2026 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
package net.imglib2.blocks;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

public class RangeValidityTest
{
	/**
	 * Brute-force reference: for every pixel, find which range it falls into in
	 * each dimension, and look up that range combination's validity.
	 */
	private static byte[] referenceExpand( final int[][] widths, final byte[] valid )
	{
		final int n = widths.length;
		final int[] size = new int[ n ];
		for ( int d = 0; d < n; ++d )
			for ( final int w : widths[ d ] )
				size[ d ] += w;

		int numPixels = 1;
		for ( int d = 0; d < n; ++d )
			numPixels *= size[ d ];

		final byte[] dest = new byte[ numPixels ];
		final int[] pos = new int[ n ];
		for ( int i = 0; i < numPixels; ++i )
		{
			int rem = i;
			for ( int d = 0; d < n; ++d )
			{
				pos[ d ] = rem % size[ d ];
				rem /= size[ d ];
			}
			int rangeIndex = 0;
			int rangeStep = 1;
			for ( int d = 0; d < n; ++d )
			{
				int x = 0;
				int r = 0;
				while ( x + widths[ d ][ r ] <= pos[ d ] )
					x += widths[ d ][ r++ ];
				rangeIndex += r * rangeStep;
				rangeStep *= widths[ d ].length;
			}
			dest[ i ] = valid[ rangeIndex ];
		}
		return dest;
	}

	private static int numCombinations( final int[][] widths )
	{
		int c = 1;
		for ( final int[] w : widths )
			c *= w.length;
		return c;
	}

	@Test
	public void expand1d()
	{
		final int[][] widths = { { 3, 5, 1 } };
		final byte[] valid = { 1, 0, 1 };
		final RangeValidity validity = new RangeValidity( widths, valid );

		assertEquals( 9, validity.length() );

		final byte[] dest = new byte[ validity.length() ];
		validity.expandTo( dest );
		assertArrayEquals( new byte[] { 1, 1, 1, 0, 0, 0, 0, 0, 1 }, dest );
	}

	@Test
	public void expand2d()
	{
		// x ranges of width 2 and 3; y ranges of width 1 and 2
		final int[][] widths = { { 2, 3 }, { 1, 2 } };
		//        x0 x1
		// y0      1  0
		// y1      0  1
		final byte[] valid = { 1, 0, 0, 1 };
		final RangeValidity validity = new RangeValidity( widths, valid );

		final byte[] dest = new byte[ validity.length() ];
		validity.expandTo( dest );
		assertArrayEquals( new byte[] {
				1, 1, 0, 0, 0,   // y=0
				0, 0, 1, 1, 1,   // y=1
				0, 0, 1, 1, 1 }, // y=2
				dest );
	}

	@Test
	public void expandMatchesReferenceRandomized()
	{
		final Random random = new Random( 1L );
		for ( int trial = 0; trial < 200; ++trial )
		{
			final int n = 1 + random.nextInt( 4 );
			final int[][] widths = new int[ n ][];
			for ( int d = 0; d < n; ++d )
			{
				widths[ d ] = new int[ 1 + random.nextInt( 4 ) ];
				for ( int i = 0; i < widths[ d ].length; ++i )
					widths[ d ][ i ] = 1 + random.nextInt( 4 );
			}
			final byte[] valid = new byte[ numCombinations( widths ) ];
			for ( int i = 0; i < valid.length; ++i )
				valid[ i ] = ( byte ) random.nextInt( 2 );

			final RangeValidity validity = new RangeValidity( widths, valid );
			final byte[] dest = new byte[ validity.length() ];
			validity.expandTo( dest );

			assertArrayEquals( "trial " + trial + " " + validity,
					referenceExpand( widths, valid ), dest );
		}
	}

	@Test
	public void allValid()
	{
		final int[][] widths = { { 2, 3 }, { 1, 2 } };

		final RangeValidity all = new RangeValidity( widths, new byte[] { 1, 1, 1, 1 } );
		assertTrue( all.allValid() );

		final RangeValidity none = new RangeValidity( widths, new byte[] { 0, 0, 0, 0 } );
		assertFalse( none.allValid() );

		final RangeValidity some = new RangeValidity( widths, new byte[] { 0, 1, 0, 0 } );
		assertFalse( some.allValid() );
	}

	/**
	 * {@link RangeValidity#allValid()} must agree with an AND over the expanded
	 * per-pixel validity.
	 */
	@Test
	public void allAgreesWithExpanded()
	{
		final Random random = new Random( 2L );
		for ( int trial = 0; trial < 200; ++trial )
		{
			final int n = 1 + random.nextInt( 3 );
			final int[][] widths = new int[ n ][];
			for ( int d = 0; d < n; ++d )
			{
				widths[ d ] = new int[ 1 + random.nextInt( 3 ) ];
				for ( int i = 0; i < widths[ d ].length; ++i )
					widths[ d ][ i ] = 1 + random.nextInt( 3 );
			}
			final byte[] valid = new byte[ numCombinations( widths ) ];
			for ( int i = 0; i < valid.length; ++i )
				valid[ i ] = ( byte ) ( random.nextInt( 4 ) == 0 ? 0 : 1 );

			final RangeValidity validity = new RangeValidity( widths, valid );
			final byte[] dest = new byte[ validity.length() ];
			validity.expandTo( dest );

			boolean expectedAll = true;
			for ( final byte b : dest )
				expectedAll &= b != 0;
			assertEquals( "trial " + trial, expectedAll, validity.allValid() );
		}
	}
}
